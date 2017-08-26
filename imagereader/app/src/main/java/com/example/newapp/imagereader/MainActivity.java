package com.example.newapp.imagereader;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.ImageReader.MESSAGE";
    public static final String IMGS = "com.example.ImageReader.IMAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onFragmentInteractionListener(Uri uri)
    {

    }
}
