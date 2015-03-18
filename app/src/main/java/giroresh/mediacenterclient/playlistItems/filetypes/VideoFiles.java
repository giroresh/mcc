package giroresh.mediacenterclient.playlistItems.filetypes;

/**
 * Created by giro on 2014.12.15..
 * class that represents VideoFiles
 */
public class VideoFiles extends PlaylistItems {
    private int id;
    private int type = 300;
    private String label;

    @Override
    public int getID() {
        return id;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public void setID(int ID) {
        this.id = ID;
    }

    @Override
    public void setLabel(String Label) {
        this.label = Label;
    }

    @Override
    public void setType(int type) {
        this.type = type;
    }
}
