package com.ingenieriajhr.visualcontroll.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.animation.ObjectAnimator;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Layout;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;


import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.L;
import com.airbnb.lottie.LottieAnimationView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.ingenieriajhr.visualcontroll.MainActivity;
import com.ingenieriajhr.visualcontroll.R;
import com.ingenieriajhr.visualcontroll.accessibility.internet.Request;
import com.ingenieriajhr.visualcontroll.accessibility.saveData.SaveState;
import com.ingenieriajhr.visualcontroll.accessibility.utils.MoveListener;
import com.ingenieriajhr.visualcontroll.accessibility.utils.adapters.ModelCiberPaz;
import com.ingenieriajhr.visualcontroll.accessibility.utils.adapters.SimpleCustomAdapter;
import com.ingenieriajhr.visualcontroll.accessibility.utils.datos.DatosCuriososCerebroCurioso;
import com.ingenieriajhr.visualcontroll.accessibility.utils.interace.OnTouchListenerCallback;
import com.ingenieriajhr.visualcontroll.camara.LuminosityAnalyzer;
import com.ingenieriajhr.visualcontroll.customlive.CustomLifecycleOwner;
import com.ingenieriajhr.visualcontroll.interfaces.BitmapResponse;
import com.ingenieriajhr.visualcontroll.interfaces.ValuePorcentages;
import com.ingenieriajhr.visualcontroll.livedata.SensinbilidadObserve;
import com.ingenieriajhr.visualcontroll.livedata.DiferenciaX;
import com.ingenieriajhr.visualcontroll.mediapipe.FaceLandMarkerHelper;
import com.ingenieriajhr.visualcontroll.models.ModelPorcentages;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;


