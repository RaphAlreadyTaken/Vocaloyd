//
// Copyright (c) ZeroC, Inc. All rights reserved.
//
//
// Ice version 3.7.2
//
// <auto-generated>
//
// Generated from file `Discotheque.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>
//

package discotheque;

public class Morceau implements java.lang.Cloneable,
                                java.io.Serializable
{
    public String artiste;

    public String album;

    public String titre;

    public String file;

    public Morceau()
    {
        this.artiste = "";
        this.album = "";
        this.titre = "";
        this.file = "";
    }

    public Morceau(String artiste, String album, String titre, String file)
    {
        this.artiste = artiste;
        this.album = album;
        this.titre = titre;
        this.file = file;
    }

    public boolean equals(java.lang.Object rhs)
    {
        if(this == rhs)
        {
            return true;
        }
        Morceau r = null;
        if(rhs instanceof Morceau)
        {
            r = (Morceau)rhs;
        }

        if(r != null)
        {
            if(this.artiste != r.artiste)
            {
                if(this.artiste == null || r.artiste == null || !this.artiste.equals(r.artiste))
                {
                    return false;
                }
            }
            if(this.album != r.album)
            {
                if(this.album == null || r.album == null || !this.album.equals(r.album))
                {
                    return false;
                }
            }
            if(this.titre != r.titre)
            {
                if(this.titre == null || r.titre == null || !this.titre.equals(r.titre))
                {
                    return false;
                }
            }
            if(this.file != r.file)
            {
                if(this.file == null || r.file == null || !this.file.equals(r.file))
                {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    public int hashCode()
    {
        int h_ = 5381;
        h_ = com.zeroc.IceInternal.HashUtil.hashAdd(h_, "::discotheque::Morceau");
        h_ = com.zeroc.IceInternal.HashUtil.hashAdd(h_, artiste);
        h_ = com.zeroc.IceInternal.HashUtil.hashAdd(h_, album);
        h_ = com.zeroc.IceInternal.HashUtil.hashAdd(h_, titre);
        h_ = com.zeroc.IceInternal.HashUtil.hashAdd(h_, file);
        return h_;
    }

    public Morceau clone()
    {
        Morceau c = null;
        try
        {
            c = (Morceau)super.clone();
        }
        catch(CloneNotSupportedException ex)
        {
            assert false; // impossible
        }
        return c;
    }

    public void ice_writeMembers(com.zeroc.Ice.OutputStream ostr)
    {
        ostr.writeString(this.artiste);
        ostr.writeString(this.album);
        ostr.writeString(this.titre);
        ostr.writeString(this.file);
    }

    public void ice_readMembers(com.zeroc.Ice.InputStream istr)
    {
        this.artiste = istr.readString();
        this.album = istr.readString();
        this.titre = istr.readString();
        this.file = istr.readString();
    }

    static public void ice_write(com.zeroc.Ice.OutputStream ostr, Morceau v)
    {
        if(v == null)
        {
            _nullMarshalValue.ice_writeMembers(ostr);
        }
        else
        {
            v.ice_writeMembers(ostr);
        }
    }

    static public Morceau ice_read(com.zeroc.Ice.InputStream istr)
    {
        Morceau v = new Morceau();
        v.ice_readMembers(istr);
        return v;
    }

    static public void ice_write(com.zeroc.Ice.OutputStream ostr, int tag, java.util.Optional<Morceau> v)
    {
        if(v != null && v.isPresent())
        {
            ice_write(ostr, tag, v.get());
        }
    }

    static public void ice_write(com.zeroc.Ice.OutputStream ostr, int tag, Morceau v)
    {
        if(ostr.writeOptional(tag, com.zeroc.Ice.OptionalFormat.FSize))
        {
            int pos = ostr.startSize();
            ice_write(ostr, v);
            ostr.endSize(pos);
        }
    }

    static public java.util.Optional<Morceau> ice_read(com.zeroc.Ice.InputStream istr, int tag)
    {
        if(istr.readOptional(tag, com.zeroc.Ice.OptionalFormat.FSize))
        {
            istr.skip(4);
            return java.util.Optional.of(Morceau.ice_read(istr));
        }
        else
        {
            return java.util.Optional.empty();
        }
    }

    private static final Morceau _nullMarshalValue = new Morceau();

    /** @hidden */
    public static final long serialVersionUID = -8195435759872893120L;
}
