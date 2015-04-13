package giroresh.mediacenterclient.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import giroresh.mediacenterclient.ControlPlayback;
import giroresh.mediacenterclient.MCCException.NoTagsException;
import giroresh.mediacenterclient.ParseXML;
import giroresh.mediacenterclient.R;
import giroresh.mediacenterclient.SocketAsyncTask.SocketAsyncTask;
import giroresh.mediacenterclient.helper.MCCArrayAdapter;
import giroresh.mediacenterclient.helper.MCCFragHelper;
import giroresh.mediacenterclient.helper.MCCToast;
import giroresh.mediacenterclient.playlistItems.filetypes.MCCNullHandler;
import giroresh.mediacenterclient.playlistItems.filetypes.PlaylistItems;
import giroresh.mediacenterclient.playlistItems.tags.VideoTags;

/**
 * * Based on the android example code
 * The fragment that handles video files
 */
public class VideoPageFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String ARG_PAGE = "VIDEO_PAGE";

    private int mPage;
    private MCCArrayAdapter adapter;
    private List<String> listItems;
    private ListView lv;
    private String serverIP;
    private int portNr;
    private int playID;
    private String selectedID;
    private TextView infoTV;
    private final int type = 300;
    private int length = 50;
    private int offset = 0;
    private Button lengthButton;
    private Button lengthMinusButton;
    private Button offsetButton;
    private Button offsetMinusButton;
    private View view;
    private TextView lengthTV;
    private TextView offsetTV;
    private int maxOffset;
    private ParseXML xml;
    private String titleToPlay;
    private int maxLength;

    public static VideoPageFragment newInstance(int page) {
            Bundle args = new Bundle();
            args.putInt(ARG_PAGE, page);
            VideoPageFragment fragment = new VideoPageFragment();
            fragment.setArguments(args);
            return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.videofragmentview, container, false);
        lv = (ListView) view.findViewById(R.id.videolist);

        lengthButton = (Button) view.findViewById(R.id.lengthButton);
        lengthButton.setOnClickListener(this);
        lengthMinusButton = (Button) view.findViewById(R.id.lengthButtonMinus);
        lengthMinusButton.setOnClickListener(this);

        offsetButton = (Button) view.findViewById(R.id.offsetButton);
        offsetButton.setOnClickListener(this);
        offsetMinusButton = (Button) view.findViewById(R.id.offsetMinusButton);
        offsetMinusButton.setOnClickListener(this);

        lengthTV = (TextView) view.findViewById(R.id.lengthTV2);
        lengthTV.setTypeface(null, Typeface.BOLD_ITALIC);
        lengthTV.setTextColor(getResources().getColor(R.color.lengthOffestTV2));
        lengthTV.setTextSize(TypedValue.COMPLEX_UNIT_PT, 12);
        lengthTV.setText(" " + length + " ");

        offsetTV = (TextView) view.findViewById(R.id.offsetTV2);
        offsetTV.setTypeface(null, Typeface.BOLD_ITALIC);
        offsetTV.setTextColor(getResources().getColor(R.color.lengthOffestTV2));
        offsetTV.setTextSize(TypedValue.COMPLEX_UNIT_PT, 12);
        offsetTV.setText(" " + offset + " ");

        Intent intent = getActivity().getIntent();
        serverIP = intent.getStringExtra("IP");
        portNr = intent.getIntExtra("port", 0);

        listItems = new ArrayList<>();

        try {
            List<PlaylistItems> playlistItemsFromXML;
            xml = new ParseXML();
            if ( (playlistItemsFromXML = xml.getPlaylistItems(new SocketAsyncTask().execute(serverIP, portNr, "LIST " + type + " " + offset + " " + length))) == null ) {
            }
            if (playlistItemsFromXML != null) {
                if (!playlistItemsFromXML.isEmpty()) {
                    if (playlistItemsFromXML.size() == 1) {
                        if (playlistItemsFromXML.get(0).getClass().getName().contains("MCCNullHandler")) {
                            MCCNullHandler mccNullHandler = (MCCNullHandler) playlistItemsFromXML.get(0);
                            if (mccNullHandler.getMsg().contains("CONNREFUSED")) {
                                listItems.add(getResources().getString(R.string.getPlaylistItemCONNREFUSED));
                            } else {
                                listItems.add(mccNullHandler.getMsg());
                            }
                        }
                    } else {
                        for (int i = 0; i < playlistItemsFromXML.size(); i++) {
                            listItems.add(playlistItemsFromXML.get(i).getID() + " | " + playlistItemsFromXML.get(i).getLabel());
                        }
                    }
                    adapter = new MCCArrayAdapter(this.getActivity(), R.layout.playlistitem, playlistItemsFromXML);
                    lv.setAdapter(adapter);
                    lv.setOnItemClickListener(this);
                    registerForContextMenu(lv);
                    maxOffset = adapter.getCount()-1;
                    length = adapter.getCount();
                    maxLength = adapter.getCount();
                    lengthTV.setText(" " + maxLength + " ");
                }
            }
        } catch (XmlPullParserException e) {
            MCCToast.makeText(getActivity(), getResources().getString(R.string.xmlError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
        } catch (IOException e) {
            MCCToast.makeText(getActivity(), getResources().getString(R.string.ioError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
        } catch (ExecutionException e) {
            MCCToast.makeText(getActivity(), getResources().getString(R.string.exeError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
        } catch (InterruptedException e) {
            MCCToast.makeText(getActivity(), getResources().getString(R.string.interruptError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
        }
        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
       super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.playlistcontextmenu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (getUserVisibleHint()) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

            selectedID = ((TextView) info.targetView.findViewById(R.id.playlistItemTV)).getText().toString().substring(0, 8);

            View view = getView();
            if (view == null) {
                return false;
            } else {
                infoTV = (TextView) view.findViewById(R.id.infoTV);
                switch (item.getItemId()) {
                    case R.id.info:
                        try {
                            Object selectedFile = xml.getTagInfo(new SocketAsyncTask().execute(serverIP, portNr, "INFO " + selectedID));
                            String classTypeOfTags;
                            if (selectedFile == null) {
                                infoTV.setText(R.string.lostConnection);
                                return false;
                            } else {
                                classTypeOfTags = selectedFile.getClass().getName();
                                if (classTypeOfTags.contains("VideoTags")) {
                                    VideoTags vt = (VideoTags) selectedFile;
                                    infoTV.setText(MCCFragHelper.getMultiLangString(getResources(), vt.getAllTagInfo().split("\n")));
                                    return true;
                                } else {
                                    infoTV.setText(R.string.videoOnly);
                                    return false;
                                }
                            }
                        } catch (XmlPullParserException e) {
                            MCCToast.makeText(getActivity(), getResources().getString(R.string.xmlError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                            return false;
                        } catch (IOException e) {
                            MCCToast.makeText(getActivity(), getResources().getString(R.string.ioError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                            return false;
                        } catch (ExecutionException e) {
                            MCCToast.makeText(getActivity(), getResources().getString(R.string.exeError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                            return false;
                        } catch (InterruptedException e) {
                            MCCToast.makeText(getActivity(), getResources().getString(R.string.interruptError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                            return false;
                        } catch (NoTagsException e) {
                            titleToPlay = ((TextView) info.targetView.findViewById(R.id.playlistItemTV)).getText().toString();
                            titleToPlay = titleToPlay.substring(titleToPlay.indexOf("|") + 1);
                            infoTV.setText(getResources().getString(R.string.noTagInfo, Integer.parseInt(selectedID), titleToPlay));
                            return true;
                        }
                    default:
                        return false;
                }
            }
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /** Should be changed to something more sophisticated!!!!
         * ATM playID is fetched from list element
         */
        String titleToPlay = ((TextView) view).getText().toString();
        if (titleToPlay == null || titleToPlay.length() == 0) {
            MCCToast.makeText(getActivity(), getResources().getString(R.string.missingPlayID), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
        } else {
            playID = Integer.parseInt(titleToPlay.substring(0, 8));

            Boolean playReturnCode;
            try {
                playReturnCode = xml.getStatus(new SocketAsyncTask().execute(serverIP, portNr, "PLAY " + playID));
                if (playReturnCode) {
                    if (xml.getStatus(new SocketAsyncTask().execute(serverIP, portNr, "STOP"))) {
                        playReturnCode = xml.getStatus(new SocketAsyncTask().execute(serverIP, portNr, "PLAY " + playID));
                        if (playReturnCode) {
                            ParseXML xml = new ParseXML();
                            int prevID = xml.getPrevID(new SocketAsyncTask().execute(serverIP, portNr, "LIST " + type + " " + offset + " " + length), playID);
                            int nextID = xml.getNextID(new SocketAsyncTask().execute(serverIP, portNr, "LIST " + type + " " + offset + " " + length), playID);
                            titleToPlay = xml.getTitleToPlay(new SocketAsyncTask().execute(serverIP, portNr, "LIST " + type + " " + offset + " " + length), playID);
                            Intent intentPlayback = new Intent(getActivity(), ControlPlayback.class);
                            intentPlayback.putExtra("IP", serverIP);
                            intentPlayback.putExtra("port", portNr);
                            intentPlayback.putExtra("playID", playID);
                            intentPlayback.putExtra("prevID", prevID);
                            intentPlayback.putExtra("nextID", nextID);
                            intentPlayback.putExtra("titleToPlay", titleToPlay);
                            startActivityForResult(intentPlayback, 2);
                        } else {
                            MCCToast.makeText(getActivity(), getResources().getString(R.string.playUnsuccessful), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                        }
                    }
                }
            } catch (ExecutionException e) {
                MCCToast.makeText(getActivity(), getResources().getString(R.string.exeError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
            } catch (InterruptedException e) {
                MCCToast.makeText(getActivity(), getResources().getString(R.string.interruptError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
            } catch (XmlPullParserException e) {
                MCCToast.makeText(getActivity(), getResources().getString(R.string.xmlError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
            } catch (IOException e) {
                MCCToast.makeText(getActivity(), getResources().getString(R.string.ioError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
            }
        }
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lengthButton:
                if (length < maxLength) {
                    length++;
                    lengthTV.setText(" " + length + " ");
                    adapter.clear();
                    adapter = new MCCFragHelper(getActivity()).doListChange(adapter, length, offset, type, serverIP, portNr);
                }
                break;
            case R.id.lengthButtonMinus:
                if (length > 1) {
                    length--;
                    lengthTV.setText(" " + length + " ");
                    adapter.clear();
                    adapter = new MCCFragHelper(getActivity()).doListChange(adapter, length, offset, type, serverIP, portNr);
                }
                break;
            case R.id.offsetButton:
                if (offset < maxOffset) {
                    offset++;
                    offsetTV.setText(" " + offset + " ");
                    adapter.clear();
                    adapter = new MCCFragHelper(getActivity()).doListChange(adapter, length, offset, type, serverIP, portNr);
                }
                break;
            case R.id.offsetMinusButton:
                if (offset > 0) {
                    offset--;
                    offsetTV.setText(" " + offset + " ");
                    adapter.clear();
                    adapter = new MCCFragHelper(getActivity()).doListChange(adapter, length, offset, type, serverIP, portNr);
                }
                break;
        }
    }
}