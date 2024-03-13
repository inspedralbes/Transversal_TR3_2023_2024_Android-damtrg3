package com.mygdx.game.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetManager {
    public static TiledMap tiledMap;

    public static Skin neon_skin;
    public static Texture menu_bg;

    //Player sprites
    private static Texture cat_spritesheet;
    public static TextureRegion cat_idle_bottom;
    public static TextureRegion cat_idle_right;
    public static TextureRegion cat_idle_top;
    public static TextureRegion cat_idle_left;
    public static TextureRegion[] cat_walk_bottom = new TextureRegion[8];
    public static TextureRegion[] cat_walk_right = new TextureRegion[8];
    public static TextureRegion[] cat_walk_top = new TextureRegion[8];
    public static TextureRegion[] cat_walk_left = new TextureRegion[8];
    public static Animation<TextureRegion> cat_walk_bottom_animation;
    public static Animation<TextureRegion> cat_walk_right_animation;
    public static Animation<TextureRegion> cat_walk_top_animation;
    public static Animation<TextureRegion> cat_walk_left_animation;

    public static void load() {
        menu_bg = new Texture(Gdx.files.internal("menu_bg.jpg"));

        tiledMap = new TmxMapLoader().load("maps/map.tmx");

        neon_skin = new Skin(Gdx.files.internal("skins/neon/neon-ui.json"));

        cat_spritesheet = new Texture(Gdx.files.internal("characters/cat_spritesheet.png"));
        cat_idle_bottom = new TextureRegion(cat_spritesheet, 0, 0, 32, 32);
        cat_idle_right = new TextureRegion(cat_spritesheet, 32, 0, 32, 32);
        cat_idle_top = new TextureRegion(cat_spritesheet, 64, 0, 32, 32);
        cat_idle_left = new TextureRegion(cat_spritesheet, 96, 0, 32, 32);

        for (int i = 0; i < 4; i++) {
            cat_walk_bottom[i] = new TextureRegion(cat_spritesheet, 32 * i, 32, 32, 32);
            cat_walk_bottom[i + 4] = new TextureRegion(cat_spritesheet, 32 * i, 64, 32, 32);
            cat_walk_left[i] = new TextureRegion(cat_spritesheet, 32 * i, 96, 32, 32);
            cat_walk_left[i + 4] = new TextureRegion(cat_spritesheet, 32 * i, 128, 32, 32);
            cat_walk_right[i] = new TextureRegion(cat_spritesheet, 32 * i, 160, 32, 32);
            cat_walk_right[i + 4] = new TextureRegion(cat_spritesheet, 32 * i, 192, 32, 32);
            cat_walk_top[i] = new TextureRegion(cat_spritesheet, 32 * i, 224, 32, 32);
            cat_walk_top[i + 4] = new TextureRegion(cat_spritesheet, 32 * i, 256, 32, 32);
        }

        cat_walk_bottom_animation = new Animation<TextureRegion>(0.1f, cat_walk_bottom);
        cat_walk_bottom_animation.setPlayMode(Animation.PlayMode.LOOP);
        cat_walk_right_animation = new Animation<TextureRegion>(0.1f, cat_walk_right);
        cat_walk_right_animation.setPlayMode(Animation.PlayMode.LOOP);
        cat_walk_top_animation = new Animation<TextureRegion>(0.1f, cat_walk_top);
        cat_walk_top_animation.setPlayMode(Animation.PlayMode.LOOP);
        cat_walk_left_animation = new Animation<TextureRegion>(0.1f, cat_walk_left);
        cat_walk_left_animation.setPlayMode(Animation.PlayMode.LOOP);

    }

    public static void dispose() {
        tiledMap.dispose();
        neon_skin.dispose();
    }
}
