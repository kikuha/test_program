package com.example.hayato.hallo2;

import java.io.OutputStream;
import java.security.interfaces.RSAKey;
import java.util.Timer;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.TimerTask;
import java.util.UUID;
import android.app.Instrumentation;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.style.UpdateAppearance;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.util.Log.i;

public class CommandGen extends Service{

    private Timer mTimer = null;
    Handler mHandler = new Handler();
    public int tmp = 0;
    TouchTest touch = new TouchTest();
    volatile double[][] InputData = new double[Const.nOfDimensions][Const.RingDataNum];
    double[][] RsMean = new double[Const.nOfMoveTypes][Const.RingDataNum];
    double RsTmpAve = 0;
    double[][] RsPeakUpVal = new double[Const.nOfMoveTypes][4];
    int[][] RsPeakUpPos = new int[Const.nOfMoveTypes][4];
    double[][] RsPeakDownVal = new double[Const.nOfMoveTypes][4];
    int[][] RsPeakDownPos = new int[Const.nOfMoveTypes][4];
    double[] maxRspeakUpVal = new double[Const.nOfMoveTypes];
    int[] maxRspeakUpPos = new int[Const.nOfMoveTypes];
    double[] maxRspeakDownVal = new double[Const.nOfMoveTypes];
    int[] maxRspeakDownPos = new int[Const.nOfMoveTypes];
    int[][] peakCnt = new int[Const.nOfMoveTypes][2];
    volatile int flag_BLE = 0;
    volatile int flag_ComGen = 0;
    volatile int RingBufCnt_Input = 0;
    volatile int RingBufCnt_RsMean = 0;
    int dalayTime = 50;
    Globals globals;
    double[][] test_input = new double[2020][3];
    int Cnt_debug = 0;
    int d_temp = 0;
    public CommandGen() throws IOException{

    }


