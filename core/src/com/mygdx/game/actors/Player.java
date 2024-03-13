package com.mygdx.game.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.utils.Settings;

public class Player extends Actor {
    private Vector2 position;
    private int width, height;
    private Vector2 direction;
    private float stateTime;

    public Player() {
        position = Settings.PLAYER_START;
        width = Settings.PLAYER_WIDTH;
        height = Settings.PLAYER_HEIGHT;
        direction = new Vector2(0, 0);
        stateTime = 0;
    }

    public void act(float delta){
        this.position.x += direction.x * Settings.PLAYER_SPEED * delta;
        this.position.y += direction.y * Settings.PLAYER_SPEED * delta;
        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        // Choose the texture or animation based on the direction
        TextureRegion texture;
        if (direction.x > 0) {
            texture = (direction.x == 0 && direction.y == 0) ? AssetManager.cat_idle_right : AssetManager.cat_walk_right_animation.getKeyFrame(stateTime, true);
        } else if (direction.x < 0) {
            texture = (direction.x == 0 && direction.y == 0) ? AssetManager.cat_idle_left : AssetManager.cat_walk_left_animation.getKeyFrame(stateTime, true);
        } else if (direction.y > 0) {
            texture = (direction.x == 0 && direction.y == 0) ? AssetManager.cat_idle_top : AssetManager.cat_walk_top_animation.getKeyFrame(stateTime, true);
        } else {
            texture = (direction.x == 0 && direction.y == 0) ? AssetManager.cat_idle_bottom : AssetManager.cat_walk_bottom_animation.getKeyFrame(stateTime, true);
        }

        batch.draw(texture, position.x, position.y, width, height);
    }

    public Vector2 getDirection() {
        return direction;
    }
}
