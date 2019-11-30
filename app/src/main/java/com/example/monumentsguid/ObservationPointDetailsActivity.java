package com.example.monumentsguid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Objects;

public class ObservationPointDetailsActivity extends AppCompatActivity {

    // rozmiar ekranu urzadzenia
    int screenHeight;
    int screenWidth;

    private TextView monumentName;
    private TextView observationPointComment;
    private ImageView observationPointOldImage;
    private TextView observationPointOldYear;
    private ImageView observationPointNewImage;
    private TextView observationPointNewDate;
    private Button btnLeft;
    private Button btnMiddle;
    private Button btnRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_point_details);

        // Pobiera dane z poprzedniego widoka
        Intent intent = getIntent();
        final String id = Objects.requireNonNull(intent.getExtras()).getString("id");
        final String name = Objects.requireNonNull(intent.getExtras()).getString("name");
        final String comment = Objects.requireNonNull(intent.getExtras()).getString("comment");
        final String image = Objects.requireNonNull(intent.getExtras()).getString("image");
        final String year = Objects.requireNonNull(intent.getExtras()).getString("year");
        String customImagePath = Objects.requireNonNull(intent.getExtras()).getString("customImagePath");
        String customImageDate = Objects.requireNonNull(intent.getExtras()).getString("customImageDate");
        String mode = Objects.requireNonNull(intent.getExtras()).getString("mode");

        String oldImageYearText = getString(R.string.poczatek_data_stare_zdjecie) + " " + year;
        String newImageDateText = getString(R.string.poczatek_data_nowe_zdjecie) + " " + customImageDate;

        // Pobiera parametry ekranu urzadzenia
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;

        // Definiuje kontrolki
        monumentName = findViewById(R.id.monumentName);
        observationPointComment = findViewById(R.id.observationPointComment);
        observationPointOldImage = findViewById(R.id.observationPointOldImage);
        observationPointOldYear = findViewById(R.id.observationPointOldYear);
        observationPointNewImage = findViewById(R.id.observationPointNewImage);
        observationPointNewDate = findViewById(R.id.observationPointNewYear);
        btnLeft = findViewById(R.id.btn_left);
        btnMiddle = findViewById(R.id.btn_middle);
        btnRight = findViewById(R.id.btn_right);

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
                    //Intent intent = new Intent(view.getContext(), MainActivity.class);
                    //view.getContext().startActivity(intent);
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
                    i.putExtra("name", name);
                    i.putExtra("comment", comment);
                    i.putExtra("image", image);
                    i.putExtra("year", year);

                    startActivity(i);
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
                    //Intent intent = new Intent(view.getContext(), MainActivity.class);
                    //view.getContext().startActivity(intent);
                }
            });
            btnRight.setVisibility(View.VISIBLE);
        }
    }
}
