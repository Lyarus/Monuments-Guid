package com.example.monumentsguid;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
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

import com.example.monumentsguid.Entities.ObservationPoint;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;
import java.util.Objects;

public class MapsShowActivity extends FragmentActivity implements OnMapReadyCallback {
    private List<ObservationPoint> observationPoints;

    private static final int DEFAULT_ZOOM = 13;
    private final LatLng mDefaultLocation = new LatLng(51.098781, 17.036716);
    // rozmiar ekranu urzadzenia
    int screenHeight;
    int screenWidth;
    int screenOrientation;
    // elementy
    private FrameLayout layoutMapa;
    private Button btnLeft;
    private Button btnRight;
    private Button btnMenu;
    private int btnMenuWidth;
    private int btnBottomWidth;
    private int imageLayoutHeight;
    private PopupWindow mPopupWindow;
    private String id;
    private String monument_image;
    private String comment;
    private String image;
    private String description;
    private double lat;
    private double lng;
    private String name;
    private String year;
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
        setContentView(R.layout.activity_maps_show);

        Intent intent = getIntent();
        observationPoints = getIntent().getParcelableArrayListExtra("observationPoints");
        monument_ref = Objects.requireNonNull(intent.getExtras()).getString("id");
        monument_image = Objects.requireNonNull(intent.getExtras()).getString("image");
        if (monument_image != null && monument_image.equals("")) {
            monument_image = null;
        }
        lat = Objects.requireNonNull(intent.getExtras()).getDouble("lat");
        lng = Objects.requireNonNull(intent.getExtras()).getDouble("lng");
        name = Objects.requireNonNull(intent.getExtras()).getString("name");
        description = Objects.requireNonNull(intent.getExtras()).getString("description");

        location = new LatLng(lat, lng);

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
            setPopupWindowContent(customView, popupWidth, popupHeight, name, monument_image, description);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        if (location != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
        }

        // Reaguje na klikniecie na mape
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
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
        mClusterManager = new ClusterManager<>(this, googleMap);
        mClusterManager.setRenderer(new MarkerClusterRenderer(this, googleMap, mClusterManager));
        googleMap.setOnCameraIdleListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
        googleMap.setOnInfoWindowClickListener(mClusterManager);
        addClusterItems();
        mClusterManager.cluster();

        // Buduje okno informacyjne
        CustomInfoWindowGoogleMap customInfoWindowGoogleMap = new CustomInfoWindowGoogleMap(getApplicationContext());
        googleMap.setInfoWindowAdapter(customInfoWindowGoogleMap);
    }

    /**
     * Dodaje markery zabytkow na mape (pobiera z bazy)
     */
    private void addClusterItems() {
        for (final ObservationPoint observationPoint : observationPoints) {
            if (observationPoint.getMonumentRef().equals(monument_ref)) {
                id = observationPoint.getId();
                lat = observationPoint.getLatitude();
                lng = observationPoint.getLongitude();
                comment = observationPoint.getComment();
                year = observationPoint.getYear();
                image = observationPoint.getImage();
                mClusterManager.addItem(new ClusterItem(lat, lng, name, comment));
                mClusterManager.setOnClusterItemInfoWindowClickListener(
                        new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterItem>() {
                            @Override
                            public void onClusterItemInfoWindowClick(ClusterItem ClusterItem) {
                                // Wstawia wartosc prycisku Info - pokazuje pzycisk
                                btnLeft.setText(R.string.info);
                                final ViewGroup.LayoutParams paramsInfo = btnLeft.getLayoutParams();
                                paramsInfo.width = btnBottomWidth;
                                btnLeft.setLayoutParams(paramsInfo);
                                btnLeft.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        showPopupInfo = true;
                                        customView = null;
                                        if (inflater != null) {
                                            customView = inflater.inflate(R.layout.popup_info, null);
                                        }
                                        setPopupWindowContent(customView, popupWidth, popupHeight, name, monument_image, description);
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
                                        Intent i = new Intent(getApplicationContext(),
                                                ObservationPointDetailsActivity.class);
                                        i.putExtra("id", id);
                                        i.putExtra("comment", comment);
                                        i.putExtra("name", name);
                                        i.putExtra("year", year);
                                        i.putExtra("image", image);
                                        startActivity(i);
                                    }
                                });
                                btnRight.setVisibility(View.VISIBLE);
                            }
                        });
            }
        }
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
