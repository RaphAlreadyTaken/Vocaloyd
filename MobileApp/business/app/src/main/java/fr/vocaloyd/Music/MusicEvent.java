package fr.vocaloyd.Music;

import java.util.HashMap;

public class MusicEvent
{
    private HashMap<String, String> eventResult;

    public MusicEvent(HashMap<String, String> result)
    {
        eventResult = result;
    }

    public HashMap<String, String> getResult()
    {
        return eventResult;
    }
}