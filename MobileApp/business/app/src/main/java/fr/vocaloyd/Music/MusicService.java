package fr.vocaloyd.Music;

import org.greenrobot.eventbus.EventBus;

import android.os.AsyncTask;

import java.util.Arrays;
import java.util.HashMap;

import discotheque.*;
import fr.vocaloyd.VocaloydApp;

public class MusicService extends AsyncTask<Object, Void, HashMap<String, String>>
{
    @SafeVarargs
    protected final HashMap<String, String> doInBackground(Object... args)
    {
        String ip = "192.168.43.15";

        com.zeroc.Ice.Properties props = com.zeroc.Ice.Util.createProperties();
        props.setProperty("Ice.MessageSizeMax", "40000");
        com.zeroc.Ice.InitializationData initData = new com.zeroc.Ice.InitializationData();
        initData.properties = props;
        HashMap<String, String> result = new HashMap<>();

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

            //If new client
            if (VocaloydApp.getPort() == 0)
            {
                System.out.println("Setting port");
                VocaloydApp.setPort(clientManager.subscribe());
            }

            int port = VocaloydApp.getPort();

            String action = (String) args[0];

            switch(action)
            {
                case "init":
                    System.out.println("Client " + port + " request : init");
                    result.put("music", "");
                    Object[] arg = Arrays.copyOfRange(args, 1, 3);
                    String[] entry = Arrays.copyOf(arg, arg.length, String[].class);

                    Morceau[] tracks = searchMethod(manager, entry);

                    if (tracks.length == 0)
                    {
                        System.out.println("No track found. Try again");
                        break;
                    }

                    String path = manager.jouerMorceaux(tracks, port);

                    result.put("uri", "http://" + ip + ":" + port + path);
                    break;

                case "previousTrack":
                    System.out.println("Client " + port + " request : previousTrack");
                    manager.previousTrack(port);
                    break;

                case "nextTrack":
                    System.out.println("Client " + port + " request : nextTrack");
                    manager.nextTrack(port);
                    break;

                case "getInfos":
                    System.out.println("Client " + port + " request : getInfos");
                    result.put("info", "");
                    Entry[] infos = manager.getInfos(port);

                    for (Entry ent : infos)
                    {
                        result.put(ent.key, ent.value);
                    }
                    break;

                case "unsub":
                    System.out.println("Client " + port + " request : getInfos");
                    clientManager.unsubscribe(VocaloydApp.getPort());

                default:
                    break;
            }
        }

        return result;
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
            default:
                break;
        }

        return null;
    }

    protected void onPostExecute(HashMap<String, String> result)
    {
        EventBus.getDefault().post(new MusicEvent(result));
    }
}