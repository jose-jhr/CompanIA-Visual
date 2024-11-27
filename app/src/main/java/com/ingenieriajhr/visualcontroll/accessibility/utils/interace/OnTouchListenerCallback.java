package com.ingenieriajhr.visualcontroll.accessibility.utils.interace;

import android.view.View;

public interface OnTouchListenerCallback {
    void onTouchMoved(int newX, int newY, boolean isClic, View view);
}
