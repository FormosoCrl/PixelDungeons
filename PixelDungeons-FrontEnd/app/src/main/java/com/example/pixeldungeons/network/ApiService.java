package com.example.pixeldungeons.network;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    @POST("api/registro/")
    Call<Map<String, Object>> registro(@Body Map<String, Object> body);

    @POST("api/login/")
    Call<Map<String, Object>> login(@Body Map<String, Object> body);

    @GET("api/salas/")
    Call<Map<String, Object>> buscarSala(@Query("codigo") String codigo);

    @GET("api/salas/")
    Call<List<Map<String, Object>>> getSalas();

    @POST("api/salas/")
    Call<Map<String, Object>> crearSala(@Body Map<String, Object> body);

    @DELETE("api/salas/{sala_id}/")
    Call<Map<String, Object>> eliminarSala(@Path("sala_id") int salaId);

    @GET("api/salas/{sala_id}/heroes/")
    Call<List<Map<String, Object>>> getHeroesDeSala(@Path("sala_id") int salaId);

    @POST("api/heroes/")
    Call<Map<String, Object>> crearHero(@Body Map<String, Object> body);

    @GET("api/heroes/{hero_id}/")
    Call<Map<String, Object>> getHero(@Path("hero_id") int heroId);

    @PUT("api/heroes/{hero_id}/")
    Call<Map<String, Object>> actualizarHero(@Path("hero_id") int heroId, @Body Map<String, Object> body);

    @DELETE("api/heroes/{hero_id}/")
    Call<Map<String, Object>> eliminarHero(@Path("hero_id") int heroId);

    @GET("api/heroes/{hero_id}/inventario/")
    Call<List<Map<String, Object>>> getInventario(@Path("hero_id") int heroId);

    @POST("api/heroes/{hero_id}/inventario/")
    Call<Map<String, Object>> darItem(@Path("hero_id") int heroId, @Body Map<String, Object> body);

    @PUT("api/heroes/{hero_id}/inventario/{item_id}/")
    Call<Map<String, Object>> equiparItem(@Path("hero_id") int heroId, @Path("item_id") int itemId, @Body Map<String, Object> body);

    @DELETE("api/heroes/{hero_id}/inventario/{item_id}/")
    Call<Map<String, Object>> quitarItem(@Path("hero_id") int heroId, @Path("item_id") int itemId);

    @GET("api/items/")
    Call<List<Map<String, Object>>> getItems(@Query("sala_id") int salaId);

@POST("api/items/")
    Call<Map<String, Object>> crearItem(@Body Map<String, Object> body);

    @DELETE("api/items/{item_id}/")
    Call<Map<String, Object>> eliminarItem(@Path("item_id") int itemId);

    @GET("api/salas/{sala_id}/mapas/")
    Call<List<Map<String, Object>>> getMapas(@Path("sala_id") int salaId);

    @GET("api/mapas/{mapa_id}/")
    Call<Map<String, Object>> getMapaDetalle(@Path("mapa_id") int mapaId);

    @POST("api/salas/{sala_id}/mapas/")
    Call<Map<String, Object>> crearMapa(@Path("sala_id") int salaId, @Body Map<String, Object> body);

    @PUT("api/mapas/{mapa_id}/")
    Call<Map<String, Object>> actualizarMapa(@Path("mapa_id") int mapaId, @Body Map<String, Object> body);

    @DELETE("api/mapas/{mapa_id}/")
    Call<Map<String, Object>> eliminarMapa(@Path("mapa_id") int mapaId);
}
