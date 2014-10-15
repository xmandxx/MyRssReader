package com.example.sanyinchen.myrssreader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.util.AbAppUtil;


public class MainActivity extends AbActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("AndBase", "AndBase-----started");
        TextView mtexview = (TextView) findViewById(R.id.andbaseviewtext);
        mtexview.setText("Hello AndBbase");

    }


}
