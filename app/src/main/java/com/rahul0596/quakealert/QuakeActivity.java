package com.rahul0596.quakealert;

import android.app.Dialog;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class QuakeActivity  extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Quake>> {
    ListView listView;
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?";
    TextView emptyView,emptyDesc;
    ImageView emptyImageView;
    ProgressBar progressBar;
    SwipeRefreshLayout mySwipeRefreshLayout;
    LoaderManager loaderManager;
    String myLat,myLon,myRadius;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quake);
        listView = (ListView) findViewById(R.id.listView);
        progressBar = (ProgressBar) findViewById(R.id.pbar);
        emptyView = (TextView) findViewById(R.id.empty_view);
        emptyDesc = (TextView) findViewById(R.id.empty_view_desc);
        emptyImageView = (ImageView) findViewById(R.id.empty_imageview);
        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        listView.setEmptyView(emptyView);
        listView.setEmptyView(emptyImageView);
        listView.setEmptyView(emptyDesc);
        loaderManager = getLoaderManager();
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        loaderManager.restartLoader(0, null,QuakeActivity.this);
                    }
                }
        );


        if (googleServicesAvailable()) {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                loaderManager.initLoader(0, null,QuakeActivity.this);
            }
            else{
                progressBar.setVisibility(View.GONE);
                emptyView.setText(R.string.noInternet);
                emptyDesc.setText(R.string.emptyDesc);
                emptyImageView.setImageResource(R.drawable.no_connection);
            }

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(QuakeActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        if(id == R.id.action_about) {
            Intent aboutIntent = new Intent(this,About.class);
            startActivity(aboutIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        Log.i("location",location);
        switch (location)
        {
            case "all":
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
                myRadius = "40";
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
        return new QuakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Quake>> loader,ArrayList<Quake> earthquakes) {
        emptyView.setText(R.string.emptyText);
        emptyDesc.setText(R.string.emptyview_desc);
        mySwipeRefreshLayout.setRefreshing(false);
        emptyImageView.setImageResource(R.drawable.happy_earth);
        if (earthquakes != null && !earthquakes.isEmpty()) {
            updateUi(earthquakes);
            emptyImageView.setVisibility(View.GONE);
            emptyView.setVisibility(View.GONE);
        }
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Quake>> loader) {
    }


    public boolean googleServicesAvailable() {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int isAvailable = api.isGooglePlayServicesAvailable(this);
        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (api.isUserResolvableError(isAvailable)) {
            Dialog dialog = api.getErrorDialog(this, isAvailable, 0);
            dialog.show();
        } else {
            Toast.makeText(this, "Cannot connect to Google Play Services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void updateUi(ArrayList<Quake> quakeArrayList) {

        final QuakeAdapter quakeAdapter = new QuakeAdapter(this, quakeArrayList);
        listView.setAdapter(quakeAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Quake currentQuake = quakeAdapter.getItem(position);
                Intent intent = new Intent(QuakeActivity.this, QuakeDetailsActivity.class);
                intent.putExtra("title", currentQuake.getTitle());
                intent.putExtra("mag", currentQuake.getMagnitude());
                intent.putExtra("date", currentQuake.getDate());
                intent.putExtra("latitude", currentQuake.getLatitude());
                intent.putExtra("longitude", currentQuake.getLongitude());
                intent.putExtra("depth", currentQuake.getDepth());
                intent.putExtra("felt", currentQuake.getFelt());
                startActivity(intent);
            }
        });
    }
}
