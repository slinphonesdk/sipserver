package org.joinsip.demousipserver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.joinsip.usipserver.USipServerActivity;


public class MainActivityUSipserver extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_usipserver);

        Intent intent = new Intent(this, USipServerActivity.class);
        startActivity(intent);
    }

}
