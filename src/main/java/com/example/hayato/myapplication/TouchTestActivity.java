package com.example.hayato.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;



/**
 * Created by hayato on 2016/11/05.
 */

public class TouchTestActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //簡単なタッチイベント処理
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch ( event.getAction() ) {

            case MotionEvent.ACTION_DOWN:
                //画面がタッチされたときの動作
                Log.d( "TestService" , "Touch!!!" );
                break;

            case MotionEvent.ACTION_MOVE:
                //タッチしたまま移動したときの動作
                break;

            case MotionEvent.ACTION_UP:
                //タッチが離されたときの動作
                break;

            case MotionEvent.ACTION_CANCEL:
                //他の要因によってタッチがキャンセルされたときの動作
                break;

        }

        return super.onTouchEvent(event);
    }

}