    @Override
    public void onCreate() {
        Log.i("CommandGen", "onCreate");
             globals = (Globals) this.getApplication();
             globals.GlobalsAllInit();
             globals.globalContext = getApplication();
        ////テストデータ生成部分省略

        ////0315//
        //呼び出し時点で、BTのスレッドを作る
        //＝無限ループでデータ取得し続ける
        //データはbuffered readerに格納していく

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.i("CommandGen","onStartCommand");
        mTimer = new Timer(true);
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable(){
                    public void run(){

                        //                       UpdateData(test_input[Cnt_debug%2020]);
                        //                       if(RingBufCnt_Input>Const.dataProcNum && dalayTime<0){
                        //                           String s = String.valueOf(ComGen_Main());
                        //                           Log,i("CommandGen_Debug",s);
                        //                       }
                        //                       Cnt_debug++;


                        //UpdateData(globals.BT_tmp);
                        if(RingBufCnt_Input>Const.dataProcNum && dalayTime<0){
                            touch.swipe(ComGen_Main());
                        }
                        Log.d("CommandGEn","Timer run");

                    }
                });
                tmp++;
                dalayTime--;
            }
        }, 1000, 50);
        return START_STICKY;
    }


    @Override
    public void onDestroy(){
        Log.i("CommandGen","onDestroy");
        if(mTimer !=null){
            mTimer.cancel();
            mTimer = null;
        }
        Toast.makeText(this, "MyService?onDestroy", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent arg0){
        Log.i("CommandGen","onBind");
        return null;
    }



    public void UpdateData(double[] input){
        if(flag_BLE == 0){
            flag_BLE = 1;

            /////////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////////
            for(int i = 0; i<Const.nOfDimensions;i++){
                InputData[i][RingBufCnt_Input%Const.RingDataNum] = input[i];
            }
            ////////////////////////////////////////////////////////////////////
            InputData[0][RingBufCnt_Input%Const.RingDataNum] = input[0];
            InputData[1][RingBufCnt_Input%Const.RingDataNum] = input[1];
            InputData[2][RingBufCnt_Input%Const.RingDataNum] = input[2];
            ////////////////////////////////////////////////////////////////////
            ///////////////////////////////////////////////////////////////////

            RingBufCnt_Input++;
            flag_BLE = 0;

            ///////////////////////////////////////////////////////////////////
            ///////////////////////////////////////////////////////////////////
            for(int i = 0;i<Const.nOfMoveTypes;i++) {
                double RsTmpAve = 0;
                for (int j = 0; j < Const.nOfDimensions; j++) {
                    for (int k = 0; k < Const.dataProcNum; k++) {
                        if (RingBufCnt_Input > 100) {
                            RsTmpAve = RsTmpAve + InputData[j][((RingBufCnt_Input - Const.dataProcNum - 1 + k) % Const.RingDataNum)] * Const.refSignalData[i][j][k];
                        }
                    }
                }
                RsMean[i][RingBufCnt_RsMean%Const.RingDataNum] = RsTmpAve / Const.dataScale;
            }
            /////////////////////////////////////////////////////////////////
            double[] RsTmpAve = {0,0,0,0};

            for(int i=0; i<Const.dataProcNum;i++){
                RsTmpAve[0] = RsTmpAve[0] + InputData[0][((RingBufCnt_Input - Const.dataProcNum - 1 + i) % Const.RingDataNum)] * Const.refSignalData[0][0][i]
                        +InputData[1][((RingBufCnt_Input - Const.dataProcNum - 1 + i) % Const.RingDataNum)] * Const.refSignalData[0][1][i]
                        +InputData[2][((RingBufCnt_Input - Const.dataProcNum - 1 + i) % Const.RingDataNum)] * Const.refSignalData[0][2][i];

                RsTmpAve[1] = RsTmpAve[1] + InputData[0][((RingBufCnt_Input - Const.dataProcNum - 1 + i) % Const.RingDataNum)] * Const.refSignalData[1][0][i]
                        +InputData[1][((RingBufCnt_Input - Const.dataProcNum - 1 + i) % Const.RingDataNum)] * Const.refSignalData[1][1][i]
                        +InputData[2][((RingBufCnt_Input - Const.dataProcNum - 1 + i) % Const.RingDataNum)] * Const.refSignalData[1][2][i];

                RsTmpAve[2] = RsTmpAve[2] + InputData[0][((RingBufCnt_Input - Const.dataProcNum - 1 + i) % Const.RingDataNum)] * Const.refSignalData[2][0][i]
                        +InputData[1][((RingBufCnt_Input - Const.dataProcNum - 1 + i) % Const.RingDataNum)] * Const.refSignalData[2][1][i]
                        +InputData[2][((RingBufCnt_Input - Const.dataProcNum - 1 + i) % Const.RingDataNum)] * Const.refSignalData[2][2][i];

                RsTmpAve[3] = RsTmpAve[3] + InputData[0][((RingBufCnt_Input - Const.dataProcNum - 1 + i) % Const.RingDataNum)] * Const.refSignalData[3][0][i]
                        +InputData[1][((RingBufCnt_Input - Const.dataProcNum - 1 + i) % Const.RingDataNum)] * Const.refSignalData[3][1][i]
                        +InputData[2][((RingBufCnt_Input - Const.dataProcNum - 1 + i) % Const.RingDataNum)] * Const.refSignalData[3][2][i];
            }
            RsMean[0][RingBufCnt_RsMean%Const.RingDataNum] = RsTmpAve[0] / Const.dataScale;
            RsMean[1][RingBufCnt_RsMean%Const.RingDataNum] = RsTmpAve[1] / Const.dataScale;
            RsMean[2][RingBufCnt_RsMean%Const.RingDataNum] = RsTmpAve[2] / Const.dataScale;
            RsMean[3][RingBufCnt_RsMean%Const.RingDataNum] = RsTmpAve[3] / Const.dataScale;
            //////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////////////////////////

            RingBufCnt_RsMean++;
        }
    }

    public int ComGen_Main(){
        int Move = 0;
        int Cnt = RingBufCnt_Input - Const.dataProcNum-1;

        ////////省略//////
        return Move;
    }

    public int DetectMove(int num){
        int out = 0;
        int MainPeakPos = 0;
        double MAinPeakVal = 0;

        /////////////省略///////////
        i("ComGEn_DetectMove","(ry run");
        return out;
    }


}

