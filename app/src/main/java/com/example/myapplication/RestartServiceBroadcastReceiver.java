package com.example.myapplication;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class RestartServiceBroadcastReceiver extends BroadcastReceiver {
    public static final String TAG = RestartServiceBroadcastReceiver.class.getSimpleName();
    private static JobScheduler jobScheduler;
    private RestartServiceBroadcastReceiver restartServiceBroadcastReceiver;

    public static long getVersionCode(Context context){
        PackageInfo pInfo;
        try{
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
            long versionCode = System.currentTimeMillis();
            return versionCode;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return 0;
    }
    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(TAG,"about to start timer" + context.toString());
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            scheduleJob(context);
        } else {
            registerRestarterReceiver(context);
            ProcessMainClass bck = new ProcessMainClass();
            bck.launchService(context);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void scheduleJob(Context context) {
        if(jobScheduler == null) {
            jobScheduler = (JobScheduler) context
                    .getSystemService(Context.JOB_SCHEDULER_SERVICE);
        }
        ComponentName componentName = new ComponentName(context,
                JobService.class);
        JobInfo jobInfo = new JobInfo.Builder(1,componentName)
                .setOverrideDeadline(0)
                .setPersisted(true).build();
        jobScheduler.schedule(jobInfo);
    }

    public static void reStartTracker(Context context) {
        Log.i(TAG,"Restarting tracker");
        Intent broadcastIntent = new Intent(Globals.RESTART_INTENT);
        context.sendBroadcast(broadcastIntent);
    }

    public void registerRestarterReceiver(final Context context) {
        if(restartServiceBroadcastReceiver == null)
            restartServiceBroadcastReceiver = new RestartServiceBroadcastReceiver();
        else try{
            context.unregisterReceiver(restartServiceBroadcastReceiver);
        } catch (Exception e) {

        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                IntentFilter filter = new IntentFilter();
                filter.addAction(Globals.RESTART_INTENT);
                try {
                    context.registerReceiver(restartServiceBroadcastReceiver, filter);
                } catch (Exception e) {
                    try {
                        context.getApplicationContext().registerReceiver(restartServiceBroadcastReceiver, filter);
                    } catch (Exception ex) {

                    }
                }
            }
        },1000);
    }
}
