package com.example.monumentsguid;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
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

import com.example.monumentsguid.Entities.City;
import com.example.monumentsguid.Entities.Country;
import com.example.monumentsguid.Entities.Monument;
import com.example.monumentsguid.Entities.ObservationPoint;
import com.example.monumentsguid.HelpClasses.ClusterItem;
import com.example.monumentsguid.HelpClasses.CustomInfoWindowGoogleMap;
import com.example.monumentsguid.HelpClasses.DirectionsJSONParser;
import com.example.monumentsguid.HelpClasses.MarkerClusterRenderer;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.Cluster;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<ClusterItem> {

    private static final String TAG = MapsActivity.class.getSimpleName();
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int CLOSE_ZOOM = 17;
    private static final int FAR_ZOOM = 10;
    private static final int RADIUS = 10;
    private LatLng mDefaultLocation;

    private GoogleMap mMap;
    private Context mContext;

    private List<Country> countries;
    private List<City> cities;
    private List<Monument> monuments;
    private List<ObservationPoint> observationPoints;

    // rozmiar ekranu urzadzenia
    int screenHeight;
    int screenWidth;
    int screenOrientation;

    // elementy
    private FrameLayout layoutMapa;
    private Button btnMenu;
    private int btnMenuWidth;
    private int btnBottomWidth;
    private int imageLayoutHeight;
    private PopupWindow mPopupWindow;
    double latFromDetails;
    private boolean isCamera;
    private String curPopupMode;
    double lngFromDetails;
    private double curLat;
    private double curLng;
    private String curImage;
    private String curComment;
    private String curYear;
    private boolean curIsHorizontal;
    private String curName;
    private String curMonumentImage;
    private String curDescription;
    private String curCustomImagePath;
    private String curCustomImageDate;
    private Polyline mPolyline;
    private LatLng mOrigin;
    private LatLng mDestination;
    private LayoutInflater inflater;
    private int popupWidth;
    private int popupHeight;
    private boolean showPopupInfo;
    private String curId;

    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Clustery
    private ClusterManager<ClusterItem> mClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getLocationPermission();

        mDefaultLocation = new LatLng(51.098781, 17.036716);

        Intent intent = getIntent();
        latFromDetails = Objects.requireNonNull(intent.getExtras()).getDouble("lat");
        lngFromDetails = Objects.requireNonNull(intent.getExtras()).getDouble("lng");
        countries = getIntent().getParcelableArrayListExtra("countries");
        cities = getIntent().getParcelableArrayListExtra("cities");
        monuments = getIntent().getParcelableArrayListExtra("monuments");
        observationPoints = getIntent().getParcelableArrayListExtra("observationPoints");

        // Get the application context
        mContext = getApplicationContext();

        // Pobiera parametry ekranu urzadzenia
        screenHeight = getResources().getDisplayMetrics().heightPixels;
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenOrientation = getResources().getConfiguration().orientation;

        // Definiuje obiekty w zaleznosci od parametrow ekranu urzadzenia
        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            btnMenuWidth = screenHeight / 3;
            btnBottomWidth = screenHeight / 4;
            popupHeight = screenHeight * 9 / 10;
            popupWidth = screenWidth - 3 * btnBottomWidth;
            imageLayoutHeight = screenHeight / 2;
        } else if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            btnMenuWidth = screenWidth / 3;
            btnBottomWidth = screenWidth / 4;
            popupHeight = screenHeight * 2 / 3;
            popupWidth = screenWidth * 9 / 10;
            imageLayoutHeight = screenHeight / 3;
        }

        // Definiuje wszystkie obiekty
        layoutMapa = findViewById(R.id.mapa);
        btnMenu = findViewById(R.id.btn_menu);

        inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        showPopupInfo = false;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onBackPressed() {
        if (showPopupInfo) {
            mPopupWindow.dismiss();
            showPopupInfo = false;
            Button btnLeft = findViewById(R.id.btn_info);
            Button btnRight = findViewById(R.id.btn_trasa);
            Button btnMiddle = findViewById(R.id.btn_kamera);
            btnLeft.setEnabled(true);
            btnRight.setEnabled(true);
            btnMiddle.setEnabled(true);
            btnMenu.setEnabled(true);
        } else {
            super.onBackPressed();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
            intent.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
            intent.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
            intent.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            mMap.clear();
            mClusterManager.clearItems();
            this.finish();
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
     * Reaguje na zmiany rotacji ekranu
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (screenOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Ustawienia dla poziomej orientacji
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                popupHeight = screenHeight * 9 / 10;
                popupWidth = screenWidth - 3 * btnBottomWidth;
                imageLayoutHeight = screenHeight / 2;
            }
            // Ustawienia dla pionowej orientacji
            else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                popupHeight = screenWidth * 2 / 3;
                popupWidth = screenHeight * 9 / 10;
                imageLayoutHeight = screenWidth / 3;
            }
        } else if (screenOrientation == Configuration.ORIENTATION_PORTRAIT) {
            // Ustawienia dla poziomej orientacji
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                popupHeight = screenWidth * 9 / 10;
                popupWidth = screenHeight - 3 * btnBottomWidth;
                imageLayoutHeight = screenWidth / 2;
            }
            // Ustawienia dla pionowej orientacji
            else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                popupHeight = screenHeight * 2 / 3;
                popupWidth = screenWidth * 9 / 10;
                imageLayoutHeight = screenHeight / 3;
            }
        }

        // Jezeli przy obróceniu ekrana popup był widoczny
        if (showPopupInfo) {
            // Zeruje poprzedni popup
            View customView;
            mPopupWindow.dismiss();
            //Tworzy nowy
            Button btnLeft = findViewById(R.id.btn_info);
            Button btnRight = findViewById(R.id.btn_trasa);
            Button btnCenter = findViewById(R.id.btn_kamera);
            if (inflater != null) {
                customView = inflater.inflate(R.layout.popup_info, null);
                createBottomBtns(btnLeft, btnCenter, btnRight, curLat, curLng, curName, curMonumentImage, curDescription, curCustomImagePath, curCustomImageDate, customView, isCamera);
                setPopupWindowContent(curPopupMode, customView, popupWidth, popupHeight, curName, curMonumentImage, curDescription, btnLeft, btnCenter, btnRight);
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        // Ustawienia lokalizacji urzadzenia
        updateLocationUI();
        if (latFromDetails != 0.0 && lngFromDetails != 0.0) {
            getMyLocation(FAR_ZOOM, false);
            mDefaultLocation = new LatLng(latFromDetails, lngFromDetails);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, CLOSE_ZOOM));
        } else {
            getMyLocation(FAR_ZOOM, true);
        }

        // Pozwala na uzywanie clusterow (liczy ile obiektow jest, a nie wyswietla wszystkie pinezki)
        mClusterManager = new ClusterManager<>(this, mMap);
        mClusterManager.setRenderer(new MarkerClusterRenderer(this, mMap, mClusterManager));
        mClusterManager.setOnClusterClickListener(this);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        addClusterItems();
        mClusterManager.cluster();

        // Reaguje na klikniecie na mape
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                if (mDestination == null) {
                    Button btnLeft = findViewById(R.id.btn_info);
                    btnLeft.setVisibility(View.INVISIBLE);
                    Button btnRight = findViewById(R.id.btn_trasa);
                    btnRight.setVisibility(View.INVISIBLE);
                    Button btnCenter = findViewById(R.id.btn_kamera);
                    btnCenter.setVisibility(View.INVISIBLE);
                } else {
                    Button btnRight = findViewById(R.id.btn_trasa);
                    btnRight.setText(R.string.pokaz);
                    ViewGroup.LayoutParams paramsTrasa = btnRight.getLayoutParams();
                    paramsTrasa.width = btnBottomWidth;
                    btnRight.setLayoutParams(paramsTrasa);
                    btnRight.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rounded_corners_button_darkblue));
                    btnRight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mDestination != null) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDestination, CLOSE_ZOOM));
                            }
                        }
                    });
                    btnRight.setVisibility(View.VISIBLE);
                }
            }
        });

        // Definiuje przycisk Menu
        ViewGroup.LayoutParams paramsMenu = btnMenu.getLayoutParams();
        paramsMenu.width = btnMenuWidth;
        btnMenu.setLayoutParams(paramsMenu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
                intent.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
                intent.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
                intent.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                mMap.clear();
                mClusterManager.clearItems();
            }
        });

        // Buduje okno informacyjne
        CustomInfoWindowGoogleMap customInfoWindowGoogleMap = new CustomInfoWindowGoogleMap(getApplicationContext());
        mMap.setInfoWindowAdapter(customInfoWindowGoogleMap);
    }

    /**
     * Tworzy wszystkie dolne przyciski w zależności od akcji
     **/
    private void createBottomBtns(final Button btnLeft, final Button btnMiddle, final Button btnRight, final double lat, final double lng, final String name, final String monument_image, final String description, final String customImagePath, final String customImageDate, final View customView, boolean isCamera) {
        final LatLng curPosition = new LatLng(lat, lng);
        // Jeżeli przycisk kamery ma się pokzywać i urządzenie posiada kamerę
        if (isCamera && getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Toast.makeText(getApplicationContext(), getString(R.string.toast_na_miejscu), Toast.LENGTH_SHORT).show();
            final ViewGroup.LayoutParams paramsKamera = btnMiddle.getLayoutParams();
            paramsKamera.width = btnBottomWidth;
            btnMiddle.setLayoutParams(paramsKamera);
            btnMiddle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // jeżeli zdjęcie już jest
                    if (customImagePath != null && customImageDate != null) {
                        // wlacza Szczegóły
                        Intent i = new Intent(getApplicationContext(), ObservationPointDetailsActivity.class);
                        i.putExtra("id", curId);
                        i.putExtra("name", name);
                        i.putExtra("comment", curComment);
                        i.putExtra("year", curYear);
                        i.putExtra("image", curImage);
                        i.putExtra("customImagePath", customImagePath);
                        i.putExtra("customImageDate", customImageDate);
                        i.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
                        i.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
                        i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
                        i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
                        i.putExtra("mode", "fromMapsActivity");
                        i.putExtra("image_exists", true);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        // pokazuje popup ze wskazówką
                        curPopupMode = "prompt";
                        showPopupInfo = true;
                        setPopupWindowContent(curPopupMode, customView, popupWidth, popupHeight, name, monument_image, description, btnLeft, btnMiddle, btnRight);
                    }
                }
            });
            btnMiddle.setVisibility(View.VISIBLE);
        }
        // Wstawia wartosc prycisku Wybierz - pokazuje pzycisk
        btnRight.setText(R.string.trasa);
        final ViewGroup.LayoutParams paramsWybierz = btnRight.getLayoutParams();
        paramsWybierz.width = btnBottomWidth;
        btnRight.setLayoutParams(paramsWybierz);
        if (mDestination == null || !mDestination.equals(curPosition)) {
            btnRight.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rounded_corners_button_darkblue));
        } else {
            btnRight.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rounded_corners_button_grey));
        }
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Klikniecie jezeli zabytek nie jest wybrany - rysuje trase
                if (mDestination == null || !mDestination.equals(curPosition)) {
                    getMyLocation(CLOSE_ZOOM, true);
                    mDestination = curPosition;
                    if (mOrigin != null) {
                        drawRoute();
                    }
                    btnRight.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rounded_corners_button_grey));
                }
                // Klikniecie jezeli zabytek juz jest wybrany - usuwa trase i przyciski
                else {
                    if (mPolyline != null) {
                        mPolyline.remove();
                        mDestination = null;
                        btnLeft.setVisibility(View.INVISIBLE);
                        btnRight.setVisibility(View.INVISIBLE);
                        btnRight.setBackground(ContextCompat.getDrawable(mContext, R.drawable.rounded_corners_button_darkblue));
                        btnMiddle.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        btnRight.setVisibility(View.VISIBLE);

        // Wstawia wartosc prycisku Info - pokazuje pzycisk
        final ViewGroup.LayoutParams paramsInfo = btnLeft.getLayoutParams();
        paramsInfo.width = btnBottomWidth;
        btnLeft.setLayoutParams(paramsInfo);
        // Set a click listener for the text view
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curPopupMode = "info";
                showPopupInfo = true;
                if (inflater != null) {
                    setPopupWindowContent(curPopupMode, customView, popupWidth, popupHeight, name, monument_image, description, btnLeft, btnMiddle, btnRight);
                }
            }
        });
        btnLeft.setVisibility(View.VISIBLE);
    }

    /**
     * Dodaje markery zabytkow na mape
     */
    private void addClusterItems() {
        for (final ObservationPoint observationPoint : observationPoints) {
            String id = observationPoint.getId();
            double lat = observationPoint.getLatitude();
            double lng = observationPoint.getLongitude();
            String image = observationPoint.getImage();
            String year = observationPoint.getYear();
            String comment = observationPoint.getComment();
            boolean isHorizontal = observationPoint.isHorizontal();
            String customImagePath = observationPoint.getCustomImagePath();
            String customImageDate = observationPoint.getCustomImageDate();
            String name = null;
            String description = null;
            String monument_image = null;
            for (Monument monument : monuments) {
                if (monument.getId().equals(observationPoint.getMonumentRef())) {
                    name = monument.getName();
                    description = monument.getDescription();
                    monument_image = monument.getImage();
                    if (monument_image.equals("")) {
                        monument_image = null;
                    }
                    break;
                } else {
                    name = "brak";
                    description = "brak";
                    monument_image = null;
                }
            }
            mClusterManager.addItem(new ClusterItem(lat, lng, name, comment, monument_image, description, image, year, id, RADIUS, isHorizontal, customImagePath, customImageDate));
            mClusterManager.setOnClusterItemInfoWindowClickListener(
                    new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterItem>() {
                        @Override
                        public void onClusterItemInfoWindowClick(ClusterItem ClusterItem) {
                            String name = ClusterItem.getName();
                            String monument_image = ClusterItem.getMonument_image();
                            String description = ClusterItem.getDescription();
                            curId = ClusterItem.getId();
                            curLat = ClusterItem.getPosition().latitude;
                            curLng = ClusterItem.getPosition().longitude;
                            curImage = ClusterItem.getImage();
                            curComment = ClusterItem.getComment();
                            curYear = ClusterItem.getYear();
                            curIsHorizontal = ClusterItem.isHorizontal();
                            curCustomImagePath = ClusterItem.getCustomImagePath();
                            curCustomImageDate = ClusterItem.getCustomImageDate();
                            Button btnLeft = findViewById(R.id.btn_info);
                            Button btnRight = findViewById(R.id.btn_trasa);
                            Button btnCenter = findViewById(R.id.btn_kamera);
                            View customView;
                            isCamera = false;
                            // reaguje na przyblizenie do punktu
                            float[] distance = new float[2];
                            Location.distanceBetween(ClusterItem.getPosition().latitude,
                                    ClusterItem.getPosition().longitude, mOrigin.latitude,
                                    mOrigin.longitude, distance);
                            if (distance[0] < ClusterItem.getRadius()) {
                                isCamera = true;
                            } else if (distance[0] >= ClusterItem.getRadius()) {
                                isCamera = false;
                            }
                            if (inflater != null) {
                                customView = inflater.inflate(R.layout.popup_info, null);
                                // przy naciśnięciu na pinezkę tworzymy nowe przyciski i popup
                                createBottomBtns(btnLeft, btnCenter, btnRight, curLat, curLng, name, monument_image, description, curCustomImagePath, curCustomImageDate, customView, isCamera);
                            }
                        }
                    });
        }
    }

    /**
     * Definiuje co się stanie, kiedy klikamy na cluster (przyblizamy kamere)
     */
    @Override
    public boolean onClusterClick(Cluster<ClusterItem> cluster) {
        Button btnLeft = findViewById(R.id.btn_info);
        btnLeft.setVisibility(View.INVISIBLE);
        Button btnRight = findViewById(R.id.btn_trasa);
        btnRight.setVisibility(View.INVISIBLE);
        Button btnCenter = findViewById(R.id.btn_kamera);
        btnCenter.setVisibility(View.INVISIBLE);
        // Create the builder to collect all essential cluster items for the bounds.
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem item : cluster.getItems()) {
            builder.include(item.getPosition());
        }
        // Get the LatLngBounds
        final LatLngBounds bounds = builder.build();

        // Animate camera to the bounds
        try {
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * Tworzy popup z informacja o zabytku
     */
    private void setPopupWindowContent(String mode, View view, int width, int height, String title, String image, String description, Button btnLeft, Button btnCenter, Button btnRight) {
        // przypisuje dane do zmiennych tymczasowych, jezeli pokazujemy popup z informacją
        curName = title;
        curMonumentImage = image;
        curDescription = description;
        // Tworzy popup
        mPopupWindow = new PopupWindow(view, width, height);
        mPopupWindow.setElevation(5.0f);
        // Ustawia elementy popupa
        Button btnClose = null;
        Button btnOk = null;
        TextView infoTitle = null;
        ImageView infoImage = null;
        TextView infoDescription = null;
        LinearLayout imageLayout = null;
        if (view != null) {
            infoTitle = view.findViewById(R.id.info_text_title);
            infoImage = view.findViewById(R.id.info_image);
            infoDescription = view.findViewById(R.id.info_text_description);
            btnClose = view.findViewById(R.id.close);
            btnOk = view.findViewById(R.id.ok);
            imageLayout = view.findViewById(R.id.info_image_layout);
        }
        if (btnClose != null && btnOk != null && infoTitle != null && infoImage != null && infoDescription != null) {
            btnOk.setVisibility(View.GONE);
            // Jezeli przekzujemy dane wskazowki
            if (mode.equals("prompt")) {
                // pobieramy dane wskazówki
                title = getString(R.string.wskazowka_naglowek);
                if (curIsHorizontal) {
                    image = "horizontal";
                    description = getString(R.string.wskazowka_tresc_poziomo);
                } else {
                    image = "vertical";
                    description = getString(R.string.wskazowka_tresc_pionowo);
                }
                // ustawiamy odpowiednio obrazek
                if (image.equals("vertical")) {
                    infoImage.setImageResource(R.drawable.vertical);
                } else {
                    infoImage.setImageResource(R.drawable.horizontal);
                }
                // definiujemy przycisk Ok, włączający kamerę
                ViewGroup.LayoutParams paramsOk = btnOk.getLayoutParams();
                paramsOk.width = screenWidth / 4;
                btnOk.setLayoutParams(paramsOk);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // wlacza kamere
                        Intent i = new Intent(getApplicationContext(), CapturePictureActivity.class);
                        i.putExtra("id", curId);
                        i.putExtra("lat", curLat);
                        i.putExtra("lng", curLng);
                        i.putExtra("name", curName);
                        i.putExtra("comment", curComment);
                        i.putExtra("description", curDescription);
                        i.putExtra("image", curImage);
                        i.putExtra("year", curYear);
                        i.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
                        i.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
                        i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
                        i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
                        i.putExtra("image_exists", false);
                        startActivity(i);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                });
                btnOk.setVisibility(View.VISIBLE);
            } else {
                // ladujemy obrazek
                new DownloadImageTask(infoImage).execute(image);
            }
            // Wstawia nagłówek
            infoTitle.setText(Html.fromHtml(title));
            // Wstawia opis
            infoDescription.setText(description);
            // Wstawia obrazek
            ViewGroup.LayoutParams params = imageLayout.getLayoutParams();
            params.height = imageLayoutHeight;
            imageLayout.setLayoutParams(params);
            // Ustwienia przycisku na wyłączenie
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Dismiss the popup window
                    mPopupWindow.dismiss();
                    showPopupInfo = false;
                    Button btnLeft = findViewById(R.id.btn_info);
                    Button btnRight = findViewById(R.id.btn_trasa);
                    Button btnCenter = findViewById(R.id.btn_kamera);
                    btnLeft.setEnabled(true);
                    btnRight.setEnabled(true);
                    btnCenter.setEnabled(true);
                    btnMenu.setEnabled(true);
                    curName = null;
                    curMonumentImage = null;
                    curDescription = null;
                }
            });
        }
        // Pokazuje popup po środku, przyciski ustawia na nieaktywne
        mPopupWindow.showAtLocation(layoutMapa, Gravity.CENTER, 0, 0);
        btnLeft.setEnabled(false);
        btnRight.setEnabled(false);
        btnCenter.setEnabled(false);
        btnMenu.setEnabled(false);
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
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
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

    private void getMyLocation(final int zoom, final boolean animate) {
        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // The entry point to the Fused Location Provider.
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        LocationListener mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mOrigin = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mOrigin, CLOSE_ZOOM));
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

        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                if (mLocationManager != null) {
                    mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, mLocationListener);
                }
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mOrigin = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                if (animate) {
                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mOrigin, zoom));
                                }
                            }

                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            if (animate) {
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, zoom));
                            }
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
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
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

    public static class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
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
