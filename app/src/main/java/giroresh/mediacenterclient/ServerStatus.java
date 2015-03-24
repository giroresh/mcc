package giroresh.mediacenterclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/** This activity displays the server status
 * Created by giro on 2015.03.23..
 */
public class ServerStatus extends Activity {

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
            ParseXML xml = new ParseXML();
            serverStatus = xml.getServerStatus(new SocketAsyncTask().execute(serverIP, portNr, "STAT"));
            if (!serverStatus.isEmpty()) {
                ArrayList<String> list = new ArrayList<>(Arrays.asList(serverStatus.split("\n")));
                String displayed = "";
                for (int x = 0; x < list.size(); x++) {
                    if (x==0) {
                        displayed += getResources().getString(R.string.serverStatusMsgBegin, list.get(x).substring(0, list.get(x).indexOf("\t")), list.get(x).substring(list.get(x).indexOf("\t")));
                    } else {
                        displayed += getResources().getString(R.string.serverStatusMsgRst, list.get(x).substring(0, list.get(x).indexOf("\t")), list.get(x).substring(list.get(x).indexOf("\t")+1));
                    }
                }
                TextView serverStatusInfoTV = (TextView) findViewById(R.id.serverStatusInfoTV);
                serverStatusInfoTV.setText(displayed);
            }
        } catch (ExecutionException e) {
            Toast.makeText(this, "Exe Error", Toast.LENGTH_SHORT).show();
        } catch (InterruptedException e) {
            Toast.makeText(this, "Interrupt Error", Toast.LENGTH_SHORT).show();
        } catch (XmlPullParserException e) {
            Toast.makeText(this, "XML Error", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "IO Error", Toast.LENGTH_SHORT).show();
        }
    }
}
