module discotheque
{
    struct Morceau
    {
        string titre;
        string artiste;
        string album;
        string genre;
        string file;
        string duree;
    };

    sequence<Morceau> Morceaux;

    interface trackManagement
    {
        void ajouterTitre(Morceau song);
        Morceaux recupererTitres();
        Morceaux rechercher(string info);
        Morceaux rechercherParTitre(string title);
        Morceaux rechercherParArtiste(string artist);
        Morceaux rechercherParAlbum(string album);
        Morceaux rechercherParGenre(string genre);
        Morceaux rechercherParDuree(string duration);
        bool supprimerTitre(string title, string artist);
        bool supprimerAlbum(string artist, string album);
        bool supprimerArtiste(string artist);
        string jouerMorceaux(Morceaux morceaux, int port);
        void playPause(int port);
        void nextTrack(int port);
        void previousTrack(int port);
    };

    interface clientManagement
    {
        int subscribe();
        void unsubscribe(int port);
    };
};