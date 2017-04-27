package com.rahul0596.quakealert;

public class Quake {

    private double magnitude;

    private String location;

    private String time;

    private String date;

    private String url;

    private String felt;

    private double latitude;

    private double longitude;

    private double depth;

    public Quake(double lMagnitude, String lLocation, String lTime, String lDate, String lUrl, String lFelt, double lLatitude, double lLongitude, double lDepth)
    {
        magnitude = lMagnitude;
        location = lLocation;
        time = lTime;
        date = lDate;
        url = lUrl;
        felt = lFelt;
        latitude = lLatitude;
        longitude = lLongitude;
        depth = lDepth;
    }

    public double getMagnitude() { return magnitude;}
    public String getLocation() { return location; }
    public String getTime() { return time; }
    public String getDate() { return  date; }
    public String getTitle() { return url; }
    public String getFelt() {return felt;}
    public double getLatitude() {return latitude;}
    public double getLongitude() {return longitude;}
    public double getDepth() {return depth;}
}
