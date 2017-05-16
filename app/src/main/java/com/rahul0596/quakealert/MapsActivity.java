package com.rahul0596.quakealert;

import android.app.LoaderManager;
import android.content.Intent;
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

            mGoogleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(quakeArrayList.get(i).getLongitude(), quakeArrayList.get(i).getLatitude()))
                    .snippet(quakeArrayList.get(i).getLocation())
                    .title("Magnitude - " + String.valueOf(quakeArrayList.get(i).getMagnitude()))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                    ));

            mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    int id = getMarkerIndex(marker.getId());

                    Intent intent = new Intent(MapsActivity.this, QuakeDetailsActivity.class);
                    intent.putExtra("title", quakeArrayList.get(id).getTitle());
                    intent.putExtra("mag", quakeArrayList.get(id).getMagnitude());
                    intent.putExtra("date", quakeArrayList.get(id).getDate());
                    intent.putExtra("latitude", quakeArrayList.get(id).getLatitude());
                    intent.putExtra("longitude", quakeArrayList.get(id).getLongitude());
                    intent.putExtra("depth", quakeArrayList.get(id).getDepth());
                    intent.putExtra("felt", quakeArrayList.get(id).getFelt());
                    startActivity(intent);
                }
            });
        }

    }
    private int getMarkerIndex(String index){
        int id = -1;
        try{
            id = Integer.parseInt(index.replace("m", ""));
        }catch(NumberFormatException nfe){
            Log.e("Error", nfe.getMessage());
        }
        return id;
    }
}
