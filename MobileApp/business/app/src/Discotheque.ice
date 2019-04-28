module discotheque
{
    sequence<byte> data;

    struct Morceau
    {
        string artiste;
        string album;
        string titre;
        string file;
    };

    sequence<Morceau> Morceaux;

    interface trackManagement
    {
        void ajout(Morceau song);
        Morceaux recupTitres();
        Morceaux rechercheParTitre(string title);
        Morceaux rechercheParArtiste(string artist);
        bool suppressionTitre(string title, string artist);
        bool suppressionAlbum(string artist, string album);
        void jouerTitres(string title);
    };
};