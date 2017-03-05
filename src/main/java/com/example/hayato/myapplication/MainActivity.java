package com.example.hayato.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import static android.R.attr.id;


//+ implements Runnable
public class MainActivity extends AppCompatActivity {



    //private final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

    //LinearLayout ll;
    Button btnStart;
    Button btnStop;
    //TextView text ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("MainActivity", "onCreate");
        setContentView(R.layout.activity_main);

//        ll = new LinearLayout(this);
//        ll.setGravity( Gravity.CENTER_VERTICAL);
//        ll.setOrientation( LinearLayout.VERTICAL );

        btnStart = (Button) findViewById(R.id.Start_S);
        btnStop  = (Button) findViewById(R.id.Stop_S);

       // text = new TextView(this);
       // text.setText("Text_test");
       // ll.addView(text, WC);
//
        btnStart.setOnClickListener( new ServiceOnClickListener() );
        btnStop.setOnClickListener( new ServiceOnClickListener() );
       // btnStart.setText("Service Start");
       // btnStop.setText("Service Stop");
       // ll.addView( btnStart,  WC );
       // ll.addView( btnStop ,  WC );
       // setContentView(ll);
    }

    class ServiceOnClickListener implements Button.OnClickListener{

        @Override
        public void onClick(View view) {
            if( view == btnStart ){
                // サービス開始
                startService( new Intent( MainActivity.this, TestService.class ) );
            }else if( view == btnStop ){
                // サービス停止
                stopService( new Intent( MainActivity.this, TestService.class ) );
            }
        }

    }



}


