package giroresh.mediacenterclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class Control extends Activity implements OnClickListener {
    private Button playlist;
    private Button setAdminKeyButton;
    private Button restartButton;
    private Button shutdownButton;
    private String serverIP = null;
    private int portNr = 0;
    private String adminKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control);
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
            case R.id.restartButton:
                try {
                    Boolean status = ParseXML.getCTRLReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "RESTART " + adminKey));
                    if (status) {
                        Toast.makeText(this, "server restarted", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "restart failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (ExecutionException e) {
                    Toast.makeText(this, "restart failed - Execution Error", Toast.LENGTH_SHORT).show();
                } catch (InterruptedException e) {
                    Toast.makeText(this, "restart failed - Interrupt Error", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.shutdownButton:
                try {
                    Boolean status = ParseXML.getCTRLReturnCodeStatus(new SocketAsyncTask().execute(serverIP, portNr, "SHUTDOWN " + adminKey));
                    if (status) {
                        playlist.setClickable(false);
                        playlist.setEnabled(false);
                        setAdminKeyButton.setClickable(false);
                        setAdminKeyButton.setEnabled(false);
                        restartButton.setClickable(false);
                        restartButton.setEnabled(false);
                        shutdownButton.setClickable(false);
                        shutdownButton.setEnabled(false);
                        Toast.makeText(this, "server shutdown", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "shutdown failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (ExecutionException e) {
                    Toast.makeText(this, "shutdown failed - Execution Error", Toast.LENGTH_SHORT).show();
                } catch (InterruptedException e) {
                    Toast.makeText(this, "shutdown failed - Interrupt Error", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.closeApp:
                finish();
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
        if (reqCode == 1) {
            if (resCode == RESULT_OK) {
                serverIP = data.getExtras().getString("IP");
                portNr = data.getExtras().getInt("port");
                playlist.setClickable(true);
                playlist.setEnabled(true);
                setAdminKeyButton.setClickable(true);
                setAdminKeyButton.setEnabled(true);
            } else if (resCode == RESULT_CANCELED) {
                Toast.makeText(this, "Back at Control!", Toast.LENGTH_SHORT).show();
            }
        } else if (reqCode == 3) {
            if (resCode == RESULT_OK) {
                adminKey = data.getExtras().getString("AdminKey");
                restartButton.setClickable(true);
                restartButton.setEnabled(true);
                shutdownButton.setClickable(true);
                shutdownButton.setEnabled(true);
            } else if (resCode == RESULT_CANCELED) {
                Toast.makeText(this, "Back at Control!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
