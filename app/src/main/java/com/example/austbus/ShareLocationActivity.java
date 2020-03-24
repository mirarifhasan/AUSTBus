package com.example.austbus;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.austbus.Model.Bus;

public class ShareLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_location);

        Bus bus = new Bus(getIntent().getIntExtra("busID", 0));

    }
}
