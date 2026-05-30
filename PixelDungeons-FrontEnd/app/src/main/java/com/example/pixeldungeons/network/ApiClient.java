package com.example.pixeldungeons.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // Para emulador: http://10.0.2.2:8000/   Para móvil físico: http://192.168.1.65:8000/
    // Producción: http://161.97.73.46:8000/
    private static final String BASE_URL = "http://161.97.73.46:8000/";
    private static ApiService service;

    public static ApiService getService() {
        if (service == null) {
            // OkHttp configurado para evitar "unexpected end of stream"
            // que ocurre cuando gunicorn cierra conexiones keep-alive
            // antes de que OkHttp las reutilice.
            OkHttpClient client = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    // gunicorn cierra las conexiones keep-alive a los ~2s. Forzamos
                    // "Connection: close" para no reutilizar conexiones ya muertas,
                    // que provocaban "unexpected end of stream" en la primera
                    // petición tras el login (lista de salas vacía hasta recrear).
                    .addInterceptor(chain -> chain.proceed(
                            chain.request().newBuilder()
                                    .header("Connection", "close")
                                    .build()))
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(ApiService.class);
        }
        return service;
    }
}
