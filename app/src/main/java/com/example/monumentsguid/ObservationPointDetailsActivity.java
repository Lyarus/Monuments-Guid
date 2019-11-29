package com.example.monumentsguid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
    private Button btnPorownaj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_point_details);

        // Pobiera dane z poprzedniego widoka
        Intent intent = getIntent();
        String name = Objects.requireNonNull(intent.getExtras()).getString("name");
        String comment = Objects.requireNonNull(intent.getExtras()).getString("comment");
        String image = Objects.requireNonNull(intent.getExtras()).getString("image");
        String year = Objects.requireNonNull(intent.getExtras()).getString("year");
        String customImagePath = Objects.requireNonNull(intent.getExtras()).getString("customImagePath");
        String customImageDate = Objects.requireNonNull(intent.getExtras()).getString("customImageDate");

        String oldImageYearText = "Zdjęcie z lat " + year;
        String newImageDateText = "Zdjęcie z dnia " + customImageDate;

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
        btnPorownaj = findViewById(R.id.porownaj);

        // Konfiguruje elementy
        monumentName.setText(name);

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

        ViewGroup.LayoutParams paramsPorownaj = btnPorownaj.getLayoutParams();
        paramsPorownaj.width = screenWidth / 4;
        btnPorownaj.setLayoutParams(paramsPorownaj);
        btnPorownaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(view.getContext(), MainActivity.class);
                //view.getContext().startActivity(intent);
            }
        });

    }
}
