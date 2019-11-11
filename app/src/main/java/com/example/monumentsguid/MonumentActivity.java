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


public class MonumentActivity extends AppCompatActivity {

    // Połaczenie z BD
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private List<String> monumentNames;
    private List<String> monumentImages;
    private List<String> monumentIds;
    private String name;
    private String id;
    private String city_ref;

    private GridView gridView;
    private ItemGridAdapter monumentAdapter;
    private GridView.OnItemClickListener gridViewOnItemClickListener = new GridView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent i = new Intent(getApplicationContext(),
                    CityActivity.class);
            i.putExtra("id", monumentIds.get(position));
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
        gridTitle.setText(R.string.zabytki);
        ViewGroup.LayoutParams paramsTitle = gridTitle.getLayoutParams();
        paramsTitle.width = screenWidth * 3 / 4;
        gridTitle.setLayoutParams(paramsTitle);

        Intent intent = getIntent();
        city_ref = Objects.requireNonNull(intent.getExtras()).getString("id");
        monumentNames = new ArrayList<>();
        monumentImages = new ArrayList<>();
        monumentIds = new ArrayList<>();
        addGridItems();
    }

    private void addGridItems() {
        // pobieramy dane z bazy, tworzymy widoki krajów
        db.collection("monument")
                .whereEqualTo("city_ref", city_ref)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                name = document.getString("name");
                                id = document.getId();
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                monumentNames.add(name);
                                monumentImages.add("");
                                monumentIds.add(id);
                            }
                            monumentAdapter = new ItemGridAdapter(MonumentActivity.this, monumentIds, monumentNames, monumentImages);
                            gridView.setAdapter(monumentAdapter);
                            //gridView.setOnItemClickListener(gridViewOnItemClickListener);
                        }
                    }
                });
    }

}
