package com.mygdx.game.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetManager {
    public static TiledMap tiledMap;

    public static Skin neon_skin;

    public static Skin lava_skin;

    public static Skin lava2_skin;
    public static Texture menu_bg;

    public static Texture menu_bg2;
    public static TextureRegion white;

    public static void load() {
        menu_bg = new Texture(Gdx.files.internal("menu_bg.jpg"));

        menu_bg2 = new Texture(Gdx.files.internal("bgNou.jpg"));

        tiledMap = new TmxMapLoader().load("maps/map.tmx");

        neon_skin = new Skin(Gdx.files.internal("skins/neon/neon-ui.json"));
        lava_skin = new Skin(Gdx.files.internal("skins/lava/tracer-ui.json"));
        white = neon_skin.getRegion("white");

    }

    public static void dispose() {
        tiledMap.dispose();
        neon_skin.dispose();
    }
}
