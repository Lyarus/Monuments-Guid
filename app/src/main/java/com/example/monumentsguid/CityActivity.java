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
import com.example.monumentsguid.Entities.Country;
import com.example.monumentsguid.Entities.Monument;
import com.example.monumentsguid.Entities.ObservationPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CityActivity extends AppCompatActivity {
    private List<City> cities;
    private List<Monument> monuments;
    private List<ObservationPoint> observationPoints;
    private List<Country> countries;
    private List<ObservationPoint> observationPointsFiltered;
    private List<ObservationPoint> observationPointsFilteredCity;
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
                    String monumentId = monument.getId();
                    // filtrue punkty obserwacji zabytków wybranego miasta
                    for (ObservationPoint observationPoint : observationPointsFiltered) {
                        String monumentRef = observationPoint.getMonumentRef();
                        if (monumentRef.equals(monumentId)) {
                            observationPointsFilteredCity.add(observationPoint);
                        }
                    }
                }

            }

            // wysyła odfiltrowane dane do sidoku miast
            Intent i = new Intent(getApplicationContext(),
                    MonumentActivity.class);
            i.putExtra("city_id", cityId);
            i.putExtra("country_ref", country_ref);
            i.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
            i.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
            i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
            i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
            i.putParcelableArrayListExtra("observationPointsFiltered", (ArrayList<? extends Parcelable>) observationPointsFilteredCity);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        Intent intent = getIntent();
        country_ref = Objects.requireNonNull(intent.getExtras()).getString("country_id");
        countries = getIntent().getParcelableArrayListExtra("countries");
        cities = getIntent().getParcelableArrayListExtra("cities");
        monuments = getIntent().getParcelableArrayListExtra("monuments");
        observationPoints = getIntent().getParcelableArrayListExtra("observationPoints");
        observationPointsFiltered = getIntent().getParcelableArrayListExtra("observationPointsFiltered");

        observationPointsFilteredCity = new ArrayList<>();
        cityItems = new ArrayList<>();

        // Pobiera rozmiar ekranu urzadzenia
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        gridView = findViewById(R.id.gridview);
        TextView gridTitle = findViewById(R.id.textOdkryteZabytki);
        gridTitle.setText(R.string.miasta);
        ViewGroup.LayoutParams paramsTitle = gridTitle.getLayoutParams();
        paramsTitle.width = screenWidth * 3 / 4;
        gridTitle.setLayoutParams(paramsTitle);

        cityIds = new ArrayList<>();
        addGridItems();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, CountryActivity.class);
        intent.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
        intent.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
        intent.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
        intent.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        this.finish();
    }

    private void addGridItems() {

        for (City city : cities) {
            if (city.getCountryRef().equals(country_ref)) {
                String id = city.getId();
                String name = city.getName();
                String image = city.getImage();
                boolean isActive = false;
                // jeżeli w pamięci urządzenia są obrazki z punktów obserwacji danego zabytku - ustawiamy isActive na true
                if (observationPointsFiltered != null) {
                    for (ObservationPoint observationPoint : observationPointsFiltered) {
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
