package fr.vocaloyd;

import java.util.Map;

public class AnalyzeEvent
{
    private Map.Entry<String, String> eventResult;

    public AnalyzeEvent(Map.Entry<String, String> result)
    {
        eventResult = result;
    }

    public Map.Entry<String, String> getResult()
    {
        return eventResult;
    }
}