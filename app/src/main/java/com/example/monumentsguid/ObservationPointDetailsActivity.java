package com.example.monumentsguid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.monumentsguid.Entities.City;
import com.example.monumentsguid.Entities.Country;
import com.example.monumentsguid.Entities.Monument;
import com.example.monumentsguid.Entities.ObservationPoint;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ObservationPointDetailsActivity extends AppCompatActivity {

    // rozmiar ekranu urzadzenia
    int screenHeight;
    int screenWidth;

    private String id;
    private String country_ref;
    private String city_ref;
    private String monument_ref;
    private double lat;
    private double lng;
    private String mode;
    private String image;
    private String year;
    private String name;
    private String comment;
    private String description;
    private String customImagePath;
    private String customImageDate;
    private List<Country> countries;
    private List<City> cities;
    private List<Monument> monuments;
    private List<ObservationPoint> observationPoints;
    private List<ObservationPoint> observationPointsFilteredMonument;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_point_details);

        // Pobiera dane z poprzedniego widoka
        Intent intent = getIntent();
        id = Objects.requireNonNull(intent.getExtras()).getString("id");
        lat = Objects.requireNonNull(intent.getExtras()).getDouble("lat");
        lng = Objects.requireNonNull(intent.getExtras()).getDouble("lng");
        name = Objects.requireNonNull(intent.getExtras()).getString("name");
        comment = Objects.requireNonNull(intent.getExtras()).getString("comment");
        description = Objects.requireNonNull(intent.getExtras()).getString("description");
        image = Objects.requireNonNull(intent.getExtras()).getString("image");
        year = Objects.requireNonNull(intent.getExtras()).getString("year");
        customImagePath = Objects.requireNonNull(intent.getExtras()).getString("customImagePath");
        customImageDate = Objects.requireNonNull(intent.getExtras()).getString("customImageDate");
        mode = Objects.requireNonNull(intent.getExtras()).getString("mode");
        countries = getIntent().getParcelableArrayListExtra("countries");
        cities = getIntent().getParcelableArrayListExtra("cities");
        monuments = getIntent().getParcelableArrayListExtra("monuments");
        observationPoints = getIntent().getParcelableArrayListExtra("observationPoints");
        if (mode != null && mode.equals("fromMapsShowActivity")) {
            country_ref = Objects.requireNonNull(intent.getExtras()).getString("country_ref");
            city_ref = Objects.requireNonNull(intent.getExtras()).getString("city_ref");
            monument_ref = Objects.requireNonNull(intent.getExtras()).getString("monument_ref");
        }

        observationPointsFilteredMonument = new ArrayList<>();

        String oldImageYearText = getString(R.string.poczatek_data_stare_zdjecie) + " " + year;
        String newImageDateText = getString(R.string.poczatek_data_nowe_zdjecie) + " " + customImageDate;

        // Pobiera parametry ekranu urzadzenia
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;

        // Definiuje kontrolki
        TextView monumentName = findViewById(R.id.monumentName);
        TextView observationPointComment = findViewById(R.id.observationPointComment);
        ImageView observationPointOldImage = findViewById(R.id.observationPointOldImage);
        TextView observationPointOldYear = findViewById(R.id.observationPointOldYear);
        ImageView observationPointNewImage = findViewById(R.id.observationPointNewImage);
        TextView observationPointNewDate = findViewById(R.id.observationPointNewYear);
        Button btnLeft = findViewById(R.id.btn_left);
        Button btnMiddle = findViewById(R.id.btn_middle);
        Button btnRight = findViewById(R.id.btn_right);

        // Konfiguruje elementy
        monumentName.setText(Html.fromHtml(name));

        observationPointComment.setText(comment);

        ViewGroup.LayoutParams paramsOldImage = observationPointOldImage.getLayoutParams();
        paramsOldImage.height = screenHeight / 3;
        observationPointOldImage.setLayoutParams(paramsOldImage);
        if (image != null) {
            new MapsActivity.DownloadImageTask(observationPointOldImage).execute(image);
        }

        observationPointOldYear.setText(oldImageYearText);

        ViewGroup.LayoutParams paramsNewImage = observationPointNewImage.getLayoutParams();
        paramsNewImage.height = screenHeight / 3;
        observationPointNewImage.setLayoutParams(paramsNewImage);
        if (customImagePath != null) {
            File file = new File(customImagePath);
            if (file.exists()) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(customImagePath, bmOptions);
                observationPointNewImage.setImageBitmap(bitmap);
            }
        }

        observationPointNewDate.setText(newImageDateText);

        if (mode != null && mode.equals("fromMapsShowActivity")) {
            // Definiuje środkowy przycisk
            ViewGroup.LayoutParams paramsBtnMiddle = btnMiddle.getLayoutParams();
            paramsBtnMiddle.width = screenWidth / 4;
            btnMiddle.setLayoutParams(paramsBtnMiddle);
            btnMiddle.setText(R.string.por_wnaj);
            btnMiddle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), ImageComparatorActivity.class);
                    i.putExtra("id", id);
                    i.putExtra("lat", lat);
                    i.putExtra("lng", lng);
                    i.putExtra("name", name);
                    i.putExtra("comment", comment);
                    i.putExtra("description", description);
                    i.putExtra("image", image);
                    i.putExtra("year", year);
                    i.putExtra("customImagePath", customImagePath);
                    i.putExtra("customImageDate", customImageDate);
                    i.putExtra("mode", mode);
                    if (mode != null && mode.equals("fromMapsShowActivity")) {
                        i.putExtra("country_ref", country_ref);
                        i.putExtra("city_ref", city_ref);
                        i.putExtra("monument_ref", monument_ref);
                    }
                    i.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
                    i.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
                    i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
                    i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            btnMiddle.setVisibility(View.VISIBLE);
        } else if (mode != null && mode.equals("fromMapsActivity")) {
            // Definiuje lewy przycisk
            ViewGroup.LayoutParams paramsBtnLeft = btnLeft.getLayoutParams();
            paramsBtnLeft.width = screenWidth / 4;
            btnLeft.setLayoutParams(paramsBtnLeft);
            btnLeft.setText(R.string.nowe_zdjecie);
            btnLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), CapturePictureActivity.class);
                    i.putExtra("id", id);
                    i.putExtra("lat", lat);
                    i.putExtra("lng", lng);
                    i.putExtra("name", name);
                    i.putExtra("comment", comment);
                    i.putExtra("description", description);
                    i.putExtra("image", image);
                    i.putExtra("year", year);
                    i.putExtra("customImagePath", customImagePath);
                    i.putExtra("customImageDate", customImageDate);
                    i.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
                    i.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
                    i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
                    i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
                    i.putExtra("image_exists", true);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            btnLeft.setVisibility(View.VISIBLE);

            // Definiuje prawy przycisk
            ViewGroup.LayoutParams paramsBtnRight = btnRight.getLayoutParams();
            paramsBtnRight.width = screenWidth / 4;
            btnRight.setLayoutParams(paramsBtnRight);
            btnRight.setText(R.string.por_wnaj);
            btnRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), ImageComparatorActivity.class);
                    i.putExtra("id", id);
                    i.putExtra("lat", lat);
                    i.putExtra("lng", lng);
                    i.putExtra("name", name);
                    i.putExtra("comment", comment);
                    i.putExtra("description", description);
                    i.putExtra("image", image);
                    i.putExtra("year", year);
                    i.putExtra("customImagePath", customImagePath);
                    i.putExtra("customImageDate", customImageDate);
                    i.putExtra("mode", mode);
                    if (mode != null && mode.equals("fromMapsShowActivity")) {
                        i.putExtra("country_ref", country_ref);
                        i.putExtra("city_ref", city_ref);
                        i.putExtra("monument_ref", monument_ref);
                    }
                    i.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
                    i.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
                    i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
                    i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            btnRight.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mode.equals("fromMapsShowActivity")) {
            for (ObservationPoint observationPoint : observationPoints) {
                if (observationPoint.getMonumentRef().equals(monument_ref)) {
                    observationPointsFilteredMonument.add(observationPoint);
                }
            }
            Intent i = new Intent(getApplicationContext(), MapsShowActivity.class);
            i.putExtra("monument_id", monument_ref);
            i.putExtra("country_ref", country_ref);
            i.putExtra("city_ref", city_ref);
            i.putExtra("name", name);
            i.putExtra("lat", lat);
            i.putExtra("lng", lng);
            i.putExtra("description", description);
            i.putExtra("image", image);
            i.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
            i.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
            i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
            i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
            i.putParcelableArrayListExtra("observationPointsFiltered", (ArrayList<? extends Parcelable>) observationPointsFilteredMonument);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            this.finish();
        } else if (mode.equals("fromMapsActivity")) {
            for (ObservationPoint observationPoint : observationPoints) {
                if (observationPoint.getId().equals(id)) {
                    observationPoint.setCustomImagePath(customImagePath);
                    observationPoint.setCustomImageDate(customImageDate);
                }
            }
            Intent i = new Intent(this, MapsActivity.class);
            i.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
            i.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
            i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
            i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
            i.putExtra("lat", lat);
            i.putExtra("lng", lng);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            this.finish();
        }

    }
}
