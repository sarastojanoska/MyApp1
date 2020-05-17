package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AsyncTask extends android.os.AsyncTask<Void, Void, String> {

    public Context mContext;
    public CheckMyNetwork networkcheck;
    private SharedPreferences prefs;
    public AsyncTask(Context context)
    {
        mContext = context;
    }

    @Override
    protected String doInBackground(Void... voids) {
        networkcheck = new CheckMyNetwork(mContext.getApplicationContext());

        Log.i("sara","do in background");
       // Random r = new Random();
        //int n = r.nextInt(51);

        // Make the task take long enough that we have
        // time to rotate the phone while it is running
        //int s = n * 200;

        // Sleep for the random amount of time

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

                for (int j=0; j<=(int)(600/jobPeriod);j++)
                {
                    makePing(host,count,packetSize);
                }
                try{
                    Thread.sleep(jobPeriod*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            i++;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  null;

    }
    @Override
    protected void  onPostExecute(String string) {
        super.onPostExecute(string);
    }

    public String makePing(String Host, int Count, int PacketSize) {
        String Ping = "";
        try {

        String pingCmd = "ping-c" + Count +"-s" + PacketSize+ " " + Host;
        //pingCmd = pingCmd + ;
        //pingCmd = pingCmd ;

        //String pingResult = "";
        Runtime runtime = Runtime.getRuntime();
        Process pping = runtime.exec(pingCmd);
        BufferedReader in = new BufferedReader(new InputStreamReader(pping.getInputStream()));
        String line = " ";
        while ((line = in.readLine()) != null) {
            Ping += line;
        }

        in.close();
        //if (pping != null) {
          //  pping.getOutputStream().close();
            //pping.getInputStream().close();
            //pping.getErrorStream().close();
        //}
            Log.i("sara" , Ping);
            prefs = mContext.getSharedPreferences("com.example.myapplication.Service",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            if(networkcheck.networkCheck())
            {
                NetworkUtils2.postInfo(Ping);
                if(prefs.getString("result1",null)!= null)
                {
                    NetworkUtils2.postInfo(prefs.getString("result1",null));
                    editor.putString("result1",null);
                }
                if(prefs.getString("result2",null)!= null)
                {
                    NetworkUtils2.postInfo(prefs.getString("result2",null));
                    editor.putString("result2",null);
                }
                else
                {
                    if((prefs.getString("result1",null)!=null ) && (prefs.getString("result1",null)!=null)
                            || (prefs.getString("result1",null)==null ) && (prefs.getString("result1",null)==null) ){
                        editor.putString("result1",Ping);
                    }else {
                        editor.putString("result2",Ping);
                    }
                    editor.apply();
                }
                }
            }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return Ping;
    }
        }



