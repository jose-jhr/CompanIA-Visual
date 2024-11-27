package com.ingenieriajhr.visualcontroll.livedata;

import android.graphics.Bitmap;

import androidx.camera.view.PreviewView;
import androidx.lifecycle.MutableLiveData;

import com.ingenieriajhr.visualcontroll.models.ModelDataEye;

public class MouseDirectionLiveData extends MutableLiveData<ModelDataEye> {

    public static MouseDirectionLiveData instance;

    public static MouseDirectionLiveData getInstance(){
        if (instance == null){
            instance = new MouseDirectionLiveData();
        }

        return instance;
    }

}
