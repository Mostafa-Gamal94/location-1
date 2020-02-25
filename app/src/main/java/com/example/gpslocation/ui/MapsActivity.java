package com.example.gpslocation.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.gpslocation.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private AutocompleteSupportFragment autocompleteFragment;
    static Place placee;
    private ImageView iGps;
    public static TextView TXSUCCESSFUL, TXSEARCH;
    private LatLng  latLng ;
    private Address address;
    public static CameraPosition CAMERAPOSITION;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        iGps = (ImageView) findViewById(R.id.ic_gps);
        TXSEARCH = findViewById(R.id.input_search);
        TXSUCCESSFUL = findViewById(R.id.txSuccessful);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final String TAG = "placeautocomplete";

        Places.initialize(getApplicationContext(), "AIzaSyDZBBzdhWw64zTG3F-sHCyrsdMTL_zvtXE");
        PlacesClient placesClient = Places.createClient(this);
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                placee = place;
                String searchString = placee.getName();
                TXSEARCH.setText(searchString);

                Geocoder geocoder = new Geocoder(MapsActivity.this);
                List<Address> addressList = new ArrayList<>();

                try {
                    addressList = geocoder.getFromLocationName(searchString, 1);

                } catch (IOException e) {
                    Log.d(TAG, "geoLocate: " + e.getMessage());
                }

                if (addressList.size() > 0) {
                     address = addressList.get(0);
                    addressList.get(0).toString();
                    Log.i("PlaceInfo", addressList.toString());
                     latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.getUiSettings().setZoomControlsEnabled(true);

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                    mMap.addMarker(new MarkerOptions().position(latLng).title(address.getCountryName())).setVisible(false);;
                    TXSEARCH.setText(place.getName());


                }
            }

            @Override
            public void onError(@NonNull Status status) {
                Log.i(TAG, "An error occurred: " + status);


            }
        });

    }

    public void location_setting(){
                final ViewModell viewModell;
        viewModell =  ViewModelProviders.of(this).get(ViewModell.class);
        viewModell.getData();
    }





        @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Geocoder geocoder = new Geocoder(MapsActivity.this);
                CAMERAPOSITION = mMap.getCameraPosition();
                List<Address> addressList =new ArrayList<>();;
                try {

                    addressList = geocoder.getFromLocation(CAMERAPOSITION.target.latitude, CAMERAPOSITION.target.longitude, 1);

                } catch (IOException e) {
                    e.getMessage();
                }

                if (addressList.size() > 0) {
                    Address address2 = addressList.get(0);
                    addressList.get(0).toString();
                    Log.i("PlaceInfo", addressList.toString());
                    latLng = new LatLng(address2.getLatitude(), address2.getLongitude());
                    location_setting();

                    TXSEARCH.setText(address2.getFeatureName());

            }}
        });



        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                LatLng here = new LatLng(location.getLatitude(), location.getLongitude());

                mMap.addMarker(new MarkerOptions().position(here).title("I Am Here ")).setVisible(false);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here,14));
                mMap.getUiSettings().setZoomControlsEnabled(true);

                iGps.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LatLng here2 = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(here2).title("I Am Here Hello ")).setVisible(false);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here2,13));
                        mMap.getUiSettings().setZoomControlsEnabled(true);
                        TXSEARCH.setText("I Am Here Hello ");

                    }
                });}

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }};
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        }else {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
        }}}