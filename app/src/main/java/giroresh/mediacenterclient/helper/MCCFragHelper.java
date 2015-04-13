package giroresh.mediacenterclient.helper;

import android.content.Context;
import android.content.res.Resources;
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

/**
 * Does the jobs which are equal to all Fragments
 * For now:
 *      1. getting the tag info string
 *      2. updating the adapter of the list
 *      3. checking for the empty file type
 * Created by giro on 2015.03.22..
 */
public class MCCFragHelper {
    private Context context;
    List<String> listItems = new ArrayList<>();
    private ParseXML xml;

    public MCCFragHelper(Context context) {
        this.context = context;
        try {
            this.xml = new ParseXML();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns a String with all available tag information in the right language
     * @param resources android resource
     * @param tagInfo String array which is read from the tag class
     * @return all available tag info in the right language
     */
    public static String getMultiLangString(Resources resources, String[] tagInfo) {
        String tagInfoMultiLang = resources.getString(R.string.tagNoInfo);
        for (String aTagInfo : tagInfo) {
            if (aTagInfo.startsWith("title")) {
                tagInfoMultiLang = resources.getString(R.string.tagTitle) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            } else if (aTagInfo.startsWith("album")) {
                tagInfoMultiLang += resources.getString(R.string.tagAlbum) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            } else if (aTagInfo.startsWith("artist")) {
                tagInfoMultiLang += resources.getString(R.string.tagArtist) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            } else if (aTagInfo.startsWith("genre")) {
                tagInfoMultiLang += resources.getString(R.string.tagGenre) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            } else if (aTagInfo.startsWith("track")) {
                tagInfoMultiLang += resources.getString(R.string.tagTrack) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            } else if (aTagInfo.startsWith("year")) {
                tagInfoMultiLang += resources.getString(R.string.tagYear) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            } else if (aTagInfo.startsWith("length")) {
                tagInfoMultiLang += resources.getString(R.string.tagLength) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            } else if (aTagInfo.startsWith("bitrate")) {
                tagInfoMultiLang += resources.getString(R.string.tagBitrate) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            } else if (aTagInfo.startsWith("sample")) {
                tagInfoMultiLang += resources.getString(R.string.tagSample) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            } else if (aTagInfo.startsWith("channels")) {
                tagInfoMultiLang += resources.getString(R.string.tagChannels) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            } else if (aTagInfo.startsWith("comment")) {
                tagInfoMultiLang += resources.getString(R.string.tagComment) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
            }
        }
        return tagInfoMultiLang.substring(0, tagInfoMultiLang.lastIndexOf("\n"));
    }

    /**
     * Updates the adapter of the list
     * @param adapter adapter of the original list
     * @param length length we want to display
     * @param offset offset from which we start to display
     * @param type file type to display
     * @param serverIP server IP
     * @param portNr server port nr
     * @return updated adapter
     */
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

    /**
     * checking if an empty file type is present if so true
     * necessary for the case that we lose connection to the server
     * @param playlistItemsFromXML list of all the playlist items
     * @return true if MCCNullHandler is present otherwise false
     */
    public boolean getMCCNullHandlerContained(List<PlaylistItems> playlistItemsFromXML) {
        for (Object obj : playlistItemsFromXML) {
            if (obj instanceof MCCNullHandler) {
                return true;
            }
        }
        return false;
    }
}
