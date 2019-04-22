import java.util.HashMap;
import java.util.Map.Entry;

import src.Analyzer;

/**
 * Analyze executable, designed to analyze incoming text and associate commands to it (Regex analysis)
 */
public class Analyze
{
    /**
     * main
     * @param args
     */
    public static void main(String[] args)
    {
        Analyzer analyzer = new Analyzer();
        HashMap<String, String> output;
        output = analyzer.analyzeText("volume up with me");
        
        Entry<String, String> entry = output.entrySet().iterator().next();
        System.out.println("Command: " + entry.getKey() + ", Content: " + entry.getValue());

        return;
    }
}