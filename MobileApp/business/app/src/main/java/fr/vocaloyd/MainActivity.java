package fr.vocaloyd;

import android.Manifest;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

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
import java.util.Map;

import fr.vocaloyd.Analysis.AnalyzeEvent;
import fr.vocaloyd.Analysis.AnalyzeService;
import fr.vocaloyd.Music.MusicEvent;
import fr.vocaloyd.Music.MusicService;
import fr.vocaloyd.Transcription.TranscribeEvent;
import fr.vocaloyd.Transcription.TranscribeService;

public class MainActivity extends AppCompatActivity
{
    private int port = 0;
    private boolean recording = false;
    private MediaRecorder rec = new MediaRecorder();
    private Context servContext = VocaloydApp.getAppContext();
    private ExoPlayer mainPlayer = ExoPlayerFactory.newSimpleInstance(servContext);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        String[] permissions = {Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, permissions, 10);

        EventBus.getDefault().register(this);
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

    @Subscribe
    public void onTranscribeEvent(TranscribeEvent tEvent)
    {
        System.out.println("Transcription event received:");
        System.out.println(tEvent.getResult());

        analyzeCommand(tEvent.getResult());
    }

    @Subscribe
    public void onAnalyzeEvent(AnalyzeEvent aEvent)
    {
        System.out.println("Analyze event received:");
        System.out.println(aEvent.getResult());

        musicCommand(aEvent.getResult());
    }

    @Subscribe
    public void onMusicEvent(MusicEvent mEvent)
    {
        System.out.println("Music event received:");
        System.out.println(mEvent.getResult());

        if (mEvent.getResult() == null)
        {
            System.out.println("No result");
            return;
        }

        PlayerView view = findViewById(R.id.playerView);
        view.setPlayer(mainPlayer);
        String agent = Util.getUserAgent(servContext, servContext.getApplicationInfo().name);
        DefaultDataSourceFactory data = new DefaultDataSourceFactory(servContext, agent);
        MediaSource source = new ExtractorMediaSource.Factory(data).createMediaSource(mEvent.getResult());
        mainPlayer.prepare(source);
        mainPlayer.setPlayWhenReady(true);
    }

    public void record(View view) throws IOException
    {
        File audioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/command.amr");

        if (!recording)
        {
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

    public void analyzeCommand(String transcription)
    {
        System.out.println("Analysis");

        AnalyzeService aServ = new AnalyzeService();
        aServ.execute(transcription);
    }

    public void musicCommand(Map.Entry<String, String> command)
    {
        System.out.println("Streaming");

        MusicService mServ = new MusicService();
        mServ.execute(this, "init", command.getKey(), command.getValue());
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
        mServ.execute(this, "previousTrack");
    }

    public void nextTrack(View view)
    {
        MusicService mServ = new MusicService();
        mServ.execute(this, "nextTrack");
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int paramPort)
    {
        port = paramPort;
    }
}