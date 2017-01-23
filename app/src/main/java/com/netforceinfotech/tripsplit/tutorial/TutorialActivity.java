package com.netforceinfotech.tripsplit.tutorial;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.netforceinfotech.tripsplit.R;


public class TutorialActivity extends AppCompatActivity {


    Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        b = (Button) findViewById(R.id.defaultbutton);





    }
    public void startDefaultIntro(View v) {
        Intent intent = new Intent(this, DefaultIntro.class);
        startActivity(intent);
    }



}



