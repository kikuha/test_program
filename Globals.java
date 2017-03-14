package com.example.hayato.hallo2;


import android.app.Application;
import android.content.Context;

import java.io.BufferedReader;

/**
 * Created by KIKUTA_NOTE on 2017/03/10.
 */

public class Globals extends Application{
    double[] BT_tmp = new double[3];
    int[] BT_data = new int[6];
    public static Context globalContext;




    public  void GlobalsAllInit(){
        BT_tmp[0]=0.0;
        BT_tmp[1]=0.0;
        BT_tmp[2]=0.0;
    }
}
