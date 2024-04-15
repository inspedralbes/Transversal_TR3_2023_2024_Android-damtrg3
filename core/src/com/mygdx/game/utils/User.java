package com.mygdx.game.utils;

public class User {
    private String nomUsuari;
    private String correu;
    private String contrasenya;

public User(String nomUsuari, String correu, String contrasenya) {
        this.nomUsuari = nomUsuari;
        this.correu = correu;
        this.contrasenya = contrasenya;
    }

    public String getnomUsuari() {
        return nomUsuari;
    }

    public void setnomUsuari(String nomUsuari) {
        this.nomUsuari = nomUsuari;
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
