package giroresh.mediacenterclient;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import giroresh.mediacenterclient.playlistItems.filetypes.PlaylistItems;
import giroresh.mediacenterclient.playlistItems.tags.AudioTags;
import giroresh.mediacenterclient.playlistItems.tags.RomTags;
import giroresh.mediacenterclient.playlistItems.tags.VideoTags;

/**
 * Created by giro on 2014.11.30..
 * This class handles the playlist
 * ATM we get a predefined List of entries ->
 * this predefined List will be the server answer!
 * we can touch each list item and get its text ->
 * Later on each touch will start the playback
 * Even Later on each touch gives different options
 */
public class Playlist extends ListActivity implements OnItemClickListener, AdapterView.OnItemSelectedListener, NumberPicker.OnValueChangeListener {
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapterSpinner;
    List<String> spinnerItems;
    List<String> listItems;
    private Spinner typesSpinner;
    private String serverIP;
    private int portNr;
    private String playID;
    private String selectedID;
    private TextView infoTV;
    private NumberPicker lengthPicker;
    private NumberPicker offsetPicker;
    private int type = 0;
    private int length = 50;
    private int offset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);

        /**
         * temporarily this is a static list for the Drop Down
         */
        spinnerItems = new ArrayList<>();
        spinnerItems.add("All");
        spinnerItems.add("Audio");
        spinnerItems.add("Video");
        spinnerItems.add("ROM");

        typesSpinner = (Spinner)findViewById(R.id.spinner);
        adapterSpinner = new ArrayAdapter<String>(this, R.layout.spinneritem, spinnerItems);

        typesSpinner.setAdapter(adapterSpinner);
        typesSpinner.setOnItemSelectedListener(this);

        lengthPicker = (NumberPicker) findViewById(R.id.length);
        lengthPicker.setMaxValue(100);
        lengthPicker.setMinValue(1);
        lengthPicker.setOnValueChangedListener(this);

        offsetPicker = (NumberPicker) findViewById(R.id.offset);
        offsetPicker.setMaxValue(100);
        offsetPicker.setMinValue(0);
        offsetPicker.setOnValueChangedListener(this);

        Intent intent = getIntent();
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

            adapter = new ArrayAdapter<>(this, R.layout.playlistitem, listItems);
            getListView().setAdapter(adapter);
            getListView().setOnItemClickListener(this);
            registerForContextMenu(getListView());

        } catch (XmlPullParserException e) {
            Toast.makeText(this, "XML Error", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "IO Error", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            Toast.makeText(this, "Interrupt Error", Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(this, "Execution Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playlistcontextmenu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        selectedID = ((TextView) info.targetView.findViewById(R.id.playlistItemTV)).getText().toString().substring(0, 8);

        infoTV = (TextView) findViewById(R.id.infoTV);

        switch (item.getItemId()) {
            case R.id.info:
                try {
                    Object selectedFile = new ParseXML().getTagInfo(new SocketAsyncTask().execute(serverIP, portNr, "INFO " + selectedID));

                    String classTypeOfTags = selectedFile.getClass().getName();

                    if (classTypeOfTags.contains("AudioTags")) {
                        AudioTags at = (AudioTags) selectedFile;
                        infoTV.setText(at.getAllTagInfos());
                    } else if (classTypeOfTags.contains("VideoTags")) {
                        VideoTags vt = (VideoTags) selectedFile;
                        infoTV.setText(vt.getAllTagInfos());
                    } else if (classTypeOfTags.contains("RomTags")) {
                        RomTags rt = (RomTags) selectedFile;
                        infoTV.setText(rt.getAllTagInfos());
                    } else {
                        infoTV.setText("selected filetype is unsupported");
                    }
                } catch (XmlPullParserException e) {
                    Toast.makeText(this, "XML Error", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(this, "IO Error", Toast.LENGTH_SHORT).show();
                } catch (ExecutionException e) {
                    Toast.makeText(this, "Execution Error", Toast.LENGTH_SHORT).show();
                } catch (InterruptedException e) {
                    Toast.makeText(this, "Interrupt Error", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /** Should be changed to something more sophisticated!!!!
         * ATM playID is fetched from list element
         */
        playID = ((TextView)view).getText().toString().substring(0, 8);

        Boolean playReturnCode = false;
        try {
            playReturnCode = ParseXML.getPlayReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "PLAY " + playID));
            if (playReturnCode) {
                if (ParseXML.getCTRLReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "STOP"))) {
                    playReturnCode = ParseXML.getPlayReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "PLAY " + playID));
                    if (playReturnCode) {
                        Intent intentPlayback = new Intent(Playlist.this, ControlPlayback.class);
                        intentPlayback.putExtra("IP", serverIP);
                        intentPlayback.putExtra("port", portNr);
                        intentPlayback.putExtra("playID", playID);
                        startActivityForResult(intentPlayback, 2);
                    } else {
                        Toast.makeText(getBaseContext(), "ERROR playing selected file", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getBaseContext(), "ERROR stopping selected file", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getBaseContext(), "ERROR playing selected file", Toast.LENGTH_SHORT).show();
            }
        } catch (ExecutionException e) {
            Toast.makeText(getBaseContext(), "Execution Error", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException ie) {
            Toast.makeText(getBaseContext(), "Interrupt Error", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * <p>Callback method to be invoked when an item in this view has been
     * selected. This callback is invoked only when the newly selected
     * position is different from the previously selected position or if
     * there was no selected item.</p>
     * <p/>
     * Impelmenters can call getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param parent   The AdapterView where the selection happened
     * @param view     The view within the AdapterView that was clicked
     * @param position The position of the view in the adapter
     * @param id       The row id of the item that is selected
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int sid = typesSpinner.getSelectedItemPosition();

        switch(sid) {
            case 0:
                type = 0;
                break;
            case 1:
                type = 100;
                break;
            case 2:
                type = 200;
                break;
            case 3:
                type = 300;
                break;
            default:
                type = 0;
                break;
        }
        doListChanges();
    }

    /**
     * Called upon a change of the current value.
     *
     * @param picker The NumberPicker associated with this listener.
     * @param oldVal The previous value.
     * @param newVal The new value.
     */
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        int selectedPicker = picker.getId();

        switch (selectedPicker) {
            case R.id.length:
                length = newVal;
                break;
            case R.id.offset:
                offset = newVal;
                break;
            default:
                length = 50;
                offset = 0;
                break;
        }
        doListChanges();
    }

    /**
     * Updates the list of the listview
     * it depends on type, offset and length variable
     * type is set by onItemSelected
     * offset and length by onValueChange
     */
    private void doListChanges() {
        List<PlaylistItems> playlistItemsFromXML = new ArrayList<>();
        try {
            ParseXML xmlItems = new ParseXML();
            playlistItemsFromXML.addAll(xmlItems.getPlaylistItems(new SocketAsyncTask().execute(serverIP, portNr, "LIST " + type + " " + offset + " " + length)));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "ERROR IO Error", Toast.LENGTH_SHORT).show();
        } catch (ExecutionException e) {
            Toast.makeText(getBaseContext(), "ERROR Exe Error", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            Toast.makeText(getBaseContext(), "ERROR Interrupt Error", Toast.LENGTH_SHORT).show();
        }

        listItems.clear();

        for (int i =0; i < playlistItemsFromXML.size(); i++) {
            listItems.add(playlistItemsFromXML.get(i).getID() + " | " + playlistItemsFromXML.get(i).getLabel());
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * Callback method to be invoked when the selection disappears from this
     * view. The selection can disappear for instance when touch is activated
     * or when the adapter becomes empty.
     *
     * @param parent The AdapterView that now contains no selected item.
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}