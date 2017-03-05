package com.example.hayato.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by hayato on 2016/11/02.
 */

public class TestService extends Service{

    private Timer mTimer = null;
    Handler mHandler = new Handler();

    @Override
    public void onCreate() {
        Log.i("TestService", "onCreate");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("TestService", "onStartCommand");

        // タイマーの設定 1秒毎にループ
        mTimer = new Timer(true);
        mTimer.schedule( new TimerTask(){
            @Override
            public void run(){
                mHandler.post( new Runnable(){
                    public void run(){
                        Log.d( "TestService" , "Timer run" );
                    }
                });
            }
        }, 1000, 1000);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i("TestService", "onDestroy");

        // タイマー停止
        if( mTimer != null ){
            mTimer.cancel();
            mTimer = null;
        }
        Toast.makeText(this, "MyService　onDestroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i("TestService", "onBind");
        return null;
    }
}
