package com.example.myapplication;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils2 {
    private static final String  MY_BASE_URL= "http://192.168.0.101:5000/postresults";

    private static final String LOG_TAG = NetworkUtils2.class.getSimpleName();

    static String postInfo(String ping) throws IOException{
       String result = "result";
        String response = null;
            java.net.URL url = new URL(MY_BASE_URL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept","application/json");
            con.setDoOutput(true);
            String jsonInputString = "{\"result\": "+ "\"" + ping + "\"}";
            try(OutputStream os = con.getOutputStream())
            {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            try(BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(),"utf-8")))
            {
                StringBuilder responseString = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null){
                    responseString.append(responseLine.trim());
                }
                response = responseString.toString();
                Log.i("sara","postJSON:" +response);
            }



        return jsonInputString;
    }
}