public class MouseAccessibilityService extends AccessibilityService implements FaceLandMarkerHelper.LandmarkerListener,
        OnTouchListenerCallback{


    private static final String ACTIVATION_KEYWORD = "<YOUR_ACTIVATION_KEYWORD>";
    private String CHANNE_ID = "my_channel_id";
    private int NOTIFICATION_ID = 1;

    private RemoteViews remoteViews;


    private static final String TAG = MouseAccessibilityService.class.getName();

    /**
     * Views
     */
    private View cursorView;
    private View retrocederView;

    private View desplazamientoView;

    private LayoutParams desplazamientoLayout;
    private View brainIntelligent;
    private LayoutParams brainIntelligentLayout;

    private LayoutParams retrocesoLayout;

    private LayoutParams chatBotLayout;
    private View chatBotView;

    private LayoutParams ciberPazLayout;
    private View ciberPazView;

    private View imgDesArriba,imgDesAbajo,imgRetroceder;

    private LayoutParams policeFraudeLayout;
    private View policeFraudeView;

    //ciberpaz
    private RecyclerView recyclerCiberPaz;
    private TextView txtCiberPazTittle;
    private Button btnModuloCiberPaz;

    private ImageView imgCiberPaz;

    //Boolean se hizo scroll
    Boolean isScrollReady = true;

    private View cameraView;
    private WindowManager windowManager;
    private LayoutParams cursorLayout;

    private LayoutParams cameraLayout;

    private float widthDisplay;
    private float heightDisplay;

    //live data
    //private MouseDirectionLiveData mouseDirectionLiveData = MouseDirectionLiveData.getInstance();
    //captura el porcentaje del movimiento que hara el mouse.
    private DiferenciaX diferenciaX = DiferenciaX.getInstance();

    //calibracion manual instancia
    private SensinbilidadObserve sensinbilidadObserve = SensinbilidadObserve.getInstance();

    //camera
    private LuminosityAnalyzer luminosityAnalyzer = new LuminosityAnalyzer();
    private ImageAnalysis imageAnalysis;
    private Executor cameraExecutor = Executors.newSingleThreadExecutor();

    private Preview imagePreview;

    CustomLifecycleOwner serviceLifecycleOwner = new CustomLifecycleOwner();


    //Dimens camera
    Integer widthCamera = 400;
    Integer heightCamera = 400;

    //width camera live
    Integer widthImgLive = 0;
    Integer heightImgLive = 0;

    //view liveCamera bitmap
    ImageView imgLive = null;
    ImageView imgBrainIntelligent = null;

    ImageView imgPoliceFraude = null;


    //draw points in mediapipe
    com.google.mediapipe.examples.facelandmarker.OverlayView viewDraw;

    //face helper detect
    FaceLandMarkerHelper faceLandMarkerHelper;

    //Valores de calibracion
    float diferenciaXiCalibration = 0f;
    float diferenciaXfCalibration = 0f;

    //notifica que los resultados de la interpretacion del bitmap estan listos.
    private Boolean okCamaraFrame = true;

    private PowerManager.WakeLock wakeLock;

    private Boolean startService = false;

    private float transitionSpeedObserve = 0.1f;


    private BroadcastReceiver receiver;

    private PreviewView previewLive;

    private Boolean isInitCamera = false;



    /**
     *
     * Brain intelligent
     */
    private TextView txtBrainIntelligent;
    private LinearLayout containerAnimation;
    private LottieAnimationView brain_lottie;
    private Button btnCancelBrain;

    /**
     *
     * Police fraude
     */
    private LottieAnimationView lott_document;
    private TextView txtPoliceFraude;
    private LinearLayout containerCommands;

    private TextView txtRespuesta;

    private Button btnSendMessageApi;

    private EditText inspectionData;

    private ImageView imgMic;

    /**
     * ChatBot
     */
    private LottieAnimationView lott_pregunta;
    private EditText edtPreguntaChatBot;
    private TextView txtRespuetaChatBot;
    private Button btnPreguntarChatBot;
    private ImageView btnCopyChatBot;
    private TextView txtChatBotTittle;

    private TextView txtConsumoAgua;

    ImageView imgChatBot;
    float porcentajeAgua = 1f;
    float consumoTotal = 0f;
    int division = 0;
    Button btnCancellChatBot;

    /**
     * Open view
     */
    private Long isOpenCiberPaz = 0L;
    private Long isOpenBrain = 0L;

    private Long isPoliceTime = 0L;

    private Long isChatBotTime = 0L;
    private Long timeOpenAndCloseElements = 2000L;

    private Button btnCancelPolice;

    int screenWidth = 0;
    int screenHeight = 0;


    int layout_parms;

    Intent recognizerIntent;

    /**
     * Spech
     */
    private SpeechRecognizer speechRecognizer;

    /**
     *
     * Lister
     */
    private boolean listChatBot = false;
    private boolean listInspector = false;

    private boolean changeOpenBoca = false;



    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        // Ejemplo: Si el evento corresponde a una acción específica, se puede activar el reconocimiento de voz.
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
            //startSpeechRecognition();
        }
    }




    /**
     * Enviamos datos de actualización a la actividad prinicipal
     */
    public void saveStatusCameraDataSharePreference(boolean isInitCamera){
        new SaveState().SaveStateCamera(this,isInitCamera);
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.eventTypes=AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.notificationTimeout = 100;
        info.packageNames = null;
        setServiceInfo(info);
        Log.d("OnService24","Servicio conectado");
        IntentFilter filter = new IntentFilter("com.ingenieriajhr.visualcontroll");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getStringExtra("accion");
                Log.d("service_camara","iniciar camara");

                if (action != null){
                    if (action.equals("iniciar_camara")){
                        initCamara();
                        Log.d("service_camara","iniciar camara");
                    }
                    if (action.equals("detener_camara")){
                        Log.d("service_camara","detener camara");
                        stopCamera();
                    }
                }
            }
        };
        registerReceiver(receiver,filter);
        //retornar a mi aplicacion
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setComponent(new ComponentName("com.ingenieriajhr.visualcontroll","com.ingenieriajhr.visualcontroll.MainActivity"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);


    }



    @Override
    public void onInterrupt() {
    }




    @Override
    public void onCreate() {
        super.onCreate();

         createNotificationChannel();



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layout_parms = LayoutParams.TYPE_APPLICATION_OVERLAY;
            //layout_parms = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layout_parms = WindowManager.LayoutParams.TYPE_PHONE;
        }

        cursorView = View.inflate(getBaseContext(), R.layout.cursor, null);
        cameraView = View.inflate(getBaseContext(),R.layout.camera_preview,null);
        desplazamientoView = View.inflate(getBaseContext(),R.layout.desplazamiento,null);
        retrocederView = View.inflate(getBaseContext(),R.layout.retroceso,null);
        brainIntelligent = View.inflate(getBaseContext(),R.layout.view_brain_intelligent,null);
        policeFraudeView = View.inflate(getBaseContext(),R.layout.view_police_fraude,null);
        chatBotView = View.inflate(getBaseContext(),R.layout.view_chat_bot,null);
        ciberPazView = View.inflate(getBaseContext(),R.layout.view_ciberpaz_contenido,null);


        //views in camera view
        imgLive = cameraView.findViewById(R.id.imgLive);
        previewLive = cameraView.findViewById(R.id.previewLive);
        viewDraw = cameraView.findViewById(R.id.viewDrawService);
        //cerebro inteligente
        imgBrainIntelligent = brainIntelligent.findViewById(R.id.imgBrainIntelligent);
        txtBrainIntelligent = brainIntelligent.findViewById(R.id.txtBrainIntelligent);
        containerAnimation = brainIntelligent.findViewById(R.id.containerAnimation);
        brain_lottie = brainIntelligent.findViewById(R.id.lott_brain);
        btnCancelBrain = brainIntelligent.findViewById(R.id.btnCancelBrain);

        //policia evalua fraude
        lott_document = policeFraudeView.findViewById(R.id.lott_document);
        txtPoliceFraude = policeFraudeView.findViewById(R.id.txtPoliceFraude);
        containerCommands = policeFraudeView.findViewById(R.id.containerCommands);
        btnSendMessageApi = policeFraudeView.findViewById(R.id.btnSendMessageApi);
        inspectionData = policeFraudeView.findViewById(R.id.inspectionData);
        txtRespuesta = policeFraudeView.findViewById(R.id.txtRespuesta);
        imgMic = policeFraudeView.findViewById(R.id.imgMic);
        imgPoliceFraude = policeFraudeView.findViewById(R.id.imgPoliceFraude);
        btnCancelPolice = policeFraudeView.findViewById(R.id.btnCancelPolice);

        //chat bot
        lott_pregunta  = chatBotView.findViewById(R.id.lott_pregunta);
        edtPreguntaChatBot = chatBotView.findViewById(R.id.edtPreguntaChatBot);
        txtRespuetaChatBot = chatBotView.findViewById(R.id.txtRespuetaChatBot);
        btnPreguntarChatBot = chatBotView.findViewById(R.id.btnPreguntarChatBot);
        btnCopyChatBot = chatBotView.findViewById(R.id.btnCopyChatBot);
        txtChatBotTittle = chatBotView.findViewById(R.id.txtChatBotTittle);
        txtConsumoAgua = chatBotView.findViewById(R.id.txtConsumoAgua);
        imgChatBot = chatBotView.findViewById(R.id.imgChatBot);
        btnCancellChatBot = chatBotView.findViewById(R.id.btnCancellChatBot);

        //CiberPaz
        recyclerCiberPaz = ciberPazView.findViewById(R.id.recyclerCiberPaz);
        txtCiberPazTittle = ciberPazView.findViewById(R.id.txtCiberPazTittle);
        btnModuloCiberPaz = ciberPazView.findViewById(R.id.btnModuloCiberPaz);
        imgCiberPaz = ciberPazView.findViewById(R.id.imgCiberPaz);



        //Views desplazamiento
        imgDesArriba = desplazamientoView.findViewById(R.id.imgDesArriba);
        imgDesAbajo = desplazamientoView.findViewById(R.id.imgDesAbajo);

        //Views in brain Intelligent

        imgRetroceder = retrocederView.findViewById(R.id.imgRetroceder);
        imgRetroceder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("movimiento2024d","Hacia atras.....");
                performGlobalAction(GLOBAL_ACTION_BACK);
            }
        });


        cursorLayout = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                layout_parms,
                LayoutParams.FLAG_NOT_FOCUSABLE|
                        LayoutParams.FLAG_DISMISS_KEYGUARD |
                        LayoutParams.FLAG_NOT_TOUCHABLE|
                        LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        cursorLayout.gravity = Gravity.TOP | Gravity.LEFT;

        LayoutParams layoutParams = new LayoutParams(
                widthCamera,
                heightCamera,
                layout_parms,
                LayoutParams.FLAG_NOT_FOCUSABLE|
                LayoutParams.FLAG_DISMISS_KEYGUARD |
                        LayoutParams.FLAG_NOT_TOUCHABLE|
                        LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );

        cameraLayout = layoutParams;
        cameraLayout.gravity = Gravity.BOTTOM|Gravity.END;



        /** ----Desplazamiento configuracion ---- **/
        desplazamientoLayout = new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                layout_parms,
                LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        LayoutParams.FLAG_NOT_FOCUSABLE |
                        LayoutParams.FLAG_DISMISS_KEYGUARD|
                        LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH |
                        LayoutParams.FLAG_NOT_TOUCHABLE, // Agregado FLAG_NOT_TOUCHABLE
                PixelFormat.TRANSLUCENT
        );
        //position desplazamiento
        desplazamientoLayout.gravity = Gravity.END|Gravity.CENTER_VERTICAL;

        /** ----Retroceso configuracion ----**/
        retrocesoLayout =  new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                layout_parms,
                LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        LayoutParams.FLAG_NOT_FOCUSABLE |
                        LayoutParams.FLAG_DISMISS_KEYGUARD|
                        //LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                        //LayoutParams.FLAG_NOT_TOUCHABLE, // Agregado FLAG_NOT_TOUCHABLE
                PixelFormat.TRANSLUCENT
        );
        //retroceder layout
        retrocesoLayout.gravity = Gravity.CENTER|Gravity.START;


        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        widthDisplay = windowManager.getDefaultDisplay().getWidth();
        heightDisplay = windowManager.getDefaultDisplay().getHeight();


        /**----------------------  Brain Intelligent  -----------------------**/
        brainIntelligentLayout =  new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                layout_parms,
                LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        LayoutParams.FLAG_NOT_FOCUSABLE |
                        LayoutParams.FLAG_DISMISS_KEYGUARD|
                        LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        //LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                //LayoutParams.FLAG_NOT_TOUCHABLE, // Agregado FLAG_NOT_TOUCHABLE
                PixelFormat.TRANSLUCENT
        );
        //retroceder layout
        brainIntelligentLayout.gravity = Gravity.END|Gravity.TOP;


        /**----------------------  Policia Fraude  -----------------------**/
        policeFraudeLayout =  new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                layout_parms,
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE|
                LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        //LayoutParams.FLAG_NOT_FOCUSABLE |
                        LayoutParams.FLAG_DISMISS_KEYGUARD|
                        LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        //LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                //LayoutParams.FLAG_NOT_TOUCHABLE, // Agregado FLAG_NOT_TOUCHABLE
                PixelFormat.TRANSLUCENT
        );
        //retroceder layout
        policeFraudeLayout.gravity = Gravity.END|Gravity.TOP;


        /**----------------------  Chat Bot  -----------------------**/
        chatBotLayout =  new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                layout_parms,
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE|
                LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        //LayoutParams.FLAG_NOT_FOCUSABLE |
                        LayoutParams.FLAG_DISMISS_KEYGUARD|
                        LayoutParams.FLAG_NOT_TOUCH_MODAL ,
                     //   LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                //LayoutParams.FLAG_NOT_TOUCHABLE, // Agregado FLAG_NOT_TOUCHABLE
                PixelFormat.TRANSLUCENT
        );
        //retroceder layout
        chatBotLayout.gravity = Gravity.END|Gravity.TOP;

        /**--------------------  CiberPaz Layout  ----------------------------------**/
        ciberPazLayout =  new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                layout_parms,
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE|
                        LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        //LayoutParams.FLAG_NOT_FOCUSABLE |
                        LayoutParams.FLAG_DISMISS_KEYGUARD|
                        LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        //LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                //LayoutParams.FLAG_NOT_TOUCHABLE, // Agregado FLAG_NOT_TOUCHABLE
                PixelFormat.TRANSLUCENT
        );
        //retroceder layout
        ciberPazLayout.gravity = Gravity.START|Gravity.TOP;



        /**------------------------ End Layouts ---------------------------------**/


        // Obtener el WindowManager
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        // Obtener el tamaño de la pantalla
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);

            // Obtener el ancho y alto de la pantalla
            screenWidth = displayMetrics.widthPixels;
            screenHeight = displayMetrics.heightPixels;

            division = (screenHeight/2)/6;

            brainIntelligentLayout.y = division;
            policeFraudeLayout.y = division;
            chatBotLayout.y = division*2;
        }


        brainIntelligent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                return true;
            }
        });


        MoveListener moveListener = new MoveListener(brainIntelligent,this,brainIntelligentLayout.x,brainIntelligentLayout.y);
        brainIntelligent.setOnTouchListener(moveListener);
        MoveListener moveListener1 = new MoveListener(policeFraudeView,this, policeFraudeLayout.x, policeFraudeLayout.y);
        policeFraudeView.setOnTouchListener(moveListener1);
        MoveListener moveListener2 = new MoveListener(chatBotView,this, chatBotLayout.x, chatBotLayout.y);
        chatBotView.setOnTouchListener(moveListener2);
        MoveListener moveListener3 = new MoveListener(ciberPazView,this, ciberPazLayout.x, ciberPazLayout.y);
        ciberPazView.setOnTouchListener(moveListener3);


        //policeFraudeView.setOnTouchListener(moveListener2);
        //chatBotView.setOnTouchListener(moveListener3);

        edtPreguntaChatBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inspectionData.clearFocus();
                // Solicitar que se muestre el teclado cuando se toque el EditText
                edtPreguntaChatBot.requestFocus();
