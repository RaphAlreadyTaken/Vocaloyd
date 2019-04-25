package fr.vocaloyd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Command class, designed to hold commands and compatible values
 * WARNING : keys and values need to be the same length
 */
public class Command
{
    private HashMap<String, ArrayList<String>> commands;

    private ArrayList<String> keys = new ArrayList<String>(Arrays.asList("play", "pause", "previous", "next", "up", "down"));
    
    private ArrayList<String> playValues = new ArrayList<>(Arrays.asList("play", "jou"));
    private ArrayList<String> pauseValues = new ArrayList<>(Arrays.asList("pause", "stop", "wait", "arr"));
    private ArrayList<String> previousValues = new ArrayList<>(Arrays.asList("previous", "précédent", "avant"));
    private ArrayList<String> nextValues = new ArrayList<>(Arrays.asList("next", "suivant", "après"));
    private ArrayList<String> upValues = new ArrayList<>(Arrays.asList("up", "volume up", "plus fort", "loud", "haut"));
    private ArrayList<String> downValues = new ArrayList<>(Arrays.asList("down", "volume down", "moins fort", "quiet", "bas"));
    
    private ArrayList<ArrayList<String>> values = new ArrayList<>(Arrays.asList
    (
        playValues,
        pauseValues,
        previousValues,
        nextValues,
        upValues,
        downValues
    ));
    
    /**
     * Constructor
     * @throws Exception if incompatible sizes
     */
    public Command() throws Exception
    {  
        if (keys.size() != values.size())
        {
            throw new Exception("Incompatible size between keys and values collections");
        }
        
        commands = new HashMap<String, ArrayList<String>>();
        int nbCommands = keys.size();

        for (int i = 0; i < nbCommands; ++i)
        {
            commands.put(keys.get(i), values.get(i));
        }
    }

    /**
     * Getter commands
     * @return HashMap<String, ArrayList<String>> : <list, synonyms> of supported commands
     */
    HashMap<String, ArrayList<String>> getCommands()
    {
        return commands;
    }
}
