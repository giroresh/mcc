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

import giroresh.mediacenterclient.playlistItems.MCCException.NoTagsException;
import giroresh.mediacenterclient.playlistItems.filetypes.PlaylistItems;
import giroresh.mediacenterclient.playlistItems.tags.VideoTags;

/**
 * * Based on the android example code
 * The fragment that handles video files
 */
public class VideoPageFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String ARG_PAGE = "VIDEO_PAGE";

    private int mPage;

    private ArrayAdapter<String> adapter;
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

            offsetTV = (TextView) view.findViewById(R.id.offsetTV2);
            lengthTV = (TextView) view.findViewById(R.id.lengthTV2);
            offsetTV.setTypeface(null, Typeface.BOLD_ITALIC);
            lengthTV.setTypeface(null, Typeface.BOLD_ITALIC);
            offsetTV.setTextColor(getResources().getColor(R.color.lengthOffestTV2));
            lengthTV.setTextColor(getResources().getColor(R.color.lengthOffestTV2));
            offsetTV.setTextSize(TypedValue.COMPLEX_UNIT_PT, 12);
            lengthTV.setTextSize(TypedValue.COMPLEX_UNIT_PT, 12);
            offsetTV.setText(" " + offset + " ");
            lengthTV.setText(" " + length + " ");

            Intent intent = getActivity().getIntent();
            serverIP = intent.getStringExtra("IP");
            portNr = intent.getIntExtra("port", 0);

            listItems = new ArrayList<>();

            try {
                ParseXML xmlItems = new ParseXML();
                List<PlaylistItems> playlistItemsFromXML = new ArrayList<>();

                playlistItemsFromXML.addAll(xmlItems.getPlaylistItems(new SocketAsyncTask().execute(serverIP, portNr, "LIST " + type + " " + offset + " " + length)));

                if (!playlistItemsFromXML.isEmpty()) {
                    for (int i = 0; i < playlistItemsFromXML.size(); i++) {
                        listItems.add(playlistItemsFromXML.get(i).getID() + " | " + playlistItemsFromXML.get(i).getLabel());
                    }
                }

                adapter = new ArrayAdapter<>(getActivity(), R.layout.playlistitem, listItems);
                lv.setAdapter(adapter);
                lv.setOnItemClickListener(this);
                registerForContextMenu(lv);

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
                                Object selectedFile = new ParseXML().getTagInfo(new SocketAsyncTask().execute(serverIP, portNr, "INFO " + selectedID));
                                String classTypeOfTags = selectedFile.getClass().getName();

                                if (classTypeOfTags.contains("VideoTags")) {
                                    VideoTags vt = (VideoTags) selectedFile;
                                    String[] tagInfo = vt.getAllTagInfo().split("\n");
                                    String tagInfoMultiLang = getResources().getString(R.string.tagNoInfo);
                                    for (String aTagInfo : tagInfo) {
                                        if (aTagInfo.startsWith("title")) {
                                            tagInfoMultiLang = getResources().getString(R.string.tagTitle) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
                                        } else if (aTagInfo.startsWith("album")) {
                                            tagInfoMultiLang += getResources().getString(R.string.tagAlbum) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
                                        } else if (aTagInfo.startsWith("artist")) {
                                            tagInfoMultiLang += getResources().getString(R.string.tagArtist) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
                                        } else if (aTagInfo.startsWith("genre")) {
                                            tagInfoMultiLang += getResources().getString(R.string.tagGenre) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
                                        } else if (aTagInfo.startsWith("track")) {
                                            tagInfoMultiLang += getResources().getString(R.string.tagTrack) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
                                        } else if (aTagInfo.startsWith("year")) {
                                            tagInfoMultiLang += getResources().getString(R.string.tagYear) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
                                        } else if (aTagInfo.startsWith("length")) {
                                            tagInfoMultiLang += getResources().getString(R.string.tagLength) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
                                        } else if (aTagInfo.startsWith("bitrate")) {
                                            tagInfoMultiLang += getResources().getString(R.string.tagBitrate) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
                                        } else if (aTagInfo.startsWith("sample")) {
                                            tagInfoMultiLang += getResources().getString(R.string.tagSample) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
                                        } else if (aTagInfo.startsWith("channels")) {
                                            tagInfoMultiLang += getResources().getString(R.string.tagChannels) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
                                        } else if (aTagInfo.startsWith("comment")) {
                                            tagInfoMultiLang += getResources().getString(R.string.tagComment) + aTagInfo.substring(aTagInfo.indexOf('\t'));
                                        }
                                    }
                                    infoTV.setText(tagInfoMultiLang);
                                } else {
                                    infoTV.setText(R.string.videoOnly);
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
                                Toast.makeText(getActivity(), R.string.videoShouldTags, Toast.LENGTH_SHORT).show();
                                return true;
                            }
                            break;
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
            playID = Integer.parseInt(((TextView) view).getText().toString().substring(0, 8));

            Boolean playReturnCode;
            try {
                playReturnCode = ParseXML.getPlayReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "PLAY " + playID));
                if (playReturnCode) {
                    if (ParseXML.getCTRLReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "STOP"))) {
                        playReturnCode = ParseXML.getPlayReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "PLAY " + playID));
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
                if (offset < lv.getAdapter().getCount()) {
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
            ParseXML xmlItems = new ParseXML();
            playlistItemsFromXML.addAll(xmlItems.getPlaylistItems(new SocketAsyncTask().execute(serverIP, portNr, "LIST " + type + " " + offset + " " + length)));
        } catch (XmlPullParserException e) {
            Toast.makeText(getActivity(), "ERROR XML Error", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getActivity(), "ERROR IO Error", Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(getActivity(), "ERROR Exe Error", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            Toast.makeText(getActivity(), "ERROR Interrupt Error", Toast.LENGTH_SHORT).show();
        }

        listItems.clear();

        for (int i =0; i < playlistItemsFromXML.size(); i++) {
            listItems.add(playlistItemsFromXML.get(i).getID() + " | " + playlistItemsFromXML.get(i).getLabel());
        }
        adapter.notifyDataSetChanged();
    }
}