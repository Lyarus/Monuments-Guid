package com.example.monumentsguid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.monumentsguid.Entities.City;
import com.example.monumentsguid.Entities.Country;
import com.example.monumentsguid.Entities.Monument;
import com.example.monumentsguid.Entities.ObservationPoint;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public long back_pressed;

    public List<Country> countries;
    public List<City> cities;
    public List<Monument> monuments;
    public List<ObservationPoint> observationPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countries = getIntent().getParcelableArrayListExtra("countries");
        cities = getIntent().getParcelableArrayListExtra("cities");
        monuments = getIntent().getParcelableArrayListExtra("monuments");
        observationPoints = getIntent().getParcelableArrayListExtra("observationPoints");

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

        Button zabytki = findViewById(R.id.btn_odkryte_zabytki);
        ViewGroup.LayoutParams paramsOdkryteZabytki = zabytki.getLayoutParams();
        paramsOdkryteZabytki.height = btnHeight;
        paramsOdkryteZabytki.width = btnWidth;
        zabytki.setLayoutParams(paramsOdkryteZabytki);
        zabytki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CountryActivity.class);
                intent.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
                intent.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
                intent.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
                intent.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
                view.getContext().startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
                intent.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
                intent.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
                intent.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
                intent.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
                view.getContext().startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
                intent.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
                intent.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
                intent.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
                intent.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
                view.getContext().startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    // wyłącza aplikację przy podwójnym kliknięciu
    @Override
    public void onBackPressed() {
        if (back_pressed + 1000 > System.currentTimeMillis()) {
            finish();
            moveTaskToBack(true);
            finishAffinity();
            System.exit(0);
            android.os.Process.killProcess(android.os.Process.myPid());

        } else {
            String textMess = getString(R.string.double_click_on_exit_text);
            Toast.makeText(getBaseContext(), textMess, Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }
}
