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

    private ArrayList<String> keys = new ArrayList<String>(Arrays.asList("play", "playTrack", "playAlbum", "playArtist", "playGenre", "playDuration"));
    
    private ArrayList<String> playValues = new ArrayList<String>(Arrays.asList("jou", "plai", "play"));
    private ArrayList<String> playTrackValues = new ArrayList<String>(Arrays.asList("musi", "piste", "tit", "track"));
    private ArrayList<String> playAlbumValues = new ArrayList<String>(Arrays.asList("album"));
    private ArrayList<String> playArtistValues = new ArrayList<String>(Arrays.asList("artist", "band", "chanteu", "group", "singer"));
    private ArrayList<String> playGenreValues = new ArrayList<String>(Arrays.asList("genre", "style"));
    private ArrayList<String> playDurationValues = new ArrayList<String>(Arrays.asList("pendant", "for"));
    
    private ArrayList<ArrayList<String>> values = new ArrayList<ArrayList<String>>(Arrays.asList
    (
        playValues,
        playTrackValues,
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
