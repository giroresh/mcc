package giroresh.mediacenterclient.playlistItems.filetypes;

/** if we do not get a response from the server this class is used to get the error message
 * only getMsg and setMsg are used, the rest SHOULD NEVER be USED!!!
 * Created by giro on 2015.04.09..
 */
public class MCCNullHandler extends PlaylistItems {
    private String msg;

    public MCCNullHandler() {

    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getNextID() {
        return 0;
    }

    @Override
    public int getPrevID() {
        return 0;
    }

    @Override
    public void setID(int ID) {

    }

    @Override
    public void setLabel(String Label) {

    }

    @Override
    public void setType(int type) {

    }

    @Override
    public void setNextID(int nextID) {

    }

    @Override
    public void setPrevID(int prevID) {

    }
}
