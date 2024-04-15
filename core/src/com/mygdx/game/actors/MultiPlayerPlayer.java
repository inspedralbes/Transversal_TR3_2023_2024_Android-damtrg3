package com.mygdx.game.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.screens.MenuSalasScreen;

import org.json.JSONObject;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.socket.client.Socket;

public class MultiPlayerPlayer extends Player{
    private String user;
    private boolean isCurrentUser;
    private Socket socket;
    private int skin;

    private TextureRegion cat_idle_bottom;
    private TextureRegion cat_idle_right;
    private TextureRegion cat_idle_top;
    private TextureRegion cat_idle_left;
    private TextureRegion[] cat_walk_bottom = new TextureRegion[8];
    private TextureRegion[] cat_walk_right = new TextureRegion[8];
    private TextureRegion[] cat_walk_top = new TextureRegion[8];
    private TextureRegion[] cat_walk_left = new TextureRegion[8];
    private Animation<TextureRegion> cat_walk_bottom_animation;
    private Animation<TextureRegion> cat_walk_right_animation;
    private Animation<TextureRegion> cat_walk_top_animation;
    private Animation<TextureRegion> cat_walk_left_animation;

    public MultiPlayerPlayer(String user, int skin){
        super();
        this.user = user;
        isCurrentUser = false;
        socket = MenuSalasScreen.socket;
        this.skin = skin;
    }

    public void updatePosition(float rotation, String salaId){
        if(isCurrentUser && super.logHitCooldown >= 1){
            super.updatePosition(rotation);

            JSONObject data = new JSONObject();
            data.put("player", user);
            data.put("salaId", salaId);
            data.put("x", super.getPosition().x);
            data.put("y", super.getPosition().y);
            data.put("rotation", rotation);
            socket.emit("hitByLog", data.toString());

            super.logHitCooldown = 0;
        }
    }

    public void updatePosition(Vector2 direction, String salaId){
        if(isCurrentUser && !isInvulnerable){
            super.updatePosition(direction);

            JSONObject data = new JSONObject();
            data.put("player", user);
            data.put("salaId", salaId);
            data.put("x", super.getPosition().x);
            data.put("y", super.getPosition().y);
            data.put("directionX", direction.x);
            data.put("directionY", direction.y);
            socket.emit("hitByPlayer", data.toString());
        }
    }

    @Override
    public void loadSkin(Batch batch){
        cargarTexturasPersonaje();

        TextureRegion texture;

        Vector2 direction = super.getDirection();

        if (direction.x > 0) {
            texture = (direction.x == 0 && direction.y == 0) ? cat_idle_right : cat_walk_right_animation.getKeyFrame(stateTime, true);
        } else if (direction.x < 0) {
            texture = (direction.x == 0 && direction.y == 0) ? cat_idle_left : cat_walk_left_animation.getKeyFrame(stateTime, true);
        } else if (direction.y > 0) {
            texture = (direction.x == 0 && direction.y == 0) ? cat_idle_top : cat_walk_top_animation.getKeyFrame(stateTime, true);
        } else {
            texture = (direction.x == 0 && direction.y == 0) ? cat_idle_bottom : cat_walk_bottom_animation.getKeyFrame(stateTime, true);
        }

        batch.draw(texture, super.position.x, super.position.y, super.width, super.height);
    }

    private void cargarTexturasPersonaje() {
        Texture cat_spritesheet;
        switch (this.skin) {
            case 1:
                cat_spritesheet = new Texture(Gdx.files.internal("characters/Sprite1.png"));
                break;
            case 2:
                cat_spritesheet = new Texture(Gdx.files.internal("characters/Sprite2.png"));
                break;
            case 3:
                cat_spritesheet = new Texture(Gdx.files.internal("characters/Sprite3.png"));
                break;
            case 4:
                cat_spritesheet = new Texture(Gdx.files.internal("characters/Sprite4.png"));
                break;
            case 5:
                cat_spritesheet = new Texture(Gdx.files.internal("characters/Sprite5.png"));
                break;
            case 6:
                cat_spritesheet = new Texture(Gdx.files.internal("characters/Sprite6.png"));
                break;
            default:
                cat_spritesheet = new Texture(Gdx.files.internal("/Sprite2.png"));
                break;
        }

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

    public String getUser(){
        return user;
    }

    public boolean isCurrentUser(){
        return isCurrentUser;
    }

    public void setIsCurrentUser(boolean isCurrentUser){
        this.isCurrentUser = isCurrentUser;
    }
}
