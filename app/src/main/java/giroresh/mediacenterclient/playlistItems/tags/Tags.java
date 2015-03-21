package giroresh.mediacenterclient.playlistItems.tags;

/**
 * Created by giro on 2014.12.16..
 * Abstract class for all Tags
 */
public abstract class Tags {
    public abstract String getTitle();
    public abstract String getArtist();
    public abstract String getAlbum();
    public abstract String getYear();
    public abstract String getTrack();
    public abstract String getGenre();
    public abstract String getBitrate();
    public abstract String getSample();
    public abstract String getChannels();
    public abstract String getLength();
    public abstract String getComment();

    public abstract void setTitle(String title);
    public abstract void setArtist(String artist);
    public abstract void setAlbum(String album);
    public abstract void setYear(String year);
    public abstract void setTrack(String track);
    public abstract void setGenre(String genre);
    public abstract void setBitrate(String bitrate);
    public abstract void setSample(String samplerate);
    public abstract void setChannels(String channels);
    public abstract void setLength(String length);
    public abstract void setComment(String s);

    public abstract String getAllTagInfo();
}
