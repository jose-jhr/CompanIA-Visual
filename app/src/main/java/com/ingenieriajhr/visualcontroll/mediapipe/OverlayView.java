package com.google.mediapipe.examples.facelandmarker;


import static java.lang.Math.max;
import static java.lang.Math.min;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.core.content.ContextCompat;

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult;
import com.ingenieriajhr.visualcontroll.R;

import com.ingenieriajhr.visualcontroll.interfaces.ValuePorcentages;
import com.ingenieriajhr.visualcontroll.mediapipe.IrisVls;
import com.ingenieriajhr.visualcontroll.interfaces.ValuesEyeResult;
import com.ingenieriajhr.visualcontroll.models.ModelDataEye;
import com.ingenieriajhr.visualcontroll.models.ModelPorcentages;

import java.util.List;


public class OverlayView extends View {

    private FaceLandmarkerResult results;
    private Paint linePaint = new Paint();
    private Paint pointPaint = new Paint();

    //init listener
    ValuesEyeResult valuesEyeResult;

    ValuePorcentages valuePorcentages;


    private float scaleFactor = 1f;
    private int imageWidth = 1;
    private int imageHeight = 1;

    float diameter = 0;
    float radius = 0;


    //Contador de tiempo
    // Variable para almacenar el tiempo del primer parpadeo
    // Variable para almacenar el tiempo del primer parpadeo
    long tiempoPrimerParpadeo = 0;

    // Umbral de tiempo para considerar un parpadeo como parte del mismo "click"
    final long umbralParpadeo = 1000; // 1000 milisegundos (1 segundo)
    final long umbralBloque = 300; // 300 milisegundos (0.3 segundos)

    private Boolean isClick = false;
    //Home retur
    private Boolean isHome = false;

    private Boolean isAppsOpen = false;

    private Long systemTimeOpenBoca = 0L;
    private Long timeOpenApps = 1700L;

    private boolean initTime = false;

    public void addListenerResultPointEye(ValuesEyeResult valuesEyeResult){
        this.valuesEyeResult = valuesEyeResult;
    }

    public void addListenerValuePorcentages(ValuePorcentages valuePorcentages){
        this.valuePorcentages = valuePorcentages;
    }

