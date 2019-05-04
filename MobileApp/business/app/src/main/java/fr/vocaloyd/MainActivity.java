package fr.vocaloyd;

import android.Manifest;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
{

    boolean recording = false;
    MediaRecorder rec = new MediaRecorder();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] permissions = {Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        ActivityCompat.requestPermissions(this, permissions, 10);
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

    public void record(View view) throws IOException
    {
        File audioFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/command.amr");

        if (recording == false)
        {
            rec.setAudioSource(MediaRecorder.AudioSource.MIC);
            rec.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);
            rec.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
            rec.setAudioSamplingRate(16000);
            rec.setOutputFile(audioFile.getPath());

            rec.prepare();

            rec.start();
            view.setBackground(ContextCompat.getDrawable(this, R.mipmap.ic_mic_active));
            recording = true;
            System.out.println("Starting recording");
        }
        else
        {
            rec.stop();
            view.setBackground(ContextCompat.getDrawable(this, R.mipmap.ic_mic));
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
    public String transcribeCommand(File file)
    {
        System.out.println("Transcription");

        TranscribeTask task = new TranscribeTask(this);
        task.execute(file);

        return null;
    }
}