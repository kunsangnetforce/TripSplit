package com.netforceinfotech.tripsplit.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.netforceinfotech.tripsplit.R;
import com.netforceinfotech.tripsplit.util.Validation;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etEmail;
    Button buttonSend;
    private MaterialDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initView();
        setupToolBar("Forgot password");
    }

    private void setupToolBar(String app_name) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(app_name);


    }

    private void initView() {
        progressDialog = new MaterialDialog.Builder(this)
                .title(R.string.progress_dialog)
                .content(R.string.please_wait)
                .progress(true, 0).build();
        buttonSend = (Button) findViewById(R.id.buttonSend);
        etEmail = (EditText) findViewById(R.id.etemail);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Validation.isEmailAddress(etEmail, true)) {
                    sendPassword(etEmail.getText().toString());
                } else {
                    showMessage("Email not valide");
                }
            }
        });
    }

    private void sendPassword(String s) {
        progressDialog.show();
        //services.php?opt=forgotpassword&email=dileep@netforceinfotech.com
        String baseUrl = getString(R.string.url);
        String url = baseUrl + "services.php?opt=forgotpassword&email=" + s;
        Log.i("kurl", url);
        Ion.with(this)
                .load(url)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                                 @Override
                                 public void onCompleted(Exception e, JsonObject result) {
                                     progressDialog.dismiss();
                                     if (result != null) {
                                         Log.i("kresult", result.toString());
                                         if (result.get("status").getAsString().equalsIgnoreCase("success")) {
                                             showMessage("Password sent to email. Please check");
                                         }

                                     } else {
                                         e.printStackTrace();
                                     }
                                 }
                             }

                );

    }

    private void showMessage(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
