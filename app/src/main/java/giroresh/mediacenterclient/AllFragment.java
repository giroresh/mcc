package giroresh.mediacenterclient;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import giroresh.mediacenterclient.helper.MCCFragHelper;
import giroresh.mediacenterclient.playlistItems.MCCException.NoTagsException;
import giroresh.mediacenterclient.playlistItems.filetypes.PlaylistItems;
import giroresh.mediacenterclient.playlistItems.tags.AudioTags;
import giroresh.mediacenterclient.playlistItems.tags.VideoTags;

/**
 * Based on the android example code
 * Fragment show when no filetype filtering is applied
 */
public class AllFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String ARG_PAGE = "ALL_PAGE";
    private int mPage;
    private ArrayAdapter<String> adapter;
    private List<String> listItems;
    private ListView lv;
    private String serverIP;
    private int portNr;
    private int playID;
    private String selectedID;
    private TextView infoTV;
    private final int type = 0;
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

    public static AllFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        AllFragment fragment = new AllFragment();
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
        view = inflater.inflate(R.layout.allfragmentview, container, false);
        lv = (ListView) view.findViewById(R.id.list);

        lengthButton = (Button) view.findViewById(R.id.lengthButton);
        lengthButton.setOnClickListener(this);
        lengthMinusButton = (Button) view.findViewById(R.id.lengthButtonMinus);
        lengthMinusButton.setOnClickListener(this);

        offsetButton = (Button) view.findViewById(R.id.offsetButton);
        offsetButton.setOnClickListener(this);
        offsetMinusButton = (Button) view.findViewById(R.id.offsetMinusButton);
        offsetMinusButton.setOnClickListener(this);

        offsetTV = (TextView) view.findViewById(R.id.offsetTV2);
        lengthTV = (TextView) view.findViewById(R.id.lengthTV2);
        offsetTV.setTypeface(null, Typeface.BOLD_ITALIC);
        lengthTV.setTypeface(null, Typeface.BOLD_ITALIC);
        offsetTV.setTextColor(getResources().getColor(R.color.lengthOffestTV2));
        lengthTV.setTextColor(getResources().getColor(R.color.lengthOffestTV2));
        offsetTV.setTextSize(TypedValue.COMPLEX_UNIT_PT, 10);
        lengthTV.setTextSize(TypedValue.COMPLEX_UNIT_PT, 10);
        offsetTV.setText(" " + offset + " ");
        lengthTV.setText(" " + length + " ");

        Intent intent = getActivity().getIntent();
        serverIP = intent.getStringExtra("IP");
        portNr = intent.getIntExtra("port", 0);

        listItems = new ArrayList<>();

        try {
            List<PlaylistItems> playlistItemsFromXML = new ArrayList<>();

            xml = new ParseXML();
            playlistItemsFromXML.addAll(xml.getPlaylistItems(new SocketAsyncTask().execute(serverIP, portNr, "LIST " + type + " " + offset + " " + length)));

            if (!playlistItemsFromXML.isEmpty()) {
                for (int i = 0; i < playlistItemsFromXML.size(); i++) {
                    listItems.add(playlistItemsFromXML.get(i).getID() + " | " + playlistItemsFromXML.get(i).getLabel());
                }
            }

            adapter = new ArrayAdapter<>(getActivity(), R.layout.playlistitem, listItems);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(this);
            registerForContextMenu(lv);

            maxOffset = adapter.getCount()-2;

        } catch (XmlPullParserException e) {
            Toast.makeText(getActivity(), "XML Error", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getActivity(), "IO Error", Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(getActivity(), "Execution Error", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            Toast.makeText(getActivity(), "Interrupt Error", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e) {
            Toast.makeText(getActivity(), "NullPointer Error", Toast.LENGTH_SHORT).show();
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

            try {
                infoTV = (TextView) getView().findViewById(R.id.infoTV);
                switch (item.getItemId()) {
                    case R.id.info:
                        try {
                            Object selectedFile = xml.getTagInfo(new SocketAsyncTask().execute(serverIP, portNr, "INFO " + selectedID));
                            String classTypeOfTags = selectedFile.getClass().getName();

                            if (classTypeOfTags.contains("AudioTags")) {
                                AudioTags at = (AudioTags) selectedFile;
                                infoTV.setText(MCCFragHelper.getMultiLangString(getResources(), at.getAllTagInfo().split("\n")));
                                return true;
                            } else if (classTypeOfTags.contains("VideoTags")) {
                                VideoTags vt = (VideoTags) selectedFile;
                                infoTV.setText(MCCFragHelper.getMultiLangString(getResources(), vt.getAllTagInfo().split("\n")));
                                return true;
                            } else {
                                infoTV.setText(R.string.unsupportedFiletype);
                                return false;
                            }
                        } catch (XmlPullParserException e) {
                            Toast.makeText(getActivity(), "XML Error", Toast.LENGTH_SHORT).show();
                            return false;
                        } catch (IOException e) {
                            Toast.makeText(getActivity(), "IO Error", Toast.LENGTH_SHORT).show();
                            return false;
                        } catch (ExecutionException e) {
                            Toast.makeText(getActivity(), "Execution Error", Toast.LENGTH_SHORT).show();
                            return false;
                        } catch (InterruptedException e) {
                            Toast.makeText(getActivity(), "Interrupt Error", Toast.LENGTH_SHORT).show();
                            return false;
                        } catch (NoTagsException e) {
                            infoTV.setText(getResources().getText(R.string.noTagInfo).toString() +  playID);
                            return true;
                        }
                    default:
                        return false;
                }
            } catch (NullPointerException e) {
                Toast.makeText(getActivity(), "Something went really bad", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /** Should be changed to something more sophisticated!!!!
         * ATM playID is fetched from list element
         */
        playID = Integer.parseInt(((TextView)view).getText().toString().substring(0, 8));
        String titleToPlay = ((TextView) view).getText().toString();
        titleToPlay = titleToPlay.substring(titleToPlay.indexOf("|")+1);

        Boolean playReturnCode;
        try {
            playReturnCode = xml.getStatus(new SocketAsyncTask().execute(serverIP, portNr, "PLAY " + playID));
            if (playReturnCode) {
                if (xml.getStatus(new SocketAsyncTask().execute(serverIP, portNr, "STOP"))) {
                    playReturnCode = xml.getStatus(new SocketAsyncTask().execute(serverIP, portNr, "PLAY " + playID));
                    if (playReturnCode) {
                        ParseXML xml = new ParseXML();
                        int prevID = xml.getPrevID(new SocketAsyncTask().execute(serverIP, portNr, "LIST " + type + " " + offset + " " + length),playID);
                        int nextID = xml.getNextID(new SocketAsyncTask().execute(serverIP, portNr, "LIST " + type + " " + offset + " " + length), playID);
                        Intent intentPlayback = new Intent(getActivity(), ControlPlayback.class);
                        intentPlayback.putExtra("IP", serverIP);
                        intentPlayback.putExtra("port", portNr);
                        intentPlayback.putExtra("playID", playID);
                        intentPlayback.putExtra("prevID", prevID);
                        intentPlayback.putExtra("nextID", nextID);
                        intentPlayback.putExtra("titleToPlay", titleToPlay);
                        startActivityForResult(intentPlayback, 2);
                    } else {
                        Toast.makeText(getActivity(), R.string.playUnsuccessful, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), R.string.stopUnsuccessful, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getActivity(), R.string.playUnsuccessful, Toast.LENGTH_SHORT).show();
            }
        } catch (ExecutionException e) {
            Toast.makeText(getActivity(), "Execution Error", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException ie) {
            Toast.makeText(getActivity(), "Interrupt Error", Toast.LENGTH_SHORT).show();
        } catch (XmlPullParserException e) {
            Toast.makeText(getActivity(), "XML Error", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getActivity(), "IO Error", Toast.LENGTH_SHORT).show();
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
                if (length < 50) {
                    length++;
                    lengthTV.setText(" " + length + " ");
                    doListChange();
                }
                break;
            case R.id.lengthButtonMinus:
                if (length > 1) {
                    length--;
                    lengthTV.setText(" " + length + " ");
                    doListChange();
                }
                break;
            case R.id.offsetButton:
                if (offset < maxOffset) {
                    offset++;
                    offsetTV.setText(" " + offset + " ");
                    doListChange();
                }
                break;
            case R.id.offsetMinusButton:
                if (offset > 0) {
                    offset--;
                    offsetTV.setText(" " + offset + " ");
                    doListChange();
                }
                break;
        }
    }

    void doListChange() {
        List<PlaylistItems> playlistItemsFromXML = new ArrayList<>();
        try {
            playlistItemsFromXML.addAll(xml.getPlaylistItems(new SocketAsyncTask().execute(serverIP, portNr, "LIST " + type + " " + offset + " " + length)));
        } catch (XmlPullParserException e) {
            Toast.makeText(getActivity(), "ERROR XML Error", Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(getActivity(), "ERROR Exe Error", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            Toast.makeText(getActivity(), "ERROR Interrupt Error", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getActivity(), "ERROR IO Error", Toast.LENGTH_SHORT).show();
        }

        listItems.clear();

        for (int i = 0; i < playlistItemsFromXML.size(); i++) {
            listItems.add(playlistItemsFromXML.get(i).getID() + " | " + playlistItemsFromXML.get(i).getLabel());
        }
        adapter.notifyDataSetChanged();
    }
}
