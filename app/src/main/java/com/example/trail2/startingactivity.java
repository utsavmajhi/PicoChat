package com.example.trail2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class startingactivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startingactivity);
    }

    public void regbutton1(View view) {
        startActivity(new Intent(startingactivity.this,registeractivity.class));
    }

    public void lgbutton(View view) {
        startActivity(new Intent(startingactivity.this,loginactivity.class));
    }
}
