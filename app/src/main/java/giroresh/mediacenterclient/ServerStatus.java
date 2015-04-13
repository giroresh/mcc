package giroresh.mediacenterclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import giroresh.mediacenterclient.SocketAsyncTask.SocketAsyncTask;
import giroresh.mediacenterclient.helper.MCCToast;

/** This activity displays the server status
 * Created by giro on 2015.03.23..
 */
public class ServerStatus extends Activity implements View.OnClickListener {

    private int portNr;
    private String serverIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serverstatus);

        Intent intent = getIntent();
        serverIP = intent.getStringExtra("IP");
        portNr = intent.getIntExtra("port", 0);

        try {
            String serverStatus = "";
            TextView serverStatusInfoTV = (TextView) findViewById(R.id.serverStatusInfoTV);
            ParseXML xml = new ParseXML();
            serverStatus = xml.getServerStatus(new SocketAsyncTask().execute(serverIP, portNr, "STAT"));
            if (serverStatus == null || serverStatus.isEmpty()) {
                serverStatusInfoTV.setText(getResources().getString(R.string.serverStatusNoMsg));
            } else {
                ArrayList<String> list = new ArrayList<>(Arrays.asList(serverStatus.split("\n")));
                String displayed = "";
                for (int x = 0; x < list.size(); x++) {
                    if (x == 0) {
                        displayed += getResources().getString(R.string.serverStatusMsgBegin, list.get(x).substring(0, list.get(x).indexOf("\t")), list.get(x).substring(list.get(x).indexOf("\t")));
                    } else {
                        displayed += getResources().getString(R.string.serverStatusMsgRst, list.get(x).substring(0, list.get(x).indexOf("\t")), list.get(x).substring(list.get(x).indexOf("\t") + 1));
                    }
                }
                serverStatusInfoTV.setText(displayed);
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
        Button backApp = (Button) findViewById(R.id.backApp);
        backApp.setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.backApp:
                Intent backIntent = new Intent(ServerStatus.this, Control.class);
                setResult(RESULT_CANCELED, backIntent);
                finish();
                break;
        }
    }
}
