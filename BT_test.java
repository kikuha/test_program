package com.example.hayato.hallo2;

/**
 * Created by KIKUTA_NOTE on 2017/03/10.
 */
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
//?
//import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BT_test extends Activity implements Runnable,View.OnClickListener{

    private static final String TAG = "BluetoothSample";
    private BluetoothAdapter mAdapter;
    private BluetoothDevice mDevice;
    /* Bluetooth UUID */
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final String DEVICE_NAME = "Galaxy S7 edge";
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

    BluetoothConnector BC;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectButton = (Button)findViewById(R.id.Start_S);
        writeButton = (Button)findViewById(R.id.Start_D);
        connectButton.setOnClickListener(this);
        writeButton.setOnClickListener(this);

        globals = (Globals) this.getApplication();
        globals.GlobalsAllInit();

        mInputTextView = (TextView) findViewById(R.id.BT_data);
        mStatusTextView = (TextView) findViewById(R.id.BT_status);


        Log.i("BT_test", "onCreate");


        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mStatusTextView.setText("SearchDevice");
        Set<BluetoothDevice> devices = mAdapter.getBondedDevices();
        for (BluetoothDevice device : devices) {

            if (device.getName().equals(DEVICE_NAME)) {
                mStatusTextView.setText("find: " + device.getName());
                mDevice = device;
            }
        }

        //BC = new BluetoothConnector(mDevice,true,mAdapter,)
    }

    @Override
    protected void onPause(){
        super.onPause();

        isRunning = false;
        try{
            mSocket.close();
        }
        catch(Exception e){}
    }

    @Override
    public void run(){
            Log.i("BT_test","Run");
            mmInStream = null;
            Message valueMsg = new Message();
            valueMsg.what = VIEW_STATUS;
            valueMsg.obj = "connectiong...";
            mHandler.sendMessage(valueMsg);

            try{
                // 取得したデバイス名を使ってBluetoothでSocket接続
                mSocket = mDevice.createRfcommSocketToServiceRecord(
                      UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
//                mSocket = mDevice.createRfcommSocketToServiceRecord(MY_UUID);
//                mSocket = mDevice.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                mSocket.connect();
                mmInStream = mSocket.getInputStream();
                mmOutputStream = mSocket.getOutputStream();


                byte[] buffer = new byte[1024];

                int bytes;
                valueMsg =new Message();
                valueMsg.what = VIEW_STATUS;
                valueMsg.obj = "connected..";
                mHandler.sendMessage(valueMsg);
                isRunning = true;
                connectFlg = true;

                while(isRunning){
                    bytes = mmInStream.read(buffer);
                    Log.i(TAG,"bytes="+bytes);

                    final String readMsg = new String(buffer,0,bytes);
                    if(readMsg.trim() != null && !readMsg.trim().equals("")){
                        Log.i(TAG,"value="+readMsg.trim());
                        valueMsg = new Message();
                        valueMsg.what = VIEW_INPUT;
                        valueMsg.obj = readMsg;
                        mHandler.sendMessage(valueMsg);
                    }else{
                        Log.i(TAG,"value=nodata");
                    }
                }
            }catch(Exception e){
                valueMsg = new Message();
                valueMsg.what = VIEW_STATUS;
                valueMsg.obj ="Error1:" + e;
                mHandler.sendMessage(valueMsg);
                try{
                    mSocket.close();
                }catch (Exception ee){}
                isRunning = false;
                connectFlg = false;

            }
    }

    @Override
    public void onClick(View v) {
        if(v.equals(connectButton)) {
            // 接続されていない場合のみ
            if (!connectFlg) {
                mStatusTextView.setText("try connect");

                mThread = new Thread(this);
                // Threadを起動し、Bluetooth接続
                isRunning = true;
                mThread.start();
            }
        } else if(v.equals(writeButton)) {
            // 接続中のみ書込みを行う
            if (connectFlg) {
                try {
                    mmOutputStream.write("2".getBytes());
                    mStatusTextView.setText("Write:");
                } catch (IOException e) {
                    Message valueMsg = new Message();
                    valueMsg.what = VIEW_STATUS;
                    valueMsg.obj = "Error3:" + e;
                    mHandler.sendMessage(valueMsg);
                }
            } else {
                mStatusTextView.setText("Please push the connect button");
            }
        }
    }


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            int action = msg.what;
            String msgStr = (String)msg.obj;
            if(action == VIEW_INPUT){
                mInputTextView.setText(msgStr);
            }else if(action == VIEW_STATUS){
                mStatusTextView.setText(msgStr);
            }
        }
    };



}