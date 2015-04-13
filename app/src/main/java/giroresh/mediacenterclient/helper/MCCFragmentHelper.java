package giroresh.mediacenterclient.helper;

import android.content.Context;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import giroresh.mediacenterclient.ParseXML;
import giroresh.mediacenterclient.R;
import giroresh.mediacenterclient.SocketAsyncTask.SocketAsyncTask;
import giroresh.mediacenterclient.playlistItems.filetypes.MCCNullHandler;
import giroresh.mediacenterclient.playlistItems.filetypes.PlaylistItems;

/** unites all the common methods for all the fragments
 * Created by giro on 2015.04.11..
 */
public class MCCFragmentHelper {

    private Context context;
    List<String> listItems = new ArrayList<>();
    private ParseXML xml;

    public MCCFragmentHelper(Context contxt) {
        this.context = contxt;
        try {
            this.xml = new ParseXML();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    public MCCArrayAdapter doListChange(MCCArrayAdapter adapter, int length, int offset, int type, String serverIP, int portNr) {
        List<PlaylistItems> playlistItemsFromXML;

        try {
            if ( (playlistItemsFromXML = xml.getPlaylistItems(new SocketAsyncTask().execute(serverIP, portNr, "LIST " + type + " " + offset + " " + length))) == null ) {
                playlistItemsFromXML = new ArrayList<>();
                playlistItemsFromXML.add(new MCCNullHandler(context.getResources().getString(R.string.getPlaylistItemCONNREFUSED)));
            } else {
                if (!playlistItemsFromXML.isEmpty()) {
                    if (getMCCNullHandlerContained(playlistItemsFromXML)) {
                        playlistItemsFromXML.add(new MCCNullHandler(context.getResources().getString(R.string.getPlaylistItemCONNREFUSED)));
                    } else {
                        adapter.addAll(playlistItemsFromXML);
                    }
                }
            }
        } catch (XmlPullParserException e) {
            MCCToast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.xmlError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
        } catch (IOException e) {
            MCCToast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.ioError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
        } catch (ExecutionException e) {
            MCCToast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.exeError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
        } catch (InterruptedException e) {
            MCCToast.makeText(context.getApplicationContext(), context.getResources().getString(R.string.interruptError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
        }
        return adapter;
    }

    public boolean getMCCNullHandlerContained(List<PlaylistItems> playlistItemsFromXML) {
        for (Object obj : playlistItemsFromXML) {
            if (obj instanceof MCCNullHandler) {
                return true;
            }
        }
        return false;
    }
}
