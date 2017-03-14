package com.example.hayato.hallo2;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ToggleButton;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.logging.Handler;

public class MainActivity extends Activity{
    Button btnStart;
    Button btnStop;
    Button btnDebugg;
    Button btnStopDebugg;
    Button btnBLE;
    Button btnBT;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("MainActivity","onCreate");
        btnStart = (Button)findViewById(R.id.Start_S);
        btnStop = (Button)findViewById(R.id.Stop_S);
        btnDebugg = (Button)findViewById(R.id.Start_D);
        btnStopDebugg = (Button)findViewById(R.id.Stop_D);
        btnBT = (Button)findViewById(R.id.Start_BT);

        btnStart.setOnClickListener( new ServiceOnClickListener());
        btnStop.setOnClickListener( new ServiceOnClickListener());
        btnDebugg.setOnClickListener( new ServiceOnClickListener());
        btnStopDebugg.setOnClickListener( new ServiceOnClickListener());
        btnBT.setOnClickListener( new ServiceOnClickListener());
    }

    class ServiceOnClickListener implements OnClickListener{
        @Override
        public void onClick(View view){
            Log.i("MainActivity", "onClick");
            if(view == btnStart){
                Log.i("MainActivity","btnStart");
                startService(new Intent(MainActivity.this,TestService.class));
            }else if(view == btnStop){
                Log.i("MainActivity","btnStop");
                stopService(new Intent(MainActivity.this,TestService.class));

                //以下省略
            }else if(view == btnDebugg){
                Log.i("MainActivity","btnDebugg");
                startService(new Intent(MainActivity.this,BTtest0315.class));
                startService(new Intent(MainActivity.this,CommandGen.class));
            }else if(view == btnStopDebugg){
                stopService(new Intent(MainActivity.this,BTtest0315.class));
                stopService(new Intent(MainActivity.this,CommandGen.class));
            }else if(view == btnBLE){

            }else if(view == btnBT){
                Log.i("MainActivity","btnBT");
                startActivity(new Intent(MainActivity.this,BT_test.class));
            }
        }
    }

}