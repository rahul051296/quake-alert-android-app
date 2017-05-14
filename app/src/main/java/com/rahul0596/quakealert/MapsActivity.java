package com.rahul0596.quakealert;

import android.app.LoaderManager;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<ArrayList<Quake>> {
    GoogleMap mGoogleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String themes = sharedPrefs.getString(
                getString(R.string.settings_themes_key),
                getString(R.string.settings_themes_default));
        switch (themes) {
            case "dark":
                setTheme(R.style.AppTheme);
                break;
            case "light":
                setTheme(R.style.AppTheme_Light);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, this);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        LatLng lg = new LatLng(0, 0);
        CameraUpdate update = CameraUpdateFactory.newLatLng(lg);
        mGoogleMap.moveCamera(update);
    }

    @Override
    public Loader<ArrayList<Quake>> onCreateLoader(int i, Bundle bundle) {
        return new QuakeActivity.QuakeLoader(this, QuakeActivity.uri_string);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Quake>> loader, ArrayList<Quake> earthquakes) {

        updateUi(earthquakes);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Quake>> loader) {
    }

    public void updateUi(final ArrayList<Quake> quakeArrayList) {
        Log.i("ArrayList", String.valueOf(quakeArrayList.size()));
        for (int i = 0; i < quakeArrayList.size(); i++) {

            createMarker(quakeArrayList.get(i).getLatitude(), quakeArrayList.get(i).getLongitude(), quakeArrayList.get(i).getMagnitude(), quakeArrayList.get(i).getLocation());
        }

    }

    protected Marker createMarker(final double latitude, final double longitude, final double magnitude, String location) {
        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(longitude, latitude))
                .snippet(location)
                .title("Magnitude - " + String.valueOf(magnitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                ));

        return marker;
    }
}
