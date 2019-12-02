package com.example.monumentsguid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.monumentsguid.Entities.City;
import com.example.monumentsguid.Entities.Country;
import com.example.monumentsguid.Entities.Image;
import com.example.monumentsguid.Entities.Monument;
import com.example.monumentsguid.Entities.ObservationPoint;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class MainActivity extends AppCompatActivity {
    public long back_pressed;

    public List<Country> countries;
    public List<City> cities;
    public List<Monument> monuments;
    public List<ObservationPoint> observationPoints;
    private List<Image> imageList;
    // Połaczenie z BD
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        countries = new ArrayList<>();
        cities = new ArrayList<>();
        monuments = new ArrayList<>();
        observationPoints = new ArrayList<>();
        imageList = new ArrayList<>();

        String storagePath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.example.monumentsguid/files/Pictures/";
        File file = new File(storagePath);
        if (file.exists() && file.isDirectory()) {
            walkdir(file);
        }

        // Pobiera rozmiar ekranu urzadzenia
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int imageHeight = screenHeight / 3;
        int btnHeight = (screenHeight - imageHeight) / 5;
        int btnWidth = screenWidth * 2 / 3;

        // zapisuje pobrane dane do list obiektow
        getCountryDataFromDB();
        getCityDataFromDB();
        getMonumentDataFromDB();
        getObservationPointDataFromDB();

        LinearLayout main_icon = findViewById(R.id.main_icon_layout);
        ViewGroup.LayoutParams paramsMainIcon = main_icon.getLayoutParams();
        paramsMainIcon.height = imageHeight;
        main_icon.setLayoutParams(paramsMainIcon);

        Button zabytki = findViewById(R.id.btn_odkryte_zabytki);
        ViewGroup.LayoutParams paramsOdkryteZabytki = zabytki.getLayoutParams();
        paramsOdkryteZabytki.height = btnHeight;
        paramsOdkryteZabytki.width = btnWidth;
        zabytki.setLayoutParams(paramsOdkryteZabytki);
        zabytki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CountryActivity.class);
                intent.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
                intent.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
                intent.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
                intent.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
                view.getContext().startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        Button mapa = findViewById(R.id.btn_mapa);
        ViewGroup.LayoutParams paramsMapa = mapa.getLayoutParams();
        paramsMapa.height = btnHeight;
        paramsMapa.width = btnWidth;
        mapa.setLayoutParams(paramsMapa);
        mapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MapsActivity.class);
                intent.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
                intent.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
                view.getContext().startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        Button informacja = findViewById(R.id.btn_informacja);
        ViewGroup.LayoutParams paramsInformacja = informacja.getLayoutParams();
        paramsInformacja.height = btnHeight;
        paramsInformacja.width = btnWidth;
        informacja.setLayoutParams(paramsInformacja);
        informacja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), InfoActivity.class);
                view.getContext().startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    // wyłącza aplikację przy podwójnym kliknięciu
    @Override
    public void onBackPressed() {
        if (back_pressed + 1000 > System.currentTimeMillis()) {
            this.finish();
            System.exit(0);
        } else {
            String textMess = getString(R.string.double_click_on_exit_text);
            Toast.makeText(getBaseContext(), textMess, Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    // pobiera dane krajow z BD
    private void getCountryDataFromDB() {
        db.collection("country")
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
    }

    // pobiera dane miast z BD
    private void getCityDataFromDB() {
        db.collection("city")
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
    }

    // pobiera dane zabytkow z BD
    private void getMonumentDataFromDB() {
        db.collection("monument")
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

    }

    // pobiera dane punktow obserwacji z BD
    private void getObservationPointDataFromDB() {
        db.collection("observation_point")
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
                                boolean isHorizontal = document.getBoolean("isHorizontal");
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
