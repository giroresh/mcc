package giroresh.mediacenterclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

/**
 * Created by giro on 2014.11.27..
 * This class is for the login procedure to the server
 * ATM it simply lets us specify the server IP,
 * but no real connection gets established
 */
public class Login extends Activity implements OnClickListener {
    private Button connButton;
    private Button backApp;
    private String serverIPString = null;
    private int portNrString = 0;
    private Boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        connButton = (Button) findViewById(R.id.connButton);
        connButton.setOnClickListener(this);

        backApp = (Button) findViewById(R.id.backApp);
        backApp.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.connButton:
                Intent intent = new Intent(Login.this, Control.class);
                EditText serverIP = (EditText) findViewById(R.id.serverIP);
                EditText portNr = (EditText) findViewById(R.id.portNr);
                serverIPString = serverIP.getText().toString();
                portNrString = Integer.valueOf(portNr.getText().toString());

                try {
                    connected = ParseXML.getLoginStatus(new SocketAsyncTask().execute(serverIPString, portNrString, "STAT"));
                } catch (InterruptedException e) {
                    Toast.makeText(this, "Interrupt Error", Toast.LENGTH_LONG).show();
                } catch (ExecutionException e) {
                    Toast.makeText(this, "Execution Error ", Toast.LENGTH_LONG).show();
                }
                if (connected) {
                    intent.putExtra("IP", serverIPString);
                    intent.putExtra("port", portNrString);
                    setResult(RESULT_OK, intent);
                    Toast.makeText(this, "successfully connected to Server!", Toast.LENGTH_LONG).show();
                    finish();
                    break;
                } else {
                    setResult(RESULT_CANCELED, intent);
                    Toast.makeText(this, "Server unreachable!", Toast.LENGTH_LONG).show();
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
