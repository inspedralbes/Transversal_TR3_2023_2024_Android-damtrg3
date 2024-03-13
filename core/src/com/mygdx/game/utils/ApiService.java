package com.mygdx.game.utils;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("registrarUsuari")
    Call<Resposta> EnviarUsuario(@Body UsuariLocalitzat usuariTrobat);

    @POST("usuarisLogin")
    Call<Resposta> EnviarUsuari(@Body UsuariLocalitzat usuariTrobat);
}
