package com.candeo.app.user;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.candeo.app.CandeoApplication;
import com.candeo.app.R;
import com.candeo.app.util.CandeoUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {

    Spinner emailSelector;
    EditText name;
    Button signup;
    ArrayList<String> emails;
    private static final String API_REGISTER_URL = CandeoApplication.baseUrl+"/api/v1/users/register";
    private static final String API_LOGIN_URL = CandeoApplication.baseUrl+"/api/v1/users/login";
    private static final String TAG ="Candeo - Login Activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailSelector = (Spinner)findViewById(R.id.candeo_login_email_selector);
        emails= CandeoUtil.emailAddresses(this);
        ArrayAdapter<String> emailSelectorAdapter = new ArrayAdapter<String>(this, R.layout.candeo_email_spinner_item,emails.toArray(new String[emails.size()]));
        emailSelectorAdapter.setDropDownViewResource(R.layout.candeo_spinner_dropdown_item);
        emailSelector.setAdapter(emailSelectorAdapter);
        name=(EditText)findViewById(R.id.candeo_user_name);
        signup =(Button)findViewById(R.id.candeo_user_register);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               registerUser(name.getText().toString(), emailSelector.getSelectedItem().toString());

            }
        });

    }

    private void registerUser(final String name, final String email)
    {
        System.out.println("name is "+name+" email is "+email);
        Map<String, String> params = new HashMap<>();

        params.put("name",name);
        params.put("email",email);

        JsonObjectRequest registerRequest = new JsonObjectRequest(Request.Method.POST,
                API_REGISTER_URL,
                new JSONObject(params),
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            String id = Integer.toString(response.getInt("id"));
                            System.out.println("Response is "+id);
                            Map<String, String> loginParams = new HashMap<>();
                            loginParams.put("id",id);
                            JsonObjectRequest loginRequest = new JsonObjectRequest(
                              Request.Method.POST,
                              API_LOGIN_URL,
                              new JSONObject(loginParams),
                              new Response.Listener<JSONObject>() {
                                  @Override
                                  public void onResponse(JSONObject response) {
                                      Log.e(TAG, "Response is "+response);
                                      CandeoUtil.appAlertDialog(LoginActivity.this, "You Must Have Received an email. Please check to continue login.");
                                  }
                              },
                              new Response.ErrorListener() {
                                  @Override
                                  public void onErrorResponse(VolleyError error) {
                                      System.out.println("Something went wrong");
                                  }
                              }

                            );
                            CandeoApplication.getInstance().getAppRequestQueue().add(loginRequest);
                        }
                        catch (JSONException je)
                        {
                            je.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Something went wrong");
                    }
                });
        System.out.println("Getting Request Queue "+CandeoApplication.getInstance());
        CandeoApplication.getInstance().getAppRequestQueue().add(registerRequest);
    }



}
