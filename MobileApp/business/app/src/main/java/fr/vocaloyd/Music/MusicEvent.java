package fr.vocaloyd.Music;

import android.net.Uri;

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