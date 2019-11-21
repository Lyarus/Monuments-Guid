package com.example.monumentsguid;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
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

import com.example.monumentsguid.Entities.Monument;
import com.example.monumentsguid.Entities.ObservationPoint;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapsShowActivity extends FragmentActivity implements OnMapReadyCallback {
    private List<ObservationPoint> observationPoints;
    private List<Monument> monuments;

    private static final int DEFAULT_ZOOM = 13;
    private final LatLng mDefaultLocation = new LatLng(51.098781, 17.036716);
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
    private String curId;
    private String curComment;
    private double curLat;
    private double curLng;
    private String curImage;
    private String curYear;
    private String monument_image;
    private String description;
    private String name;
    private LatLng location;
    private boolean showPopupInfo;
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
        monuments = getIntent().getParcelableArrayListExtra("monuments");
        monument_image = Objects.requireNonNull(intent.getExtras()).getString("image");
        if (monument_image != null && monument_image.equals("")) {
            monument_image = null;
        }
        double lat = Objects.requireNonNull(intent.getExtras()).getDouble("lat");
        double lng = Objects.requireNonNull(intent.getExtras()).getDouble("lng");
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
            Button btnCenter = findViewById(R.id.btn_szczegoly);

            if (inflater != null) {
                customView = inflater.inflate(R.layout.popup_info, null);
                createBottomBtns(btnLeft, btnCenter, btnRight, curId, curComment, curLat, curLng, name, monument_image, description, curImage, curYear, customView);
                setPopupWindowContent(customView, popupWidth, popupHeight, name, monument_image, description, btnLeft, btnRight, btnCenter);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        // Pozwala na uzywanie clusterow (liczy ile obiektow jest, a nie wyswietla wszystkie pinezki)
        mClusterManager = new ClusterManager<>(this, googleMap);
        mClusterManager.setRenderer(new MarkerClusterRenderer(this, googleMap, mClusterManager));
        googleMap.setOnCameraIdleListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
        googleMap.setOnInfoWindowClickListener(mClusterManager);
        addClusterItems();
        mClusterManager.cluster();

        if (location != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
        }

        // Reaguje na klikniecie na mape
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                Button btnLeft = findViewById(R.id.btn_info);
                btnLeft.setVisibility(View.INVISIBLE);
                Button btnRight = findViewById(R.id.btn_trasa);
                btnRight.setVisibility(View.INVISIBLE);
                Button btnCenter = findViewById(R.id.btn_szczegoly);
                btnCenter.setVisibility(View.INVISIBLE);
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

        // Buduje okno informacyjne
        CustomInfoWindowGoogleMap customInfoWindowGoogleMap = new CustomInfoWindowGoogleMap(getApplicationContext());
        googleMap.setInfoWindowAdapter(customInfoWindowGoogleMap);
    }

    /**
     * Dodaje markery zabytkow na mape (pobiera z bazy)
     */
    private void addClusterItems() {
        for (final ObservationPoint observationPoint : observationPoints) {
            String id = observationPoint.getId();
            double lat = observationPoint.getLatitude();
            double lng = observationPoint.getLongitude();
            String comment = observationPoint.getComment();
            String image = observationPoint.getImage();
            String year = observationPoint.getYear();
            mClusterManager.addItem(new ClusterItem(lat, lng, name, comment, monument_image, description, image, year, id, 0));
            mClusterManager.setOnClusterItemInfoWindowClickListener(
                    new ClusterManager.OnClusterItemInfoWindowClickListener<ClusterItem>() {
                        @Override
                        public void onClusterItemInfoWindowClick(ClusterItem ClusterItem) {
                            String id = ClusterItem.getId();
                            String name = ClusterItem.getName();
                            String comment = ClusterItem.getComment();
                            String monument_image = ClusterItem.getMonument_image();
                            String description = ClusterItem.getDescription();
                            double lat = ClusterItem.getPosition().latitude;
                            double lng = ClusterItem.getPosition().longitude;
                            String image = ClusterItem.getImage();
                            String year = ClusterItem.getYear();
                            curId = id;
                            curComment = comment;
                            curLat = lat;
                            curLng = lng;
                            curImage = image;
                            curYear = year;
                            Button btnLeft = findViewById(R.id.btn_info);
                            Button btnRight = findViewById(R.id.btn_trasa);
                            Button btnCenter = findViewById(R.id.btn_szczegoly);
                            View customView;
                            if (inflater != null) {
                                customView = inflater.inflate(R.layout.popup_info, null);
                                createBottomBtns(btnLeft, btnCenter, btnRight, id, comment, lat, lng, name, monument_image, description, image, year, customView);
                            }
                        }
                    });
        }
    }


    private void createBottomBtns(final Button btnLeft, final Button btnCenter, final Button btnRight, final String id, final String comment, final double lat, final double lng, final String name, final String monument_image, final String description, final String image, final String year, final View customView) {
        // Wstawia wartosc prycisku Wybierz - pokazuje pzycisk
        final ViewGroup.LayoutParams paramsWybierz = btnRight.getLayoutParams();
        paramsWybierz.width = btnBottomWidth;
        btnRight.setLayoutParams(paramsWybierz);
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),
                        MapsActivity.class);
                i.putExtra("lat", lat);
                i.putExtra("lng", lng);
                i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
                i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
                startActivity(i);
            }
        });
        btnRight.setVisibility(View.VISIBLE);

        // Wstawia wartosc prycisku Szczegóły - pokazuje pzycisk
        final ViewGroup.LayoutParams paramsSzczegoly = btnCenter.getLayoutParams();
        paramsSzczegoly.width = btnBottomWidth;
        btnCenter.setLayoutParams(paramsSzczegoly);
        btnCenter.setOnClickListener(new View.OnClickListener() {
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
        btnCenter.setVisibility(View.VISIBLE);

        // Wstawia wartosc prycisku Info - pokazuje pzycisk
        final ViewGroup.LayoutParams paramsInfo = btnLeft.getLayoutParams();
        paramsInfo.width = btnBottomWidth;
        btnLeft.setLayoutParams(paramsInfo);
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupInfo = true;
                if (inflater != null) {
                    setPopupWindowContent(customView, popupWidth, popupHeight, name, monument_image, description, btnLeft, btnRight, btnCenter);
                }
            }
        });
        btnLeft.setVisibility(View.VISIBLE);
    }


    /**
     * Tworzy popup z informacja o zabytku
     */
    private void setPopupWindowContent(View view, int width, int height, String title, String image, String description, Button btnLeft, Button btnRight, Button btnCenter) {
        // Tworzy popup
        mPopupWindow = new PopupWindow(view, width, height);
        mPopupWindow.setElevation(5.0f);
        // Ustawia elementy popupa
        Button btnClose = null;
        TextView infoTitle = null;
        ImageView infoImage = null;
        TextView infoDescription = null;
        LinearLayout imageLayout = null;
        if (view != null) {
            infoTitle = view.findViewById(R.id.info_text_title);
            infoImage = view.findViewById(R.id.info_image);
            infoDescription = view.findViewById(R.id.info_text_description);
            btnClose = view.findViewById(R.id.close);
            imageLayout = view.findViewById(R.id.info_image_layout);
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
                    Button btnLeft = findViewById(R.id.btn_info);
                    Button btnRight = findViewById(R.id.btn_trasa);
                    Button btnCenter = findViewById(R.id.btn_szczegoly);
                    btnLeft.setEnabled(true);
                    btnRight.setEnabled(true);
                    btnCenter.setEnabled(true);
                    btnMenu.setEnabled(true);
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
}
