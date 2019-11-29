package com.example.monumentsguid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.monumentsguid.Entities.City;
import com.example.monumentsguid.Entities.Monument;
import com.example.monumentsguid.Entities.ObservationPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CityActivity extends AppCompatActivity {

    private List<City> cities;
    private List<Monument> monuments;
    private List<ObservationPoint> observationPoints;

    private List<String> cityNames;
    private List<String> cityImages;
    private List<String> cityIds;
    private String country_ref;

    private GridView gridView;
    private GridView.OnItemClickListener gridViewOnItemClickListener = new GridView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i = new Intent(getApplicationContext(),
                    MonumentActivity.class);
            i.putExtra("id", cityIds.get(position));
            i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
            i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
            startActivity(i);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        cities = getIntent().getParcelableArrayListExtra("cities");
        monuments = getIntent().getParcelableArrayListExtra("monuments");
        observationPoints = getIntent().getParcelableArrayListExtra("observationPoints");


        // Pobiera rozmiar ekranu urzadzenia
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        gridView = findViewById(R.id.gridview);
        TextView gridTitle = findViewById(R.id.textOdkryteZabytki);
        gridTitle.setText(R.string.miasta);
        ViewGroup.LayoutParams paramsTitle = gridTitle.getLayoutParams();
        paramsTitle.width = screenWidth * 3 / 4;
        gridTitle.setLayoutParams(paramsTitle);

        Intent intent = getIntent();
        country_ref = Objects.requireNonNull(intent.getExtras()).getString("id");
        cityNames = new ArrayList<>();
        cityImages = new ArrayList<>();
        cityIds = new ArrayList<>();
        addGridItems();
    }

    private void addGridItems() {
        for (City city : cities) {
            if (city.getCountryRef().equals(country_ref)) {
                cityIds.add(city.getId());
                cityNames.add(city.getName());
                cityImages.add(city.getImage());
            }
        }
        ItemGridAdapter cityAdapter = new ItemGridAdapter(CityActivity.this, cityIds, cityNames, cityImages, true);
        gridView.setAdapter(cityAdapter);
        gridView.setOnItemClickListener(gridViewOnItemClickListener);
    }
}
