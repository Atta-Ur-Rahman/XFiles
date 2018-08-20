package com.xfiles.techease;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.attaurrahman.recordapplication.R;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Utilities.connectFragmentWithOutBackStack(this,new RecordingViewFragment());
    }
}
