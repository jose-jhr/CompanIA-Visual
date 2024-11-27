package com.ingenieriajhr.visualcontroll.accessibility.saveData;

import android.content.Context;
import android.content.SharedPreferences;

public class SaveState {

    private Context mContext;
    //Share preference
    private String PREFERENCE_NAME = "DATA_STATUS";
    private String KEY_STATUS = "STATUS_CAMARA";

    public void SaveStateCamera(Context context,Boolean status){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(KEY_STATUS,status).apply();
    }

    public boolean getStateCamera(Context context){
        return context.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE).getBoolean(KEY_STATUS,false);
    }


}
