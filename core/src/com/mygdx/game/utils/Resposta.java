package com.mygdx.game.utils;

public class Resposta {

    private boolean autoritzacio;

    public Resposta(boolean autoritzacio) {
        this.autoritzacio = autoritzacio;
    }

    public boolean isAutoritzacio() {
        return autoritzacio;
    }

    public void setAutoritzacio(boolean autoritzacio) {
        this.autoritzacio = autoritzacio;
    }
    @Override
    public String toString() {
        return "Resposta{" +
                "autoritzacio=" + autoritzacio +
                '}';
    }
}
