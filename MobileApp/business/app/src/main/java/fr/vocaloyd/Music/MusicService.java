package fr.vocaloyd.Music;

import org.greenrobot.eventbus.EventBus;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.android.exoplayer2.ExoPlayer;

import java.util.Arrays;

import discotheque.Morceau;
import fr.vocaloyd.MainActivity;

public class MusicService extends AsyncTask<Object, Void, Uri>
{
    @SafeVarargs
    protected final Uri doInBackground(Object... args)
    {
        String ip = "192.168.1.15";

        com.zeroc.Ice.Properties props = com.zeroc.Ice.Util.createProperties();
        props.setProperty("Ice.MessageSizeMax", "40000");
        com.zeroc.Ice.InitializationData initData = new com.zeroc.Ice.InitializationData();
        initData.properties = props;
        Uri uri = null;

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(initData))
        {
            com.zeroc.Ice.ObjectPrx base = communicator.stringToProxy("SimpleManager:default -h " + ip + " -p 10000");
            com.zeroc.Ice.ObjectPrx baseClient = communicator.stringToProxy("SimpleClientManager:default -h " + ip + " -p 10000");
            discotheque.trackManagementPrx manager = discotheque.trackManagementPrx.checkedCast(base);
            discotheque.clientManagementPrx clientManager = discotheque.clientManagementPrx.checkedCast(baseClient);

            if (manager == null)
            {
                throw new Error("Invalid proxy");
            }

            MainActivity activ = (MainActivity) args[0];

            //If new client
            if (activ.getPort() == 0)
            {
                System.out.println("Setting port");
                activ.setPort(clientManager.subscribe());
            }

            String action = (String) args[1];

            switch(action)
            {
                case "init":
                    Object[] arg = Arrays.copyOfRange(args, 2, 4);
                    String[] entry = Arrays.copyOf(arg, arg.length, String[].class);

                    Morceau[] tracks = searchMethod(manager, entry);

                    if (tracks.length == 0)
                    {
                        System.out.println("No track found. Try again");
                        break;
                    }

                    String path = manager.jouerMorceaux(tracks, activ.getPort());

                    uri = Uri.parse("http://" + ip + ":" + activ.getPort() + path);
                    break;
            }
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