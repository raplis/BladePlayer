package v.blade.library;

import com.squareup.picasso.Picasso;

import java.util.List;

import v.blade.sources.SourceInformation;

public class Playlist extends LibraryObject
{
    final List<Song> songs;
    private SourceInformation sourceInformation;
    private final String playlistSubtitle;

    public Playlist(String name, List<Song> songList, String image, String subtitle, SourceInformation sourceInformation)
    {
        this.name = name;
        this.imageStr = image;
        this.imageRequest = (image == null || image.equals("")) ? null : Picasso.get().load(image);
        if(songList == null) {
            this.songs = new java.util.ArrayList<>();
        }
        else {
            this.songs = songList;
        }
        this.sourceInformation = sourceInformation;
        this.playlistSubtitle = subtitle;
    }

    public SourceInformation getSource()
    {
        return sourceInformation;
    }
    public void setSource(SourceInformation sourceInformation)
    {
        this.sourceInformation = sourceInformation;
    }
    public List<Song> getSongs()
    {
        return songs;
    }

    public String getSubtitle()
    {
        return playlistSubtitle;
    }
}
