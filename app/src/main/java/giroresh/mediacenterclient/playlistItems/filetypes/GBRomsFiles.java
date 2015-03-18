package giroresh.mediacenterclient.playlistItems.filetypes;

/**
 * Created by giro on 2015.01.29..
 * * GB Rom Files handling class
 */
public class GBRomsFiles extends RomFiles {
    private int id;
    private int type = 201;
    private String label;
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

    @Override
    public int getNextID() {
        return nextID;
    }

    @Override
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
