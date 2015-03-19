package giroresh.mediacenterclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by giro on 2015.01.23..
 * Sets the admin key for the server
 */
public class SetAdminKey extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setadminkey);

        Button setAdminKeyButton2 = (Button) findViewById(R.id.setAdminKeyButton2);
        setAdminKeyButton2.setOnClickListener(this);

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
            case R.id.setAdminKeyButton2:
                EditText adminKey = (EditText) findViewById(R.id.setAdminKeyET);
                String adminKeyString = adminKey.getText().toString();
                Intent intent = new Intent(SetAdminKey.this, Control.class);
                if (!adminKeyString.isEmpty()) {
                    intent.putExtra("AdminKey", adminKeyString);
                    setResult(RESULT_OK, intent);
                    Toast.makeText(this, "admin key set!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(this, "unable to set admin key!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_CANCELED, intent);
                    finish();
                }
                break;
            case R.id.backApp:
                Intent backIntent = new Intent(SetAdminKey.this, Control.class);
                setResult(RESULT_CANCELED, backIntent);
                finish();
                break;
        }
    }
}
