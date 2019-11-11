package com.example.monumentsguid;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.maps.android.clustering.ClusterManager;

import java.util.Objects;

public class MapsShowActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final int DEFAULT_ZOOM = 13;
    private final LatLng mDefaultLocation = new LatLng(51.098781, 17.036716);
    // rozmiar ekranu urzadzenia
    int screenHeight;
    int screenWidth;
    int screenOrientation;
    // Połaczenie z BD
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GoogleMap mMap;
    // elementy
    private FrameLayout layoutMapa;
    private Button btnLeft;
    private Button btnRight;
    private Button btnMenu;
    private int btnMenuWidth;
    private int btnBottomWidth;
    private int imageLayoutHeight;
    private PopupWindow mPopupWindow;
    private String title;
    private String comment;
    private String image;
    private String description;
    private double lat;
    private double lng;
    private LatLng location;
    private String monument_ref;
    private boolean showPopupInfo;
    private View customView;
    private LayoutInflater inflater;
    private int popupWidth;
    private int popupHeight;
    // Clustery
    private ClusterManager<ClusterItem> mClusterManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Get the application context
        Context mContext = getApplicationContext();

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
        btnLeft = findViewById(R.id.btn_info);
        btnRight = findViewById(R.id.btn_trasa);
        btnMenu = findViewById(R.id.btn_menu);

        inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        customView = null;
        showPopupInfo = false;

        Intent intent = getIntent();
        monument_ref = Objects.requireNonNull(intent.getExtras()).getString("id");
        lat = Objects.requireNonNull(intent.getExtras()).getDouble("lat");
        lng = Objects.requireNonNull(intent.getExtras()).getDouble("lng");
        location = new LatLng(lat, lng);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
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
            customView = null;
            mPopupWindow.dismiss();
            //Tworzy nowy
            if (inflater != null) {
                customView = inflater.inflate(R.layout.popup_info, null);
            }
            setPopupWindowContent(customView, popupWidth, popupHeight, title, image, description);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);
        if (location != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));
        } else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
        }

        // Reaguje na klikniecie na mape
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                btnLeft.setVisibility(View.INVISIBLE);
                btnRight.setVisibility(View.INVISIBLE);
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
                view.getContext().startActivity(intent);
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

        // dodaje markery zabytkow na mape (pobiera z bazy)
        db.collection("observation_point")
                .whereEqualTo("monument_ref", monument_ref)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                title = document.getString("name");
                                comment = document.getString("comment");
                                image = document.getString("image_ref");
                                description = document.getString("description");
                                lat = Objects.requireNonNull(document.getGeoPoint("lat_lng")).getLatitude();
                                lng = Objects.requireNonNull(document.getGeoPoint("lat_lng")).getLongitude();
                                mClusterManager.addItem(new ClusterItem(lat, lng, title, comment, description));
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                mClusterManager.setOnClusterItemInfoWindowClickListener(
                                        new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterItem>() {
                                            @Override
                                            public void onClusterItemInfoWindowClick(ClusterItem ClusterItem) {
                                                // Wstawia wartosc prycisku Info - pokazuje pzycisk
                                                btnLeft.setText(R.string.info);
                                                final ViewGroup.LayoutParams paramsInfo = btnLeft.getLayoutParams();
                                                paramsInfo.width = btnBottomWidth;
                                                btnLeft.setLayoutParams(paramsInfo);
                                                // Set a click listener for the text view
                                                btnLeft.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        showPopupInfo = true;
                                                        customView = null;
                                                        if (inflater != null) {
                                                            customView = inflater.inflate(R.layout.popup_info, null);
                                                        }
                                                        setPopupWindowContent(customView, popupWidth, popupHeight, title, image, description);
                                                    }
                                                });
                                                btnLeft.setVisibility(View.VISIBLE);

                                                // Wstawia wartosc prycisku Wybierz - pokazuje pzycisk
                                                btnRight.setText(R.string.wybierz);
                                                final ViewGroup.LayoutParams paramsWybierz = btnRight.getLayoutParams();
                                                paramsWybierz.width = btnBottomWidth;
                                                btnRight.setLayoutParams(paramsWybierz);
                                                btnRight.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                    }
                                                });
                                                btnRight.setVisibility(View.VISIBLE);
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
     * Tworzy popup z informacja o zabytku
     */
    private void setPopupWindowContent(View view, int width, int height, String title, String image, String description) {
        // Tworzy popup
        mPopupWindow = new PopupWindow(view, width, height);
        mPopupWindow.setElevation(5.0f);
        // Ustawia elementy popupa
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
            // Wstawia nagłówek
            infoTitle.setText(title);
            // Wstawia obrazek
            ViewGroup.LayoutParams params = imageLayout.getLayoutParams();
            params.height = imageLayoutHeight;
            imageLayout.setLayoutParams(params);
            if (image != null) {
                new MapsActivity.DownloadImageTask(infoImage).execute(image);
            }
            // Wstawia opis
            infoDescription.setText(description);
            // Ustwienia przycisku na wyłączenie
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Dismiss the popup window
                    mPopupWindow.dismiss();
                    showPopupInfo = false;
                    btnLeft.setEnabled(true);
                    btnRight.setEnabled(true);
                    btnMenu.setEnabled(true);
                }
            });
        }
        // Pokazuje popup po środku, przyciski ustawia na nieaktywne
        mPopupWindow.showAtLocation(layoutMapa, Gravity.CENTER, 0, 0);
        btnLeft.setEnabled(false);
        btnRight.setEnabled(false);
        btnMenu.setEnabled(false);
    }

}
