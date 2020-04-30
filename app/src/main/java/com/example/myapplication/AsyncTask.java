package com.example.myapplication;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public class AsyncTask extends android.os.AsyncTask<Void, Void, String> {


    @Override
    protected String doInBackground(Void... voids) {
        Random r = new Random();
        int n = r.nextInt(51);

        // Make the task take long enough that we have
        // time to rotate the phone while it is running
        int s = n * 200;

        // Sleep for the random amount of time
        try{
            Thread.sleep(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            String data = NetworkUtils.getInfo();
            Log.i("Sara",data);
            JSONArray itemsArray = new JSONArray(data);
            int i = 0;


            while (i < itemsArray.length()){
                JSONObject sara = itemsArray.getJSONObject(i);
                Log.i("sara","json ="+sara.toString());
                String date = sara.getString("jobType");
                Log.i("sara","type ="+date);
                String host = sara.getString("host");
                Log.i("sara","host ="+host);
                int count = sara.getInt("count");
                Log.i("sara","count ="+count);
                int packetSize = sara.getInt("packetSize");
                Log.i("sara","packetSize ="+packetSize);
                int jobPeriod = sara.getInt("jobPeriod");
                Log.i("sara","jobPeriod ="+jobPeriod);
                String jobType = sara.getString("jobType");
                Log.i("sara","jobType ="+jobType);

                String pingCmd = "ping-c"+ count;
                pingCmd = pingCmd + "-s" +packetSize;
                pingCmd = pingCmd + " " + host;

                String pingResult = "";
                Process pping = Runtime.getRuntime().exec(pingCmd);
                BufferedReader in = new BufferedReader(new InputStreamReader(pping.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null){
                    pingResult += inputLine;
                }

                in.close();
                if(pping != null){
                    pping.getOutputStream().close();
                    pping.getInputStream().close();
                    pping.getErrorStream().close();
                }
                Log.i("sara","pingResult =" +pingResult);
                return pingResult;
            }
            i++;

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        // Return a String result
        return "Awake at last after sleeping for " + s + " milliseconds!";

    }
    protected void  onPostExecute(String string) {
        super.onPostExecute(string);
    }
}
