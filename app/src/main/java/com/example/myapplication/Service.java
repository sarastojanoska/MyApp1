package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;


public class Service extends android.app.Service{
    protected static final int NOTIFICATION_ID = 1337;
    private static String TAG = "Service";
    private static Service mCurrentService;
    private int counter = 0;
    public CheckMyNetwork check;
    public Context context;
    public int pingNumber = 0;
    public final static int INTERVAL = 60*1000;

    public Service() { super(); }

    @Override
    public void onCreate(){
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            restartForeground();
        }
        mCurrentService = this;
    }

    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        super.onStartCommand(intent,flags,startId);
        Log.d(TAG, "restarting Service!!");
        check = new CheckMyNetwork(this);
        boolean network = check.networkCheck();
        //counter = 0;
        if(network){
            Log.i("sara","Connected!!");
            //sleepDelay();
            Thread t = new Thread(){
                public void run(){
                    taskDelayLoop();
                }
            };
            t.start();

        }
        else{
            Log.i("sara","Not Connected!!");
        }
        Log.i("makeping","onStartCommand() is called");
        SharedPreferences prefs= getSharedPreferences("com.example.myapplication.ActiveServiceRunning", MODE_PRIVATE);

        if(prefs.getInt("timeCounter",0)!=0) {
            int timeCounter = prefs.getInt("counter", 0);
        }
        if(intent == null){
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(this);
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            restartForeground();
        }

        startTimer();

        return START_STICKY;
    }
    public void taskDelayLoop(){
        while(true){

            new AsyncTask(getApplicationContext()).execute();
            Log.i("MAKEPING","doInBackground");
            try{
                Thread.sleep(INTERVAL);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void restartForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG,"restarting foreground");
            try {
                Notification notification = new Notification();
                startForeground(NOTIFICATION_ID, notification.setNotification(this,"Service notification","This is the service's notification",R.drawable.ic_filter_vintage_black_24dp));
                startTimer();
            } catch (Exception e) {
                Log.e(TAG,"Error in notification" + e.getMessage());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy called");
        try {
            SharedPreferences prefs = getSharedPreferences("com.example.qoscheckapp.ActiveServiceRunning", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            int timeCounter = 0;
            editor.putInt("timeCounter", timeCounter);
            editor.apply();

        } catch (NullPointerException e) {
            Log.e("SERVER", "Error " +e.getMessage());

        }
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        stoptimertask();
        try{

        } catch (NullPointerException e) {
            Log.i("Server","are you testing?"+ e.getMessage());
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG,"onTaskRemoved called");

        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
    }
    //public void sleepDelay(){
      //  final Handler handler = new Handler();
       // new AsyncTask().execute();
        //new Runnable() {
         //   @Override
           // public void run() {
             //   handler.postDelayed(this,INTERVAL);
               // Log.i("brr" , "");
                //new AsyncTask().execute();
            //}
        //}.run();
    //}


    private static Timer timer;
    private static TimerTask timerTask;
    long oldTime = 0;

    public void startTimer() {
        Log.i(TAG, "Starting timer");
        //new AsyncTask().execute();
        stoptimertask();
        timer = new Timer();

        initializeTimerTask();

        Log.i(TAG,"Scheduling...");
        timer.schedule(timerTask,1000,1000);
    }
    public void initializeTimerTask() {
        Log.i(TAG,"initialising timerTask");
        timerTask = new TimerTask() {
            public void run() {
                Log.i("in timer", "in timer ++++  " + (counter++));

            }
        };
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    public static Service getmCurrentService() { return mCurrentService; }

    public static void setmCurrentService(Service mCurrentService) {
        Service.mCurrentService = mCurrentService;
    }
}
