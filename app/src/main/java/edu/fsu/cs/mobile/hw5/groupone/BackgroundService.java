package edu.fsu.cs.mobile.hw5.groupone;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class BackgroundService extends Service {

    private Boolean isRunning;
    private Context context;
    private Thread backgroundThread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //super.onCreate();

        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }

    private Runnable myTask = new Runnable() {
        @Override
        public void run() {
            //initiate onDestroy method here
            System.out.println("BACKGROUND SERVICE IS RUNNING");
            Log.i("TAG_1","BACKGROUND SERVICE IS RUNNING");
            //stopSelf();
        }
    };

    @Override
    public void onDestroy() {
        //super.onDestroy();

        this.isRunning = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //return super.onStartCommand(intent, flags, startId);

        if(!this.isRunning){
            this.isRunning = true;
            this.backgroundThread.start();
        }

        return START_STICKY;
    }







}
