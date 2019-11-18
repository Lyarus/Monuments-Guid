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

import com.example.monumentsguid.Entities.Monument;
import com.example.monumentsguid.Entities.ObservationPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MonumentActivity extends AppCompatActivity {

    private List<Monument> monuments;
    private List<ObservationPoint> observationPoints;

    private List<String> monumentNames;
    private List<String> monumentImages;
    private List<String> monumentIds;
    private List<Double> monumentLat;
    private List<Double> monumentLng;
    private List<String> monumentDescription;

    private String city_ref;

    private GridView gridView;
    private GridView.OnItemClickListener gridViewOnItemClickListener = new GridView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i = new Intent(getApplicationContext(),
                    MapsShowActivity.class);
            i.putExtra("name", monumentNames.get(position));
            i.putExtra("lat", monumentLat.get(position));
            i.putExtra("lng", monumentLng.get(position));
            i.putExtra("description", monumentDescription.get(position));
            i.putExtra("image", monumentImages.get(position));
            i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
            i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
            startActivity(i);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        monuments = getIntent().getParcelableArrayListExtra("monuments");
        observationPoints = getIntent().getParcelableArrayListExtra("observationPoints");

        // Pobiera rozmiar ekranu urzadzenia
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        gridView = findViewById(R.id.gridview);

        TextView gridTitle = findViewById(R.id.textOdkryteZabytki);
        gridTitle.setText(R.string.zabytki);
        ViewGroup.LayoutParams paramsTitle = gridTitle.getLayoutParams();
        paramsTitle.width = screenWidth * 3 / 4;
        gridTitle.setLayoutParams(paramsTitle);

        Intent intent = getIntent();
        city_ref = Objects.requireNonNull(intent.getExtras()).getString("id");
        monumentNames = new ArrayList<>();
        monumentImages = new ArrayList<>();
        monumentDescription = new ArrayList<>();
        monumentIds = new ArrayList<>();
        monumentLat = new ArrayList<>();
        monumentLng = new ArrayList<>();
        addGridItems();
    }

    private void addGridItems() {
        for (Monument monument : monuments) {
            if (monument.getCityRef().equals(city_ref)) {
                monumentIds.add(monument.getId());
                monumentNames.add(monument.getName());
                monumentImages.add(monument.getImage());
                monumentDescription.add(monument.getDescription());
                monumentLat.add(monument.getLatitude());
                monumentLng.add(monument.getLongitude());
            }
        }
        ItemGridAdapter monumentAdapter = new ItemGridAdapter(MonumentActivity.this, monumentIds, monumentNames, Collections.<String>emptyList(), false);
        gridView.setAdapter(monumentAdapter);
        gridView.setOnItemClickListener(gridViewOnItemClickListener);
    }
}
