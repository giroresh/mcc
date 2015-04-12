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
                listItems.add(context.getResources().getString(R.string.getPlaylistItemCONNREFUSED));
            }
            if (playlistItemsFromXML != null) {
                if (!playlistItemsFromXML.isEmpty()) {
                    if (getMCCNullHandlerContained(playlistItemsFromXML)) {
                        listItems.add(context.getResources().getString(R.string.getPlaylistItemCONNREFUSED));
                    } else {
                        for (int i = 0; i < playlistItemsFromXML.size(); i++) {
                            listItems.add(playlistItemsFromXML.get(i).getID() + " | " + playlistItemsFromXML.get(i).getLabel());
                        }
                    }
                }
            }
            adapter.addAll(listItems);
        } catch (XmlPullParserException e) {
            Toast.makeText(context.getApplicationContext(), "ERROR XML Error", Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(context.getApplicationContext(), "ERROR Exe Error", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            Toast.makeText(context.getApplicationContext(), "ERROR Interrupt Error", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context.getApplicationContext(), "ERROR IO Error", Toast.LENGTH_SHORT).show();
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
