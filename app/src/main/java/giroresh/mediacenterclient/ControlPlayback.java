package giroresh.mediacenterclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
    private String playID;
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
        playID = intent.getStringExtra("playID");

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
                    if (ParseXML.getCTRLReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "STOP"))) {
                        if (ParseXML.getCTRLReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "CTRL 1"))) {
                            Toast.makeText(this, "Playback of previous file was successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Playback of previous file was unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Playback of previous file was unsuccessful", Toast.LENGTH_SHORT).show();                    }
                    break;
                case R.id.nextButton:
                    if (ParseXML.getCTRLReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "STOP"))) {
                        if (ParseXML.getCTRLReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "CTRL 2"))) {
                            Toast.makeText(this, "Playback of next file was successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Playback of next file was unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Playback of next file was unsuccessful", Toast.LENGTH_SHORT).show();
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