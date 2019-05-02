package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Command class, designed to hold commands and compatible values
 * WARNING : keys and values of respective attributes need to be the same length
 */
public class Command
{
    private HashMap<String, ArrayList<String>> baseCommands;
    private HashMap<String, ArrayList<String>> specificCommands;

    private ArrayList<String> baseKeys = new ArrayList<>(Arrays.asList("play"));
    private ArrayList<String> playValues = new ArrayList<>(Arrays.asList("jou", "plai", "play"));

    private ArrayList<ArrayList<String>> baseValues = new ArrayList<>(Arrays.asList
    (
        playValues
    ));

    private ArrayList<String> specificKeys = new ArrayList<>(Arrays.asList("playTrack", "playAlbum", "playArtist", "playGenre", "playDuration"));
    private ArrayList<String> playTrackValues = new ArrayList<>(Arrays.asList("chanson", "musi", "piste", "tit", "track"));
    private ArrayList<String> playAlbumValues = new ArrayList<>(Arrays.asList("album"));
    private ArrayList<String> playArtistValues = new ArrayList<>(Arrays.asList("artist", "band", "chanteu", "group", "singer"));
    private ArrayList<String> playGenreValues = new ArrayList<>(Arrays.asList("genre", "style"));
    private ArrayList<String> playDurationValues = new ArrayList<>(Arrays.asList("pendant", "for"));
    
    private ArrayList<ArrayList<String>> specificValues = new ArrayList<>(Arrays.asList
    (
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
        if (baseKeys.size() != baseValues.size() || specificKeys.size() != specificValues.size())
        {
            throw new Exception("Incompatible size between keys and values collections");
        }
        
        baseCommands = new HashMap<>();
        specificCommands = new HashMap<>();

        int nbBaseCommands = baseKeys.size();
        int nbSpecificCommands = specificKeys.size();

        for (int i = 0; i < nbBaseCommands; ++i)
        {
            baseCommands.put(baseKeys.get(i), baseValues.get(i));
        }

        for (int i = 0; i < nbSpecificCommands; ++i)
        {
            specificCommands.put(specificKeys.get(i), specificValues.get(i));
        }
    }

    /**
     * Getter base commands
     * @return HashMap<String, ArrayList<String>> : <name, synonyms> of supported commands
     */
    HashMap<String, ArrayList<String>> getBaseCommands()
    {
        return baseCommands;
    }

    /**
     * Getter specific commands
     * @return HashMap<String, ArrayList<String>> : <name, synonyms> of supported commands
     */
    HashMap<String, ArrayList<String>> getSpecificCommands()
    {
        return specificCommands;
    }
}
