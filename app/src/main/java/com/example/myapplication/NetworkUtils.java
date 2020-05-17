package com.example.myapplication;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {
    private static final String MY_BASE_URL = "http://192.168.0.101:5000/getjobs/hardware";


    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    static String getInfo() {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String JSONString = null;
        try {
            Uri bultURI = Uri.parse(MY_BASE_URL).buildUpon().build();
            Log.i("Sara", "connecting to" + bultURI);
            URL requestURL = new URL(bultURI.toString());

            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();

            StringBuilder builder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
            if (builder.length() == 0) {
                // Stream was empty.  No point in parsing.
                // return null;
                return null;
            }


            // Catch errors.
        } catch (IOException e) {
            e.printStackTrace();

            // Close the connections.
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.i("sara","getJSON:" +JSONString);
        return JSONString;
    }
}
