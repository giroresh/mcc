package giroresh.mediacenterclient.playlistItems.tags;

/**
 * Created by giro on 2015.01.18..
 * class that represents VideoTags
 */
public class VideoTags extends Tags {
    private String title;
    private String artist;
    private String album;
    private String year;
    private String track;
    private String genre;
    private String bitrate;
    private String samplerate;
    private String channels;
    private String length;
    private String comment;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getArtist() {
        return artist;
    }

    @Override
    public String getAlbum() {
        return album;
    }

    @Override
    public String getYear() {
        return year;
    }

    @Override
    public String getTrack() {
        return track;
    }

    @Override
    public String getGenre() {
        return genre;
    }

    @Override
    public String getBitrate() {
        return bitrate;
    }

    @Override
    public String getSamplerate() {
        return samplerate;
    }

    @Override
    public String getChannels() {
        return channels;
    }

    @Override
    public String getLength() {
        return length;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public void setAlbum(String album) {
        this.album = album;
    }

    @Override
    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public void setTrack(String track) {
        this.track = track;
    }

    @Override
    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public void setBitrate(String bitrate) {
        this.bitrate = bitrate;
    }

    @Override
    public void setSamplerate(String samplerate) {
        this.samplerate = samplerate;
    }

    @Override
    public void setChannels(String channels) {
        this.channels = channels;
    }

    @Override
    public void setLength(String length) {
        this.length = length;
    }

    @Override
    public void setComment(String s) {
        this.comment = s;
    }

    public String getAllTagInfos() {
        String allInfos = "";

        if (getTitle() != null) {
            allInfos += "title: \t" + getTitle() + "\n";
        }
        if (getAlbum() != null) {
            allInfos += "album: \t" + getAlbum() + "\n";
        }
        if (getTrack() != null) {
            allInfos += "track: \t" + getTrack() + "\n";
        }
        if (getArtist() != null) {
            allInfos += "artist: \t" + getArtist() + "\n";
        }
        if (getGenre() != null) {
            allInfos += "genre: \t" + getGenre() + "\n";
        }
        if (getYear() != null) {
            allInfos += "Year: \t" + getYear() + "\n";
        }
        if (getComment() != null) {
            allInfos += "Comment: \t" + getComment() + "\n";
        }

        return allInfos;
    }
}