// Obtener el servicio InputMethodManager para mostrar el teclado
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

// Verificar si el InputMethodManager no es null y mostrar el teclado
                if (imm != null) {
                    imm.showSoftInput(edtPreguntaChatBot, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });


        /**............   SOLICITUD A API PARA REVISIÓN DE TEXTO SOSPECHOSO     ........... **/
        inspectionData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtPreguntaChatBot.clearFocus();
// Solicitar que se muestre el teclado cuando se toque el EditText
                inspectionData.requestFocus();
// Obtener el servicio InputMethodManager para mostrar el teclado
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

// Verificar si el InputMethodManager no es null y mostrar el teclado
                if (imm != null) {
                    imm.showSoftInput(inspectionData, InputMethodManager.SHOW_IMPLICIT);
                }
                //showSoftKeyboard(view);
            }
        });
        txtRespuesta.setVisibility(View.GONE);
        txtRespuesta.setMovementMethod(new ScrollingMovementMethod());
        txtRespuetaChatBot.setMovementMethod(new ScrollingMovementMethod());
        //action clic in send message inspector to openai
        btnSendMessageApi.setOnClickListener(view -> {
            if (inspectionData.getText().toString().isEmpty()){
                inspectionData.setError("Introduzca un texto para inspeccionar");
                txtRespuesta.setVisibility(View.GONE);
            }else{
                txtRespuesta.setVisibility(View.VISIBLE);
                txtRespuesta.setText("Esperando respuesta...");
                new Request(getBaseContext()).sendRequestApi(inspectionData.getText().toString(), (isSuccess, mensaje) -> {
                    txtRespuesta.setText(mensaje);
                });
            }
        });

        /**........ SOLICITUD A API PARA PREGUNTA CHATBOT  .............**/
        edtPreguntaChatBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSoftKeyboard(view);
            }
        });
        btnCopyChatBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copiarAlPortapapeles(getBaseContext(),txtRespuetaChatBot.getText().toString());
            }
        });
        //--------------iniciamos la animacion desde el final --------------------
        lott_pregunta.setProgress(porcentajeAgua);
        txtRespuetaChatBot.setVisibility(View.GONE);
        AtomicReference<Boolean> response = new AtomicReference<>(false);
        btnPreguntarChatBot.setOnClickListener(view -> {
            //limpiamos la caja de respuesta.
            txtRespuetaChatBot.setText("");
            if (!response.get()){
                response.set(true);
                if (edtPreguntaChatBot.getText().toString().isEmpty()){
                    edtPreguntaChatBot.setError("Introduzca una pregunta");
                }else{
                    txtRespuesta.setText("Esperando respuesta...");
                    txtRespuetaChatBot.setVisibility(View.VISIBLE);
                    if (porcentajeAgua>0){
                        porcentajeAgua = porcentajeAgua - 0.2f;
                    }else{
                        if (porcentajeAgua == 0){
                            porcentajeAgua = 1;
                        }
                    }

                    lott_pregunta.setProgress(porcentajeAgua);
                    new Request(getBaseContext()).sendQuestion(edtPreguntaChatBot.getText().toString(), (isSuccess, mensaje) -> {
                        consumoTotal +=25;
                        txtConsumoAgua.setText("Ha consumido "+consumoTotal+" mililitros de agua.");
                        txtRespuetaChatBot.setText(mensaje);
                        response.set(false);
                    });
                }
            }

        });


        /**--------------------- Datos de Banner ------------------------------**/
        ArrayList<ModelCiberPaz> listData = new ArrayList<ModelCiberPaz>();
        listData.add(new ModelCiberPaz("CiberPaz Sensibilizaciones (clic aqui)","El Ministerio TIC lleva la alfabetización digital a todos los rincones de Colombia mediante talleres presenciales y gratuitos que promueven el uso empático, seguro y responsable de las Tecnologías de la Información y las Comunicaciones.",R.mipmap.ciber_paz,"https://ciberpaz.gov.co/portal/Secciones/Ciberpaz-Sensibilizaciones/",R.color.alpha_red));
        listData.add(new ModelCiberPaz("Ciberpaz Formaciones (clic aqui)","CiberPaz tiene una oferta de formación en línea con el objetivo de fortalecer las habilidades digitales de 30.000 personas en el territorio nacional en 2024, brindándoles las herramientas necesarias para navegar y aprovechar el mundo digital de manera segura y responsable.",R.mipmap.ciber_paz,"https://ciberpaz.gov.co/portal/Secciones/Ciberpaz-Formaciones/",R.color.alpha_amarillo));
        listData.add(new ModelCiberPaz("Ciber Contenidos (clic aqui)","Explorando los contenidos de CiberPaz podrás mantenerte al día con las últimas noticias del programa, descubrir los lugares donde hemos realizado nuestras sensibilizaciones y acceder a artículos de interés sobre el mundo de las TIC.",R.mipmap.ciber_paz,"https://ciberpaz.gov.co/portal/Secciones/Contenidos/", R.color.alpha_verde));
        listData.add(new ModelCiberPaz("Ciber Inclusión (clic aqui)","Conoce más de los programas de la dirección de apropiación TIC para la población con discapacidad.",R.mipmap.ciber_paz,"https://ciberpaz.gov.co/portal/Secciones/Ciberpaz-Inclusion/", R.color.alpha_morado));

        SimpleCustomAdapter customAdapter = new SimpleCustomAdapter(getApplicationContext(),listData);
        recyclerCiberPaz.setAdapter(customAdapter);
        recyclerCiberPaz.setLayoutManager(new LinearLayoutManager(this));

        btnModuloCiberPaz.setOnClickListener(clickListener ->{
            String url = "https://sensibilizacion.ciberpaz.gov.co/#/data-ciberpaz/response/64?type=public";
            // Crear un Intent para abrir la URL en el navegador
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

            // Añadir el flag FLAG_ACTIVITY_NEW_TASK para indicar que es un contexto fuera de la actividad
            browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Iniciar la actividad del navegador desde el Service
            startActivity(browserIntent);
        });

        customAdapter.setOnClickListener(new SimpleCustomAdapter.ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                String url = listData.get(position).getUrl();
                // Crear un Intent para abrir la URL en el navegador
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                // Añadir el flag FLAG_ACTIVITY_NEW_TASK para indicar que es un contexto fuera de la actividad
                browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Iniciar la actividad del navegador desde el Service
                startActivity(browserIntent);
            }
        });


        /**
         * Activador de funciones -------------------------------
         */
        broadCastActiveFunctions();

        /**
         * Movimiento de textos
         */
        txtRespuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTextWithScroll(txtRespuesta);
            }
        });

        txtRespuetaChatBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTextWithScroll(txtRespuetaChatBot);
            }
        });

        btnCancellChatBot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewsFunction(0,0,true,chatBotView);
            }
        });

        btnCancelPolice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewsFunction(0,0,true,policeFraudeView);
            }
        });

        btnCancelBrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewsFunction(0,0,true,brainIntelligent);
            }
        });


        imgMic.setOnClickListener(view -> {
            // Crear el Intent para el reconocimiento de voz
//            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora...");

            // Iniciar el reconocimiento de voz
            //speechRecognizer.startListening(intent);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                    //hablaAhora();
                }
            });

        });

        //end oncreate
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            // Obtener el InputMethodManager para controlar el teclado
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            // Intentar mostrar el teclado
            boolean isShowing = imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);

            // Si el teclado no se muestra automáticamente, forzamos que se muestre
            if (!isShowing) {
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
            }
        }
    }




    /**
     * Aqui recibimos las funciones que se van a activar
     */
    private void broadCastActiveFunctions() {
        IntentFilter filter = new IntentFilter("com.ingenieriajhr.visualcontroll");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getStringExtra("activar");
                Log.d("service_camara","iniciar camara");

                if (action != null){
                    if(action.equals("activar_brain")){
                        Log.d("service_camara","activar brain");
                        activarBrain();
                    }
                    if (action.equals("desactivar_brain")){
                        desactivarBrain();
                        Log.d("service_camara","desactivar_brain");
                    }

                    if (action.equals("activar_policia")){
                        Log.d("service_camara","activar police");
                        activarPolice();
                    }
                    if (action.equals("desactivar_policia")){
                        desactivarPolice();
                        Log.d("service_camara","desactivar_police");
                    }
                    if (action.equals("activar_chatbot")){
                        Log.d("service_camara","activar police");
                        activarChatBot();
                    }
                    if (action.equals("desactivar_chatbot")){
                        desactivarChatBot();
                        Log.d("service_camara","desactivar_police");
                    }
                    if (action.equals("activar_ciberpaz")){
                        activarCiberPaz();
                    }
                    if (action.equals("desactivar_ciberpaz")){
                        Log.d("service_camara","desactivar_police");
                        desactivarCiberPaz();
                    }

                }
            }
        };
        registerReceiver(receiver,filter);
    }

    /**
     * Desactivar ciber paz
     */
    private void desactivarCiberPaz() {
        if (ciberPazView.isAttachedToWindow())windowManager.removeView(ciberPazView);
    }

    private void activarCiberPaz() {
        windowManager.addView(ciberPazView,ciberPazLayout);
    }


    /**
     * Activar ChatBot
     */
    private void activarChatBot() {
        windowManager.addView(chatBotView,chatBotLayout);
    }

    /**
     * Desactivar ChatBot
     */
    private void desactivarChatBot() {
        if (chatBotView.isAttachedToWindow()) windowManager.removeView(chatBotView);
    }

    /**
     * Desactivar police Fraude
     */
    private void desactivarPolice() {
        if (policeFraudeView.isAttachedToWindow()) windowManager.removeView(policeFraudeView);
    }

    /**
     * Activiar police Fraude
     */
    private void activarPolice() {
        windowManager.addView(policeFraudeView,policeFraudeLayout);
    }

    /**
     * Desactivo brain Datos interesantes
     */
    private void desactivarBrain() {
       if (brainIntelligent.isAttachedToWindow())windowManager.removeView(brainIntelligent);
    }

    /**
     * Activa brain Datos interesantes
     */
    private void activarBrain() {
        windowManager.addView(brainIntelligent,brainIntelligentLayout);
    }

    /**
     *
     * Creamos una notificación
     *
     */
    private void createNotificationChannel(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNE_ID,
                    "Servicio canal profundo",
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public boolean scrollView(AccessibilityNodeInfo nodeInfo, int dx, float percentage) {
        if (nodeInfo == null) return false;

        if (nodeInfo.isScrollable()) {
            Rect scrollBounds = new Rect();
            nodeInfo.getBoundsInScreen(scrollBounds);
            int scrollableHeight = scrollBounds.height();

            // Reduce the scroll speed by using a smaller percentage
            float slowedPercentage = percentage * 0.3f; // Reduces scroll speed to 30% of original
            int scrollAmount = (int) (scrollableHeight * slowedPercentage);

            // Add small delays between scroll actions to create a smoother, slower effect
            Bundle arguments = new Bundle();

            if (percentage < 0) {
                arguments.putInt(String.valueOf(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD), scrollAmount);
                boolean result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD, arguments);

                // Add a small delay to slow down scrolling
                try {
                    Thread.sleep(100); // 100 milliseconds delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return result;
            } else {
                arguments.putInt(String.valueOf(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD), scrollAmount);
                boolean result = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD, arguments);

                // Add a small delay to slow down scrolling
                try {
                    Thread.sleep(100); // 100 milliseconds delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                return result;
            }
        }

        // Recorrer los hijos recursivamente
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            if (scrollView(nodeInfo.getChild(i), dx, percentage)) {
                return true;
            }
        }

        return false; // No se pudo realizar el scroll
    }



