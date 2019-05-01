module discotheque
{
    sequence<byte> data;

    struct Morceau
    {
        string artiste;
        string album;
        string titre;
        string genre;
        string duree;
        string file;
    };

    sequence<Morceau> Morceaux;

    interface trackManagement
    {
        void ajouterTitre(Morceau song);
        Morceaux recupererTitres();
        Morceaux rechercherParTitre(string title);
        Morceaux rechercherParArtiste(string artist);
        Morceaux rechercherParAlbum(string album);
        Morceaux rechercherParGenre(string genre);
        Morceaux rechercherParDuree(string duration);
        bool supprimerTitre(string title, string artist);
        bool supprimerAlbum(string artist, string album);
        bool supprimerArtiste(string artist);
        string jouerMorceaux(Morceaux morceaux);
    };
};