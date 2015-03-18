package giroresh.mediacenterclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import giroresh.mediacenterclient.playlistItems.tags.AudioTags;
import giroresh.mediacenterclient.playlistItems.tags.RomTags;
import giroresh.mediacenterclient.playlistItems.tags.VideoTags;

/**
 * Created by giro on 2014.12.02..
 * Here we control the actual forked program alias the mediaplayer
 */
public class ControlPlayback extends Activity implements OnClickListener {
    private Button backApp;
    private String serverIP;
    private int portNr;
    private int prevID;
    private int nextID;
    private int playID;
    private Button playButton;
    private Button stopButton;
    private Button prevButton;
    private Button nextButton;
    private Button muteButton;
    private Button louderButton;
    private Button quieterButton;
    private TextView playbackInfoTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.controlplayback);

        Intent intent = getIntent();
        serverIP = intent.getStringExtra("IP");
        portNr = intent.getIntExtra("port", 0);
        playID = intent.getIntExtra("playID", 0);
        prevID = intent.getIntExtra("prevID", 0);
        nextID = intent.getIntExtra("nextID", 0);

        Log.d("CTRLPLAYBACK", "playID: " + playID + " prevID: " + prevID + "  nextID: " + nextID);

        playbackInfoTV = (TextView) findViewById(R.id.playbackInfoTV);

        try {
            Object selectedFile = new ParseXML().getTagInfo(new SocketAsyncTask().execute(serverIP, portNr, "INFO " + playID));
            String classTypeOfTags = selectedFile.getClass().getName();

            if (classTypeOfTags.contains("AudioTags")) {
                AudioTags at = (AudioTags) selectedFile;
                playbackInfoTV.setText(at.getAllTagInfos());
            } else if (classTypeOfTags.contains("VideoTags")) {
                VideoTags vt = (VideoTags) selectedFile;
                playbackInfoTV.setText(vt.getAllTagInfos());
            } else if (classTypeOfTags.contains("RomTags")) {
                RomTags rt = (RomTags) selectedFile;
                playbackInfoTV.setText(rt.getAllTagInfos());
            } else {
                playbackInfoTV.setText("selected filetype is unsupported");
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
        playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(this);

        stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(this);

        prevButton = (Button) findViewById(R.id.prevButton);
        prevButton.setOnClickListener(this);

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);

        muteButton = (Button) findViewById(R.id.muteButton);
        muteButton.setOnClickListener(this);

        louderButton = (Button) findViewById(R.id.louderButton);
        louderButton.setOnClickListener(this);

        quieterButton = (Button) findViewById(R.id.quieterButton);
        quieterButton.setOnClickListener(this);

        backApp = (Button) findViewById(R.id.backApp);
        backApp.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            if (ParseXML.getCTRLReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "STOP"))) {
                Toast.makeText(this, "stopped playback", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Did not stop playback", Toast.LENGTH_SHORT).show();
            }
        } catch (ExecutionException e) {
            Toast.makeText(this, "Execution Error", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            Toast.makeText(this, "Interrupt Error", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch(v.getId()) {
                case R.id.playButton:
                    if (ParseXML.getCTRLReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "CTRL p"))) {
                        Toast.makeText(this, "Did pause playback", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Did not pause playback", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.stopButton:
                    if (ParseXML.getCTRLReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "STOP"))) {
                        Intent intentPlayback = new Intent(ControlPlayback.this, Playlist.class);
                        intentPlayback.putExtra("IP", serverIP);
                        intentPlayback.putExtra("port", portNr);
                        startActivityForResult(intentPlayback, 2);
                    } else {
                        Toast.makeText(this, "did not stop playback", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.prevButton:
                    try {
                        Boolean playReturnCode;
                        Log.d("prev", "prevID is: "+prevID);
                        if (prevID == 0) {
                            Toast.makeText(this, "First item on the List!", Toast.LENGTH_SHORT).show();
                        } else {
                            playReturnCode = ParseXML.getPlayReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "PLAY " + prevID));
                            if (playReturnCode) {
                                if (ParseXML.getCTRLReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "STOP"))) {
                                    playReturnCode = ParseXML.getPlayReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "PLAY " + prevID));
                                    if (playReturnCode) {
                                        ParseXML xml = new ParseXML();
                                        int prevID2 = xml.getPrevID(new SocketAsyncTask().execute(serverIP, portNr, "LIST 100 0 50"), prevID);
                                        Intent intentPlayback = new Intent(this, ControlPlayback.class);
                                        Log.d("CTRLPLAYBACK", "playID: " + playID + " prevID: " + prevID + "  prevID2: " + prevID2);
                                        intentPlayback.putExtra("IP", serverIP);
                                        intentPlayback.putExtra("port", portNr);
                                        intentPlayback.putExtra("playID", prevID);
                                        intentPlayback.putExtra("prevID", prevID2);
                                        intentPlayback.putExtra("nextID", playID);
                                        startActivityForResult(intentPlayback, 2);
                                    } else {
                                        Toast.makeText(this, "ERROR1 playing selected file", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(this, "ERROR2 stopping selected file", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "ERROR3 stopping selected file", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (ExecutionException e) {
                        Toast.makeText(this, "Execution Error", Toast.LENGTH_SHORT).show();

                    } catch (InterruptedException e) {
                        Toast.makeText(this, "Interrupt Error", Toast.LENGTH_SHORT).show();

                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.nextButton:
                    try {
                        if (nextID == 0) {
                            Toast.makeText(this, "Last item on the list!", Toast.LENGTH_SHORT).show();
                        } else {
                            Boolean playReturnCode;
                            playReturnCode = ParseXML.getPlayReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "PLAY " + nextID));
                            if (playReturnCode) {
                                if (ParseXML.getCTRLReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "STOP"))) {
                                    playReturnCode = ParseXML.getPlayReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "PLAY " + nextID));
                                    if (playReturnCode) {
                                        ParseXML xml = new ParseXML();
                                        int nextID2 = xml.getNextID(new SocketAsyncTask().execute(serverIP, portNr, "LIST 100 0 50"), nextID);
                                        Intent intentPlayback = new Intent(this, ControlPlayback.class);
                                        intentPlayback.putExtra("IP", serverIP);
                                        intentPlayback.putExtra("port", portNr);
                                        intentPlayback.putExtra("playID", nextID);
                                        intentPlayback.putExtra("prevID", playID);
                                        intentPlayback.putExtra("nextID", nextID2);
                                        startActivityForResult(intentPlayback, 2);
                                    } else {
                                        Toast.makeText(this, "ERROR1 playing selected file", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(this, "ERROR2 stopping selected file", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "ERROR3 stopping selected file", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (ExecutionException e) {
                        Toast.makeText(this, "Execution Error", Toast.LENGTH_SHORT).show();

                    } catch (InterruptedException e) {
                        Toast.makeText(this, "Interrupt Error", Toast.LENGTH_SHORT).show();

                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case R.id.muteButton:
                    break;
                case R.id.louderButton:
                    if (ParseXML.getCTRLReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "CTRL +"))) {
                        Toast.makeText(this, "increased volume", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "unable to increase volume", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.quieterButton:
                    if (ParseXML.getCTRLReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "CTRL -"))) {
                        Toast.makeText(this, "decreased volume", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "unable to decrease volume", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.backApp:
                    Intent backIntent = new Intent(ControlPlayback.this, Playlist.class);
                    if (ParseXML.getCTRLReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "STOP"))) {
                        Toast.makeText(this, "back to Playlist", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "back button press was unable to stop playback", Toast.LENGTH_SHORT).show();
                    }
                    setResult(RESULT_CANCELED, backIntent);
                    finish();
                    break;
            }
        } catch (ExecutionException e) {
            Toast.makeText(this, "Execution Error", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            Toast.makeText(this, "Interrupt Error", Toast.LENGTH_SHORT).show();
        }
    }
}