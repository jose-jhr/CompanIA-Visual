package com.ingenieriajhr.visualcontroll.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.ingenieriajhr.visualcontroll.livedata.DiferenciaX;
import com.ingenieriajhr.visualcontroll.livedata.MouseDirectionLiveData;
import com.ingenieriajhr.visualcontroll.models.ModCalLimEye;
import com.ingenieriajhr.visualcontroll.models.ModelDataEye;


public class ViewOnDraw extends View {

    private Integer widtView;
    private Integer heightView;
    public Boolean isCalibrate = false;
    public Boolean isDrawEye = false;
    public Integer valueCalibration = 0;
    //value position eye
    public ModelDataEye modelDataEye;

    public ModCalLimEye modCalLimEye = new ModCalLimEye(0f,0f,0f,0f);
    //diferencia calibrada
    float differenceXi = 0;
    float differenceXf = 0;

    //number calibration
    Integer numberCalibration = 0;

    DiferenciaX diferenciaX = DiferenciaX.getInstance();


    public ViewOnDraw(Context context) {
        super(context);
    }

    public ViewOnDraw(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewOnDraw(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ViewOnDraw(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        widtView = MeasureSpec.getSize(widthMeasureSpec);
        heightView = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        if (isCalibrate) calibrate(canvas,paint);
        //draw values with data
        //if (isDrawEye) drawPointPosition(canvas,paint);
        drawPointPosition(canvas,paint);
        invalidate();
    }

    /**
     * Draw point position center eye
     *
     * @param canvas
     * @param paint
     */
    private void drawPointPosition(Canvas canvas, Paint paint) {
        if (modelDataEye!=null){
            if (modCalLimEye !=null && modCalLimEye.getXf()>0 && valueCalibration == 0){
                //percentage iris and eye
                //float valuePercentage = ((modelDataEye.getIrisCenterX()-modCalLimEye.getXi())/
                //        Math.abs(modCalLimEye.getXf()-modCalLimEye.getXi()));

                float fit_iriscenter_x = Math.abs((modelDataEye.getIrisCenterX()-modelDataEye.getLimitXi()))+modelDataEye.getLimitXi();
                //ajustar calibracion xi, aqui se define el limite izquierdo en cuanto al ejex
                //se suma ya que el limite izquierdo esta a la izquierda y la posicion de diferencia
                //estara a la izquierda.
                float fit_cal_xi = Math.abs(modelDataEye.getLimitXi()+differenceXi);
                //ajustar calibracion xf, aqui se define el limite derecho en cuanto al eje x
                //se resta ya que la posicion limite estara a la derecha y la diferencia a la
                //izquierda.
                float fit_cal_xf = Math.abs(modelDataEye.getLimitXf()-differenceXf);
                //el porcentaje del movimiento del ojo estara determinado por la posicion del centro
                //del ojo en cuanto a los limites ya calculados.
                float valuePercentage = ((modelDataEye.getIrisCenterX()-fit_cal_xi)/
                        Math.abs(fit_cal_xf-fit_cal_xi));

//                float valuePercentage = ((modelDataEye.getIrisCenterX()-modelDataEye.getLimitXi())/
//                               Math.abs(modelDataEye.getLimitXf()-modelDataEye.getLimitXi()));

                //dibujamos un circulo de prueba.
                canvas.drawCircle(valuePercentage*widtView,100,10,paint);

            }
        }


        /*if (posEyes.length>1){
            //Views is max eyes
            Integer maxXView = Math.abs(pointCalibration.get(1)[0].x-pointCalibration.get(0)[0].x);
            Integer maxYView = pointCalibration.get(2)[0].y/2;

            //Display
            Integer maxXDisplay = widtView/2;
            Integer maxYDisplay = heightView/2;

            Integer positionX = ((maxXDisplay*posEyes[0].x)/maxXView)-widtView;

            canvas.drawPoint(positionX,100,paint);
        }*/
    }


    /**
     * Dibuja 4 puntos en pantalla, necesarios para calibrarla.
     * @param canvas
     * @param paint
     */
    public void calibrate(Canvas canvas,Paint paint){

        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(10);
        //Continuo dibujo 1
        if (valueCalibration==1){
            canvas.drawCircle(0,0,20,paint);
            modCalLimEye.setXi(modelDataEye.getIrisCenterX());
            modCalLimEye.setYi(modelDataEye.getIrisCenterY());
            Log.d("position_x 1",modelDataEye.getIrisCenterX()+"");
        }
        if (valueCalibration==2){
            modCalLimEye.setXf(modelDataEye.getIrisCenterX());
            modCalLimEye.setYf(modelDataEye.getIrisCenterY());
            canvas.drawCircle(widtView,0,20,paint);
            Log.d("position_x 1",modelDataEye.getIrisCenterX()+"");

        }
        if (valueCalibration == 3){
            //guado la diferencia entre los puntos xi limitie izquiero y yf limite derecho y le resto
            //la posicion actual del iris con el fin de saber cual es su posicion a la hora de mirar
            //sea a la izquierda o derecha. esta diferencia luego ayudara a calcular el max y min
            //en cuanto a la pantalla.
            differenceXi = (differenceXi+Math.abs(modelDataEye.getLimitXi()-modCalLimEye.getXi()))/2;
            differenceXf = (differenceXf+Math.abs(modelDataEye.getLimitXf()-modCalLimEye.getXf()))/2;
            Log.d("datadiference","difference xi: "+differenceXi+" difference xf: "+differenceXf);
            Log.d("datadiference","\n");

            Log.d("datadiference","limit xi: "+modelDataEye.getLimitXi()+" limit xf: "+modelDataEye.getLimitXf());
            Log.d("datadiference","mod_cal xi: "+modCalLimEye.getXi()+" model_cal xf: "+modCalLimEye.getXf());



            //add number calibration
            //isCalibrate = false;

            //Reseteo la funcionalidad de calibracion en caso de que se quiera volver a calibrar.
            /**
             * @numberCalibration numero de calibraciones que se van a hacer.
             * si esta ya calibro 2 veces entonces termina.
             */
            if (numberCalibration>=2) {
                //enviamos diferencias luego de la calibracion.
                diferenciaX.setValue(new float[]{differenceXi, differenceXf});
                //est calibrando
                isCalibrate= false;
                //Contador de calibración.
                numberCalibration = 0;
                //Valor de calibración.
                valueCalibration = 0;
            }else{
                numberCalibration++;
                valueCalibration = 1;
            }

        }

//        if (valueCalibration==2){
//            canvas.drawCircle(0,heightView,20,paint);
//        }
//        if (valueCalibration==3){
//            canvas.drawCircle(widtView,heightView,20,paint);
//        }

    }

}
