package com.rahul0596.quakealert;


import android.util.Log;

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

public class Utils {

    private Utils() {
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            return null;
        }
        return url;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);
        } catch (IOException e) {

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }


    public static ArrayList<Quake> extractEarthquakes(String stringJson) {
        Date dateObject;
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy\n h:mm a", Locale.ENGLISH);
        ArrayList<Quake> earthquakes = new ArrayList<>();
        try {
            URL url = createUrl(stringJson);
            String mqUrl = makeHttpRequest(url);
            JSONObject root = new JSONObject(mqUrl);
            JSONArray features = root.getJSONArray("features");
            for (int i = 0; i < features.length(); i++) {
                JSONObject earthquake = features.getJSONObject(i);
                JSONObject properties = earthquake.getJSONObject("properties");
                JSONObject geometry = earthquake.getJSONObject("geometry");
                JSONArray coordinates = geometry.getJSONArray("coordinates");
                double lat = coordinates.getDouble(0);
                double lon = coordinates.getDouble(1);
                double depth = coordinates.getDouble(2);
                double mag = properties.getDouble("mag");
                String loc = properties.getString("place");
                String title = properties.getString("title");
                String date = properties.getString("time");
                String felt = properties.getString("felt");
                long time = Long.parseLong(date);
                dateObject = new Date(time);
                String dateToDisplay = dateFormat.format(dateObject);
                earthquakes.add(new Quake(mag, loc, dateToDisplay, date, title, felt, lat, lon, depth));
            }

        } catch (JSONException e) {

            Log.e("Utils", "Problem parsing the earthquake JSON results", e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return earthquakes;
    }

}
