package v.blade.library;

import com.squareup.picasso.RequestCreator;

import java.util.ArrayList;
import java.util.List;

import v.blade.sources.Source;
import v.blade.sources.SourceInformation;

public class Song extends LibraryObject
{
    List<SourceInformation> sources;
    Artist[] artists;
    Album album;
    int track_number;

    protected Song(String name, Album album, Artist[] artists, int track_number)
    {
        this.name = name;
        this.artists = artists;
        this.album = album;
        this.track_number = track_number;
        this.sources = new ArrayList<>();
    }

    protected void addSource(Source source, Object id)
    {
        if(source == null || id == null) return;

        //check if song contains same source
        for(SourceInformation si : sources) if(si.source == source) return;

        sources.add(new SourceInformation(source, id));
    }

    public Artist[] getArtists()
    {
        return artists;
    }

    public String getArtistsString()
    {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < artists.length; i++)
        {
            sb = sb.append(artists[i].name);
            if(i != artists.length - 1) sb = sb.append(", ");
        }
        return sb.toString();
    }

    public SourceInformation getBestSource()
    {
        if(sources.size() == 0) return null;

        //TODO : Sort the list by ids at construction, and return source.get(0) maybe
        SourceInformation best = sources.get(0);
        int min = sources.get(0).source.getIndex();
        for(int i = 1; i < sources.size(); i++)
        {
            if(sources.get(i).source.getIndex() < min)
            {
                best = sources.get(i);
                min = best.source.getIndex();
            }
        }
        return best;
    }

    public int getTrackNumber()
    {
        return track_number;
    }

    public Album getAlbum()
    {
        return album;
    }

    public List<SourceInformation> getSources()
    {
        return sources;
    }

    @Override
    public RequestCreator getImageRequest()
    {
        return album.imageRequest;
    }

    public RequestCreator getBigImageRequest()
    {
        return album.imageBig;
    }
}
