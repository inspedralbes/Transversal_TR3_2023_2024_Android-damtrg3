package com.mygdx.game.utils;

import com.google.gson.annotations.SerializedName;

public class UsuariLocalitzat {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("nomUsuari")
    private String nomUsuari;

    @SerializedName("correu")
    private String correu;

    @SerializedName("contrasenya")
    private String contrasenya;

    public UsuariLocalitzat(String nomUsuari, String correu, String contrasenya) {
        this.nomUsuari = nomUsuari;
        this.correu = correu;
        this.contrasenya = contrasenya;
    }
    public UsuariLocalitzat(String nomUsuari, String contrasenya) {
        this.nomUsuari = nomUsuari;
        this.contrasenya = contrasenya;
    }
}
