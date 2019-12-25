package com.example.monumentsguid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.monumentsguid.Entities.City;
import com.example.monumentsguid.Entities.Country;
import com.example.monumentsguid.Entities.Image;
import com.example.monumentsguid.Entities.Monument;
import com.example.monumentsguid.Entities.ObservationPoint;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class LoadingActivity extends AppCompatActivity {

    public List<Country> countries;
    public List<City> cities;
    public List<Monument> monuments;
    public List<ObservationPoint> observationPoints;
    private List<Image> imageList;

    private ProgressBar spinner;

    // Połaczenie z BD
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        countries = new ArrayList<>();
        cities = new ArrayList<>();
        monuments = new ArrayList<>();
        observationPoints = new ArrayList<>();
        imageList = new ArrayList<>();

        // Pobiera rozmiar ekranu urzadzenia
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int imageHeight = screenHeight / 3;
        int spinnerSize = screenHeight / 4;

        // definiuje obrazek
        LinearLayout main_icon = findViewById(R.id.main_icon_layout);
        ViewGroup.LayoutParams paramsMainIcon = main_icon.getLayoutParams();
        paramsMainIcon.height = imageHeight;
        main_icon.setLayoutParams(paramsMainIcon);

        // definiuje spinner
        spinner = findViewById(R.id.progressBar);
        ViewGroup.LayoutParams paramsSpinner = spinner.getLayoutParams();
        paramsSpinner.height = spinnerSize;
        paramsSpinner.width = spinnerSize;
        spinner.setVisibility(View.VISIBLE);

        // pobiera wszystkie zdjęcie znajdujące się w dyrektorii
        String storagePath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.example.monumentsguid/files/Pictures/";
        File file = new File(storagePath);
        if (file.exists() && file.isDirectory()) {
            walkdir(file);
        }

        // dane są pobierane z BD w postaci Tasków
        Task getCountryData = db.collection("country")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                String id = document.getId();
                                String name = document.getString("name");
                                String image = document.getString("image");
                                Country country = new Country(id, name, image);
                                country.setId(id);
                                country.setName(name);
                                country.setImage(image);
                                countries.add(country);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }

                        }
                    }
                });
        Task getCityData = db.collection("city")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                String id = document.getId();
                                String name = document.getString("name");
                                String image = document.getString("image");
                                String countryRef = document.getString("country_ref");
                                City city = new City(id, name, image, countryRef);
                                city.setId(id);
                                city.setName(name);
                                city.setImage(image);
                                city.setCountryRef(countryRef);
                                cities.add(city);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        }
                    }
                });
        Task getMonumentData = db.collection("monument")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                String id = document.getId();
                                String name = document.getString("name");
                                String image = document.getString("image");
                                String description = document.getString("description");
                                double lat = Objects.requireNonNull(document.getGeoPoint("lat_lng")).getLatitude();
                                double lng = Objects.requireNonNull(document.getGeoPoint("lat_lng")).getLongitude();
                                String cityRef = document.getString("city_ref");
                                Monument monument = new Monument(id, name, image, lat, lng, cityRef, description);
                                monument.setId(id);
                                monument.setName(name);
                                monument.setImage(image);
                                monument.setDescription(description);
                                monument.setLatitude(lat);
                                monument.setLongitude(lng);
                                monument.setCityRef(cityRef);
                                monuments.add(monument);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        }
                    }
                });
        Task getObservationPointData = db.collection("observation_point")
                .whereEqualTo("is_active", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                String id = document.getId();
                                String comment = document.getString("comment");
                                String image = document.getString("image");
                                double lat = Objects.requireNonNull(document.getGeoPoint("lat_lng")).getLatitude();
                                double lng = Objects.requireNonNull(document.getGeoPoint("lat_lng")).getLongitude();
                                String year = document.getString("year");
                                boolean isHorizontal = document.getBoolean("is_horizontal");
                                String monumentRef = document.getString("monument_ref");
                                Image customImage = getImage(id);
                                String customImagePath = null;
                                String customImageDate = null;
                                if (customImage != null) {
                                    customImagePath = customImage.getPath();
                                    customImageDate = getSimpleDate(customImage.getDate());
                                }
                                ObservationPoint observationPoint = new ObservationPoint(id, comment, image, lat, lng, year, isHorizontal, monumentRef, customImagePath, customImageDate);
                                observationPoint.setId(id);
                                observationPoint.setComment(comment);
                                observationPoint.setImage(image);
                                observationPoint.setLatitude(lat);
                                observationPoint.setLongitude(lng);
                                observationPoint.setYear(year);
                                observationPoint.setMonumentRef(monumentRef);
                                observationPoint.setCustomImagePath(customImagePath);
                                observationPoint.setCustomImageDate(customImageDate);
                                observationPoints.add(observationPoint);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        }
                    }
                });

        // kiedy wszystkie taski zostana wykonane
        Tasks.whenAllSuccess(getCountryData, getCityData, getMonumentData, getObservationPointData)
                .addOnSuccessListener(new OnSuccessListener<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> list) {
                        //Do what you need to do with your list
                        spinner.setVisibility(View.GONE);
                        startMainActivity();
                    }
                });
    }

    /**
     * Metoda włączająca Main activity
     */
    private void startMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        i.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
        i.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
        i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
        i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    /**
     * Metoda wyciągająca samą datę z obiektu typu Date w celu prezentacji w aplikacji
     */
    private String getSimpleDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return sdf.format(date);
    }

    /**
     * Metoda porównująca nazwę pliku z id, wyszukuje najnowszą wersję i zwraca
     */
    private Image getImage(String id) {
        Image imageName = null;
        Date tempDate = new Date(0L);
        for (Image image : imageList) {
            if (image != null) {
                if (image.getPath().contains("_" + id + "_") && image.getDate().after(tempDate)
                ) {
                    tempDate = image.getDate();
                    imageName = image;
                }
            }
        }
        return imageName;
    }

    /**
     * Metoda zapisująca do listy wszystkie pliki podanej dyrektorii
     */
    public void walkdir(File dir) {
        File[] listFile = dir.listFiles();
        if (listFile != null) {
            for (File file : listFile) {
                if (file.isDirectory()) {// if its a directory need to get the files under that directory
                    walkdir(file);
                } else {
                    String path = file.getPath();
                    Date date = new Date(file.lastModified());
                    Image image = new Image(path, date);
                    imageList.add(image);
                }
            }

        }
    }


}
