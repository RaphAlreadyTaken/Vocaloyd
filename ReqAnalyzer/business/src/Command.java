package src;

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

    private ArrayList<String> keys = new ArrayList<String>(Arrays.asList("playMusic", "playAlbum", "playArtist", "playGenre", "playDuration"));
    
    private ArrayList<String> playMusicValues = new ArrayList<String>(Arrays.asList("play", "play musi", "play tit", "play track", "joue", "joue musi", "joue tit", "joue track"));
    private ArrayList<String> playAlbumValues = new ArrayList<String>(Arrays.asList("play_al", "stop", "wait", "arr"));
    private ArrayList<String> playArtistValues = new ArrayList<String>(Arrays.asList("previous", "précédent", "avant"));
    private ArrayList<String> playGenreValues = new ArrayList<String>(Arrays.asList("next", "suivant", "après"));
    private ArrayList<String> playDurationValues = new ArrayList<String>(Arrays.asList("up", "volume up", "plus fort", "loud", "haut"));
    
    private ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>(Arrays.asList
    (
        playMusicValues,
        playAlbumValues,
        playArtistValues,
        playGenreValues,
        playDurationValues
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
     * @return HashMap<String, ArrayList<String>> : <name, synonyms> of supported commands
     */
    HashMap<String, ArrayList<String>> getCommands()
    {
        return commands;
    }
}
