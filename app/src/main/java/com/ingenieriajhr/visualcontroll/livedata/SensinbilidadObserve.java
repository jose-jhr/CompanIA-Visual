package com.ingenieriajhr.visualcontroll.livedata;

import androidx.lifecycle.MutableLiveData;

public class SensinbilidadObserve extends MutableLiveData<Float> {

    public static SensinbilidadObserve instance;

    public static SensinbilidadObserve getInstance(){
        if (instance == null){
            instance = new SensinbilidadObserve();
        }

        return instance;
    }

}
