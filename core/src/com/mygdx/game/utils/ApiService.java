package com.mygdx.game.utils;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("registrarUsuari")
    Call<Resposta> EnviarUsuario(@Body UsuariLocalitzat usuariTrobat);

    @POST("usuarisLogin")
    Call<Resposta> EnviarUsuari(@Body UsuariLocalitzat usuariTrobat);
    @GET("/api/products")
    Call<List<Product>> getProducts();
    @POST("/api/buy")
    Call<Void> buyProduct(@Body Product product);
    @GET("/api/userProducts")
    Call<List<Integer>> getUserProducts(@Query("username") String username);
    @GET("/api/getUserMoney")
    Call<Double> getUserMoney(@Query("username") String username);
}