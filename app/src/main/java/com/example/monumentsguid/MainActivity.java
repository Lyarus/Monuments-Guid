package com.example.monumentsguid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    // rozmiar ekranu urzadzenia
    private int screenHeight;
    private int screenWidth;
    private int btnHeight;
    private int btnWidth;
    private int imageHeight;

    private Button odkryte_zabytki;
    private Button mapa;
    private Button informacja;
    private LinearLayout main_icon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pobiera parametry ekranu urzadzenia
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        imageHeight = screenHeight / 3;
        btnHeight = (screenHeight - imageHeight) / 5;
        btnWidth = screenWidth * 2 / 3;

        main_icon = findViewById(R.id.main_icon_layout);
        ViewGroup.LayoutParams paramsMainIcon = main_icon.getLayoutParams();
        paramsMainIcon.height = imageHeight;
        main_icon.setLayoutParams(paramsMainIcon);

        odkryte_zabytki = findViewById(R.id.btn_odkryte_zabytki);
        ViewGroup.LayoutParams paramsOdkryteZabytki = odkryte_zabytki.getLayoutParams();
        paramsOdkryteZabytki.height = btnHeight;
        paramsOdkryteZabytki.width = btnWidth;
        odkryte_zabytki.setLayoutParams(paramsOdkryteZabytki);

        mapa = findViewById(R.id.btn_mapa);
        ViewGroup.LayoutParams paramsMapa = mapa.getLayoutParams();
        paramsMapa.height = btnHeight;
        paramsMapa.width = btnWidth;
        mapa.setLayoutParams(paramsMapa);
        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MapsActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        informacja = findViewById(R.id.btn_informacja);
        ViewGroup.LayoutParams paramsInformacja = informacja.getLayoutParams();
        paramsInformacja.height = btnHeight;
        paramsInformacja.width = btnWidth;
        informacja.setLayoutParams(paramsInformacja);
    }

}
