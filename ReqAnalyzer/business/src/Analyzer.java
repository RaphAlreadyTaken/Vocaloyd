package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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

    //TODO: Après identification commande, avancer jusqu'à l'espace suivant pour suite de la commande (substr group(2) par ex)
    //Gérer également les articles le, le, l', the, a (définir comment)

    /**
     * Analyzes an input string to detect command and contentGroup
     * @param input : string to analyze
     * @return HashMap<String, String> : <command, content>
     */
    public HashMap<String, String> analyzeText(String input)
    {
        // input = input.toLowerCase();    //Standardizing output (INFO: no longer needed (mongo indices))
        
        Pattern basePat = null;
        Matcher matcher = null;
        String commandGroup = "";
        String contentGroup = "";
        
        HashMap<String, ArrayList<String>> baseCommands = command.getBaseCommands();
        Boolean foundBase = false;

        //Testing base command
        for (Entry<String, ArrayList<String>> entry : baseCommands.entrySet())
        {
            if (foundBase == false)
            {
                for (String str : entry.getValue())
                {
                    basePat = Pattern.compile("(?i)(" + str + ")(.*)"); //Regex
                    matcher = basePat.matcher(input);
        
                    if (matcher.matches())
                    {
                        commandGroup = matcher.group(1);
                        contentGroup = matcher.group(2);
                        System.out.println("command: " + commandGroup); //TODO: output test, à virer
                        System.out.println("content: " + contentGroup); //TODO: output test, à virer
                        foundBase = true;
                        break;
                    }
                }
            }
            else
            {
                break;
            }
        }

        if (foundBase == false)
        {
            return null; //Unrecognized command
        }

        HashMap<String, ArrayList<String>> specificCommands = command.getSpecificCommands();
        Boolean foundSpecific = false;
        HashMap<String, String> output = new HashMap<String, String>();

        //Testing specific command
        for (Entry<String, ArrayList<String>> entry : specificCommands.entrySet())
        {
            if (foundSpecific == false)
            {
                for (String str : entry.getValue())
                {
                    basePat = Pattern.compile("(?i)(" + str + ")(.*)"); //Regex
                    matcher = basePat.matcher(contentGroup);
        
                    if (matcher.matches())
                    {
                        commandGroup += matcher.group(1); //Full command
                        contentGroup = matcher.group(2);
                        System.out.println("command: " + commandGroup); //TODO: output test, à virer
                        System.out.println("content: " + contentGroup); //TODO: output test, à virer
                        foundSpecific = true;
                        break;
                    }
                }
            }
            else
            {
                break;
            }
        }

        System.out.println("command: " + commandGroup);
        System.out.println("content: " + contentGroup);

        output.put(commandGroup, contentGroup);

        return output;
    }
}