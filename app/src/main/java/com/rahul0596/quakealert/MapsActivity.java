package com.rahul0596.quakealert;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity  implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<ArrayList<Quake>>  {
    GoogleMap mGoogleMap;
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null,this);
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, QuakeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        LatLng lg = new LatLng(0, -100);
        CameraUpdate update = CameraUpdateFactory.newLatLng(lg);
        mGoogleMap.moveCamera(update);
    }

    @Override
    public Loader<ArrayList<Quake>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "30");
        uriBuilder.appendQueryParameter("minmagnitude", minMagnitude);
        uriBuilder.appendQueryParameter("orderby",orderBy);
        Log.i("URL",uriBuilder.toString());
        return new QuakeActivity.QuakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Quake>> loader,ArrayList<Quake> earthquakes) {

        updateUi(earthquakes);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Quake>> loader) {
    }



    public static class QuakeLoader extends AsyncTaskLoader<ArrayList<Quake>> {
        String mUrl;

        public QuakeLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public ArrayList<Quake> loadInBackground() {
            return Utils.extractEarthquakes(mUrl);
        }
    }

    public void updateUi(ArrayList<Quake> quakeArrayList)
    {
        Log.i("ArrayList",String.valueOf(quakeArrayList.size()));
        for(int i = 0 ; i < quakeArrayList.size() ; i++ ) {

            createMarker(quakeArrayList.get(i).getLatitude(), quakeArrayList.get(i).getLongitude(), quakeArrayList.get(i).getMagnitude(), quakeArrayList.get(i).getLocation());
        }

    }
    protected Marker createMarker(double latitude, double longitude, double magnitude, String location) {

        Marker marker =  mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(longitude, latitude))
                .snippet(location)
                .title("Magnitude - "+String.valueOf(magnitude)));
        return marker;
    }
}
