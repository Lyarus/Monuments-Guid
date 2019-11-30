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

    private List<Monument> monumentsFiltered;
    private List<ObservationPoint> observationPointsFiltered;
    private List<GridItem> cityItems;

    private List<String> cityIds;
    private String country_ref;

    private GridView gridView;
    private GridView.OnItemClickListener gridViewOnItemClickListener = new GridView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // filtruje zabytki wybranego miasta
            String cityId = cityIds.get(position);
            for (Monument monument : monuments) {
                String cityRef = monument.getCityRef();
                if (cityRef.equals(cityId)) {
                    monumentsFiltered.add(monument);
                    String monumentId = monument.getId();
                    // filtrue punkty obserwacji zabytków wybranego miasta
                    for (ObservationPoint observationPoint : observationPoints) {
                        String monumentRef = observationPoint.getMonumentRef();
                        if (monumentRef.equals(monumentId)) {
                            observationPointsFiltered.add(observationPoint);
                        }
                    }
                }

            }

            // wysyła odfiltrowane dane do sidoku miast
            Intent i = new Intent(getApplicationContext(),
                    MonumentActivity.class);
            i.putExtra("id", cityId);
            i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monumentsFiltered);
            i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPointsFiltered);

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

        monumentsFiltered = new ArrayList<>();
        observationPointsFiltered = new ArrayList<>();
        cityItems = new ArrayList<>();

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
        cityIds = new ArrayList<>();
        addGridItems();
    }

    private void addGridItems() {

        for (City city : cities) {
            if (city.getCountryRef().equals(country_ref)) {
                String id = city.getId();
                String name = city.getName();
                String image = city.getImage();
                boolean isActive = false;
                // jeżeli w pamięci urządzenia są obrazki z punktów obserwacji danego zabytku - ustawiamy isActive na true
                if (observationPoints != null) {
                    for (ObservationPoint observationPoint : observationPoints) {
                        String imagePath = observationPoint.getCustomImagePath();
                        if (imagePath != null) {
                            isActive = true;
                            break;
                        }
                    }
                }
                GridItem cityItem = new GridItem(id, name, image, isActive);
                cityItems.add(cityItem);
                cityIds.add(id);
            }
        }
        ItemGridAdapter cityAdapter = new ItemGridAdapter(CityActivity.this, true, cityItems);
        gridView.setAdapter(cityAdapter);
        gridView.setOnItemClickListener(gridViewOnItemClickListener);
    }
}
