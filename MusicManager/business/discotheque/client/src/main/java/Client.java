import discotheque.Morceau;

import java.util.Scanner;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Client
{
    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedIOException
    {
        com.zeroc.Ice.Properties props = com.zeroc.Ice.Util.createProperties(args);
        props.setProperty("Ice.MessageSizeMax", "40000");
        com.zeroc.Ice.InitializationData initData = new com.zeroc.Ice.InitializationData();
        initData.properties = props;
        

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(initData))
        {
            com.zeroc.Ice.ObjectPrx base = communicator.stringToProxy("SimpleManager:default -h 192.168.1.15 -p 10000");
            discotheque.trackManagementPrx manager = discotheque.trackManagementPrx.checkedCast(base);

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
                    "5- Supprimer une piste",
                    "6- Supprimer un album",
                    "7- Jouer une piste",
                    "8- Quitter"
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
                        discotheque.Morceau track = new discotheque.Morceau();

                        System.out.print("Titre : ");
                        choixStr = saisirString();
                        track.titre = choixStr;
                        
                        System.out.print("Artiste : ");
                        choixStr = saisirString();
                        track.artiste = choixStr;

                        System.out.print("Album : ");
                        choixStr = saisirString();
                        track.album = choixStr;

                        System.out.print("Fichier : ");
                        choixStr = saisirString();
                        track.file = choixStr;

                        manager.ajout(track);

                        break;

                    case 2:
                        tracks = manager.recupTitres();
                        displayTitles(tracks);

                        break;

                    case 3:
                        System.out.print("Titre : ");
                        choixStr = saisirString();
                        tracks = manager.rechercheParTitre(choixStr);
                        displayTitles(tracks);
                        
                        break;

                    case 4:
                        System.out.print("Artiste : ");
                        choixStr = saisirString();
                        tracks = manager.rechercheParArtiste(choixStr);
                        displayTitles(tracks);

                        break;

                    case 5:
                        System.out.print("Titre : ");
                        choixStr = saisirString();
                        
                        System.out.print("Artiste : ");
                        choixStr2 = saisirString();
                        
                        retour = manager.suppressionTitre(choixStr, choixStr2);

                        if (retour == true)
                        {
                            System.out.println("\nTitre supprimé\n");
                        }
                        else
                        {
                            System.out.println("\nAucun titre correspondant trouvé\n");
                        }

                        break;

                    case 6:
                        System.out.print("Artiste : ");
                        choixStr = saisirString();
                        
                        System.out.print("Album : ");
                        choixStr2 = saisirString();
                        
                        retour = manager.suppressionAlbum(choixStr, choixStr2);

                        if (retour == true)
                        {
                            System.out.println("\nAlbum supprimé\n");
                        }
                        else
                        {
                            System.out.println("\nAucun album correspondant trouvé\n");
                        }

                        break;

                    case 7:
                        System.out.print("Titre : ");
                        choixStr = saisirString();
                        manager.jouerTitres(choixStr);

                        Runtime.getRuntime().exec("vlc http://192.168.1.15:10000/stream");
                        break;

                    case 8:
                        return;
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
                    throw new Exception("Valeur incorrecte");
                }
            }
            catch (Exception ex)
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
            catch (Exception ex)
            {
                System.out.println("\nInput invalide : " + ex.getMessage() + "\n");
            }

            valid = true;
        }

        return choix;
    }

    public static void displayTitle(discotheque.Morceau track)
    {
        System.out.println("*** " + track.titre);
        System.out.println("* Artiste : " + track.artiste);
        System.out.println("* Album : " + track.album);
        System.out.println("***");
        System.out.println();
    }

    public static void displayTitles(discotheque.Morceau [] tracks)
    {
        for (discotheque.Morceau track : tracks)
        {
            displayTitle(track);
        }
    }
}