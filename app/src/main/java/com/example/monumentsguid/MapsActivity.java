package com.example.monumentsguid;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;

    private Context mContext;

    // rozmiar ekranu urzadzenia
    int screenHeight;
    int screenWidth;

    // elementy
    private FrameLayout layoutMapa;
    private Button btnInfo;
    private Button btnWybierz;
    private Button btnMenu;
    private PopupWindow mPopupWindow;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Polyline mPolyline;
    private LatLng mOrigin;
    private LatLng mDestination;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Sydney, Australia) and default zoom to use when location permission is not granted.
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Połaczenie się z BD
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Clustery
    private ClusterManager<ClusterItem> mClusterManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the application context
        mContext = getApplicationContext();

        // Pobiera rozmiar ekranu urzadzenia
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;

        // Definiuje wszystkie obiekty
        setContentView(R.layout.activity_maps);

        layoutMapa = findViewById(R.id.mapa);

        btnInfo = findViewById(R.id.btn_info);
        btnWybierz = findViewById(R.id.btn_wybierz);
        btnMenu = findViewById(R.id.btn_menu);

        // Retrieve location from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }


        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);

        // Ustawienia lokalizacji urzadzenia
        getLocationPermission();
        updateLocationUI();
        mOrigin = getMyLocation();
        if (mOrigin != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mOrigin, DEFAULT_ZOOM));
        }

        // Reaguje na klikniecie na mape
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if (mDestination == null) {
                    btnInfo.setVisibility(View.INVISIBLE);
                    btnWybierz.setVisibility(View.INVISIBLE);
                } else {
                    btnWybierz.setText(R.string.pokaz);
                    btnWybierz.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mOrigin != null) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDestination, DEFAULT_ZOOM));
                            }
                        }
                    });
                }


            }
        });

        // Definiuje przycisk Menu
        ViewGroup.LayoutParams paramsMenu = btnMenu.getLayoutParams();
        paramsMenu.width = screenWidth / 3;
        btnMenu.setLayoutParams(paramsMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MapsActivity.this, "Menu", Toast.LENGTH_LONG).show();
            }
        });


        // Pozwala na uzywanie clusterow (liczy ile obiektow jest, a nie wyswietla wszystkie pinezki)
        mClusterManager = new ClusterManager<>(this, mMap);
        mClusterManager.setRenderer(new MarkerClusterRenderer(this, mMap, mClusterManager));
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        addClusterItems();
        mClusterManager.cluster();

        // Buduje okno informacyjne
        CustomInfoWindowGoogleMap customInfoWindowGoogleMap = new CustomInfoWindowGoogleMap(getApplicationContext());
        mMap.setInfoWindowAdapter(customInfoWindowGoogleMap);
    }

    /**
     * Dodaje markery zabytkow na mape (pobiera z bazy)
     */
    private void addClusterItems() {
        db.collection("observation_point")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                final String title = document.getString("name");
                                final String comment = document.getString("comment");
                                final String image = document.getString("image_ref");
                                final String description = document.getString("description");
                                final double lat = Objects.requireNonNull(document.getGeoPoint("lat_lng")).getLatitude();
                                final double lng = Objects.requireNonNull(document.getGeoPoint("lat_lng")).getLongitude();
                                mClusterManager.addItem(new ClusterItem(lat, lng, title, comment, description));
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                mClusterManager.setOnClusterItemInfoWindowClickListener(
                                        new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterItem>() {
                                            @Override
                                            public void onClusterItemInfoWindowClick(ClusterItem ClusterItem) {
                                                // Ustawienia przyciskow
                                                // Wstawia wartosc prycisku Info - pokazuje pzycisk
                                                btnInfo.setText(R.string.info);
                                                final ViewGroup.LayoutParams paramsInfo = btnInfo.getLayoutParams();
                                                paramsInfo.width = screenWidth / 4;
                                                btnInfo.setLayoutParams(paramsInfo);
                                                // Set a click listener for the text view
                                                btnInfo.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        // Initialize a new instance of LayoutInflater service
                                                        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                                                        // Inflate the custom layout/view
                                                        View customView = null;
                                                        if (inflater != null) {
                                                            customView = inflater.inflate(R.layout.popup_info, null);
                                                        }

                                                        // Initialize a new instance of popup window
                                                        int popupWidth = screenWidth * 9 / 10;
                                                        int popupHeight = screenHeight * 2 / 3;
                                                        mPopupWindow = new PopupWindow(customView, popupWidth, popupHeight);

                                                        // Set an elevation value for popup window
                                                        mPopupWindow.setElevation(5.0f);
                                                        // Get a reference for the custom view button
                                                        Button btnClose = null;
                                                        TextView infoTitle = null;
                                                        ImageView infoImage = null;
                                                        TextView infoDescription = null;
                                                        LinearLayout imageLayout = null;
                                                        if (customView != null) {
                                                            infoTitle = customView.findViewById(R.id.info_text_title);
                                                            infoImage = customView.findViewById(R.id.info_image);
                                                            infoDescription = customView.findViewById(R.id.info_text_description);
                                                            btnClose = customView.findViewById(R.id.close);
                                                            imageLayout = customView.findViewById(R.id.info_image_layout);
                                                        }
                                                        if (btnClose != null && infoTitle != null && infoImage != null && infoDescription != null) {

                                                            infoTitle.setText(title);

                                                            ViewGroup.LayoutParams params = imageLayout.getLayoutParams();
                                                            params.height = screenHeight / 3;
                                                            imageLayout.setLayoutParams(params);

                                                            infoImage.setMaxWidth(screenWidth / 3);
                                                            infoImage.setMinimumWidth(screenWidth / 5);
                                                            if (image != null) {
                                                                // show The Image in a ImageView
                                                                new DownloadImageTask(infoImage).execute(image);
                                                            }

                                                            infoDescription.setText(description);

                                                            btnClose.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    // Dismiss the popup window
                                                                    mPopupWindow.dismiss();
                                                                    btnInfo.setEnabled(true);
                                                                    btnWybierz.setEnabled(true);
                                                                    btnMenu.setEnabled(true);
                                                                }
                                                            });
                                                        }

                                                        // Finally, show the popup window at the center location of root relative layout
                                                        mPopupWindow.showAtLocation(layoutMapa, Gravity.CENTER, 0, 0);
                                                        btnInfo.setEnabled(false);
                                                        btnWybierz.setEnabled(false);
                                                        btnMenu.setEnabled(false);
                                                    }
                                                });
                                                btnInfo.setVisibility(View.VISIBLE);

                                                // Wstawia wartosc prycisku Wybierz - pokazuje pzycisk
                                                btnWybierz.setText(R.string.wybierz);
                                                ViewGroup.LayoutParams paramsWybierz = btnWybierz.getLayoutParams();
                                                paramsWybierz.width = screenWidth / 4;
                                                btnWybierz.setLayoutParams(paramsWybierz);
                                                btnWybierz.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        getMyLocation();
                                                        mDestination = new LatLng(lat, lng);
                                                        if (mOrigin != null) {
                                                            Toast.makeText(MapsActivity.this, "Wybrano: " + mOrigin.latitude + ":" + mOrigin.longitude + " / " + mDestination.latitude + ";" + mDestination.longitude, Toast.LENGTH_LONG).show();
                                                            drawRoute();
                                                        }
                                                    }
                                                });
                                                btnWybierz.setVisibility(View.VISIBLE);

                                            }
                                        });
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        // If request is cancelled, the result arrays are empty.
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
        }
        updateLocationUI();
    }


    private LatLng getMyLocation() {
        // Getting LocationManager object from System Service LOCATION_SERVICE
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mOrigin = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOrigin, DEFAULT_ZOOM));
                if (mOrigin != null && mDestination != null) {
                    drawRoute();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        /*
         * Get the best and most recent location of the device, which may be null in rare cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, mLocationListener);

                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mOrigin = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                            }

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.animateCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
        }
        return mOrigin;
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {

        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.animateCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
        }
    }

    /**
     * Wywoluje metody do rysowania trasy
     */
    private void drawRoute() {

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(mOrigin, mDestination);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
    }

    /**
     * Buduje URL w celu uzyskania danych ze strony internetowej GoogleMaps API początku i konca trasy
     */
    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Key
        String key = "key=" + getString(R.string.google_maps_key);
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + key;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("downloadUrl", data);
            br.close();
        } catch (Exception e) {
            Log.d("Exception on download", e.toString());
        } finally {
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return data;
    }

    /**
     * A class to download data from Google Directions URL
     */
    private class DownloadTask extends AsyncTask<String, Void, String> {
        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {
            // For storing data from web service
            String data = "";
            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask", "DownloadTask : " + data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Directions in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());
            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(Objects.requireNonNull(point.get("lat")));
                    double lng = Double.parseDouble(Objects.requireNonNull(point.get("lng")));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null) {
                if (mPolyline != null) {
                    mPolyline.remove();
                }
                mPolyline = mMap.addPolyline(lineOptions);

            } else
                Toast.makeText(getApplicationContext(), "Nie znaleziono trasy.", Toast.LENGTH_LONG).show();
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", Objects.requireNonNull(e.getMessage()));
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
