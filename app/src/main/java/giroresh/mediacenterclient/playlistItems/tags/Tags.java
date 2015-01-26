package giroresh.mediacenterclient.playlistItems.tags;

/**
 * Created by giro on 2014.12.16..
 * Abstract class for all Tags
 */
public abstract class Tags {
    abstract public String getTitle();
    abstract public String getArtist();
    abstract public String getAlbum();
    abstract public String getYear();
    abstract public String getTrack();
    abstract public String getGenre();
    abstract public String getBitrate();
    abstract public String getSamplerate();
    abstract public String getChannels();
    abstract public String getLength();
    abstract public String getComment();

    abstract public void setTitle(String title);
    abstract public void setArtist(String artist);
    abstract public void setAlbum(String album);
    abstract public void setYear(String year);
    abstract public void setTrack(String track);
    abstract public void setGenre(String genre);
    abstract public void setBitrate(String bitrate);
    abstract public void setSamplerate(String samplerate);
    abstract public void setChannels(String channels);
    abstract public void setLength(String length);
    abstract public void setComment(String s);

    abstract public String getAllTagInfos();
}
