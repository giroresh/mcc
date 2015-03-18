package giroresh.mediacenterclient.playlistItems.filetypes;

/**
 * Created by giro on 2014.12.15..
 * class that represents AudioFiles
 */
public class AudioFiles extends PlaylistItems {
    private int id;
    private String label = "";
    private int type = 100;
    private int nextID;
    private int prevID;

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

    public int getNextID() {
        return nextID;
    }

    public int getPrevID() {
        return prevID;
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

    @Override
    public void setNextID(int nextID) {
        this.nextID = nextID;
    }

    @Override
    public void setPrevID(int prevID) {
        this.prevID = prevID;
    }

}
