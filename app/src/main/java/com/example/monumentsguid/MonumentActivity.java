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
import com.example.monumentsguid.HelpClasses.GridItem;
import com.example.monumentsguid.HelpClasses.ItemGridAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MonumentActivity extends AppCompatActivity {

    private List<Country> countries;
    private List<City> cities;
    private List<Monument> monuments;
    private List<ObservationPoint> observationPoints;

    private List<ObservationPoint> observationPointsFiltered;
    private List<ObservationPoint> observationPointsFilteredMonument;
    private List<ObservationPoint> observationPointsFilteredCountry;
    private List<GridItem> monumentItems;

    private List<String> monumentNames;
    private List<String> monumentImages;
    private List<String> monumentIds;
    private List<Double> monumentLat;
    private List<Double> monumentLng;
    private List<String> monumentDescription;

    private String city_ref;
    private String country_ref;

    private GridView gridView;
    private GridView.OnItemClickListener gridViewOnItemClickListener = new GridView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            String monumentId = monumentIds.get(position);
            // filtrue punkty obserwacji wybranego zabytku
            for (ObservationPoint observationPoint : observationPointsFiltered) {
                String monumentRef = observationPoint.getMonumentRef();
                if (monumentRef.equals(monumentId)) {
                    observationPointsFilteredMonument.add(observationPoint);
                }
            }

            // wysyła odfiltrowane dane do widoku mapy
            Intent i = new Intent(getApplicationContext(), MapsShowActivity.class);
            i.putExtra("monument_id", monumentId);
            i.putExtra("country_ref", country_ref);
            i.putExtra("city_ref", city_ref);
            i.putExtra("name", monumentNames.get(position));
            i.putExtra("lat", monumentLat.get(position));
            i.putExtra("lng", monumentLng.get(position));
            i.putExtra("description", monumentDescription.get(position));
            i.putExtra("image", monumentImages.get(position));
            i.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
            i.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
            i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
            i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
            i.putParcelableArrayListExtra("observationPointsFiltered", (ArrayList<? extends Parcelable>) observationPointsFilteredMonument);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        Intent intent = getIntent();
        country_ref = Objects.requireNonNull(intent.getExtras()).getString("country_ref");
        city_ref = Objects.requireNonNull(intent.getExtras()).getString("city_id");
        countries = intent.getParcelableArrayListExtra("countries");
        cities = intent.getParcelableArrayListExtra("cities");
        monuments = intent.getParcelableArrayListExtra("monuments");
        observationPoints = intent.getParcelableArrayListExtra("observationPoints");
        observationPointsFiltered = intent.getParcelableArrayListExtra("observationPointsFiltered");

        observationPointsFilteredMonument = new ArrayList<>();
        observationPointsFilteredCountry = new ArrayList<>();
        monumentItems = new ArrayList<>();

        // Pobiera rozmiar ekranu urzadzenia
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        gridView = findViewById(R.id.gridview);

        TextView gridTitle = findViewById(R.id.textOdkryteZabytki);
        gridTitle.setText(R.string.zabytki);
        ViewGroup.LayoutParams paramsTitle = gridTitle.getLayoutParams();
        paramsTitle.width = screenWidth * 3 / 4;
        gridTitle.setLayoutParams(paramsTitle);


        monumentNames = new ArrayList<>();
        monumentImages = new ArrayList<>();
        monumentDescription = new ArrayList<>();
        monumentIds = new ArrayList<>();
        monumentLat = new ArrayList<>();
        monumentLng = new ArrayList<>();
        addGridItems();
    }

    @Override
    public void onBackPressed() {
        for (City city : cities) {
            if (city.getCountryRef().equals(country_ref)) {
                String city_id = city.getId();
                for (Monument monument : monuments) {
                    if (monument.getCityRef().equals(city_id)) {
                        String monument_id = monument.getId();
                        for (ObservationPoint observationPoint : observationPoints) {
                            if (observationPoint.getMonumentRef().equals(monument_id)) {
                                observationPointsFilteredCountry.add(observationPoint);
                            }
                        }
                    }
                }
            }
        }
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(),
                CityActivity.class);
        i.putExtra("country_id", country_ref);
        i.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
        i.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
        i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
        i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
        i.putParcelableArrayListExtra("observationPointsFiltered", (ArrayList<? extends Parcelable>) observationPointsFilteredCountry);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        startActivity(i);
        this.finish();
    }

    private void addGridItems() {
        for (Monument monument : monuments) {
            if (monument.getCityRef().equals(city_ref)) {
                String id = monument.getId();
                String name = monument.getName();
                String image = monument.getImage();
                boolean isActive = false;

                // jeżeli w pamięci urządzenia są obrazki z punktów obserwacji danego zabytku - ustawiamy isActive na true
                if (observationPointsFiltered != null) {
                    for (ObservationPoint observationPoint : observationPointsFiltered) {
                        if (observationPoint.getMonumentRef().equals(id)) {
                            String imagePath = observationPoint.getCustomImagePath();
                            if (imagePath != null) {
                                isActive = true;
                                break;
                            }
                        }
                    }
                }
                GridItem monumentItem = new GridItem(id, name, image, isActive);
                monumentItems.add(monumentItem);
                monumentIds.add(id);
                monumentNames.add(name);
                monumentImages.add(image);
                monumentDescription.add(monument.getDescription());
                monumentLat.add(monument.getLatitude());
                monumentLng.add(monument.getLongitude());
            }
        }

        ItemGridAdapter monumentAdapter = new ItemGridAdapter(MonumentActivity.this, false, monumentItems);
        gridView.setAdapter(monumentAdapter);
        gridView.setOnItemClickListener(gridViewOnItemClickListener);
    }
}
