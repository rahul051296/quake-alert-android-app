package com.rahul0596.quakealert;


import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    Double latitude, longitude;
    TextView textView, magView, locView, dateView, effectView, messageView, timeView, feltView, depthView;
    String primaryLoc, LocOff, message, effect;
    SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.ENGLISH);
    SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.ENGLISH);
    Date dateObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quake_details);
        initmap();
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
        String title = bundle.getString("title");
        double mag = bundle.getDouble("mag");
        double dep = bundle.getDouble("depth");
        String dt = bundle.getString("date");
        String f = bundle.getString("felt");
        latitude = bundle.getDouble("latitude");
        longitude = bundle.getDouble("longitude");
        String[] parts = title.split(LOCATION_SEPARATOR);
        LocOff = parts[0];
        primaryLoc = parts[1];

        long tim = Long.parseLong(dt);
        dateObject = new Date(tim);
        String dateToDisplay = dateFormat.format(dateObject);
        String timeToDisplay = timeFormat.format(dateObject);

        textView.setText(primaryLoc);
        magView.setText(String.valueOf(mag));
        locView.setText(String.valueOf(longitude) + " , " + String.valueOf(latitude));
        dateView.setText(dateToDisplay);
        timeView.setText(timeToDisplay);
        depthView.setText(String.valueOf(dep) + " miles");
        feltView.setText("Felt by " + f + " people");
        a = (int) mag;

        switch (a) {
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
        effectView.setText("Ritcher Level - " + effect);
        messageView.setText(message);

        GradientDrawable magnitudeCircle = (GradientDrawable) magView.getBackground();
        QuakeAdapter quakeAdapter = new QuakeAdapter(this,null);
        int magnitudeColor = quakeAdapter.getMagnitudeColor(mag);
        magnitudeCircle.setColor(magnitudeColor);
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
