package com.example.hayato.hallo2;

/**
 * Created by hayato on 2017/03/15.
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static java.security.AccessController.getContext;
import static java.sql.Types.NULL;

public class BTtest0315 extends Service  {
    private static final String TAG = "BluetoothSample";
    private BluetoothAdapter mAdapter;
    private BluetoothDevice mDevice;
    /* Bluetooth UUID */
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final String DEVICE_NAME = "SBDBT-001bdc068047";
    private BluetoothSocket mSocket;
    private Thread mThread;
    private boolean isRunning;
    private Button connectButton;
    private Button writeButton;
    private TextView mStatusTextView;
    private TextView mInputTextView;
    private static final int VIEW_STATUS = 0;
    private static final int VIEW_INPUT = 1;
    private boolean connectFlg = false;

    OutputStream mmOutputStream = null;
    InputStream mmInStream = null;
    Globals globals;

    @Override
    public void onCreate() {
        Log.i("BT", "onCreate");
        globals = (Globals) this.getApplication();
        globals.GlobalsAllInit();
        globals.globalContext = getApplication();


        mAdapter = BluetoothAdapter.getDefaultAdapter();
        //mStatusTextView.setText("SearchDevice");
        Set<BluetoothDevice> devices = mAdapter.getBondedDevices();
        for (BluetoothDevice device : devices) {

            if (device.getName().equals(DEVICE_NAME)) {
               // mStatusTextView.setText("find: " + device.getName());
                mDevice = device;
            }
        }
    }

    protected void onPause(){
        isRunning = false;
        try{
            mSocket.close();
        }
        catch(Exception e){}
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                globals = (Globals) globals.globalContext;

                Log.i("BT_test", "Run");
                mmInStream = null;
                Message valueMsg = new Message();
                //valueMsg.what = VIEW_STATUS;
              //  valueMsg.obj = "connectiong...";
             //   mHandler.sendMessage(valueMsg);

                try {

                    // 取得したデバイス名を使ってBluetoothでSocket接続
                    mSocket = mDevice.createInsecureRfcommSocketToServiceRecord(
                            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
//                mSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
//                mSocket = mDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                    mSocket.connect();
                    mmInStream = mSocket.getInputStream();
                    mmOutputStream = mSocket.getOutputStream();


                    byte[] buffer = new byte[1024];

                    int bytes;
                    valueMsg = new Message();
                    valueMsg.what = VIEW_STATUS;
                   // valueMsg.obj = "connected..";
                   // mHandler.sendMessage(valueMsg);
                    isRunning = true;
                    connectFlg = true;


                    // データ保存用？
                    SharedPreferences dataStore = getSharedPreferences("DataStore",MODE_APPEND);
                    SharedPreferences.Editor editor = dataStore.edit();
                    int tmp = 0;

                    BufferedReader reader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));

                    while (isRunning) {
 //                       bytes = mmInStream.read(buffer);
 //                       Log.i(TAG, "bytes=" + bytes);
 //                       final String readMsg = new String(buffer, 0, bytes);
 //                       if (readMsg.trim() != null && !readMsg.trim().equals("")) {
 //                           Log.i(TAG, "value=" + readMsg.trim());
 //                           valueMsg = new Message();
 //                           valueMsg.what = VIEW_INPUT;
 //                           valueMsg.obj = readMsg;
//
 //                           editor.putString("input",readMsg);
//                            editor.commit();
//                            //  mHandler.sendMessage(valueMsg);
//                        } else {
//                            Log.i(TAG, "value=nodata");
//                        }

                        String t_st="a";
                        int t = 0;
                        while((t_st = reader.readLine())!=null){
                            Log.i("BT",t_st);
                            Log.d("BT","count:" + t);
                            t++;
                        }


                    }
                } catch (Exception e) {
                 //   valueMsg = new Message();
                 //   valueMsg.what = VIEW_STATUS;
                 //   valueMsg.obj = "Error1:" + e;
                 //   mHandler.sendMessage(valueMsg);
                    try {
                        mSocket.close();
                    } catch (Exception ee) {
                    }
                    isRunning = false;
                    connectFlg = false;

                }
            }
        }).start();
        return START_STICKY;
    }

    @Override
    public void onDestroy(){
        Log.i("CommandGen","onDestroy");
        Toast.makeText(this, "MyService?onDestroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent arg0){
        Log.i("CommandGen","onBind");
        return null;
    }


  //  Handler mHandler = new Handler(){
  //      @Override
  //      public void handleMessage(Message msg){
  //          int action = msg.what;
  //          String msgStr = (String)msg.obj;
  //          if(action == VIEW_INPUT){
  //              mInputTextView.setText(msgStr);
  //          }else if(action == VIEW_STATUS){
  //              mStatusTextView.setText(msgStr);
  //          }
  //      }
  //  };
//

}
