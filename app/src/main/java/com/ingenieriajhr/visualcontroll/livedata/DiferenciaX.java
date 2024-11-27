package com.ingenieriajhr.visualcontroll.livedata;

import android.graphics.Rect;

import androidx.lifecycle.MutableLiveData;

public class DiferenciaX extends MutableLiveData<float[]> {

    public static DiferenciaX instance;

    public static DiferenciaX getInstance(){
        if (instance == null){
            instance = new DiferenciaX();
        }

        return instance;
    }

}
