package fr.vocaloyd.Music;

import org.greenrobot.eventbus.EventBus;

import android.net.Uri;
import android.os.AsyncTask;

import com.google.android.exoplayer2.ExoPlayer;

import discotheque.Morceau;

public class MusicService extends AsyncTask<String, Void, Uri>
{
    @SafeVarargs
    protected final Uri doInBackground(String... entry)
    {
        ExoPlayer player = null;

        com.zeroc.Ice.Properties props = com.zeroc.Ice.Util.createProperties();
        props.setProperty("Ice.MessageSizeMax", "40000");
        com.zeroc.Ice.InitializationData initData = new com.zeroc.Ice.InitializationData();
        initData.properties = props;
        Uri uri;

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(initData))
        {
            com.zeroc.Ice.ObjectPrx base = communicator.stringToProxy("SimpleManager:default -h 192.168.1.15 -p 10000");
            com.zeroc.Ice.ObjectPrx baseClient = communicator.stringToProxy("SimpleClientManager:default -h 192.168.1.15 -p 10000");
            discotheque.trackManagementPrx manager = discotheque.trackManagementPrx.checkedCast(base);
            discotheque.clientManagementPrx clientManager = discotheque.clientManagementPrx.checkedCast(baseClient);

            if (manager == null)
            {
                throw new Error("Invalid proxy");
            }

            int port = clientManager.subscribe();
            Morceau[] tracks = searchMethod(manager, entry);
            String target = manager.jouerMorceaux(tracks, port);

            uri = Uri.parse("http://192.168.1.15:" + port + target);
        }

        return uri;
    }

    private Morceau[] searchMethod(discotheque.trackManagementPrx manager, String[] entry)
    {
        switch(entry[0])
        {
            case "play":
                return manager.rechercher(entry[1]);
            case "playTrack":
                return manager.rechercherParTitre(entry[1]);
            case "playAlbum":
                return manager.rechercherParAlbum(entry[1]);
            case "playArtist":
                return manager.rechercherParArtiste(entry[1]);
            case "playGenre":
                return manager.rechercherParGenre(entry[1]);
            case "playDuration":
                return manager.rechercherParDuree(entry[1]);
            default:
                break;
        }

        return null;
    }

    protected void onPostExecute(Uri uri)
    {
        EventBus.getDefault().post(new MusicEvent(uri));
    }
}