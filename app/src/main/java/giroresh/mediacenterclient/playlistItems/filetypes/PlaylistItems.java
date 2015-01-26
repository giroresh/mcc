package giroresh.mediacenterclient.playlistItems.filetypes;

/**
 * Created by giro on 2014.12.15..
 * This abstract class is the root for all items that might get displayed in the playlist
 */
public abstract class PlaylistItems {
    abstract public int getID();
    abstract public String getLabel();
    abstract public int getType();

    abstract public void setID(int ID);
    abstract public void setLabel(String Label);
    abstract public void setType(int type);
}
