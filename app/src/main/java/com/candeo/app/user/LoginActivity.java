package com.candeo.app.user;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.candeo.app.R;
import com.candeo.app.util.CandeoUtil;

import java.util.ArrayList;

public class LoginActivity extends Activity {

    Spinner emailSelector;
    Button signup;
    ArrayList<String> emails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailSelector = (Spinner)findViewById(R.id.candeo_login_email_selector);
        emails= CandeoUtil.emailAddresses(this);
        ArrayAdapter<String> emailSelectorAdapter = new ArrayAdapter<String>(this, R.layout.candeo_email_spinner_item,emails.toArray(new String[emails.size()]));
        emailSelectorAdapter.setDropDownViewResource(R.layout.candeo_spinner_dropdown_item);
        emailSelector.setAdapter(emailSelectorAdapter);

    }

}
