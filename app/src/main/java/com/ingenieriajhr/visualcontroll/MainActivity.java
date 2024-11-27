package com.ingenieriajhr.visualcontroll;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.ingenieriajhr.visualcontroll.accessibility.AccessibilityServiceUtil;
import com.ingenieriajhr.visualcontroll.accessibility.MouseAccessibilityService;
import com.ingenieriajhr.visualcontroll.accessibility.saveData.SaveState;
import com.ingenieriajhr.visualcontroll.livedata.SensinbilidadObserve;


import cn.pedant.SweetAlert.SweetAlertDialog;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SYSTEM_ALERT_WINDOW_PERMISSION_CODE = 101;

    //Btn calibrate
    Button btnStartService,btnAccesibilidad,
            btnCamara,btnAparecerEncima,btnInitCamara,
            btnActiveBrainIntelligent,btnActivarPolicia,
            btnChatBot,btnCiberPaz,btnHelp
            ;
    SeekBar seekSensibilidad;
    TextView txtSensibilidad;

    //Mouse service
    MouseAccessibilityService mouseAccessibilityService = new MouseAccessibilityService();

    //enservice
    SensinbilidadObserve sensibilidadObserve = SensinbilidadObserve.getInstance();
    //Intent
    Intent intent;

    //se inicio el servicio
    private Boolean isStartService = false;

    //Permisos
    Boolean isCamara = false;
    Boolean isAccesibilidad = false;
    Boolean isAparecerEncima = false;
    Boolean isBrainIntelligent = false;

    Boolean isPolicia = false;
    Boolean isChatBot = false;

    Boolean isCiberPaz = false;

    Float sensibilidad = 0.1f;

    public static boolean isCamaraActive = false;

    private BroadcastReceiver receiverService;

    private static final Integer REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = {
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
            // Add other permission constants here
    };
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStartService = findViewById(R.id.btnStartService);
        btnAccesibilidad = findViewById(R.id.btnAccesibilidad);
        btnAparecerEncima = findViewById(R.id.btnAparecerEncima);
        btnCamara = findViewById(R.id.btnCamara);
        txtSensibilidad = findViewById(R.id.txtSensibilidad);
        seekSensibilidad = findViewById(R.id.seekSensibilidad);
        btnInitCamara = findViewById(R.id.btnInitCamara);
        btnActiveBrainIntelligent = findViewById(R.id.btnActiveBrainIntelligent);
        btnActivarPolicia = findViewById(R.id.btnActivarPolicia);
        btnChatBot = findViewById(R.id.btnChatBot);
        btnCiberPaz = findViewById(R.id.btnCiberPaz);
        btnHelp = findViewById(R.id.btnHelp);
        //service instance
        intent = new Intent(this, mouseAccessibilityService.getClass());
        //suscribe click
        btnStartService.setOnClickListener(this);
        btnCamara.setOnClickListener(this);
        btnAccesibilidad.setOnClickListener(this);
        btnAparecerEncima.setOnClickListener(this);
        btnInitCamara.setOnClickListener(this);
        btnActiveBrainIntelligent.setOnClickListener(this);
        btnActivarPolicia.setOnClickListener(this);
        btnChatBot.setOnClickListener(this);
        btnCiberPaz.setOnClickListener(this);
        btnHelp.setOnClickListener(this);

        //sensibilidad
        sensibilidad();
        //verificamos permisos
        verificarPermisos();
        //color de barra de notificaciones
        getWindow().setStatusBarColor(getResources().getColor(R.color.green));

        //Caputaramos los datos que se procesaron en ejecucion de servicio....
        if (new SaveState().getStateCamera(this)) {
            btnInitCamara.setText("Activar Asistencia Motriz");
        } else {
            btnInitCamara.setText("Activar Asistencia Motriz");
        }



    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void sensibilidad() {
        seekSensibilidad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sensibilidad = Float.valueOf(i)/100;
                txtSensibilidad.setText("Sensibilidad: "+sensibilidad);
                sensibilidadObserve.setValue(sensibilidad);
                Log.d("cambiospeed2",sensibilidad+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        verificarPermisos();
    }

    public void permisoAudio(){
        // Verifica si el permiso ya ha sido concedido
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // Si no se ha concedido, solicita el permiso
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }


    // Método para manejar la respuesta de la solicitud de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, puedes iniciar la funcionalidad

            } else {
                // Permiso denegado, muestra un mensaje o maneja el caso
                Toast.makeText(this, "Permiso de micrófono denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void verificarPermisos() {
        //aparecer encima
        if (Settings.canDrawOverlays(this)) {
            isAparecerEncima = true;
            btnAparecerEncima.setText("Activo");
            btnAparecerEncima.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.green));
        }else{
            isAparecerEncima = false;
            btnAparecerEncima.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.red));
            btnAparecerEncima.setText("Desactivo");
        }

        //accesibilidad
        if (isAccessibilityServiceEnabled(this,MouseAccessibilityService.class)){
            btnAccesibilidad.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.green));
            btnAccesibilidad.setText("Activo");
            isAccesibilidad = true;
            btnStartService.setText("Detener servicio");
            isStartService = true;
        }else{
            btnAccesibilidad.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.red));
            btnAccesibilidad.setText("Desactivo");
            isAccesibilidad = false;
            btnStartService.setText("Iniciar Servicio");
            isStartService = false;
            btnInitCamara.setText("Activar Asistencia Motriz");
        }
        //camara
        if (isPermisoCamara()){
            isCamara = true;
            btnCamara.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.green));
            btnCamara.setText("Activo");
        }else{
            isCamara = false;
            btnCamara.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.red));
            btnCamara.setText("Desactivo");
        }
    }

    /**
     * Permiso de camara verificacion
     * @return
     */
    private boolean isPermisoCamara() {
        if (ContextCompat.checkSelfPermission(this, REQUIRED_PERMISSIONS[0]) != PackageManager.PERMISSION_GRANTED) {
                // El permiso no está activo
                return false;
        }

        // Todos los permisos están activos
        return true;
    }




    /**
     * Start service camera
     */
    private void startService() {
        startService(intent);
    }



    /**Permisos**/
    @Override
    protected void onPause() {
        super.onPause();
        //startCamera();

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

    }


    @Override
    public void onClick(View view) {
        if (view == btnStartService){
            if (isStartService){
                //permisoAudio();
                //start service
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    stopService(new Intent(this,MouseAccessibilityService.class));
                    isStartService = false;
                    btnStartService.setText("Iniciar Servicio");
                    //endServiceObserver.setValue(true);
                }else{
                    stopService(new Intent(this,MouseAccessibilityService.class));
                    isStartService = false;
                    btnStartService.setText("Iniciar Servicio");
                }
            }else{
                //endServiceObserver.setValue(true);
                btnStartService.setText("Detener servicio");
                isStartService = true;
                startService();
                Boolean isEnabledService = AccessibilityServiceUtil.isAccessibilityServiceEnabled(this,MouseAccessibilityService.class);
                if (!isEnabledService){
                    SweetAlertDialog swtWarning = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
                    swtWarning.setTitleText("Atención");
                    swtWarning.setContentText("Para activar todas las funcionalidades ve a accesibilidad, Busca Servicios Instalados " +
                            "y busca CompañIA Visual");
                    swtWarning.showCancelButton(true);
                    swtWarning.showCancelButton(true);
                    swtWarning.setConfirmText("Activar");
                    swtWarning.setConfirmClickListener(sweetAlertDialog -> openAccessibilitySettings());
                    swtWarning.setCancelClickListener(sweetAlertDialog -> sweetAlertDialog.dismiss());
                    swtWarning.setCancelText("Ahora no");
                    swtWarning.show();
                    swtWarning.setCancelable(false);
                }
            }
        }
        //Camara activar
        if (view == btnCamara & !isCamara){
            //permiso de camara
            requestPermissions(
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS);
        }
        //Aparecer encima
        if (view == btnAparecerEncima && !isAparecerEncima){
            //permiso aparecer encima
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, SYSTEM_ALERT_WINDOW_PERMISSION_CODE);}
            else {
                //servicio activo
                btnAparecerEncima.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.green));
                btnAparecerEncima.setText("Activo");
                startService();
                isStartService = true;
            }
        }
        //accesibilidad permiso
        if (view == btnAccesibilidad && !isAccesibilidad){
            //openAccessibilitySettings();
        }

        //Inicial camara
        if (btnInitCamara == view){

            if (!btnStartService.getText().toString().equals("Iniciar Servicio")){
                if (!isCamaraActive){
                    isCamaraActive = true;
                    Intent intentReceiver = new Intent("com.ingenieriajhr.visualcontroll");
                    intentReceiver.putExtra("accion","iniciar_camara");
                    sendBroadcast(intentReceiver);
                    btnInitCamara.setText("Activar Asistencia Motriz");
                }else{
                    isCamaraActive = false;
                    Intent intentReceiver = new Intent("com.ingenieriajhr.visualcontroll");
                    intentReceiver.putExtra("accion","detener_camara");
                    sendBroadcast(intentReceiver);
                    btnInitCamara.setText("Activar Asistencia Motriz");
                }
            }else{
                //anima btnStartService animation scale 0.8 return 1f animation
                btnStartService.animate().scaleX(0.8f).scaleY(0.8f).setDuration(400).withEndAction(() -> {
                        btnStartService.animate().scaleX(1f).scaleY(1f).setDuration(400);
                    });
                Toast.makeText(this,"Active primer el servicio por favor",Toast.LENGTH_SHORT).show();
        }
    }

        //Brain Intelligent curioso
        if (btnActiveBrainIntelligent == view){
            if (!isBrainIntelligent){
                isBrainIntelligent = true;
                Intent intentReceiver = new Intent("com.ingenieriajhr.visualcontroll");
                intentReceiver.putExtra("activar","activar_brain");
                sendBroadcast(intentReceiver);
            }else{
                Intent intentReceiver = new Intent("com.ingenieriajhr.visualcontroll");
                intentReceiver.putExtra("activar","desactivar_brain");
                sendBroadcast(intentReceiver);
                isBrainIntelligent = false;
            }
        }

        if (btnActivarPolicia == view){

            if (!isPolicia){
                isPolicia = true;
                Intent intentReceiver = new Intent("com.ingenieriajhr.visualcontroll");
                intentReceiver.putExtra("activar","activar_policia");
                sendBroadcast(intentReceiver);
            }else{
                isPolicia = false;
                Intent intentReceiver = new Intent("com.ingenieriajhr.visualcontroll");
                intentReceiver.putExtra("activar","desactivar_policia");
                sendBroadcast(intentReceiver);
            }

        }

        if (btnChatBot == view){
            if (!isChatBot){
                isChatBot = true;
                Intent intentReceiver = new Intent("com.ingenieriajhr.visualcontroll");
                intentReceiver.putExtra("activar","activar_chatbot");
                sendBroadcast(intentReceiver);
            }else{
                isChatBot = false;
                Intent intentReceiver = new Intent("com.ingenieriajhr.visualcontroll");
                intentReceiver.putExtra("activar","desactivar_chatbot");
                sendBroadcast(intentReceiver);
            }
        }

        if (btnCiberPaz == view){
            if (!isCiberPaz){
                isCiberPaz = true;
                Intent intentReceiver = new Intent("com.ingenieriajhr.visualcontroll");
                intentReceiver.putExtra("activar","activar_ciberpaz");
                sendBroadcast(intentReceiver);
            }else{
                isCiberPaz = false;
                Intent intentReceiver = new Intent("com.ingenieriajhr.visualcontroll");
                intentReceiver.putExtra("activar","desactivar_ciberpaz");
                sendBroadcast(intentReceiver);
            }
        }

        if (btnHelp == view){
            SweetAlertDialog swtHelp = new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE);
            swtHelp.setTitleText("Funcionalidades");
            swtHelp.setContentText("* Parpadea dos veces para abrir un elemento que apunte el Mouse      * Abre la boca para volver al menu prinicipal      *Muevete con el scroll de la derecha acercando el Mouse");
            swtHelp.showContentText(true);
            swtHelp.showCancelButton(true);
            swtHelp.show();
        }


}




    //result permission
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SYSTEM_ALERT_WINDOW_PERMISSION_CODE) {
            // Verifica si el usuario concedió el permiso
            if (Settings.canDrawOverlays(this)) {
                // El permiso ha sido concedido, inicia tu servicio de AccessibilityService aquí
                Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show();
                //startService(new Intent(this, MouseAccessibilityService.class));
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setComponent(new ComponentName("com.ingenieriajhr.visualcontroll","com.ingenieriajhr.visualcontroll.MainActivity"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                // Obtiene el color del recurso definido en colors.xml
                btnAparecerEncima.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.green));
            } else {
                // El usuario no concedió el permiso, maneja esta situación según sea necesario
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show();
            }
        }else{
            if (requestCode == REQUEST_CODE_PERMISSIONS){
                btnAparecerEncima.setBackgroundTintList(ContextCompat.getColorStateList(this,R.color.green));
            }
        }
    }

    public  boolean isAccessibilityServiceEnabled(Context context, Class<?> accessibilityService) {
      return   isServiceRunning(accessibilityService);
//        ComponentName expectedComponentName = new ComponentName(context, accessibilityService);
//
//        String enabledServicesSetting = Settings.Secure.getString(context.getContentResolver(),  Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
//        if (enabledServicesSetting == null)
//            return false;
//
//        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter(':');
//        colonSplitter.setString(enabledServicesSetting);
//
//        while (colonSplitter.hasNext()) {
//            String componentNameString = colonSplitter.next();
//            ComponentName enabledService = ComponentName.unflattenFromString(componentNameString);
//
//            if (enabledService != null && enabledService.equals(expectedComponentName))
//                return true;
//        }
//
//        return false;
    }

    // Método para verificar si el servicio está en ejecución
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;  // El servicio está en ejecución
            }
        }
        return false;  // El servicio no está en ejecución
    }


    /**
     * Open accesibilidad configuracion
     */
    private void openAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
