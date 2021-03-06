package giroresh.mediacenterclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import giroresh.mediacenterclient.SocketAsyncTask.SocketAsyncTask;
import giroresh.mediacenterclient.helper.MCCToast;
import giroresh.mediacenterclient.MCCException.NoTagsException;
import giroresh.mediacenterclient.playlistItems.tags.AudioTags;
import giroresh.mediacenterclient.playlistItems.tags.VideoTags;

/**
 * Created by giro on 2014.12.02..
 * Here we control the actual forked program alias the mediaplayer
 */
public class ControlPlayback extends Activity implements OnClickListener {
    private String serverIP;
    private int portNr;
    private int prevID;
    private int nextID;
    private int playID;
    private String titleToPlay;
    private ParseXML xml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.controlplayback);

        Intent intent = getIntent();
        serverIP = intent.getStringExtra("IP");
        portNr = intent.getIntExtra("port", 0);
        playID = intent.getIntExtra("playID", 0);
        prevID = intent.getIntExtra("prevID", 0);
        nextID = intent.getIntExtra("nextID", 0);
        titleToPlay = intent.getStringExtra("titleToPlay");

        TextView playbackInfoTV = (TextView) findViewById(R.id.playbackInfoTV);

        try {
            xml = new ParseXML();
            Object selectedFile = xml.getTagInfo(new SocketAsyncTask().execute(serverIP, portNr, "INFO " + playID));
            String classTypeOfTags = selectedFile.getClass().getName();
            String[] tagInfo = new String[0];

            if (classTypeOfTags.contains("AudioTags")) {
                AudioTags at = (AudioTags) selectedFile;
                tagInfo = at.getAllTagInfo().split("\n");
            } else if (classTypeOfTags.contains("VideoTags")) {
                VideoTags vt = (VideoTags) selectedFile;
                tagInfo = vt.getAllTagInfo().split("\n");
            }
            if (tagInfo.length != 0) {
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
                        tagInfoMultiLang += getResources().getString(R.string.tagComment) + aTagInfo.substring(aTagInfo.indexOf('\t')) + "\n";
                    }
                }
                playbackInfoTV.setText(tagInfoMultiLang.substring(0, tagInfoMultiLang.lastIndexOf("\n")));
            } else {
                playbackInfoTV.setText(R.string.unsupportedFiletype);
            }
        } catch (XmlPullParserException e) {
            MCCToast.makeText(this, getResources().getString(R.string.xmlError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
        } catch (IOException e) {
            MCCToast.makeText(this, getResources().getString(R.string.ioError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
        } catch (ExecutionException e) {
            MCCToast.makeText(this, getResources().getString(R.string.exeError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
        } catch (InterruptedException e) {
            MCCToast.makeText(this, getResources().getString(R.string.interruptError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
        } catch (NoTagsException e) {
            playbackInfoTV.setText(getResources().getString(R.string.noTagInfo, playID, titleToPlay));
        }
        Button playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(this);

        Button stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(this);

        Button prevButton = (Button) findViewById(R.id.prevButton);
        prevButton.setOnClickListener(this);

        Button nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);

        Button muteButton = (Button) findViewById(R.id.muteButton);
        muteButton.setOnClickListener(this);

        Button louderButton = (Button) findViewById(R.id.louderButton);
        louderButton.setOnClickListener(this);

        Button quieterButton = (Button) findViewById(R.id.quieterButton);
        quieterButton.setOnClickListener(this);

        Button backApp = (Button) findViewById(R.id.backApp);
        backApp.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            if (!xml.getStatus(new SocketAsyncTask().execute(serverIP, portNr, "STOP"))) {
                MCCToast.makeText(this, getResources().getString(R.string.stopUnsuccessful), Toast.LENGTH_SHORT, R.drawable.mcctoastred);                                                Toast.makeText(this, R.string.stopUnsuccessful, Toast.LENGTH_SHORT).show();
            }
        } catch (ExecutionException e) {
            MCCToast.makeText(this, getResources().getString(R.string.exeError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
        } catch (InterruptedException e) {
            MCCToast.makeText(this, getResources().getString(R.string.interruptError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
        }
    }

    @Override
    public void onClick(View v) {
        Boolean playReturnCode;
        try {
            switch(v.getId()) {
                case R.id.playButton:
                    if (xml.getStatus(new SocketAsyncTask().execute(serverIP, portNr, "CTRL p"))) {
                        MCCToast.makeText(this, getResources().getString(R.string.pausedPlay), Toast.LENGTH_SHORT, R.drawable.mcctoastgreen);
                    } else {
                        MCCToast.makeText(this, getResources().getString(R.string.pausePlayUnsuccessful), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                    }
                    break;
                case R.id.stopButton:
                    if (xml.getStatus(new SocketAsyncTask().execute(serverIP, portNr, "STOP"))) {
                        MCCToast.makeText(this, getResources().getString(R.string.stopSuccess), Toast.LENGTH_SHORT, R.drawable.mcctoastgreen);
                        Intent intentPlayback = new Intent(ControlPlayback.this, Playlist.class);
                        intentPlayback.putExtra("IP", serverIP);
                        intentPlayback.putExtra("port", portNr);
                        startActivityForResult(intentPlayback, 2);
                    } else {
                        MCCToast.makeText(this, getResources().getString(R.string.stopUnsuccessful), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                    }
                    break;
                case R.id.prevButton:
                        if (prevID == 0) {
                            MCCToast.makeText(this, getResources().getString(R.string.curItemFirst), Toast.LENGTH_SHORT, R.drawable.mcctoastblue);
                        } else {
                            playReturnCode = xml.getStatus(new SocketAsyncTask().execute(serverIP, portNr, "PLAY " + prevID));
                            if (playReturnCode) {
                                if (xml.getStatus(new SocketAsyncTask().execute(serverIP, portNr, "STOP"))) {
                                    playReturnCode = xml.getStatus(new SocketAsyncTask().execute(serverIP, portNr, "PLAY " + prevID));
                                    if (playReturnCode) {
                                        ParseXML xml = new ParseXML();
                                        int prevID2 = xml.getPrevID(new SocketAsyncTask().execute(serverIP, portNr, "LIST 0 0 50"), prevID);
                                        titleToPlay = xml.getTitleToPlay(new SocketAsyncTask().execute(serverIP, portNr, "LIST 0 0 50"), prevID);
                                        Intent intentPlayback = new Intent(this, ControlPlayback.class);
                                        intentPlayback.putExtra("IP", serverIP);
                                        intentPlayback.putExtra("port", portNr);
                                        intentPlayback.putExtra("playID", prevID);
                                        intentPlayback.putExtra("prevID", prevID2);
                                        intentPlayback.putExtra("nextID", playID);
                                        intentPlayback.putExtra("titleToPlay", titleToPlay);
                                        startActivityForResult(intentPlayback, 2);
                                    } else {
                                        MCCToast.makeText(this, getResources().getString(R.string.playUnsuccessful), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                                    }
                                } else {
                                    MCCToast.makeText(this, getResources().getString(R.string.stopUnsuccessful), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                                }
                            } else {
                                MCCToast.makeText(this, getResources().getString(R.string.playUnsuccessful), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                            }
                        }
                    break;
                case R.id.nextButton:
                        if (nextID == 0) {
                            MCCToast.makeText(this, getResources().getString(R.string.curItemLast), Toast.LENGTH_SHORT, R.drawable.mcctoastblue);
                        } else {
                            playReturnCode = xml.getStatus(new SocketAsyncTask().execute(serverIP, portNr, "PLAY " + nextID));
                            if (playReturnCode) {
                                if (xml.getStatus(new SocketAsyncTask().execute(serverIP, portNr, "STOP"))) {
                                    playReturnCode = xml.getStatus(new SocketAsyncTask().execute(serverIP, portNr, "PLAY " + nextID));
                                    if (playReturnCode) {
                                        int nextID2 = xml.getNextID(new SocketAsyncTask().execute(serverIP, portNr, "LIST 0 0 50"), nextID);
                                        titleToPlay = xml.getTitleToPlay(new SocketAsyncTask().execute(serverIP, portNr, "LIST 0 0 50"), nextID);
                                        Intent intentPlayback = new Intent(this, ControlPlayback.class);
                                        intentPlayback.putExtra("IP", serverIP);
                                        intentPlayback.putExtra("port", portNr);
                                        intentPlayback.putExtra("playID", nextID);
                                        intentPlayback.putExtra("prevID", playID);
                                        intentPlayback.putExtra("nextID", nextID2);
                                        intentPlayback.putExtra("titleToPlay", titleToPlay);
                                        startActivityForResult(intentPlayback, 2);
                                    } else {
                                        MCCToast.makeText(this, getResources().getString(R.string.playUnsuccessful), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                                    }
                                } else {
                                    MCCToast.makeText(this, getResources().getString(R.string.stopUnsuccessful), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                                }
                            } else {
                                MCCToast.makeText(this, getResources().getString(R.string.playUnsuccessful), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                            }
                        }
                    break;
                case R.id.muteButton:
                    break;
                case R.id.louderButton:
                    if (xml.getStatus(new SocketAsyncTask().execute(serverIP, portNr, "CTRL +"))) {
                        MCCToast.makeText(this, getResources().getString(R.string.volIncr), Toast.LENGTH_SHORT, R.drawable.mcctoastgreen);
                    } else {
                        MCCToast.makeText(this, getResources().getString(R.string.volIncrUnsuccessful), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                    }
                    break;
                case R.id.quieterButton:
                    if (xml.getStatus(new SocketAsyncTask().execute(serverIP, portNr, "CTRL -"))) {
                        MCCToast.makeText(this, getResources().getString(R.string.volDecr), Toast.LENGTH_SHORT, R.drawable.mcctoastgreen);
                    } else {
                        MCCToast.makeText(this, getResources().getString(R.string.volDecrUnsuccessful), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                    }
                    break;
                case R.id.backApp:
                    Intent backIntent = new Intent(ControlPlayback.this, Playlist.class);
                    if (!xml.getStatus(new SocketAsyncTask().execute(serverIP, portNr, "STOP"))) {
                        MCCToast.makeText(this, getResources().getString(R.string.backPressUnsuccessful), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                    }
                    setResult(RESULT_CANCELED, backIntent);
                    finish();
                    break;
            }
        } catch (ExecutionException e) {
            MCCToast.makeText(this, getResources().getString(R.string.exeError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
        } catch (InterruptedException e) {
            MCCToast.makeText(this, getResources().getString(R.string.interruptError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
        } catch (XmlPullParserException e) {
            MCCToast.makeText(this, getResources().getString(R.string.xmlError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
        } catch (IOException e) {
            MCCToast.makeText(this, getResources().getString(R.string.ioError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
        }
    }
}