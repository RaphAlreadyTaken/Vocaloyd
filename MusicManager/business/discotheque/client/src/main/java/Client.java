import discotheque.*;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Scanner;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

import com.zeroc.Ice.*;

public class Client
{
    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedIOException
    {
        Properties props = Util.createProperties(args);
        props.setProperty("Ice.MessageSizeMax", "40000");
        InitializationData initData = new InitializationData();
        initData.properties = props;
        

        try (Communicator communicator = Util.initialize(initData))
        {
            ObjectPrx base = communicator.stringToProxy("SimpleManager:default -h 192.168.43.15 -p 10000");
            ObjectPrx baseClient = communicator.stringToProxy("SimpleClientManager:default -h 192.168.43.15 -p 10000");
            trackManagementPrx manager = trackManagementPrx.checkedCast(base);
            clientManagementPrx clientManager = clientManagementPrx.checkedCast(baseClient);

            int port = clientManager.subscribe();

            String target = "";

            if (manager == null)
            {
                throw new Error("Invalid proxy");
            }

            while (true)
            {
                String [] choix =
                {
                    "1- Ajouter une piste",
                    "2- Lister les titres",
                    "3- Rechercher par titre",
                    "4- Rechercher par artiste",
                    "5- Rechercher par album",
                    "6- Rechercher par genre",
                    "7- Supprimer une piste",
                    "8- Supprimer un album",
                    "9- Jouer",
                    "10- Gérer lecture",
                    "0- Quitter"
                };

                System.out.println("** Gestion de discothèque **");

                for (String str : choix)
                {
                    System.out.println(str);
                }

                int choixInt = saisirInt(choix.length);

                String choixStr = "";
                String choixStr2 = "";
                Morceau [] tracks;
                Boolean retour = false;

                switch(choixInt)
                {
                    case 1:
                        Morceau track = new Morceau();

                        System.out.print("Titre : ");
                        choixStr = saisirString();
                        track.titre = choixStr;
                        
                        System.out.print("Artiste : ");
                        choixStr = saisirString();
                        track.artiste = choixStr;

                        System.out.print("Album : ");
                        choixStr = saisirString();
                        track.album = choixStr;

                        System.out.print("Genre : ");
                        choixStr = saisirString();
                        track.genre = choixStr;

                        System.out.print("Piste : ");
                        choixStr = saisirString();
                        track.piste = choixStr;

                        System.out.print("Image : ");
                        choixStr = saisirString();
                        track.image = choixStr;

                        System.out.print("Fichier : ");
                        choixStr = saisirString();
                        track.fichier = choixStr;

                        manager.ajouterTitre(track);
                        break;

                    case 2:
                        tracks = manager.recupererTitres();
                        displayTitles(tracks);
                        break;

                    case 3:
                        System.out.print("Titre : ");
                        choixStr = saisirString();
                        tracks = manager.rechercherParTitre(choixStr);
                        displayTitles(tracks);
                        break;

                    case 4:
                        System.out.print("Artiste : ");
                        choixStr = saisirString();
                        tracks = manager.rechercherParArtiste(choixStr);
                        displayTitles(tracks);
                        break;

                    case 5:
                        System.out.print("Album : ");
                        choixStr = saisirString();
                        tracks = manager.rechercherParAlbum(choixStr);
                        displayTitles(tracks);

                    case 6:
                        System.out.print("Genre : ");
                        choixStr = saisirString();
                        tracks = manager.rechercherParGenre(choixStr);
                        displayTitles(tracks);

                    case 7:
                        System.out.print("Titre : ");
                        choixStr = saisirString();
                        
                        System.out.print("Artiste : ");
                        choixStr2 = saisirString();
                        
                        retour = manager.supprimerTitre(choixStr, choixStr2);

                        if (retour == true)
                        {
                            System.out.println("\nTitre supprimé\n");
                        }
                        else
                        {
                            System.out.println("\nAucun titre correspondant trouvé\n");
                        }

                        break;

                    case 8:
                        System.out.print("Artiste : ");
                        choixStr = saisirString();
                        
                        System.out.print("Album : ");
                        choixStr2 = saisirString();
                        
                        retour = manager.supprimerAlbum(choixStr, choixStr2);

                        if (retour == true)
                        {
                            System.out.println("\nAlbum supprimé\n");
                        }
                        else
                        {
                            System.out.println("\nAucun album correspondant trouvé\n");
                        }

                        break;

                    case 9:
                        System.out.println("Type de recherche: ");

                        String[] choix2 =
                        {
                            "1- Titre",
                            "2- Artiste",
                            "3- Album",
                            "4- Genre",
                            "5- Tous"
                        };

                        for (String str : choix2)
                        {
                            System.out.println(str);
                        }

                        choixInt = saisirInt(choix2.length);

                        switch (choixInt)
                        {
                            case 1:
                                System.out.print("Titre : ");
                                choixStr2 = saisirString();
                                System.out.println(choixStr2);
                                tracks = manager.rechercherParTitre(choixStr2);
                                target = manager.jouerMorceaux(tracks, port);
                                break;

                            case 2:
                                System.out.print("Artiste : ");
                                choixStr2 = saisirString();
                                tracks = manager.rechercherParArtiste(choixStr2);
                                target = manager.jouerMorceaux(tracks, port);
                                break;

                            case 3:
                                System.out.print("Album : ");
                                choixStr2 = saisirString();
                                tracks = manager.rechercherParAlbum(choixStr2);
                                target = manager.jouerMorceaux(tracks, port);
                                break;

                            case 4:
                                System.out.print("Genre : ");
                                choixStr2 = saisirString();
                                tracks = manager.rechercherParGenre(choixStr2);
                                target = manager.jouerMorceaux(tracks, port);
                                break;

                            case 5:
                                System.out.print("Information : ");
                                choixStr2 = saisirString();
                                tracks = manager.rechercher(choixStr2);
                                target = manager.jouerMorceaux(tracks, port);
                                break;

                            default:
                                System.out.println("Input non reconnu");
                                break;
                        }

                        Runtime.getRuntime().exec("vlc http://192.168.43.15:" + port + target);
                        break;

                    case 10:
                        Boolean end = false;

                        String[] choix3 = 
                        {
                            "1- Play/Pause",
                            "2- Next track",
                            "3- Previous track",
                            "4- Info current track",
                            "5- Quitter"
                        };

                        while (end == false)
                        {
                            System.out.println("Action: ");
                            
                            for (String str : choix3)
                            {
                                System.out.println(str);
                            }

                            choixInt = saisirInt(choix3.length);

                            switch (choixInt)
                            {
                                case 1:
                                    manager.playPause(port);
                                    break;

                                case 2:
                                    manager.nextTrack(port);
                                    break;

                                case 3:
                                    manager.previousTrack(port);
                                    break;

                                case 4:
                                    Entry[] result = manager.getInfos(port);
                                    HashMap<String, String> resultMap = new HashMap<>();

                                    for (Entry ent : result)
                                    {
                                        resultMap.put(ent.key, ent.value);
                                    }
                                    
                                    String album = resultMap.get("Album");
                                    String desc = resultMap.get("Description");

                                    desc = desc.substring(2); //Remove leading "b'"
                                    
                                    //Inspired from https://stackoverflow.com/a/50682688/9908246
                                    byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(desc);
                                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
                                    File outputfile = new File("./business/discotheque/client/img/" + album + ".jpg");
                                    System.out.println(ImageIO.write(image, "jpg", outputfile));

                                    break;

                                case 5:
                                    end = true;
                                    break;

                                default:
                                    break;
                            }
                        }

                        break;

                    case 0:
                        clientManager.unsubscribe(port);
                        return;

                    default:
                        break;
                }
            }
        }
    }

