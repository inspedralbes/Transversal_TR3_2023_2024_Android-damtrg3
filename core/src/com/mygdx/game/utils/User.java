package com.mygdx.game.utils;

public class User {
    private String nomCognoms;
    private String correu;
    private String contrasenya;

public User(String nomCognoms, String correu, String contrasenya) {
        this.nomCognoms = nomCognoms;
        this.correu = correu;
        this.contrasenya = contrasenya;
    }

    public String getNomCognoms() {
        return nomCognoms;
    }

    public void setNomCognoms(String nomCognoms) {
        this.nomCognoms = nomCognoms;
    }

    public String getCorreu() {
        return correu;
    }

    public void setCorreu(String correu) {
        this.correu = correu;
    }

    public String getContrasenya() {
        return contrasenya;
    }

    public void setContrasenya(String contrasenya) {
        this.contrasenya = contrasenya;
    }
}
