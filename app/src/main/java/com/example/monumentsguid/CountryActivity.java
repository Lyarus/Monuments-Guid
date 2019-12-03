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


public class CountryActivity extends AppCompatActivity {

    private List<Country> countries;
    private List<City> cities;
    private List<Monument> monuments;
    private List<ObservationPoint> observationPoints;

    private List<City> citiesFiltered;
    private List<Monument> monumentsFiltered;
    private List<ObservationPoint> observationPointsFilteredCountry;
    private List<GridItem> countryItems;
    private List<String> countryIds;

    private GridView gridView;
    private GridView.OnItemClickListener gridViewOnItemClickListener = new GridView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // filtruje miasta wybranego kraju
            String countryId = countryIds.get(position);
            for (City city : cities) {
                String countryRef = city.getCountryRef();
                if (countryRef.equals(countryId)) {
                    citiesFiltered.add(city);
                    String cityId = city.getId();
                    // filtruje zabytki miast wybranego kraju
                    for (Monument monument : monuments) {
                        String cityRef = monument.getCityRef();
                        if (cityRef.equals(cityId)) {
                            monumentsFiltered.add(monument);
                            String monumentId = monument.getId();
                            // filtrue punkty obserwacji zabytków miast wybranego kraju
                            for (ObservationPoint observationPoint : observationPoints) {
                                String monumentRef = observationPoint.getMonumentRef();
                                if (monumentRef.equals(monumentId)) {
                                    observationPointsFilteredCountry.add(observationPoint);
                                }
                            }
                        }

                    }
                }
            }

            // wysyła odfiltrowane dane do widoku miast
            Intent i = new Intent(getApplicationContext(),
                    CityActivity.class);
            i.putExtra("country_id", countryId);
            i.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
            i.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
            i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
            i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
            i.putParcelableArrayListExtra("observationPointsFiltered", (ArrayList<? extends Parcelable>) observationPointsFilteredCountry);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        countries = getIntent().getParcelableArrayListExtra("countries");
        cities = getIntent().getParcelableArrayListExtra("cities");
        monuments = getIntent().getParcelableArrayListExtra("monuments");
        observationPoints = getIntent().getParcelableArrayListExtra("observationPoints");

        citiesFiltered = new ArrayList<>();
        monumentsFiltered = new ArrayList<>();
        observationPointsFilteredCountry = new ArrayList<>();

        countryItems = new ArrayList<>();

        // Pobiera rozmiar ekranu urzadzenia
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        gridView = findViewById(R.id.gridview);
        TextView gridTitle = findViewById(R.id.textOdkryteZabytki);
        gridTitle.setText(R.string.kraje);
        ViewGroup.LayoutParams paramsTitle = gridTitle.getLayoutParams();
        paramsTitle.width = screenWidth * 3 / 4;
        gridTitle.setLayoutParams(paramsTitle);

        countryIds = new ArrayList<>();
        addGridItems();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
        intent.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
        intent.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
        intent.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        this.finish();
    }

    private void addGridItems() {
        for (Country country : countries) {
            String id = country.getId();
            String name = country.getName();
            String image = country.getImage();
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
            GridItem countryItem = new GridItem(id, name, image, isActive);
            countryItems.add(countryItem);
            countryIds.add(id);
        }

        ItemGridAdapter countryAdapter = new ItemGridAdapter(CountryActivity.this, true, countryItems);
        gridView.setAdapter(countryAdapter);
        gridView.setOnItemClickListener(gridViewOnItemClickListener);
    }

}
