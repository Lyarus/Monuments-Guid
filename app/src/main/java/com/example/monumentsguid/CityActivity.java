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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class CityActivity extends AppCompatActivity {

    // Połaczenie z BD
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private List<String> cityNames;
    private List<String> cityImages;
    private List<String> cityIds;
    private String name;
    private String image;
    private String id;
    private String country_ref;

    private GridView gridView;
    private ItemGridAdapter cityAdapter;
    private GridView.OnItemClickListener gridViewOnItemClickListener = new GridView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i = new Intent(getApplicationContext(),
                    MonumentActivity.class);
            i.putExtra("id", cityIds.get(position));
            startActivity(i);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

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
        // pobieramy dane z bazy, tworzymy widoki krajów
        db.collection("city")
                .whereEqualTo("country_ref", country_ref)
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
                                cityNames.add(name);
                                cityImages.add(image);
                                cityIds.add(id);
                            }
                            cityAdapter = new ItemGridAdapter(CityActivity.this, cityIds, cityNames, cityImages);
                            gridView.setAdapter(cityAdapter);
                            gridView.setOnItemClickListener(gridViewOnItemClickListener);
                        }
                    }
                });
    }

}
