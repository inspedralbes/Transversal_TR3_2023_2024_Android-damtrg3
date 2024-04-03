package com.mygdx.game.utils;

import com.badlogic.gdx.math.Vector2;

public class Settings {
    public final static int SCREEN_WIDTH = 1280;
    public final static int SCREEN_HEIGHT = 720;

    //Player Settings
    public final static int PLAYER_WIDTH = 64;
    public final static int PLAYER_HEIGHT = 64;
    public final static int PLAYER_SPEED = 100;
    public final static Vector2 PLAYER_START = new Vector2(SCREEN_WIDTH/2 - PLAYER_WIDTH/2 + 100, SCREEN_HEIGHT/2 - PLAYER_HEIGHT/2 - 100);


    //INFO IPS

    public static final String IP_SERVER = "192.168.205.83";

    public static final String PUERTO_PETICIONES = "3327";
}
