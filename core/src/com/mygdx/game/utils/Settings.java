package com.mygdx.game.utils;

import com.badlogic.gdx.math.Vector2;

public class Settings {
    public static int SCREEN_WIDTH = 1280;
    public static int SCREEN_HEIGHT = 720;

    //Player Settings
    public final static int PLAYER_WIDTH = 64;
    public final static int PLAYER_HEIGHT = 64;
    public  static int PLAYER_SPEED = 100;
    public  static float PLAYER_DAMAGE_RECIEVED = 1;
    public final static Vector2 PLAYER_START = new Vector2(SCREEN_WIDTH/2 - PLAYER_WIDTH/2 + 100, SCREEN_HEIGHT/2 - PLAYER_HEIGHT/2 - 100);

    //SpinLog Settings
    public  static float SPINLOG_ACCEL = 0.1f;

    //INFO IPS

    public static final String IP_SERVER = "localhost";

    public static final String PUERTO_PETICIONES = "3327";

    public static void setWidth(int width){
        SCREEN_WIDTH = width;
    }

    public static void setHeight(int height){
        SCREEN_HEIGHT = height;
    }
}