/*
    public boolean scrollView(AccessibilityNodeInfo nodeInfo, int dx, float percentage) {




        if (nodeInfo == null) return false;

        if (nodeInfo.isScrollable()) {
            Rect scrollBounds = new Rect();
            nodeInfo.getBoundsInScreen(scrollBounds);
            int scrollableHeight = scrollBounds.height();

            // Calcular el desplazamiento según el porcentaje del tamaño scrollable
            int scrollAmount = (int) (scrollableHeight * percentage);

            // Realizar el scroll hacia adelante con el desplazamiento calculado
            Bundle arguments = new Bundle();

            if (percentage<0){
                arguments.putInt(String.valueOf(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD), scrollAmount);
                return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD, arguments);
            }else{
                arguments.putInt(String.valueOf(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD), scrollAmount);
                return nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD, arguments);
            }
        }

        // Recorrer los hijos recursivamente
        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
            if (scrollView(nodeInfo.getChild(i), dx, percentage)) {
                return true;
            }
        }

        return false; // No se pudo realizar el scroll



    }
*/

    /**
     * Open accesibilidad configuracion
     */
    private void openAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    /**
     * Tenemos permisos de accesibilidad total
     * @param context
     * @param accessibilityService
     * @return
     */
    public static boolean isAccessibilityServiceEnabled(Context context, Class<?> accessibilityService) {
        ComponentName expectedComponentName = new ComponentName(context, accessibilityService);

        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(),  Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
        if (enabledServicesSetting == null)
            return false;

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
        colonSplitter.setString(enabledServicesSetting);

        while (colonSplitter.hasNext()) {
            String componentNameString = colonSplitter.next();
            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);

            if (enabledService != null && enabledService.equals(expectedComponentName))
                return true;
        }

        return false;
    }

    private void initFaceLandMarkerHelper() {
        faceLandMarkerHelper = new FaceLandMarkerHelper(
                this,
                this);
        if (faceLandMarkerHelper.isClose()) {
            faceLandMarkerHelper.setupFaceLandmarker();
        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //start foreground debe llamarse al inicial la aplicacion o se cerrara la aplicación movil.
        Notification notification = createNotification();
        updateNotification();

        startForeground(NOTIFICATION_ID, notification);
        startService = true;

        /**
         * Objeto audio reconocimiento.....
         */
        // Inicialización de SpeechRecognizer


        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);


        // Crear un intent para iniciar el reconocimiento de voz
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES"); // Establecer el idioma
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1); // Máximo de resultados

        speechListener();

        //speechRecognizer.startListening(recognizerIntent);


        return START_STICKY;
    }



    /**
     * Iniciar proceso de camara
     */
    public void initCamara(){
        saveStatusCameraDataSharePreference(true);
        //usada para remove view luego
        isInitCamera = true;
        //start service
        startService = true;
        //camara below display
        windowManager.addView(cameraView,cameraLayout);
        windowManager.addView(desplazamientoView,desplazamientoLayout);
        windowManager.addView(retrocederView,retrocesoLayout);
        cursorView.setClickable(true);
        windowManager.addView(cursorView,cursorLayout);


        /**     Init facelandMarkerhelper    **/
        initFaceLandMarkerHelper();


        /**                                  implementation camera                    */
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =ProcessCameraProvider.getInstance(this);

        CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

        luminosityAnalyzer.returnBitmap =true;
        luminosityAnalyzer.isFrontCamera = true;

        imageAnalysis = new ImageAnalysis.Builder()
                .setImageQueueDepth(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888).build();

        imageAnalysis.setAnalyzer(cameraExecutor,luminosityAnalyzer);

        cameraProviderFuture.addListener(()->{
            imagePreview = new Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .build();
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                cameraProvider.bindToLifecycle(serviceLifecycleOwner,cameraSelector,imageAnalysis);
                //previewLive.setImplementationMode(PreviewView.ImplementationMode.COMPATIBLE);
                imagePreview.setSurfaceProvider(previewLive.getSurfaceProvider());

            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }, ContextCompat.getMainExecutor(this));


        luminosityAnalyzer.addListener(new BitmapResponse() {
            @Override
            public void bitmapReturn(Bitmap bitmap) {
                if (okCamaraFrame){
                    okCamaraFrame = false;
                    faceLandMarkerHelper.detectLiveStream(bitmap,true);
                }
                //hilo principal
                new Handler(Looper.getMainLooper()).post(() -> imgLive.setImageBitmap(bitmap));
            }
        });

        //cicl0 de vida.
        serviceLifecycleOwner.start();

        sensinbilidadObserve.observeForever(new Observer<Float>() {
            @Override
            public void onChanged(Float aFloat) {
                transitionSpeedObserve = aFloat;
                Log.d("cambiospeed",aFloat+"");
            }
        });


        /** Recognition model dato draw **/

        viewDraw.addListenerValuePorcentages(new ValuePorcentages() {
            // Variables para almacenar la posición anterior del cursor
            float prevX = 0;
            float prevY = 0;

            // Velocidad de transición para suavizar el movimiento

            @Override
            public void resultPointsEye(ModelPorcentages modelPorcentages) {

                float porcentageX = modelPorcentages.getPorcentajeX();
                float porcentageY = modelPorcentages.getPorcentajeY();
                float movY = -1;
                float movX = -1;

                //45 - 55    (0 - 100) y = mx+b
                float salidax = (((10*porcentageX)-450)/100)*widthDisplay;
                if (salidax<0) salidax = 0;
                if (salidax>widthDisplay) salidax = widthDisplay-30;
                movX = salidax;
                //46 0 49 100
                float salida = ((50*porcentageY)-2350f)/100;
                if (salida<0) salida = 0;
                if (salida>heightDisplay) salida =heightDisplay-30;
                movY = (salida*(heightDisplay));

                // Suavizar el movimiento del cursor
                float newX = prevX + (salidax - prevX) * transitionSpeedObserve;
                float newY = prevY + (movY - prevY) * transitionSpeedObserve;

                prevX = newX;
                prevY = newY;

                if (movX !=-1 && movY != -1){
                    cursorView.setX(newX);
                    cursorView.setY(newY);
                    if (startService) {
                        //actualiza el movimiento del mouse
                        windowManager.updateViewLayout(cursorView,cursorLayout);
                    }
                }

                //Model porcentage is click..
                if (modelPorcentages.getClick()) {
                    performClick((int) newX, (int) newY+50);
                }
                /** -------------------   Sección de desplazamiento  ------------------------  **/
                Log.d("movimientomouse", String.valueOf(desplazamientoView.getHeight()));
                if (cursorView.getY()>(heightDisplay/2) &&
                        cursorView.getX()>(widthDisplay-imgDesAbajo.getWidth()) &&
                        cursorView.getY()<((heightDisplay/2)+imgDesAbajo.getHeight()/2)){
                    if (isScrollReady){
                        scrollView(getRootInActiveWindow(),0,0.5f);
                        Log.d("movimiento2024d","desplazamiento abajo");
                        isScrollReady = false;
                    }
                }else{
                    if (cursorView.getY()>(heightDisplay/2)-imgDesArriba.getHeight() &&
                            cursorView.getX()>(widthDisplay-imgDesArriba.getWidth()) &&
                            cursorView.getY()<(heightDisplay/2)){
                        if (isScrollReady){
                            scrollView(getRootInActiveWindow(),0,-0.5f);
                            Log.d("movimiento2024d","desplazamiento arriba");
                            isScrollReady = false;
                        }
                    }else{
                        if (cursorView.getY()>(heightDisplay/2)-(imgRetroceder.getHeight()/2) &&
                                cursorView.getY()<(heightDisplay/2)+(imgRetroceder.getHeight()/2) &&
                                cursorView.getX()<imgRetroceder.getWidth() && cursorView.getX()>0){
                            if (isScrollReady){
                                isScrollReady = false;
                                Log.d("movimiento2024d",(heightDisplay/2)-imgRetroceder.getHeight()+" desplazamiento atras");
                                performGlobalAction(GLOBAL_ACTION_BACK);
                            }
                        }else{
                            if (cursorView.getY()<(ciberPazLayout.y+imgCiberPaz.getHeight()) &&
                                    cursorView.getY()>ciberPazLayout.y && cursorView.getX()>ciberPazLayout.x &&
                            cursorView.getX()<(ciberPazLayout.x+imgCiberPaz.getWidth()) && ciberPazView.isAttachedToWindow() &&
                                    (System.currentTimeMillis()-isOpenCiberPaz)>timeOpenAndCloseElements){
                                viewsFunction(0,0,true,ciberPazView);
                                isOpenCiberPaz = System.currentTimeMillis();
                            }else{
                                if (cursorView.getY()<(brainIntelligentLayout.y+imgBrainIntelligent.getHeight()) &&
                                        cursorView.getY()>brainIntelligentLayout.y && screenWidth-cursorView.getX()>brainIntelligentLayout.x &&
                                        screenWidth-cursorView.getX()<(brainIntelligentLayout.x+imgBrainIntelligent.getWidth()) &&
                                        brainIntelligent.isAttachedToWindow() &&
                                        (System.currentTimeMillis()-isOpenBrain)>timeOpenAndCloseElements){
                                    viewsFunction(0,0,true,brainIntelligent);
                                    isOpenBrain = System.currentTimeMillis();
                                    Log.d("ciberpazdatos","entro----------------------------------------");
                                }else{
                                    if (cursorView.getY()<(policeFraudeLayout.y+imgPoliceFraude.getHeight()) &&
                                            cursorView.getY()>policeFraudeLayout.y && screenWidth-cursorView.getX()>policeFraudeLayout.x &&
                                            screenWidth-cursorView.getX()<(policeFraudeLayout.x+imgPoliceFraude.getWidth()) &&
                                            policeFraudeView.isAttachedToWindow() &&
                                            (System.currentTimeMillis()-isPoliceTime)>timeOpenAndCloseElements){
                                        viewsFunction(0,0,true,policeFraudeView);
                                        isPoliceTime = System.currentTimeMillis();
                                        if (inspectionData.getVisibility()==View.VISIBLE){
                                            listInspector = true;
                                            openMainActivity();
                                        }else{
                                            listInspector = false;
                                        }

                                    }else{
                                        if (cursorView.getY()<(chatBotLayout.y+imgChatBot.getHeight()) &&
                                                cursorView.getY()>chatBotLayout.y && screenWidth-cursorView.getX()>chatBotLayout.x &&
                                                screenWidth-cursorView.getX()<(chatBotLayout.x+imgChatBot.getWidth()) &&
                                                chatBotView.isAttachedToWindow() &&
                                                (System.currentTimeMillis()-isChatBotTime)>timeOpenAndCloseElements){
                                            viewsFunction(0,0,true,chatBotView);
                                            if (edtPreguntaChatBot.getVisibility()==View.VISIBLE){
                                                listChatBot = true;
                                                openMainActivity();
                                            }else{
                                                listChatBot = false;
                                            }
                                            isChatBotTime = System.currentTimeMillis();
                                        }else{
                                            isScrollReady = true;
                                        }
                                    }

                                }
                            }
                        }

                    }
                }

                //Home if is home true
                if (modelPorcentages.getIsHome()){
                    performGlobalAction(GLOBAL_ACTION_HOME);
                }

                if (modelPorcentages.getIsOpenApps() && !changeOpenBoca){

                    performGlobalAction(GLOBAL_ACTION_RECENTS);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Log.d("entradaglobal","entradauno");
                    changeOpenBoca = true;
                }else {
                    if (!modelPorcentages.getIsOpenApps()){
                        changeOpenBoca = false;
                    }
                }

                okCamaraFrame = true;

                //Log.d("movimiento 2024"," Porcentaje X : %"+porcentageX+" "+" Porcentaje Y : %"+porcentageY+" mov y :"+movY);
            }
        });



        /**Get data calibration diferencia**/
        diferenciaX.observeForever(new Observer<float[]>() {
            @Override
            public void onChanged(float[] floats) {
                diferenciaXiCalibration = floats[0];
                diferenciaXfCalibration = floats[1];
            }
        });


        //Requerir permisos de accesibilidad total
        if (!isAccessibilityServiceEnabled(this, this.getClass())){
            openAccessibilitySettings();
        }

    }

    /**
     * Abrir el activity prinicipal
     */
    private void openMainActivity() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getApplicationContext());

        // Crear un intent para iniciar el reconocimiento de voz
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES"); // Establecer el idioma
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1); // Máximo de resultados

        speechListener();

        speechRecognizer.startListening(recognizerIntent);
        /*
        // Crear un intent que apunte a la actividad principal
        Intent intent = new Intent(this, MainActivity.class);
        // Establecer las flags para asegurarse de que la actividad se abra correctamente
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Necesario para iniciar actividad desde un servicio

        // Iniciar la actividad en el hilo principal
        startActivity(intent);

        //esperamos 1.5 segundos y ejecutamos el service de reconocimiento de voz
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }).start();*/
    }

    // Función para verificar las condiciones y realizar la acción
    public void checkViewAndUpdate(long lastOpenTime, View cursorView, View targetView, ViewGroup.LayoutParams targetLayoutParams, ImageView targetImage, long timeOpenAndCloseElements, long currentTime) {
        // Obtiene las coordenadas del LayoutParams (suponiendo que son coordenadas de la vista padre)
        int targetViewX = targetView.getLeft();
        int targetViewY = targetView.getTop();
        int targetViewWidth = targetImage.getWidth();
        int targetViewHeight = targetImage.getHeight();

        // Verifica si el cursor está dentro de los límites de la vista
        if (cursorView.getY() < (targetViewY + targetViewHeight) &&
                cursorView.getY() > targetViewY &&
                cursorView.getX() > targetViewX &&
                cursorView.getX() < (targetViewX + targetViewWidth) &&
                targetView.isAttachedToWindow() &&
                (currentTime - lastOpenTime) > timeOpenAndCloseElements) {

            // Llama a la función de vistas y actualiza el tiempo de apertura
            viewsFunction(0, 0, true, targetView);
            lastOpenTime = currentTime; // Actualiza el tiempo de la última apertura
        }
    }


    public void stopCamera(){
        saveStatusCameraDataSharePreference(false);
        //usada para remove view luego
        isInitCamera = false;
        if (windowManager != null && cursorView != null){
            startService = false;
            windowManager.removeView(cursorView);
            windowManager.removeView(cameraView);
            windowManager.removeView(desplazamientoView);
            windowManager.removeView(retrocederView);

        }
    }


    /**
     * Actualiza la notificacion
     */
    private void updateNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
           NotificationChannel notificationChannel = new NotificationChannel(CHANNE_ID,
                    "Servicio canal profundo",
                    NotificationManager.IMPORTANCE_LOW);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(notificationChannel);
        }

    }

    public static void copiarAlPortapapeles(Context context, String texto) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Texto GPT", texto);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context.getApplicationContext(), "Copiado en portapapeles", Toast.LENGTH_SHORT).show();
    }


        /**
         * Creamos la notificacion que sera visible
         *
         * @return
         */
    private Notification createNotification() {

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,notificationIntent,PendingIntent.FLAG_IMMUTABLE);

        remoteViews = new RemoteViews(getPackageName(),R.layout.remote_view_notification);

        return new NotificationCompat.Builder(this,CHANNE_ID)
                .setSmallIcon(R.drawable.iaeye)
                .setCustomContentView(remoteViews)
                .setCustomBigContentView(remoteViews)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[]{0,0})
                .build();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy(); // Destruir el recognizer cuando el servicio se destruye
        }
        // Liberar recursos cuando el servicio se destruye
        Log.d("destroyview","inicio");
        // Desregistrar el BroadcastReceiver cuando el servicio se destruya
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        if (windowManager != null && cursorView != null && cameraView!=null){
            Log.d("destroyview","elimina vistas");
            startService = false;
            //comprobar que windowmanager tenga a cursosView
            if (cursorView.isAttachedToWindow()) windowManager.removeView(cursorView);
            if (cameraView.isAttachedToWindow()) windowManager.removeView(cameraView);
            if (desplazamientoView.isAttachedToWindow()) windowManager.removeView(desplazamientoView);
            if (retrocederView.isAttachedToWindow()) windowManager.removeView(retrocederView);
            if (brainIntelligent.isAttachedToWindow()) windowManager.removeView(brainIntelligent);
            if (chatBotView.isAttachedToWindow()) windowManager.removeView(chatBotView);
            if (policeFraudeView.isAttachedToWindow()) windowManager.removeView(policeFraudeView);
            if (ciberPazView.isAttachedToWindow()) windowManager.removeView(ciberPazView);

        }
        saveStatusCameraDataSharePreference(false);
    }


    @Override
    public void onError(String error, int errorCode) {

    }


    /**
     * Resultados de los puntos obtenidos por la imagen preview
     * @param resultBundle
     */
    @Override
    public void onResults(FaceLandMarkerHelper.ResultBundle resultBundle) {
        widthImgLive = imgLive.getWidth();
        heightImgLive = imgLive.getHeight();
        //cuando se obtenga el ancho y el alto del bitmap para ajustar parametros a
        //imagen de salida.
        if (widthImgLive>0){
           // new Handler(Looper.getMainLooper()).post(() -> {
                if (viewDraw!=null){
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            viewDraw.setResults(resultBundle.result,
                                    widthImgLive,
                                    heightImgLive,
                                    RunningMode.LIVE_STREAM);
                        }
                    });

                    //viewDraw.invalidate();
                }
           // });
        }

    }

    @Override
    public void onEmpty() {
        okCamaraFrame = true;
        FaceLandMarkerHelper.LandmarkerListener.super.onEmpty();
    }



    /**
     * Click
     */
    private void performClick(int x, int y) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            // Encuentra el nodo más cercano a las coordenadas (x, y)
            AccessibilityNodeInfo node = findClickableNodeAt(rootNode, x, y);
            if (node != null) {
                // Simula un clic en el nodo encontrado
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                // Importante: liberar los recursos después de usarlos
                node.recycle();
            }
            rootNode.recycle();
        }
    }

    /**
     * Desplazamiento hacia arriba o hacia abajo
     * @param rootNode
     * @param x
     * @param y
     * @return
     */
    private AccessibilityNodeInfo findClickableNodeAt(AccessibilityNodeInfo rootNode, int x, int y) {
        // Recorre los nodos en busca de un nodo clickeable en las coordenadas (x, y)
        Rect boundsInScreen = new Rect();
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            AccessibilityNodeInfo node = rootNode.getChild(i);
            if (node != null) {
                node.getBoundsInScreen(boundsInScreen);
                if (boundsInScreen.contains(x, y) && node.isClickable()) {
                    return node;
                }
                // Si no se encuentra el nodo clickeable, busca en los nodos hijos
                AccessibilityNodeInfo clickableNode = findClickableNodeAt(node, x, y);
                if (clickableNode != null) {
                    return clickableNode;
                }
            }
        }
        return null;
    }


    /**
     * Metodo que recibe el callback del elementos que se esta moviendo
     * @param newX
     * @param newY
     * @param isClic
     */
    @Override
    public void onTouchMoved(int newX, int newY, boolean isClic,View view) {
        viewsFunction(newX,newY,isClic,view);
    }

    private void viewsFunction(int newX, int newY, boolean isClic, View view) {
        if (view == brainIntelligent){
            if (isClic){
                if (txtBrainIntelligent.getVisibility() == View.VISIBLE){
                    if (txtBrainIntelligent.getText().toString().equals("Hola soy cerebro curioso tocame y aprende algo nuevo")){
                        txtBrainIntelligent.setText(DatosCuriososCerebroCurioso.getDatoCurioso());
                        btnCancelBrain.setVisibility(View.VISIBLE);
                    }else{
                        Log.d("service_camara","Is clic");
                        brain_lottie.setVisibility(View.GONE);
                        txtBrainIntelligent.setVisibility(View.GONE);
                        btnCancelBrain.setVisibility(View.GONE);
                    }
                }else{
                    Log.d("service_camara","Is clic");
                    brain_lottie.playAnimation();
                    brain_lottie.setVisibility(View.VISIBLE);
                    txtBrainIntelligent.setText(DatosCuriososCerebroCurioso.getDatoCurioso());
                    txtBrainIntelligent.setVisibility(View.VISIBLE);
                    btnCancelBrain.setVisibility(View.VISIBLE);
                }

            }else{
                Log.d("service_camara","Is Move");
                //brain_lottie.setVisibility(View.GONE);
                // txtBrainIntelligent.setVisibility(View.GONE);
                brainIntelligentLayout.y = newY;
                windowManager.updateViewLayout(brainIntelligent,brainIntelligentLayout);
            }
        }
        /**
         * lott_document = policeFraudeView.findViewById(R.id.lott_document);
         *         txtPoliceFraude = policeFraudeView.findViewById(R.id.txtPoliceFraude);
         *         containerCommands
         */
        if (view == policeFraudeView){
            if (isClic){
                if (txtPoliceFraude.getVisibility() == View.VISIBLE){
                    policeFraudeLayout = eliminarFoco(2,false);
                    Log.d("service_camara","Is Clic");
                    txtPoliceFraude.setVisibility(View.GONE);
                    lott_document.setVisibility(View.GONE);
                    containerCommands.setVisibility(View.GONE);
                    btnCancelPolice.setVisibility(View.GONE);
                    inspectionData.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    //clear focus
                    inspectionData.clearFocus();
                    imm.hideSoftInputFromWindow(inspectionData.getWindowToken(), 0); // Oculta el teclado
                    //removemos o no permitira usar nuevamente el teclado en otras aplicaciones.
                    windowManager.removeView(policeFraudeView);
                    windowManager.addView(policeFraudeView,policeFraudeLayout);
                    if (cursorView.isAttachedToWindow()){
                        windowManager.removeView(cursorView);
                        windowManager.addView(cursorView,cursorLayout);
                    }


                }else{
                    policeFraudeLayout = tomarFoco(2,false);
                    inspectionData.setVisibility(View.VISIBLE);
                    edtPreguntaChatBot.clearFocus();
                    inspectionData.requestFocus();
                    Log.d("service_camara","Is clic");
                    txtPoliceFraude.setVisibility(View.VISIBLE);
                    lott_document.setVisibility(View.VISIBLE);
                    containerCommands.setVisibility(View.VISIBLE);
                    btnCancelPolice.setVisibility(View.VISIBLE);
                    lott_document.playAnimation();
                    windowManager.removeView(policeFraudeView);
                    windowManager.addView(policeFraudeView,policeFraudeLayout);
                    if (cursorView.isAttachedToWindow()){
                        windowManager.removeView(cursorView);
                        windowManager.addView(cursorView,cursorLayout);
                    }

                }
            }else{
                policeFraudeLayout.y = newY;
                windowManager.updateViewLayout(policeFraudeView,policeFraudeLayout);
            }
        }
        if (view == chatBotView){
            if (isClic){

                if (lott_pregunta.getVisibility() == View.VISIBLE) {
                    Log.d("service_camara", "Is Clic");
                    txtRespuetaChatBot.setVisibility(View.GONE);
                    btnCopyChatBot.setVisibility(View.GONE);
                    lott_pregunta.setVisibility(View.GONE);
                    edtPreguntaChatBot.setVisibility(View.GONE);
                    txtChatBotTittle.setVisibility(View.GONE);
                    btnPreguntarChatBot.setVisibility(View.GONE);
                    txtConsumoAgua.setVisibility(View.GONE);
                    btnCancellChatBot.setVisibility(View.GONE);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    //clear focus
                    edtPreguntaChatBot.clearFocus();
                    imm.hideSoftInputFromWindow(edtPreguntaChatBot.getWindowToken(), 0); // Oculta el teclado
                    //cuando las vistas estan escondidas, no toman el foco
                    chatBotLayout= eliminarFoco(3,false);
                    windowManager.removeView(chatBotView);
                    windowManager.addView(chatBotView,chatBotLayout);
                    if (cursorView.isAttachedToWindow()) {
                        windowManager.removeView(cursorView);
                        windowManager.addView(cursorView, cursorLayout);
                    }
                }else {
                    edtPreguntaChatBot.requestFocus();
                    if (txtRespuetaChatBot.getText().toString().equalsIgnoreCase("Cargando respuesta...")){
                        txtRespuesta.setVisibility(View.GONE);
                    }
                    chatBotLayout= tomarFoco(3,false);
                    Log.d("service_camara", "Is clic");
                    txtRespuetaChatBot.setVisibility(View.VISIBLE);
                    btnCopyChatBot.setVisibility(View.VISIBLE);
                    lott_pregunta.setVisibility(View.VISIBLE);
                    edtPreguntaChatBot.setVisibility(View.VISIBLE);
                    txtChatBotTittle.setVisibility(View.VISIBLE);
                    btnPreguntarChatBot.setVisibility(View.VISIBLE);
                    txtConsumoAgua.setVisibility(View.VISIBLE);
                    btnCancellChatBot.setVisibility(View.VISIBLE);
                    windowManager.removeView(chatBotView);
                    windowManager.addView(chatBotView,chatBotLayout);

                    if (cursorView.isAttachedToWindow()) {
                        windowManager.removeView(desplazamientoView);
                        windowManager.addView(desplazamientoView,desplazamientoLayout);
                        windowManager.removeView(cursorView);
                        windowManager.addView(cursorView,cursorLayout);
                    }

                }
            }else{
                chatBotLayout.y = newY;
                windowManager.updateViewLayout(chatBotView,chatBotLayout);
            }
        }

        if (view == ciberPazView){
            if (isClic){
                if (txtCiberPazTittle.getVisibility() == View.VISIBLE) {
                    Log.d("service_camara", "Is Clic");
                    txtCiberPazTittle.setVisibility(View.GONE);
                    btnModuloCiberPaz.setVisibility(View.GONE);
                    recyclerCiberPaz.setVisibility(View.GONE);

                    //cuando las vistas estan escondidas, no toman el foco
                    ciberPazLayout= eliminarFoco(1,true);
                    windowManager.removeView(ciberPazView);
                    windowManager.addView(ciberPazView,ciberPazLayout);
                }else {
                    Log.d("service_camara", "Is clic");
                    txtCiberPazTittle.setVisibility(View.VISIBLE);
                    btnModuloCiberPaz.setVisibility(View.VISIBLE);
                    recyclerCiberPaz.setVisibility(View.VISIBLE);

                    ciberPazLayout= tomarFoco(1,true);
                    windowManager.removeView(ciberPazView);
                    windowManager.addView(ciberPazView,ciberPazLayout);
                    windowManager.removeView(cursorView);
                    windowManager.addView(cursorView,cursorLayout);
                }
            }else{
                ciberPazLayout.y = newY;
                windowManager.updateViewLayout(ciberPazView,ciberPazLayout);
            }
        }

    }

    private LayoutParams eliminarFoco(int valueDivision,Boolean isStart) {
        LayoutParams params  =  new LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                layout_parms,
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE|
                        LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                        LayoutParams.FLAG_NOT_FOCUSABLE |
                        LayoutParams.FLAG_DISMISS_KEYGUARD|
                        LayoutParams.FLAG_NOT_TOUCH_MODAL,
                        //LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                //LayoutParams.FLAG_NOT_TOUCHABLE, // no toma el foco
                PixelFormat.TRANSLUCENT
        );
        if (isStart){
            params.gravity = Gravity.START|Gravity.TOP;
        }else{
            params.gravity = Gravity.END|Gravity.TOP;
        }


        params.y = division*valueDivision;

        return params;
    }

    private LayoutParams tomarFoco(int valueDivision,Boolean isStart){
        LayoutParams params  =  new LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT,
                    layout_parms,
                    LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE|
                            LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                            //LayoutParams.FLAG_NOT_FOCUSABLE |
                            LayoutParams.FLAG_DISMISS_KEYGUARD|
                            LayoutParams.FLAG_NOT_TOUCH_MODAL,
                            //LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                    //LayoutParams.FLAG_NOT_TOUCHABLE, // Toma el foco
                    PixelFormat.TRANSLUCENT
            );
        if (isStart){
            params.gravity = Gravity.START|Gravity.TOP;
        }else{
            params.gravity = Gravity.END|Gravity.TOP;
        }
        params.y = division*valueDivision;
        return params;
    }



    public void speechListener(){
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {
                //Log.d("SpeechRecognition", "Ready for speech...");

            }

            @Override
            public void onBeginningOfSpeech() {
                //Log.d("SpeechRecognition", "Speech beginning...");

            }

            @Override
            public void onRmsChanged(float v) {
               // Log.d("SpeechRecognition", "End of speech...");

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {
                Log.e("SpeechRecognition", "Error: " + i);

            }

            @Override
            public void onResults(Bundle results) {
                // Obtener los resultados del reconocimiento de voz
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    Log.d("SpeechRecognition", "Resultado: " + matches.get(0) + " "+listChatBot+"  "+listInspector);

                    if (listChatBot){
                        edtPreguntaChatBot.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("SpeechRecognition", "Resultado 2: " + matches.get(0));
                                edtPreguntaChatBot.setText(matches.get(0));
                                new Request(getBaseContext()).sendQuestion(edtPreguntaChatBot.getText().toString(), (isSuccess, mensaje) -> {
                                    consumoTotal +=25;
                                    txtConsumoAgua.setText("Ha consumido "+consumoTotal+" mililitros de agua.");
                                    txtRespuetaChatBot.setText(mensaje);
                                    listChatBot = false;
                                    showTextWithScroll(txtRespuetaChatBot);
                                });
                            }
                        });
                    }


                    if (listInspector){
                        inspectionData.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("SpeechRecognition", "Resultado 2: " + matches.get(0));
                                inspectionData.setText(matches.get(0));
                                new Request(getBaseContext()).sendRequestApi(inspectionData.getText().toString(), (isSuccess, mensaje) -> {
                                    txtRespuesta.setVisibility(View.VISIBLE);
                                    txtRespuesta.setText(mensaje);
                                    listInspector = false;
                                    showTextWithScroll(txtRespuesta);
                                });
                            }
                        });
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
    }


    private void showTextWithScroll(TextView textToDisplay) {
        // Crea una animación para desplazar el texto hacia arriba
        ObjectAnimator scrollAnimator = ObjectAnimator.ofInt(textToDisplay, "scrollY", 0, textToDisplay.getHeight());
        scrollAnimator.setDuration(8000); // Duración de la animación (en milisegundos)
        scrollAnimator.start(); // Inicia la animación
    }


}
