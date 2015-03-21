package giroresh.mediacenterclient.playlistItems.filetypes;

/**
 * Created by giro on 2014.12.15..
 * This abstract class is the root for all items that might get displayed in the playlist
 */
public abstract class PlaylistItems {
    public abstract int getID();
    public abstract String getLabel();
    public abstract int getType();
    public abstract int getNextID();
    public abstract int getPrevID();

    public abstract void setID(int ID);
    public abstract void setLabel(String Label);
    public abstract void setType(int type);
    public abstract void setNextID(int nextID);
    public abstract void setPrevID(int prevID);
}
