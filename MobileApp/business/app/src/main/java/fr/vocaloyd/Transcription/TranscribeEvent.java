package fr.vocaloyd.Transcription;

public class TranscribeEvent
{
    private String eventResult;

    public TranscribeEvent(String result)
    {
        eventResult = result;
    }

    public String getResult()
    {
        return eventResult;
    }
}