    public static int saisirInt(int valMax)
    {
        Scanner sc = new Scanner(System.in);

        int choix = -1;
        Boolean valid = false;

        while (valid == false)
        {
            try
            {
                choix = sc.nextInt();

                if (choix < 0 || choix > valMax)
                {
                    throw new java.lang.Exception("Valeur incorrecte");
                }
            }
            catch (java.lang.Exception ex)
            {
                System.out.println("\nInput invalide : " + ex.getMessage() + "\n");
            }

            valid = true;
        }

        return choix;
    }

    public static String saisirString()
    {
        Scanner sc = new Scanner(System.in);

        String choix = "";
        Boolean valid = false;

        while (valid == false)
        {
            try
            {
                choix = sc.nextLine();
            }
            catch (java.lang.Exception ex)
            {
                System.out.println("\nInput invalide : " + ex.getMessage() + "\n");
            }

            valid = true;
        }

        return choix;
    }

    public static void displayTitle(Morceau track)
    {
        System.out.println("*** " + track.titre);
        System.out.println("* Artiste : " + track.artiste);
        System.out.println("* Album : " + track.album);
        System.out.println("* Genre : " + track.genre);
        System.out.println("***");
        System.out.println();
    }

    public static void displayTitles(Morceau [] tracks)
    {
        for (Morceau track : tracks)
        {
            displayTitle(track);
        }
    }
}