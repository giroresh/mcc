package giroresh.mediacenterclient;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
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

import giroresh.mediacenterclient.playlistItems.filetypes.PlaylistItems;

/**
 * * Based on the android example code
 * The fragment that handles ROM files
 */
public class ROMPageFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private static final String ARG_PAGE = "ROM_PAGE";

    private int mPage;

    private ArrayAdapter<String> adapter;
    private List<String> listItems;
    private ListView lv;
    private String serverIP;
    private int portNr;
    private int playID;
    private String selectedID;
    private TextView infoTV;
    private final int type = 200;
    private int length = 50;
    private int offset = 0;
    private Button lengthButton;
    private Button lengthMinusButton;
    private Button offsetButton;
    private Button offsetMinusButton;
    private View view;
    private TextView lengthTV;
    private TextView offsetTV;

    public static ROMPageFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        ROMPageFragment fragment = new ROMPageFragment();
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
        view = inflater.inflate(R.layout.romfragmentview, container, false);
        lv = (ListView) view.findViewById(R.id.romlist);

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