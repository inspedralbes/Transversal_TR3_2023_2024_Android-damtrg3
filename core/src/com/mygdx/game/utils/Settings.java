package com.mygdx.game.utils;

import com.badlogic.gdx.math.Vector2;

public class Settings {
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    //Player Settings
    public final static int PLAYER_WIDTH = 64;
    public final static int PLAYER_HEIGHT = 64;
    public final static int PLAYER_SPEED = 100;
    public final static float PLAYER_DAMAGE_RECIEVED = 1;
    public final static Vector2 PLAYER_START = new Vector2(SCREEN_WIDTH/2 - PLAYER_WIDTH/2 + 100, SCREEN_HEIGHT/2 - PLAYER_HEIGHT/2 - 100);

    //SpinLog Settings
    public final static float SPINLOG_ACCEL = 0.1f;

    //INFO IPS

    public static final String IP_SERVER = "10.0.2.2";

    public static final String PUERTO_PETICIONES = "3327";

    public static void setWidth(int width){
        SCREEN_WIDTH = width;
    }

    public static void setHeight(int height){
        SCREEN_HEIGHT = height;
    }
}
