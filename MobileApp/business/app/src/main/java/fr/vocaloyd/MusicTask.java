package fr.vocaloyd;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.Map;

import discotheque.Morceau;

public class MusicTask extends AsyncTask<Map.Entry<String, String>, Void, Void>
{
    Context taskContext;

    public MusicTask(Context ctx)
    {
        taskContext = ctx;
    }

    protected Void doInBackground(Map.Entry<String, String>... entry)
    {
        com.zeroc.Ice.Properties props = com.zeroc.Ice.Util.createProperties();
        props.setProperty("Ice.MessageSizeMax", "40000");
        com.zeroc.Ice.InitializationData initData = new com.zeroc.Ice.InitializationData();
        initData.properties = props;

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(initData))
        {
            System.out.println("Stream");

            com.zeroc.Ice.ObjectPrx base = communicator.stringToProxy("SimpleManager:default -h 192.168.1.15 -p 10000");
            discotheque.trackManagementPrx manager = discotheque.trackManagementPrx.checkedCast(base);

            if (manager == null)
            {
                throw new Error("Invalid proxy");
            }

            System.out.print(entry[0].getKey());

            //TODO: tester les clés passées -> adapter les méthodes/arguments (album, artiste, ...)

            Morceau[] tracks = manager.rechercherParTitre(entry[0].getValue());
            String target = manager.jouerMorceaux(tracks);

            String uri = "http://192.168.1.15:" + target;

            ExoPlayer player = ExoPlayerFactory.newSimpleInstance(taskContext);
            String agent = Util.getUserAgent(taskContext, taskContext.getString(R.string.app_name));
            DefaultDataSourceFactory data = new DefaultDataSourceFactory(taskContext, agent);
            MediaSource source = new ExtractorMediaSource.Factory(data).createMediaSource(Uri.parse(uri));
            player.prepare(source);
            player.setPlayWhenReady(true);
        }
        return null;
    }
}
