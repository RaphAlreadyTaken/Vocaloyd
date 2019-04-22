package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Analyzer class, designed to provide analysis of incoming text and associate commands to it (Regex)
 */
public class Analyzer
{
    private Command command;

    /**
     * Constructor
     * @throws Exception from Command creation
     */
    public Analyzer()
    {
        try
        {
            command = new Command();
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Analyzes an input string to detect command and content
     * @param input : string to analyze
     * @return HashMap<String, String> : <command, content>
     */
    public HashMap<String, String> analyzeText(String input)
    {
        input = input.toLowerCase();    ///Standardizing output

        HashMap<String, ArrayList<String>> commands;
        commands = command.getCommands();

        HashMap<String, String> output = new HashMap<String, String>();

        for (Entry<String, ArrayList<String>> entry : commands.entrySet())
        {
            for (String str : entry.getValue())
            {
                if (input.matches("(?i).*" + str + ".*")) //Regex
                {
                    output.put(str, input.substring(input.indexOf(str) + str.length()));
                    return output;
                }
            }
        }

        return output;
    }
}