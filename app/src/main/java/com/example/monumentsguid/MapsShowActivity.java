package com.example.monumentsguid;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
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

import com.example.monumentsguid.Entities.City;
import com.example.monumentsguid.Entities.Country;
import com.example.monumentsguid.Entities.Monument;
import com.example.monumentsguid.Entities.ObservationPoint;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapsShowActivity extends FragmentActivity implements OnMapReadyCallback, ClusterManager.OnClusterClickListener<ClusterItem> {
    private static final int DEFAULT_ZOOM = 17;
    private List<Country> countries;
    private List<City> cities;
    private List<Monument> monuments;
    private List<ObservationPoint> observationPoints;
    private List<ObservationPoint> observationPointsFiltered;
    private List<ObservationPoint> observationPointsFilteredCity;
    private String city_ref;
    private String monument_ref;
    private String country_ref;
    private double lat;
    private double lng;
    private String curCustomImagePath;
    private String curCustomImageDate;
    private String monument_image;
    private String description;
    private String name;

    private GoogleMap mMap;
    private LatLng location;
    private LatLng mDefaultLocation;
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
        monument_ref = Objects.requireNonNull(intent.getExtras()).getString("monument_id");
        country_ref = Objects.requireNonNull(intent.getExtras()).getString("country_ref");
        city_ref = Objects.requireNonNull(intent.getExtras()).getString("city_ref");
        name = Objects.requireNonNull(intent.getExtras()).getString("name");
        lat = Objects.requireNonNull(intent.getExtras()).getDouble("lat");
        lng = Objects.requireNonNull(intent.getExtras()).getDouble("lng");
        description = Objects.requireNonNull(intent.getExtras()).getString("description");
        monument_image = Objects.requireNonNull(intent.getExtras()).getString("image");
        if (monument_image != null && monument_image.equals("")) {
            monument_image = null;
        }
        countries = getIntent().getParcelableArrayListExtra("countries");
        cities = getIntent().getParcelableArrayListExtra("cities");
        monuments = getIntent().getParcelableArrayListExtra("monuments");
        observationPoints = getIntent().getParcelableArrayListExtra("observationPoints");
        observationPointsFiltered = getIntent().getParcelableArrayListExtra("observationPointsFiltered");
        observationPointsFilteredCity = new ArrayList<>();

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

    @Override
    public void onBackPressed() {
        if (showPopupInfo) {
            mPopupWindow.dismiss();
            showPopupInfo = false;
            Button btnLeft = findViewById(R.id.btn_info);
            Button btnRight = findViewById(R.id.btn_trasa);
            Button btnMiddle = findViewById(R.id.btn_szczegoly);
            btnLeft.setEnabled(true);
            btnRight.setEnabled(true);
            btnMiddle.setEnabled(true);
            btnMenu.setEnabled(true);
        } else {
            for (Monument monument : monuments) {
                if (monument.getCityRef().equals(city_ref)) {
                    String monument_id = monument.getId();
                    for (ObservationPoint observationPoint : observationPoints) {
                        if (observationPoint.getMonumentRef().equals(monument_id)) {
                            observationPointsFilteredCity.add(observationPoint);
                        }
                    }
                }
            }

            super.onBackPressed();
            Intent i = new Intent(getApplicationContext(),
                    MonumentActivity.class);
            i.putExtra("city_id", city_ref);
            i.putExtra("country_ref", country_ref);
            i.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
            i.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
            i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
            i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
            i.putParcelableArrayListExtra("observationPointsFiltered", (ArrayList<? extends Parcelable>) observationPointsFilteredCity);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            this.finish();
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
                createBottomBtns(btnLeft, btnCenter, btnRight, curId, curComment, curLat, curLng, name, monument_image, description, curImage, curYear, curCustomImagePath, curCustomImageDate, customView);
                setPopupWindowContent(customView, popupWidth, popupHeight, name, monument_image, description, btnLeft, btnRight, btnCenter);
            }
        }
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);

        // Pozwala na uzywanie clusterow (liczy ile obiektow jest, a nie wyswietla wszystkie pinezki)
        mClusterManager = new ClusterManager<>(this, mMap);
        mClusterManager.setRenderer(new MarkerClusterRenderer(this, mMap, mClusterManager));
        mClusterManager.setOnClusterClickListener(this);
        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        addClusterItems();
        mClusterManager.cluster();

        if (location != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, DEFAULT_ZOOM));
        } else {
            mDefaultLocation = new LatLng(51.098781, 17.036716);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 10));
        }

        // Reaguje na klikniecie na mape
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
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
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                mMap.clear();
            }
        });

        // Buduje okno informacyjne
        CustomInfoWindowGoogleMap customInfoWindowGoogleMap = new CustomInfoWindowGoogleMap(getApplicationContext());
        mMap.setInfoWindowAdapter(customInfoWindowGoogleMap);
    }

    /**
     * Dodaje markery zabytkow na mape (pobiera z bazy)
     */
    private void addClusterItems() {
        for (final ObservationPoint observationPoint : observationPointsFiltered) {
            String id = observationPoint.getId();
            double lat = observationPoint.getLatitude();
            double lng = observationPoint.getLongitude();
            String comment = observationPoint.getComment();
            String image = observationPoint.getImage();
            String year = observationPoint.getYear();
            boolean isHorizontal = observationPoint.isHorizontal();
            String customImagePath = observationPoint.getCustomImagePath();
            String customImageDate = observationPoint.getCustomImageDate();
            mClusterManager.addItem(new ClusterItem(lat, lng, name, comment, monument_image, description, image, year, id, 0, isHorizontal, customImagePath, customImageDate));
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
                            String customImagePath = ClusterItem.getCustomImagePath();
                            String customImageDate = ClusterItem.getCustomImageDate();
                            curId = id;
                            curComment = comment;
                            curLat = lat;
                            curLng = lng;
                            curImage = image;
                            curYear = year;
                            curCustomImagePath = customImagePath;
                            curCustomImageDate = customImageDate;
                            Button btnLeft = findViewById(R.id.btn_info);
                            Button btnRight = findViewById(R.id.btn_trasa);
                            Button btnCenter = findViewById(R.id.btn_szczegoly);
                            View customView;
                            if (inflater != null) {
                                customView = inflater.inflate(R.layout.popup_info, null);
                                createBottomBtns(btnLeft, btnCenter, btnRight, id, comment, lat, lng, name, monument_image, description, image, year, customImagePath, customImageDate, customView);
                            }
                        }
                    });
        }
    }

    /**
     * Tworzy dolne przyciski
     */
    private void createBottomBtns(final Button btnLeft, final Button btnCenter, final Button btnRight, final String id, final String comment, final double lat, final double lng, final String name, final String monument_image, final String description, final String image, final String year, final String customImagePath, final String customImageDate, final View customView) {
        // Wstawia wartosc prycisku Wybierz - pokazuje pzycisk
        final ViewGroup.LayoutParams paramsWybierz = btnRight.getLayoutParams();
        paramsWybierz.width = btnBottomWidth;
        btnRight.setLayoutParams(paramsWybierz);
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MapsActivity.class);
                i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
                i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
                i.putExtra("lat", lat);
                i.putExtra("lng", lng);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                mMap.clear();
            }
        });
        btnRight.setVisibility(View.VISIBLE);

        // Wstawia wartosc prycisku Szczegóły - pokazuje pzycisk, jeżeli w pamięci jest własne zdjęcie
        if (customImagePath != null && customImageDate != null) {
            final ViewGroup.LayoutParams paramsSzczegoly = btnCenter.getLayoutParams();
            paramsSzczegoly.width = btnBottomWidth;
            btnCenter.setLayoutParams(paramsSzczegoly);
            btnCenter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getApplicationContext(),
                            ObservationPointDetailsActivity.class);
                    i.putExtra("id", id);
                    i.putExtra("country_ref", country_ref);
                    i.putExtra("city_ref", city_ref);
                    i.putExtra("monument_ref", monument_ref);
                    i.putExtra("comment", comment);
                    i.putExtra("name", name);
                    i.putExtra("description", description);
                    i.putExtra("lat", lat);
                    i.putExtra("lng", lng);
                    i.putExtra("year", year);
                    i.putExtra("image", image);
                    i.putExtra("customImagePath", customImagePath);
                    i.putExtra("customImageDate", customImageDate);
                    i.putParcelableArrayListExtra("countries", (ArrayList<? extends Parcelable>) countries);
                    i.putParcelableArrayListExtra("cities", (ArrayList<? extends Parcelable>) cities);
                    i.putParcelableArrayListExtra("monuments", (ArrayList<? extends Parcelable>) monuments);
                    i.putParcelableArrayListExtra("observationPoints", (ArrayList<? extends Parcelable>) observationPoints);
                    i.putParcelableArrayListExtra("observationPointsFiltered", (ArrayList<? extends Parcelable>) observationPointsFiltered);
                    i.putExtra("mode", "fromMapsShowActivity");
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
            btnCenter.setVisibility(View.VISIBLE);
        }

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
            infoTitle.setText(Html.fromHtml(title));
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

    @Override
    public boolean onClusterClick(Cluster<ClusterItem> cluster) {
        Button btnLeft = findViewById(R.id.btn_info);
        btnLeft.setVisibility(View.INVISIBLE);
        Button btnRight = findViewById(R.id.btn_trasa);
        btnRight.setVisibility(View.INVISIBLE);
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
}
