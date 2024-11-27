package com.ingenieriajhr.visualcontroll.accessibility.utils;

import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.ingenieriajhr.visualcontroll.accessibility.utils.interace.OnTouchListenerCallback;


public class MoveListener implements View.OnTouchListener {

    private int[] initialX = {0};
    private int[] initialY = {0};
    private boolean[] clic = {false};
    private WindowManager windowManager;
    private View view;
    private OnTouchListenerCallback callback;
    private Long timePress = 0L;
    private Long timeClic = 1000L;
    private Long timeCurrent = 0L;

    private int x;

    private  int y;

    // Constructor
    public MoveListener(View view, OnTouchListenerCallback callback, int x, int y) {
        this.view = view;
        this.callback = callback;
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //guardamos el tiempo en que se le hizo clic
                timeCurrent = System.currentTimeMillis();
                clic[0] = true;
                // Guardamos la posición inicial del toque (X y Y)
                initialX[0] = (int) motionEvent.getRawX();
                initialY[0] = (int) motionEvent.getRawY();
                break;

            case MotionEvent.ACTION_UP:
                //si el tiempo del sistema menos el tiempo en que se hizo clic es menor al tiempo de clic
                if (System.currentTimeMillis() -timeCurrent< timeClic){
                    //es un clic por que se levanto el dedo
                    clic[0] = false;
                    callback.onTouchMoved(x, y,true,view);
                }else{
                    clic[0] = false;
                }

                break;

            case MotionEvent.ACTION_MOVE:
                if (clic[0] && System.currentTimeMillis() -timeCurrent>timeClic) {
                    // Calculamos el desplazamiento en X y Y
                    float dx = motionEvent.getRawX() - initialX[0];
                    float dy = motionEvent.getRawY() - initialY[0];

                    // Actualizamos las coordenadas del layout
                    //layoutParams.x = (int) (layoutParams.x - dx);
                    y = (int) (y + dy);

                    // Actualizamos las coordenadas iniciales
                    initialX[0] = (int) motionEvent.getRawX();
                    initialY[0] = (int) motionEvent.getRawY();


                    // Llamamos al callback con las nuevas coordenadas
                    callback.onTouchMoved(x, y,false,view);
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }
}
