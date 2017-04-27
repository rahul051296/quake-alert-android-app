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

    public Quake(double mag, String loc, String t, String d, String u, String f, double lati, double longi, double dep)
    {
        magnitude = mag;
        location = loc;
        time = t;
        date = d;
        url = u;
        felt = f;
        latitude = lati;
        longitude = longi;
        depth = dep;
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
