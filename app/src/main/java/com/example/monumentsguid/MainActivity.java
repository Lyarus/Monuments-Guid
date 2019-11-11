package com.example.monumentsguid;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Pobiera rozmiar ekranu urzadzenia
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int imageHeight = screenHeight / 3;
        int btnHeight = (screenHeight - imageHeight) / 5;
        int btnWidth = screenWidth * 2 / 3;

        LinearLayout main_icon = findViewById(R.id.main_icon_layout);
        ViewGroup.LayoutParams paramsMainIcon = main_icon.getLayoutParams();
        paramsMainIcon.height = imageHeight;
        main_icon.setLayoutParams(paramsMainIcon);

        Button odkryte_zabytki = findViewById(R.id.btn_odkryte_zabytki);
        ViewGroup.LayoutParams paramsOdkryteZabytki = odkryte_zabytki.getLayoutParams();
        paramsOdkryteZabytki.height = btnHeight;
        paramsOdkryteZabytki.width = btnWidth;
        odkryte_zabytki.setLayoutParams(paramsOdkryteZabytki);
        odkryte_zabytki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CountryActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        Button mapa = findViewById(R.id.btn_mapa);
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

        Button informacja = findViewById(R.id.btn_informacja);
        ViewGroup.LayoutParams paramsInformacja = informacja.getLayoutParams();
        paramsInformacja.height = btnHeight;
        paramsInformacja.width = btnWidth;
        informacja.setLayoutParams(paramsInformacja);
        informacja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), InfoActivity.class);
                view.getContext().startActivity(intent);
            }
        });
    }

}
