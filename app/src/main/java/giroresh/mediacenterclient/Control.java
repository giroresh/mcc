package giroresh.mediacenterclient;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.util.concurrent.ExecutionException;

import giroresh.mediacenterclient.SocketAsyncTask.SocketAsyncTask;
import giroresh.mediacenterclient.helper.MCCToast;

public class Control extends Activity implements OnClickListener {
    private Button playlist;
    private Button setAdminKeyButton;
    private Button restartButton;
    private Button shutdownButton;
    private String serverIP = null;
    private int portNr = 0;
    private String adminKey;
    private Button serverStatusButton;
    private ParseXML xml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control);

        try {
            xml = new ParseXML();
        } catch (XmlPullParserException e) {
            Toast.makeText(this, "XML Error", Toast.LENGTH_SHORT).show();
        }

        Button connServer = (Button) findViewById(R.id.connServer);
        connServer.setOnClickListener(this);

        playlist = (Button) findViewById(R.id.playlistButton);
        playlist.setOnClickListener(this);
        playlist.setClickable(false);
        playlist.setEnabled(false);

        setAdminKeyButton = (Button) findViewById(R.id.setAdminKeyButton);
        setAdminKeyButton.setOnClickListener(this);
        setAdminKeyButton.setClickable(false);
        setAdminKeyButton.setEnabled(false);

        serverStatusButton = (Button) findViewById(R.id.serverStatusButton);
        serverStatusButton.setOnClickListener(this);
        serverStatusButton.setClickable(false);
        serverStatusButton.setEnabled(false);

        restartButton = (Button) findViewById(R.id.restartButton);
        restartButton.setOnClickListener(this);
        restartButton.setClickable(false);
        restartButton.setEnabled(false);

        shutdownButton = (Button) findViewById(R.id.shutdownButton);
        shutdownButton.setOnClickListener(this);
        shutdownButton.setClickable(false);
        shutdownButton.setEnabled(false);

        Button closeApp = (Button) findViewById(R.id.closeApp);
        closeApp.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connServer:
                Intent intent = new Intent(this, Login.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.playlistButton:
                Intent intentPlaylist = new Intent(this, Playlist.class);
                intentPlaylist.putExtra("IP", serverIP);
                intentPlaylist.putExtra("port", portNr);
                startActivityForResult(intentPlaylist, 2);
                break;
            case R.id.setAdminKeyButton:
                Intent intentSetAdminKey = new Intent(this, SetAdminKey.class);
                startActivityForResult(intentSetAdminKey, 3);
                break;
            case R.id.serverStatusButton:
                Intent intentServerStatus = new Intent(this, ServerStatus.class);
                intentServerStatus.putExtra("IP", serverIP);
                intentServerStatus.putExtra("port", portNr);
                startActivityForResult(intentServerStatus, 4);
                break;
            case R.id.restartButton:
                try {
                    Boolean status = xml.getStatus(new SocketAsyncTask().execute(serverIP, portNr, "RESTART " + adminKey));
                    if (status) {
                        MCCToast.makeText(this, getResources().getString(R.string.restartedServer), Toast.LENGTH_SHORT, R.drawable.mcctoastgreen);
                    } else {
                        MCCToast.makeText(this, getResources().getString(R.string.wrongPW), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                    }
                } catch (ExecutionException e) {
                    MCCToast.makeText(this, getResources().getString(R.string.exeError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                } catch (InterruptedException e) {
                    MCCToast.makeText(this, getResources().getString(R.string.interruptError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                }
                break;
            case R.id.shutdownButton:
                try {
                    Boolean status = xml.getStatus(new SocketAsyncTask().execute(serverIP, portNr, "SHUTDOWN " + adminKey));
                    if (status) {
                        playlist.setClickable(false);
                        playlist.setEnabled(false);
                        setAdminKeyButton.setClickable(false);
                        setAdminKeyButton.setEnabled(false);
                        serverStatusButton.setClickable(false);
                        serverStatusButton.setEnabled(false);
                        restartButton.setClickable(false);
                        restartButton.setEnabled(false);
                        shutdownButton.setClickable(false);
                        shutdownButton.setEnabled(false);
                        MCCToast.makeText(this, getResources().getString(R.string.shutdownServer), Toast.LENGTH_SHORT, R.drawable.mcctoastgreen);
                    } else {
                        MCCToast.makeText(this, getResources().getString(R.string.wrongPW), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                    }
                } catch (ExecutionException e) {
                    MCCToast.makeText(this, getResources().getString(R.string.exeError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                } catch (InterruptedException e) {
                    MCCToast.makeText(this, getResources().getString(R.string.interruptError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                }
                break;
            case R.id.closeApp:
                System.runFinalization();
                Runtime.getRuntime().gc();
                System.gc();
                String apiVersion = Build.VERSION.RELEASE;
                if (apiVersion.startsWith("5.")) {
                    finishAndRemoveTask();
                } else {
                    finish();
                }
                break;
        }
    }

    /**
     * reqcode == 1 Login
     * reqcode == 2 Playlist -> unused atm
     * reqcode == 3 SetAdmin Key
     *
     * @param reqCode request code of the activities
     * @param resCode response code of the activities
     * @param data the actual data sent by them
     */
    protected void onActivityResult(int reqCode, int resCode, Intent data) {
        switch (reqCode) {
            case 1:
                if (resCode == RESULT_OK) {
                    serverIP = data.getExtras().getString("IP");
                    portNr = data.getExtras().getInt("port");
                    playlist.setClickable(true);
                    playlist.setEnabled(true);
                    setAdminKeyButton.setClickable(true);
                    setAdminKeyButton.setEnabled(true);
                    serverStatusButton.setClickable(true);
                    serverStatusButton.setEnabled(true);
                } else if (resCode == RESULT_CANCELED) {
                    MCCToast.makeText(this, "Back at Control!", Toast.LENGTH_SHORT, R.drawable.mcctoastblue);
                }
                break;
            case 3:
                if (resCode == RESULT_OK) {
                    adminKey = data.getExtras().getString("AdminKey");
                    restartButton.setClickable(true);
                    restartButton.setEnabled(true);
                    shutdownButton.setClickable(true);
                    shutdownButton.setEnabled(true);
                } else if (resCode == RESULT_CANCELED) {
                    MCCToast.makeText(this, "Back at Control!", Toast.LENGTH_SHORT, R.drawable.mcctoastblue);
                }
                break;
        }
    }
}
