package com.rahul0596.quakealert;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static java.lang.Math.floor;


public class QuakeAdapter extends ArrayAdapter<Quake> {
    private static final String LOCATION_SEPARATOR = " of ";
    private static final String METRIC_MAG = "km";
    String depth;
    double depthCon, depthRound, distConvert;

    public QuakeAdapter(Context context, ArrayList<Quake> quakes) {

        super(context, 0, quakes);
    }

    @Nullable
    @Override
    public View getView(int position, @Nullable View convertView, ViewGroup parent) {
        View listItemView = convertView;
        listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_view, parent, false);
        Quake currentQuake = getItem(position);
        TextView magText = (TextView) listItemView.findViewById(R.id.magnitude);
        magText.setText(String.valueOf(currentQuake.getMagnitude()));


        GradientDrawable magnitudeCircle = (GradientDrawable) magText.getBackground();
        int magnitudeColor = getMagnitudeColor(currentQuake.getMagnitude());
        int magnitudeStroke = getMagnitudeStrokeColor(currentQuake.getMagnitude());
        magnitudeCircle.setColor(magnitudeColor);
        magnitudeCircle.setStroke(8, magnitudeStroke);


        String originalLocation = currentQuake.getLocation();
        String primaryLocation;
        String locationOffset;
        String distanceOffset, directionOffset;
        double distCon;
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String du = sharedPrefs.getString("distance_units", "kilometers");
        Log.i("DU", du);

        if (originalLocation.contains(LOCATION_SEPARATOR)) {

            String[] parts = originalLocation.split(LOCATION_SEPARATOR);
            locationOffset = parts[0];
            switch (du) {
                case "kilometers":
                    locationOffset = parts[0] + LOCATION_SEPARATOR;
                    break;

                case "miles":
                    if (locationOffset.contains(METRIC_MAG)) {

                        String[] units = locationOffset.split(METRIC_MAG);
                        distanceOffset = units[0];
                        directionOffset = units[1];
                        distCon = Double.parseDouble(distanceOffset);
                        distCon = distCon * 0.621371;
                        int distConver = (int) distCon;
                        distanceOffset = String.valueOf(distConver);
                        locationOffset = distanceOffset + "MI" + directionOffset + LOCATION_SEPARATOR;
                    } else {
                        locationOffset = parts[0] + LOCATION_SEPARATOR;
                    }
                    break;
            }


            primaryLocation = parts[1];


        } else {

            locationOffset = getContext().getString(R.string.near_the);

            primaryLocation = originalLocation;
        }


        switch (du) {
            case "kilometers":
                depth = String.valueOf(currentQuake.getDepth()) + "km";
                break;
            case "miles":
                depthCon = currentQuake.getDepth();
                depthCon = depthCon * 0.621371;
                depthRound = Math.round(depthCon * 100.0) / 100.0;
                depth = String.valueOf(depthRound + "mi");
                break;
        }

        TextView primaryLocationView = (TextView) listItemView.findViewById(R.id.location_main);

        primaryLocationView.setText(primaryLocation);

        TextView locationOffsetView = (TextView) listItemView.findViewById(R.id.location);

        locationOffsetView.setText(locationOffset);

        TextView depthView = (TextView) listItemView.findViewById(R.id.depth);

        depthView.setText("Depth - " + depth);

        TextView dateText = (TextView) listItemView.findViewById(R.id.date);
        dateText.setText(currentQuake.getTime());

        return listItemView;


    }

    public int getMagnitudeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10plus;
                break;
        }

        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }

    public int getMagnitudeStrokeColor(double magnitude) {
        int magnitudeColorResourceId;
        int magnitudeFloor = (int) floor(magnitude);
        switch (magnitudeFloor) {
            case 0:
            case 1:
                magnitudeColorResourceId = R.color.magnitude1s;
                break;
            case 2:
                magnitudeColorResourceId = R.color.magnitude2s;
                break;
            case 3:
                magnitudeColorResourceId = R.color.magnitude3s;
                break;
            case 4:
                magnitudeColorResourceId = R.color.magnitude4s;
                break;
            case 5:
                magnitudeColorResourceId = R.color.magnitude5s;
                break;
            case 6:
                magnitudeColorResourceId = R.color.magnitude6s;
                break;
            case 7:
                magnitudeColorResourceId = R.color.magnitude7s;
                break;
            case 8:
                magnitudeColorResourceId = R.color.magnitude8s;
                break;
            case 9:
                magnitudeColorResourceId = R.color.magnitude9s;
                break;
            default:
                magnitudeColorResourceId = R.color.magnitude10pluss;
                break;
        }

        return ContextCompat.getColor(getContext(), magnitudeColorResourceId);
    }

}
