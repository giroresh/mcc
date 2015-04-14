package giroresh.mediacenterclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.util.concurrent.ExecutionException;

import giroresh.mediacenterclient.SocketAsyncTask.SocketAsyncTask;
import giroresh.mediacenterclient.helper.MCCTextWatcher;
import giroresh.mediacenterclient.helper.MCCToast;

/**
 * Created by giro on 2014.11.27..
 * This class is for the login procedure to the server
 * ATM it simply lets us specify the server IP,
 * but no real connection gets established
 */
public class Login extends Activity implements OnClickListener {
    private String serverIPString = null;
    private int portNrString = 0;
    private Boolean connected = false;
    private EditText serverIP;
    private EditText portNr;
    private Integer portInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        serverIP = (EditText) findViewById(R.id.serverIP);
        serverIP.addTextChangedListener(new MCCTextWatcher(serverIP));

        portNr = (EditText) findViewById(R.id.portNr);
        portNr.addTextChangedListener(new MCCTextWatcher(portNr));

        Button connButton = (Button) findViewById(R.id.connButton);
        connButton.setOnClickListener(this);

        Button backApp = (Button) findViewById(R.id.backApp);
        backApp.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connButton:
                String serverIPString = serverIP.getText().toString();
                String portNrString = portNr.getText().toString();

                if (serverIPString.isEmpty()) {
                    serverIP.setError(getResources().getString(R.string.serverIPEmpty));
                    break;
                } else if (portNrString.isEmpty()) {
                    portNr.setError(getResources().getString(R.string.portNrEmpty));
                    break;
                }

                Boolean serverIPBool = new MCCTextWatcher(serverIP).doIPValidation(serverIPString);
                Boolean portNrBool = new MCCTextWatcher(portNr).doPortValidation(portNrString);

                if (!serverIPBool) {
                    serverIP.setError(getResources().getString(R.string.serverIPFaulty));
                    break;
                }
                if (!portNrBool) {
                    portNr.setError(getResources().getString(R.string.portNrFaulty));
                    break;
                }
                portInt = Integer.valueOf(portNrString);
                try {
                    ParseXML xml = new ParseXML();
                    connected = xml.getStatus(new SocketAsyncTask().execute(serverIPString, portInt, "STAT"));
                } catch (ExecutionException e) {
                    MCCToast.makeText(this, getResources().getString(R.string.exeError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                } catch (InterruptedException e) {
                    MCCToast.makeText(this, getResources().getString(R.string.interruptError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                } catch (XmlPullParserException e) {
                    MCCToast.makeText(this, getResources().getString(R.string.xmlError), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                }
                Intent intent = new Intent(Login.this, Control.class);
                if (connected) {
                    intent.putExtra("IP", serverIPString);
                    intent.putExtra("port", portNrString);
                    setResult(RESULT_OK, intent);
                    MCCToast.makeText(this, getResources().getString(R.string.serverConnected), Toast.LENGTH_SHORT, R.drawable.mcctoastgreen);
                    finish();
                    break;
                } else {
                    setResult(RESULT_CANCELED, intent);
                    MCCToast.makeText(this, getResources().getString(R.string.serverUnreachable), Toast.LENGTH_SHORT, R.drawable.mcctoastred);
                    finish();
                    break;
                }
            case R.id.backApp:
                Intent backIntent = new Intent(Login.this, Control.class);
                setResult(RESULT_CANCELED, backIntent);
                finish();
                break;
        }
    }
}
