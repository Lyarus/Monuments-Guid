package com.example.monumentsguid;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.monumentsguid.Entities.Country;

import java.util.List;

public class InfoActivity extends AppCompatActivity {
    List<Country> countries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
    }
}
