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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity  implements OnMapReadyCallback, LoaderManager.LoaderCallbacks<ArrayList<Quake>>  {
    GoogleMap mGoogleMap;
    String myLat,myLon,myRadius;
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null,this);
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

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        String location = sharedPrefs.getString(
                getString(R.string.settings_location_key),
                getString(R.string.settings_location_default));
        switch (location)
        {    case "all":
            myLat="0";
            myLon="0";
            myRadius = "180";
            break;
            case "asia":
                myLat="34.047863";
                myLon="100.619655";
                myRadius = "40";
                break;
            case "australia":
                myLat="-25.274398";
                myLon="133.77513599999997";
                myRadius = "19";
                break;
            case "africa":
                myLat="-8.783195";
                myLon="34.50852299999997";
                myRadius = "30";
                break;
            case "europe":
                myLat="54.525961";
                myLon="15.255119";
                myRadius = "30";
                break;
            case "north_america":
                myLat="54.525961";
                myLon="-105.255119";
                myRadius = "55";
                break;
            case "south_america":
                myLat="-35.675147";
                myLon="-71.54296899999997";
                myRadius = "55";
                break;
        }
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "30");
        uriBuilder.appendQueryParameter("minmagnitude", minMagnitude);
        uriBuilder.appendQueryParameter("orderby",orderBy);
        uriBuilder.appendQueryParameter("latitude",myLat);
        uriBuilder.appendQueryParameter("longitude",myLon);
        uriBuilder.appendQueryParameter("maxradius",myRadius);
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

            createMarker(quakeArrayList.get(i).getLatitude(), quakeArrayList.get(i).getLongitude(), quakeArrayList.get(i).getMagnitude(), quakeArrayList.get(i).getLocation(), quakeArrayList.get(i).getDate());
        }

    }
    protected Marker createMarker(double latitude, double longitude, double magnitude, String location, String date) {

        Marker marker =  mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(longitude, latitude))
                .snippet(location)
                .title("Magnitude - "+String.valueOf(magnitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                ));
        return marker;
    }
}
