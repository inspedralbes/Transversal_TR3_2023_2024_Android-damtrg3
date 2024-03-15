package com.mygdx.game.utils;

import com.google.gson.annotations.SerializedName;

public class UsuariLocalitzat {
    @SerializedName("user_id")
    private int userId;

    @SerializedName("nomCognoms")
    private String nomCognoms;

    @SerializedName("correu")
    private String correu;

    @SerializedName("contrasenya")
    private String contrasenya;

    public UsuariLocalitzat(String nomCognoms, String correu, String contrasenya) {
        this.nomCognoms = nomCognoms;
        this.correu = correu;
        this.contrasenya = contrasenya;
    }
    public UsuariLocalitzat(String correu, String contrasenya) {
        this.correu = correu;
        this.contrasenya = contrasenya;
    }
}
