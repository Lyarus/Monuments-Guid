package com.example.monumentsguid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.monumentsguid.Entities.City;
import com.example.monumentsguid.Entities.Country;
import com.example.monumentsguid.Entities.Monument;
import com.example.monumentsguid.Entities.ObservationPoint;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class CountryActivity extends AppCompatActivity {
    // Połaczenie z BD
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private List<Country> countries;
    private List<City> cities;
    private List<Monument> monuments;
    private List<ObservationPoint> observationPoints;

    private List<String> countryNames;
    private List<String> countryImages;
    private List<String> countryIds;
    private String name;
    private String image;
    private String id;

    private GridView gridView;
    private ItemGridAdapter countryAdapter;
    private GridView.OnItemClickListener gridViewOnItemClickListener = new GridView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i = new Intent(getApplicationContext(),
                    CityActivity.class);
            i.putExtra("id", countryIds.get(position));
            startActivity(i);
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

        // Pobiera rozmiar ekranu urzadzenia
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        gridView = findViewById(R.id.gridview);
        TextView gridTitle = findViewById(R.id.textOdkryteZabytki);
        gridTitle.setText(R.string.kraje);
        ViewGroup.LayoutParams paramsTitle = gridTitle.getLayoutParams();
        paramsTitle.width = screenWidth * 3 / 4;
        gridTitle.setLayoutParams(paramsTitle);

        countryNames = new ArrayList<>();
        countryImages = new ArrayList<>();
        countryIds = new ArrayList<>();
        addGridItems();
    }

    private void addGridItems() {
        // pobieramy dane z bazy, tworzymy widoki krajów
        db.collection("country")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                name = document.getString("name");
                                image = document.getString("image");
                                id = document.getId();
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                countryNames.add(name);
                                countryImages.add(image);
                                countryIds.add(id);
                            }
                            countryAdapter = new ItemGridAdapter(CountryActivity.this, countryIds, countryNames, countryImages);
                            gridView.setAdapter(countryAdapter);
                            gridView.setOnItemClickListener(gridViewOnItemClickListener);
                        }
                    }
                });
    }
}
