package com.rahul0596.quakealert;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class QuakeDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String LOCATION_SEPARATOR = " - ";
    GoogleMap mGoogleMap;
    int a;
    double mag,dep;
    Double latitude, longitude;
    TextView textView, magView, locView, dateView, effectView, messageView, timeView, feltView, depthView;
    String primaryLoc, LocOff, message, effect, title, dt,f, dateToDisplay,timeToDisplay;
    SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
    SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
    Date dateObject;
    ImageView cal,clock,location,flash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String themes = sharedPrefs.getString(
                getString(R.string.settings_themes_key),
                getString(R.string.settings_themes_default));
        switch (themes)
        {
            case "dark" : setTheme(R.style.MyTheme);
                break;
            case "light" : setTheme(R.style.MyTheme_Light);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quake_details);
        initmap();
        cal = (ImageView) findViewById(R.id.event);
        clock = (ImageView) findViewById(R.id.clock);
        location = (ImageView) findViewById(R.id.location_pin);
        flash = (ImageView) findViewById(R.id.flash);
        switch (themes)
        {
            case "light" :
                cal.setImageResource(R.drawable.ic_event_black_24dp);
                cal.setAlpha((float) 0.6);
                clock.setImageResource(R.drawable.ic_access_time_black_24dp);
                clock.setAlpha((float) 0.6);
                location.setImageResource(R.drawable.ic_location_on_black_24dp);
                location.setAlpha((float) 0.6);
                flash.setImageResource(R.drawable.ic_flash_on_black_24dp);
                flash.setAlpha((float)0.6);
                break;
        }
        textView = (TextView) findViewById(R.id.details);
        timeView = (TextView) findViewById(R.id.time);
        magView = (TextView) findViewById(R.id.magnitude);
        locView = (TextView) findViewById(R.id.location);
        dateView = (TextView) findViewById(R.id.date);
        feltView = (TextView) findViewById(R.id.felt);
        depthView = (TextView) findViewById(R.id.depth);
        effectView = (TextView) findViewById(R.id.effect);
        messageView = (TextView) findViewById(R.id.message);

        Bundle bundle = getIntent().getExtras();
        title = bundle.getString("title");
        mag = bundle.getDouble("mag");
        dep = bundle.getDouble("depth");
        dt = bundle.getString("date");
        f = bundle.getString("felt");
        latitude = bundle.getDouble("latitude");
        longitude = bundle.getDouble("longitude");
        String[] parts = title.split(LOCATION_SEPARATOR);
        LocOff = parts[0];
        primaryLoc = parts[1];

        long tim = Long.parseLong(dt);
        dateObject = new Date(tim);
        dateToDisplay = dateFormat.format(dateObject);
        timeToDisplay = timeFormat.format(dateObject);

        textView.setText(primaryLoc);
        magView.setText(String.valueOf(mag));
        locView.setText(String.valueOf(longitude) + " , " + String.valueOf(latitude));
        dateView.setText(dateToDisplay);
        timeView.setText(timeToDisplay);
        depthView.setText(String.valueOf(dep) + " miles");

        if(f.equals("null") || f.equals("0"))
         feltView.setText("No one reportedly felt it.");
        else if(f.equals("1"))
            feltView.setText(f +" person reportedly felt it.");
        else
            feltView.setText(f + " people reportedly felt it.");

        a = (int) mag;

        switch (a) {
            case 0:
                message = "Can be detected only by Seismograph";
                effect = "Instrumental";
            case 1:
                message = "Can be detected only by Seismograph";
                effect = "Instrumental";
                break;
            case 2:
                effect = "Feeble";
                message = "Can be noticed only by sensitive people";
                break;
            case 3:
                effect = "Slight";
                message = "Can resemble the vibrations caused by heavy traffic";
                break;
            case 4:
                effect = "Moderate";
                message = "Very moderate earthquake, can be felt by few people";
                break;
            case 5:
                effect = "Rather Strong";
                message = "Can awaken people who are sleeping";
                break;
            case 6:
                effect = "Strong";
                message = "Trees sway, some damage from overturning and falling objects";
                break;
            case 7:
                effect = "Very Strong";
                message = "Can trigger general alarms and could crack walls";
                break;
            case 8:
                effect = "Destructive";
                message = "Chimneys could fall and there will be some damage to buildings";
                break;
            case 9:
                effect = "Ruinous";
                message = "Ground begins to crack, houses begin to collapse and pipes reak";
                break;
            case 10:
                effect = "Disastrous";
                message = "Grounds are badly cracked, and many buildings are destroyed and possible landslides.";
                break;
            default:
                effect = "Catastrophic";
                message = "You wouldn't be alive to read this.";
                break;
        }
        effectView.setText("Richter Level - " + effect);
        messageView.setText(message);

        GradientDrawable magnitudeCircle = (GradientDrawable) magView.getBackground();
        QuakeAdapter quakeAdapter = new QuakeAdapter(this,null);
        int magnitudeColor = quakeAdapter.getMagnitudeColor(mag);
        magnitudeCircle.setColor(magnitudeColor);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBodyText = "An earthquake of magnitude "+mag+ " has been recorded in the region "+ primaryLoc +" on "+dateToDisplay+" at "+timeToDisplay+"\n\nGet instant updates about earthquakes on Quake Alert!! (https://goo.gl/Mhjcya)";

                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Quake Alert!!");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
                startActivity(Intent.createChooser(sharingIntent, "Share"));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void initmap() {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        goToLocation(longitude, latitude, 7);
        mGoogleMap.addCircle(new CircleOptions()
                .center(new LatLng(longitude, latitude))
                .radius(40000)
                .strokeColor(Color.argb(00, 100, 100, 125))
                .fillColor(Color.argb(100, 255, 0, 0)));
    }

    private void goToLocation(double lon, double lat, int zoom) {
        LatLng lg = new LatLng(lon, lat);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(lg, zoom);
        mGoogleMap.animateCamera(update);
    }

}
