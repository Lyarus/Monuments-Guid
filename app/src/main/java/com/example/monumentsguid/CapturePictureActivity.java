package com.example.monumentsguid;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.example.monumentsguid.Entities.Monument;
import com.example.monumentsguid.Entities.ObservationPoint;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
    private String customImagePath;

    private String customImagePathExists;
    private String customImageDateExists;
    private List<Monument> monuments;
    private List<ObservationPoint> observationPoints;
    private String id;
    private double lat;
    private double lng;
    private String image;
    private String year;
    private String name;
    private String comment;
    private String description;
    private boolean image_exists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_observation_point_details);

        // Pobiera parametry ekranu urzadzenia
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;

        Intent intent = getIntent();
        id = Objects.requireNonNull(intent.getExtras()).getString("id");
        lat = Objects.requireNonNull(intent.getExtras()).getDouble("lat");
        lng = Objects.requireNonNull(intent.getExtras()).getDouble("lng");
        name = Objects.requireNonNull(intent.getExtras()).getString("name");
        comment = Objects.requireNonNull(intent.getExtras()).getString("comment");
        description = Objects.requireNonNull(intent.getExtras()).getString("description");
        image = Objects.requireNonNull(intent.getExtras()).getString("image");
        year = Objects.requireNonNull(intent.getExtras()).getString("year");
        monuments = intent.getParcelableArrayListExtra("monuments");
        observationPoints = intent.getParcelableArrayListExtra("observationPoints");
        image_exists = Objects.requireNonNull(intent.getExtras().getBoolean("image_exists"));
        if (image_exists) {
            customImagePathExists = Objects.requireNonNull(intent.getExtras()).getString("customImagePath");
            customImageDateExists = Objects.requireNonNull(intent.getExtras()).getString("customImageDate");
        }
        //check if app has permission to access the camera.
        if (EasyPermissions.hasPermissions(CapturePictureActivity.this, Manifest.permission.CAMERA)) {
            launchCamera();
        } else {
            //If permission is not present request for the same.
            EasyPermissions.requestPermissions(CapturePictureActivity.this, getString(R.string.permission_text), CAMERA_PERMISSION_CODE, Manifest.permission.CAMERA);
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
            String customImageDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date());
            File file = new File(customImagePath);
            if (file.length() != 0) {
                Intent i = new Intent(getApplicationContext(), ObservationPointDetailsActivity.class);
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
                i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
                i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
                i.putExtra("mode", "fromMapsActivity");
                startActivity(i);
            } else {
                boolean deleted = file.delete();
                Log.v("log_tag", "deleted: " + deleted);

                if (image_exists) {
                    Intent i = new Intent(this, ObservationPointDetailsActivity.class);
                    i.putExtra("id", id);
                    i.putExtra("lat", lat);
                    i.putExtra("lng", lng);
                    i.putExtra("name", name);
                    i.putExtra("comment", comment);
                    i.putExtra("description", description);
                    i.putExtra("image", image);
                    i.putExtra("year", year);
                    i.putExtra("customImagePath", customImagePathExists);
                    i.putExtra("customImageDate", customImageDateExists);
                    i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
                    i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
                    i.putExtra("mode", "fromMapsActivity");
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                } else {
                    Intent i = new Intent(this, MapsActivity.class);
                    i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
                    i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
                    i.putExtra("lat", lat);
                    i.putExtra("lng", lng);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                }

            }

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
