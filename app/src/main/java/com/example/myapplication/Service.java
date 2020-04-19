package com.example.myapplication;

import android.content.Intent;
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
        counter = 0;

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
            } catch (Exception e) {
                Log.e(TAG,"Error in notification" + e.getMessage());
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy called");
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
        stoptimertask();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(TAG,"onTaskRemoved called");

        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        sendBroadcast(broadcastIntent);
    }

    private static Timer timer;
    private static TimerTask timerTask;
    long oldTime = 0;

    public void startTimer() {
        Log.i(TAG, "Starting timer");

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
