package com.example.monumentsguid;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import pub.devrel.easypermissions.EasyPermissions;

public class CapturePictureActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int CAMERA_REQUEST_CODE = 1450;
    private static final int CAMERA_PERMISSION_CODE = 1460;

    // rozmiar ekranu urzadzenia
    int screenHeight;
    int screenWidth;
    String textYear;
    private TextView monumentName;
    private TextView observationPointComment;
    private ImageView observationPointOldImage;
    private TextView observationPointOldYear;

    private String mCurrentPhotoPath;
    private String id;
    private ImageView observationPointNewImage;
    private TextView observationPointNewYear;
    private Button btnPorownaj;
    private String image;
    private String year;
    private String monument_name;
    private String comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_point_details);

        // Pobiera parametry ekranu urzadzenia
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;

        Intent intent = getIntent();
        id = Objects.requireNonNull(intent.getExtras()).getString("id");
        image = Objects.requireNonNull(intent.getExtras()).getString("image");
        monument_name = Objects.requireNonNull(intent.getExtras()).getString("monument_name");
        comment = Objects.requireNonNull(intent.getExtras()).getString("comment");
        String year = Objects.requireNonNull(intent.getExtras()).getString("year");
        textYear = "Zdjęcie z lat " + year;

        monumentName = findViewById(R.id.monumentName);
        observationPointComment = findViewById(R.id.observationPointComment);
        observationPointOldImage = findViewById(R.id.observationPointOldImage);
        observationPointOldYear = findViewById(R.id.observationPointOldYear);
        observationPointNewImage = findViewById(R.id.observationPointNewImage);
        observationPointNewYear = findViewById(R.id.observationPointNewYear);
        btnPorownaj = findViewById(R.id.porownaj);

        //check if app has permission to access the camera.
        if (EasyPermissions.hasPermissions(CapturePictureActivity.this, Manifest.permission.CAMERA)) {
            launchCamera();
        } else {
            //If permission is not present request for the same.
            EasyPermissions.requestPermissions(CapturePictureActivity.this, getString(R.string.permission_text), CAMERA_PERMISSION_CODE, Manifest.permission.CAMERA);
        }
    }

    /**
     * Launches the default camera application
     */
    private void launchCamera() {

        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // Create the File where the photo should go
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        // Continue only if the File was successfully created
        if (photoFile != null) {
            Uri photoURI = FileProvider.getUriForFile(this, "com.example.monumentsguid.fileprovider", photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            //Start the camera application
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    /**
     * Previews the captured picture on the app
     * Called when the picture is taken
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Preview the image captured by the camera
        if (requestCode == CAMERA_REQUEST_CODE) {
            ViewGroup.LayoutParams paramsNewImage = observationPointNewImage.getLayoutParams();
            paramsNewImage.height = screenHeight / 3;
            observationPointNewImage.setLayoutParams(paramsNewImage);
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            observationPointNewImage.setImageBitmap(bitmap);

            // wstawia tekst - data zdjecia
            String text = getString(R.string.poczatek_data_nowe_zdjecie) + " " + new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
            observationPointNewYear.setText(text);

            ViewGroup.LayoutParams paramsOldImage = observationPointOldImage.getLayoutParams();
            paramsOldImage.height = screenHeight / 3;
            observationPointOldImage.setLayoutParams(paramsOldImage);
            if (image != null) {
                new MapsActivity.DownloadImageTask(observationPointOldImage).execute(image);
            }

            observationPointOldYear.setText(textYear);

            monumentName.setText(monument_name);
            observationPointComment.setText(comment);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, CapturePictureActivity.this);
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d(TAG, "Permission has been denied");
    }


    /**
     * Creates the image file in the external directory
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "_" + id + "_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        launchCamera();
    }
}