    public OverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaints();

    }

    public void clear() {
        results = null;
        linePaint.reset();
        pointPaint.reset();
        invalidate();
        initPaints();
    }

    private void initPaints() {
        linePaint.setColor(ContextCompat.getColor(getContext(), R.color.black));
        linePaint.setStrokeWidth(LANDMARK_STROKE_WIDTH);
        linePaint.setStyle(Paint.Style.STROKE);

        pointPaint.setColor(Color.BLUE);
        pointPaint.setStrokeWidth(2);
        pointPaint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (results == null || results.faceLandmarks().isEmpty()) {
            clear();
            return;
        }

        float factorScaleWidth =  imageWidth * scaleFactor;
        float factorScaleHeight =  imageHeight * scaleFactor;


        List<List<NormalizedLandmark>> landmarks = results.faceLandmarks();

        //list points get
        List<NormalizedLandmark> listPoints = landmarks.get(0);


        NormalizedLandmark limit = listPoints.get(IrisVls.POINTCENTEREYEX.getPosition());
        canvas.drawCircle(limit.x() * factorScaleWidth, limit.y() * factorScaleHeight, 1, pointPaint);

        //Calculamos el porcentaje 50% esta mirando hacia al frente(centro)
        //mas de 50% esta viendo a la derecha
        //menos del 50% esta viendo a la izquierda.
        Float porcentageX = (listPoints.get(IrisVls.POINTCENTEREYEX.getPosition()).x()*100)/(
                listPoints.get(IrisVls.POINTLEFTLIM.getPosition()).x()+
                listPoints.get(IrisVls.POINTRIGHTLIM.getPosition()).x());

        Float porcentageY = (listPoints.get(IrisVls.POINTCENTEREYEY.getPosition()).y()*100)/(
                listPoints.get(IrisVls.POINTTOPLIM.getPosition()).y()+
                        listPoints.get(IrisVls.POINTBOTTOMLIM.getPosition()).y());

        //AQUI SE DETECTARA PARPADEO PARA CLICK PUNTO ABAJO CENTRO IRIS
        // 468, PUNTOCENTRA 159, PUNTO ARRIBA IRIS 470,
        //float centroDeteccionClick = (listPoints.get(IrisVls.POINTTOPIRIS.getPosition()).y()+listPoints.get(IrisVls.POINCENTERTIRIS.getPosition()).y())/2;

        float diametroIris = (listPoints.get(IrisVls.IID.getPosition()).y()-listPoints.get(IrisVls.IIU.getPosition()).y());
        float distanciaEye = (listPoints.get(IrisVls.POINTDOWNPARP.getPosition()).y()-listPoints.get(IrisVls.POINTCENTERPARP.getPosition()).y());

        // Obtener el tiempo actual
        long tiempoActual = System.currentTimeMillis();

        // Verificar si la coordenada y del centro del iris supera el umbral
        if (distanciaEye<diametroIris/2) {
            // Si es el primer parpadeo dentro del intervalo, registrar el tiempo
            if (tiempoPrimerParpadeo == 0) {
                tiempoPrimerParpadeo = tiempoActual;
            } else {
                // Calcular el tiempo transcurrido desde el primer parpadeo
                long tiempoTranscurrido = tiempoActual - tiempoPrimerParpadeo;

                // Si el tiempo transcurrido está dentro del umbral de bloqueo
                if (tiempoTranscurrido < umbralBloque) {
                    // No hacer nada, esperar al próximo parpadeo
                }
                // Si el tiempo transcurrido está dentro del umbral de parpadeo
                else if (tiempoTranscurrido < umbralParpadeo) {
                    isClick = true;
                    Log.d("movimiento 2024", "Click");
                    // Restablecer el tiempo del primer parpadeo
                    tiempoPrimerParpadeo = 0;
                } else {
                    // Si el tiempo transcurrido supera el umbral de parpadeo, restablecer el tiempo del primer parpadeo
                    tiempoPrimerParpadeo = 0;
                }
            }
        }else {
            // Si no se detecta un parpadeo, restablecer el tiempo del primer parpadeo
            tiempoPrimerParpadeo = 0;
        }

        //Distancia puntos labios
        float distanciaLabios = Math.abs(listPoints.get(IrisVls.POINTBOCATOP.getPosition()).y()-
                listPoints.get(IrisVls.POINTBOCADOWN.getPosition()).y());

        //Distancia puntos boca
        float distanciaBoca = Math.abs(listPoints.get(IrisVls.POINTBOCAHTOP.getPosition()).y()-
                listPoints.get(IrisVls.POINTBOCAHDOWN.getPosition()).y());

        if (distanciaBoca>distanciaLabios){
            isHome = true;
            if (!initTime){
                systemTimeOpenBoca = System.currentTimeMillis();
                initTime = true;
            }
        }else{
            isHome = false;
            isAppsOpen = false;
            initTime = false;
        }

        if (System.currentTimeMillis()-systemTimeOpenBoca>timeOpenApps && isHome){
            isAppsOpen = true;
        }



        valuePorcentages.resultPointsEye(new ModelPorcentages(porcentageX,porcentageY,isClick,isHome,isAppsOpen));
        isClick = false;


        //Log.d("eje z: ",listPoints.get(IrisVls.POINTCENTEREYE.getPosition()).x()*factorScaleHeight-listPoints.get(IrisVls.POINTLEFTLIM.getPosition()).x()*factorScaleWidth+"");

        /*valuePointCenterEye.resultPointsEye(new ModelEyePointCenter(listPoints.get(IrisVls.POINTCENTEREYE.getPosition()).x()*factorScaleWidth,
                listPoints.get(IrisVls.POINTCENTEREYE.getPosition()).y()*factorScaleHeight,
                listPoints.get(IrisVls.POINTCENTEREYE.getPosition()).z()));*/




        /*

        //get position iris left
        NormalizedLandmark getPointCenter = listPoints.get(IrisVls.IIC.getPosition());
        //calculate ratio
        float diametro = listPoints.get(IrisVls.IIL.getPosition()).x()*factorScaleWidth-listPoints.get(IrisVls.IIR.getPosition()).x()*factorScaleWidth;
        //radio
        float radio = Math.abs(diametro/2);
        //draw circle iris
        canvas.drawCircle(getPointCenter.x()* factorScaleWidth,getPointCenter.y()* factorScaleHeight,radio,pointPaint);

        //get position iris right
        NormalizedLandmark getPointCenterRight = listPoints.get(IrisVls.IDC.getPosition());
        //calculate ratio
        float diametroRight = listPoints.get(IrisVls.IDL.getPosition()).x()*factorScaleWidth-listPoints.get(IrisVls.IDR.getPosition()).x()*factorScaleWidth;
        //radio
        float radioRigth = Math.abs(diametroRight/2);
        //draw circle iris
        canvas.drawCircle(getPointCenterRight.x()* factorScaleWidth,getPointCenterRight.y()* factorScaleHeight,radioRigth,pointPaint);

        //limit left eye
        canvas.drawCircle(listPoints.get(IrisVls.LIMITEYEXL.getPosition()).x()*factorScaleWidth,
                listPoints.get(IrisVls.LIMITEYEXL.getPosition()).y()*factorScaleHeight,
                1,pointPaint);

        canvas.drawCircle(listPoints.get(IrisVls.LIMITEYEXR.getPosition()).x()*factorScaleWidth,
                listPoints.get(IrisVls.LIMITEYEXR.getPosition()).y()*factorScaleHeight,
                1,pointPaint);

        //valuesEyeResult.resultPointsEye(null);

        //returno los puntos de los limites, de cada ojo. el centro se esta obteniendo de un solo ojo
        valuesEyeResult.resultPointsEye(new ModelDataEye(listPoints.get(IrisVls.LIMITEYEXL.getPosition()).x()*factorScaleWidth,
                listPoints.get(IrisVls.LIMITEYEYT.getPosition()).y()*factorScaleHeight,
                listPoints.get(IrisVls.LIMITEYEXR.getPosition()).x()*factorScaleWidth,
                listPoints.get(IrisVls.LIMITEYEYB.getPosition()).y()*factorScaleHeight,
                getPointCenter.x()*factorScaleWidth,
                getPointCenter.y()*factorScaleHeight));
*/


       // valuesEyeResult.resultPointsEye(null);
    }

    private void drawIris(Canvas canvas, List<NormalizedLandmark> listPoints, IrisVls centerIndex, IrisVls leftIndex, IrisVls rightIndex, float factorScaleWidth, float factorScaleHeight, Paint paint) {
            if (listPoints != null && !listPoints.isEmpty()) {
                NormalizedLandmark center = listPoints.get(centerIndex.getPosition());
                NormalizedLandmark left = listPoints.get(leftIndex.getPosition());
                NormalizedLandmark right = listPoints.get(rightIndex.getPosition());

                if (center != null && left != null && right != null) {
                    diameter = left.x() * factorScaleWidth - right.x() * factorScaleWidth;
                    radius = Math.abs(diameter / 2);
                    //canvas.drawCircle(center.x() * factorScaleWidth, center.y() * factorScaleHeight, 1, paint);
                }
            }

    }


    private void drawLimitCircle(Canvas canvas, List<NormalizedLandmark> listPoints, IrisVls index, float factorScaleWidth, float factorScaleHeight, Paint paint) {
        NormalizedLandmark limit = listPoints.get(index.getPosition());
        canvas.drawCircle(limit.x() * factorScaleWidth, limit.y() * factorScaleHeight, 1, paint);
    }

    public void setResults(
            FaceLandmarkerResult faceLandmarkerResults,
            int imageHeight,
            int imageWidth,
            RunningMode runningMode) {
        results = faceLandmarkerResults;

        this.imageHeight = imageHeight;
        this.imageWidth = imageWidth;

        scaleFactor = switch (runningMode) {
            case IMAGE, VIDEO -> min(getWidth() * 1f / imageWidth, getHeight() * 1f / imageHeight);
            case LIVE_STREAM -> max(getWidth() * 1f / imageWidth, getHeight() * 1f / imageHeight);
        };

        invalidate();
    }

    private static final float LANDMARK_STROKE_WIDTH = 8F;
    private static final String TAG = "Face Landmarker Overlay";
}
