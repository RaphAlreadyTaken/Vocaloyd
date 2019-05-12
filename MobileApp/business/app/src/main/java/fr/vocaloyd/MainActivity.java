package fr.vocaloyd;

import android.graphics.BitmapFactory;

import android.Manifest;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.util.Base64;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fr.vocaloyd.Analysis.AnalyzeEvent;
import fr.vocaloyd.Analysis.AnalyzeService;
import fr.vocaloyd.Music.BackgroundMusicService;
import fr.vocaloyd.Music.MusicEvent;
import fr.vocaloyd.Music.MusicService;
import fr.vocaloyd.Transcription.TranscribeEvent;
import fr.vocaloyd.Transcription.TranscribeService;

public class MainActivity extends AppCompatActivity
{
    boolean recording;
    MediaRecorder rec;
    Context servContext;
    ExoPlayer mainPlayer;
    BackgroundMusicService bServ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        String[] permissions = {Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions, 10);

        EventBus.getDefault().register(this);

        recording = false;
        rec = new MediaRecorder();

        servContext = VocaloydApp.getAppContext();
        mainPlayer = ExoPlayerFactory.newSimpleInstance(servContext);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        bServ = new BackgroundMusicService("getInfos");
        bServ.start();
    }

    public void record(View view) throws IOException
    {
        File audioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/command.amr");

        if (!recording)
        {
            mainPlayer.setPlayWhenReady(false);
            rec.setAudioSource(MediaRecorder.AudioSource.MIC);
            rec.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
            rec.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
            rec.setAudioSamplingRate(16000);
            rec.setOutputFile(audioFile.getPath());

            rec.prepare();

            rec.start();
            view.setForeground(ContextCompat.getDrawable(this, R.mipmap.ic_mic_active_empty_foreground));
            recording = true;
            System.out.println("Starting recording");
        }
        else
        {
            rec.stop();
            view.setForeground(ContextCompat.getDrawable(this, R.mipmap.ic_mic_empty_foreground));
            recording = false;
            rec.reset();
            System.out.println("Stopping recording");
            mainPlayer.setPlayWhenReady(true);

            transcribeCommand(audioFile);
        }
    }

    /**
     *
     * @param file : File to send
     * @return String : Transcribed audio
     */
    public void transcribeCommand(File file)
    {
        System.out.println("Transcription");

        TranscribeService tServ = new TranscribeService();
        tServ.execute(file);
    }

    @Subscribe
    public void onTranscribeEvent(TranscribeEvent tEvent)
    {
        System.out.println("Transcription event received:");
        System.out.println(tEvent.getResult());

        if (tEvent.getResult() == null)
        {
            System.out.println("No result");
            return;
        }
        else
        {
            analyzeCommand(tEvent.getResult());
        }
    }

    public void analyzeCommand(String transcription)
    {
        System.out.println("Analysis");

        AnalyzeService aServ = new AnalyzeService();
        aServ.execute(transcription);
    }

    @Subscribe
    public void onAnalyzeEvent(AnalyzeEvent aEvent)
    {
        System.out.println("Analyze event received:");
        System.out.println(aEvent.getResult());

        if (aEvent.getResult() == null)
        {
            System.out.println("No result");
            return;
        }
        else
        {
            musicCommand(aEvent.getResult());
        }
    }

    public void musicCommand(Map.Entry<String, String> command)
    {
        System.out.println("Streaming");

        MusicService mServ = new MusicService();
        mServ.execute("init", command.getKey(), command.getValue());

        mServ = new MusicService();
        mServ.execute("getInfos");
    }

    @Subscribe
    public void onMusicEvent(MusicEvent mEvent)
    {
        System.out.println("Music event received:");
        System.out.println(mEvent.getResult());
        HashMap<String, String> result = mEvent.getResult();

        if (result.get("uri") != null)
        {
            PlayerView view = findViewById(R.id.playerView);
            view.setPlayer(mainPlayer);
            String agent = Util.getUserAgent(servContext, servContext.getApplicationInfo().name);
            DefaultDataSourceFactory data = new DefaultDataSourceFactory(servContext, agent);
            MediaSource source = new ExtractorMediaSource.Factory(data).createMediaSource(Uri.parse(result.get("uri")));
            mainPlayer.prepare(source);
            mainPlayer.setPlayWhenReady(true);
            ImageButton butt = findViewById(R.id.exo_play);
            playPause(butt);
            return;
        }

        if (result.get("info") != null)
        {
            Boolean freshContent = false;
            Boolean changedContent = false;

            if (result.get("Title") == null)
            {
                return;
            }

            TextView tView;

            tView = findViewById(R.id.textTitre);
            String titreBase = tView.getText().toString();

            String titre = result.get("Title");
            String album = result.get("Album");
            String artiste = result.get("Artist");

            if (titreBase.length() == 0)    //No data displayed
            {
                freshContent = true;
            }
            else    //Test if displayed data is up-to-date
            {
                tView = findViewById(R.id.textAlbum);
                String albumBase = tView.getText().toString();

                tView = findViewById(R.id.textArtiste);
                String artisteBase = tView.getText().toString();

                if (!titre.equals(titreBase) || !album.equals(albumBase) || !artiste.equals(artisteBase))
                {
                    changedContent = true;
                }
            }

            ///Outdated values → New values
            if (freshContent == true || changedContent == true)
            {
                String desc = result.get("Description");

                desc = desc.substring(2); //Remove leading "b'"

                byte[] imageStr = Base64.decode(desc, Base64.DEFAULT);
                Drawable img = new BitmapDrawable(getResources(), BitmapFactory.decodeByteArray(imageStr, 0, imageStr.length));

                PlayerView view = findViewById(R.id.playerView);
                view.setDefaultArtwork(img);

                tView = findViewById(R.id.textTitre);
                tView.setText(titre);

                tView = findViewById(R.id.textAlbum);
                tView.setText(album);

                tView = findViewById(R.id.textArtiste);
                tView.setText(artiste);

                tView = findViewById(R.id.textGenre);
                tView.setText(result.get("Genre"));

                tView = findViewById(R.id.textAnnee);
                tView.setText(result.get("Date"));
            }

            return;
        }
    }

    public void playPause(View view)
    {
        ImageButton butt = (ImageButton) view;

        if(mainPlayer.getPlaybackState() == Player.STATE_READY && mainPlayer.getPlayWhenReady())
        {
            mainPlayer.setPlayWhenReady(false);
            butt.setImageDrawable(getDrawable(R.drawable.exo_controls_play));
        }
        else
        {
            mainPlayer.setPlayWhenReady(true);
            butt.setImageDrawable(getDrawable(R.drawable.exo_controls_pause));
        }
    }

    public void previousTrack(View view)
    {
        MusicService mServ = new MusicService();
        mServ.execute("previousTrack");

        mServ = new MusicService();
        mServ.execute("getInfos");
    }

    public void nextTrack(View view)
    {
        MusicService mServ = new MusicService();
        mServ.execute("nextTrack");

        mServ = new MusicService();
        mServ.execute("getInfos");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        bServ.interrupt();
    }

    @Override
    protected void onDestroy()
    {
        System.out.println("Destroying");
        super.onDestroy();
        MusicService mServ = new MusicService();
        mServ.execute("unsub");
    }
}