package com.ingenieriajhr.visualcontroll.accessibility.internet;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.ingenieriajhr.visualcontroll.accessibility.utils.interace.ResponseApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.security.auth.callback.Callback;

public class Request {


    private Context mContext;
    public Request(Context context){
        this.mContext = context;
    }

  

    // URL de la API de OpenAI para el modelo de chat
    private static final String URL = "https://api.openai.com/v1/chat/completions";


    public void sendRequestApi(String mensaje, ResponseApi responseApi) {
        // Crear un objeto JSON con los datos que se enviarán
        JSONObject data = new JSONObject();
        try {
            // Datos para la solicitud JSON
            data.put("model", "gpt-3.5-turbo");
            JSONArray messages = new JSONArray();

            // Mensajes que se enviarán
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "compórtate como un policía que detecta si un texto tiene fraudes");
            messages.put(systemMessage);

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", "Comporta como un experto en ciberseguridad. Evalúa el siguiente texto y responde lo siguiente:\n" +
                    "\n" +
                    "en primer lugar Clasifica el riesgo como Alerta de fraude alta o Alerta de fraude baja.\n" +
                    "Explica brevemente el riesgo y cómo funciona el fraude.\n" +
                    "Da recomendaciones claras para evitar caer en el fraude.\n" +
                    "respuesta corta y precisa\n" +
                    "Texto para analizar: "+mensaje);
            messages.put(userMessage);

            data.put("messages", messages);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Configurar los encabezados de la solicitud
        final JSONObject finalData = data;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                com.android.volley.Request.Method.POST,
                URL,
                finalData,
                response -> {
                    try {
                        // Obtener la respuesta de la API
                        String messageContent = response.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

                        responseApi.rxOpenaAi(true,messageContent);
                        // Imprimir la respuesta o mostrarla
                        Log.d("Response", messageContent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        responseApi.rxOpenaAi(false,"error en los datos");
                    }
                },
                error -> {
                    // Manejar el error de la solicitud
                    Log.e("Volley Error", error.toString());
                    responseApi.rxOpenaAi(false,error.toString());
                }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + API_KEY);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(2000,1,1f));
        MySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);

        // Agregar la solicitud a la cola
        //requestQueue.add(jsonObjectRequest);
    }

    public void sendQuestion(String mensaje, ResponseApi responseApi) {
        // Crear un objeto JSON con los datos que se enviarán
        JSONObject data = new JSONObject();
        try {
            // Datos para la solicitud JSON
            data.put("model", "gpt-3.5-turbo");
            JSONArray messages = new JSONArray();

            // Mensajes que se enviarán
            JSONObject systemMessage = new JSONObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", "Comportate como gran conocedor de la ciencia y tecnología, " +
                    "ademas conoces de ciberpaz colombia iniciativas o programas orientados a promover la paz y la seguridad " +
                    "en Colombia mediante el uso de la tecnología, respuestas no tan largas y orienta a las personas a ayudar al planeta en caso de tener preguntas tipo medio ambiente,");
            messages.put(systemMessage);

            JSONObject userMessage = new JSONObject();
            userMessage.put("role", "user");
            userMessage.put("content", mensaje);
            messages.put(userMessage);

            data.put("messages", messages);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Configurar los encabezados de la solicitud
        final JSONObject finalData = data;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                com.android.volley.Request.Method.POST,
                URL,
                finalData,
                response -> {
                    try {
                        // Obtener la respuesta de la API
                        String messageContent = response.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");

                        responseApi.rxOpenaAi(true,messageContent);
                        // Imprimir la respuesta o mostrarla
                        Log.d("Response", messageContent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        responseApi.rxOpenaAi(false,"error en los datos");
                    }
                },
                error -> {
                    // Manejar el error de la solicitud
                    Log.e("Volley Error", error.toString());
                    responseApi.rxOpenaAi(false,error.toString());
                }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("Authorization", "Bearer " + API_KEY);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(2000,1,1f));
        MySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);

        // Agregar la solicitud a la cola
        //requestQueue.add(jsonObjectRequest);
    }



}
