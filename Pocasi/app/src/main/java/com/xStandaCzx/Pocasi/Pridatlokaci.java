package com.xStandaCzx.Pocasi;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;


public class Pridatlokaci extends Activity implements LocationListener {
    private LocationManager locationManager;
    private double la;
    private double lo;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pridatlokaci);



        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.d("", "Place: " + place.getName());
                la=place.getLatLng().latitude;
                lo=place.getLatLng().longitude;
                String lla=Double.toString(la);
                String llo=Double.toString(lo);
                Log.d("Pozoce",""+lla+" "+llo);
                NetworkActivity.addLoc(lla,llo);
            }


            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.d("", "An error occurred: " + status);
            }
        });

    }



    public void pridatlokaci(View view) {

        finish();
    }

    public void pridataktualni(View view) {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},120);
        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,10000,this);
            this.onLocationChanged(null);
        }


        }
    @Override
    public void onLocationChanged(Location location) {
        if(location == null){
            Log.d("","Error localisation: ");
        }else{
            la=location.getLatitude();
            lo=location.getLongitude();
            String lla=Double.toString(la);
            String llo=Double.toString(lo);
            Log.d("Pozoce",""+lla+" "+llo);
            NetworkActivity.addLoc((lla),(llo));
            this.finish();

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
}

