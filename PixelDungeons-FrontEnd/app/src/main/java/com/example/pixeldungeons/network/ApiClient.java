package com.example.pixeldungeons.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // Para emulador: http://10.0.2.2:8000/   Para móvil físico: http://192.168.1.65:8000/
    private static final String BASE_URL = "http://10.0.2.2:8000/";
    private static ApiService service;

    public static ApiService getService() {
        if (service == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            service = retrofit.create(ApiService.class);
        }
        return service;
    }
}
