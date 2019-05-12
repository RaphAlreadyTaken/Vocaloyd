module discotheque
{
    //Piste de musique
    struct Morceau
    {
        string titre;
        string artiste;
        string album;
        string genre;
        string piste;
        string image;
        string fichier;
    };

    //Playlist (tableau de pistes)
    sequence<Morceau> Morceaux;

    //Entrée (clé, valeur) de pseudo-map
    struct Entry
    {
        string key;
        string value;
    };

    //Map (tableau d'entrées)
    sequence<Entry> Map;

    //Gestion de pistes de musique
    interface trackManagement
    {
        void ajouterTitre(Morceau song);
        Morceaux recupererTitres();
        Morceaux rechercher(string info);
        Morceaux rechercherParTitre(string title);
        Morceaux rechercherParArtiste(string artist);
        Morceaux rechercherParAlbum(string album);
        Morceaux rechercherParGenre(string genre);
        bool supprimerTitre(string title, string artist);
        bool supprimerAlbum(string artist, string album);
        bool supprimerArtiste(string artist);
        string jouerMorceaux(Morceaux morceaux, int port);
        void playPause(int port);
        void nextTrack(int port);
        void previousTrack(int port);
        Map getInfos(int port);
    };

    //Gestion de clients
    interface clientManagement
    {
        int subscribe();
        void unsubscribe(int port);
    };
};