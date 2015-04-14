package giroresh.mediacenterclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import giroresh.mediacenterclient.helper.MCCTextWatcher;
import giroresh.mediacenterclient.helper.MCCToast;

/**
 * Created by giro on 2015.01.23..
 * Sets the admin key for the server
 */
public class SetAdminKey extends Activity implements View.OnClickListener {

    private EditText adminKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setadminkey);

        adminKey = (EditText) findViewById(R.id.setAdminKeyET);
        adminKey.addTextChangedListener(new MCCTextWatcher(adminKey));

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
                String adminKeyString = adminKey.getText().toString();
                Intent intent = new Intent(SetAdminKey.this, Control.class);

                if (new MCCTextWatcher(adminKey).doAdminkeyValidation(adminKeyString)) {
                    if (!adminKeyString.isEmpty()) {
                        intent.putExtra("AdminKey", adminKeyString);
                        setResult(RESULT_OK, intent);
                        MCCToast.makeText(this, getResources().getString(R.string.setPW), Toast.LENGTH_LONG, R.drawable.mcctoastgreen);
                        finish();
                    } else {
                        MCCToast.makeText(this, getResources().getString(R.string.setPWNot), Toast.LENGTH_LONG, R.drawable.mcctoastred);
                        setResult(RESULT_CANCELED, intent);
                        finish();
                    }
                    break;
                } else {
                    MCCToast.makeText(this, getResources().getString(R.string.adminKeyCheck), Toast.LENGTH_LONG, R.drawable.mcctoastred);
                    break;
                }
            case R.id.backApp:
                Intent backIntent = new Intent(SetAdminKey.this, Control.class);
                setResult(RESULT_CANCELED, backIntent);
                finish();
                break;
        }
    }
}
