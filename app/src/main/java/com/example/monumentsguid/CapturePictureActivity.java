package com.example.monumentsguid;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

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
    String oldImageYearText;

    private String customImagePath;
    private String id;
    private String image;
    private String year;
    private String name;
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
        name = Objects.requireNonNull(intent.getExtras()).getString("name");
        comment = Objects.requireNonNull(intent.getExtras()).getString("comment");
        image = Objects.requireNonNull(intent.getExtras()).getString("image");
        year = Objects.requireNonNull(intent.getExtras()).getString("year");
        oldImageYearText = getString(R.string.poczatek_data_stare_zdjecie) + " " + year;

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
            String customImageDate = new SimpleDateFormat("dd.mm.yyyy", Locale.getDefault()).format(new Date());

            Intent i = new Intent(getApplicationContext(), ObservationPointDetailsActivity.class);
            i.putExtra("id", id);
            i.putExtra("comment", comment);
            i.putExtra("name", name);
            i.putExtra("year", year);
            i.putExtra("image", image);
            i.putExtra("customImagePath", customImagePath);
            i.putExtra("customImageDate", customImageDate);
            i.putExtra("mode", "fromMapsActivity");
            startActivity(i);
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

        customImagePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        launchCamera();
    }
}
