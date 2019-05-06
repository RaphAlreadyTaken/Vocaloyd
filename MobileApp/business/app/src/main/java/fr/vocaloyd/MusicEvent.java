package fr.vocaloyd;

import android.net.Uri;

import com.google.android.exoplayer2.ExoPlayer;

public class MusicEvent
{
    private Uri eventResult;

    public MusicEvent(Uri result)
    {
        eventResult = result;
    }

    public Uri getResult()
    {
        return eventResult;
    }
}