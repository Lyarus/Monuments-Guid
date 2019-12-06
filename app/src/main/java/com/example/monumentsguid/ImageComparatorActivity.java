package com.example.monumentsguid;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.monumentsguid.Entities.City;
import com.example.monumentsguid.Entities.Country;
import com.example.monumentsguid.Entities.Monument;
import com.example.monumentsguid.Entities.ObservationPoint;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImageComparatorActivity extends Activity {

    private String id;
    private String country_ref;
    private String city_ref;
    private String monument_ref;
    private double lat;
    private double lng;
    private String mode;
    private String image;
    private boolean isHorizontal;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_comparator);

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
        country_ref = Objects.requireNonNull(intent.getExtras()).getString("country_ref");
        city_ref = Objects.requireNonNull(intent.getExtras()).getString("city_ref");
        monument_ref = Objects.requireNonNull(intent.getExtras()).getString("monument_ref");


        for (ObservationPoint observationPoint : observationPoints) {
            if (observationPoint.getId().equals(id)) {
                isHorizontal = observationPoint.isHorizontal();
                break;
            }
        }

        if (isHorizontal) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        ImageView oldPicture = findViewById(R.id.oldPicture);
        if (image != null) {
            new MapsActivity.DownloadImageTask(oldPicture).execute(image);
        }

        ImageView newPicture = findViewById(R.id.newPicture);
        if (customImagePath != null) {
            File file = new File(customImagePath);
            if (file.exists()) {
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(customImagePath, bmOptions);
                newPicture.setImageBitmap(bitmap);
            }
        }
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.tween);
        newPicture.startAnimation(myFadeInAnimation);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(),
                ObservationPointDetailsActivity.class);
        i.putExtra("id", id);
        i.putExtra("country_ref", country_ref);
        i.putExtra("city_ref", city_ref);
        i.putExtra("monument_ref", monument_ref);
        i.putExtra("comment", comment);
        i.putExtra("name", name);
        i.putExtra("description", description);
        i.putExtra("lat", lat);
        i.putExtra("lng", lng);
        i.putExtra("year", year);
        i.putExtra("image", image);
        i.putExtra("customImagePath", customImagePath);
        i.putExtra("customImageDate", customImageDate);
        i.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
        i.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
        i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
        i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
        i.putExtra("mode", mode);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        this.finish();
    }
}
