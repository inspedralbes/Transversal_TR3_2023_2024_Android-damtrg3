package com.mygdx.game.actors;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.utils.Settings;

public class PlayerSlash extends Actor {
    private Vector2 position;
    private Rectangle collisionRect;
    private float lifeTime;
    private Player player;
    private float stateTime;
    private int rotation;
    private Vector2 direction;

    public PlayerSlash(Player player){
        this.player = player;
        direction = player.getDirection();
        if(direction.x > 0) {
            //RIGHT
            position = new Vector2(player.getPosition().x + player.getWidth(), player.getPosition().y + player.getHeight() / 2);
        } else if(direction.x < 0) {
            //LEFT
            position = new Vector2(player.getPosition().x - player.getWidth(), player.getPosition().y + player.getHeight() / 2);
        } else if(direction.y > 0) {
            //UP
            position = new Vector2(player.getPosition().x + player.getWidth() / 2, player.getPosition().y + player.getHeight());
        } else {
            //DOWN AND IDLE
            position = new Vector2(player.getPosition().x + player.getWidth() / 2, player.getPosition().y - player.getHeight());
        }
        collisionRect = new Rectangle(position.x, position.y, 32, 32);
        lifeTime = AssetManager.slash_animation.getAnimationDuration();
        stateTime = 0;
    }

    @Override
    public void act(float delta){
        super.act(delta);
        lifeTime -= delta;
        if(lifeTime <= 0){
            this.remove();
        }

        if(direction.x > 0) {
            //RIGHT
            collisionRect.x = player.getPosition().x + player.getWidth() + 35;
            collisionRect.y = player.getPosition().y + player.getHeight() / 2;
        } else if(direction.x < 0) {
            //LEFT
            collisionRect.x = player.getPosition().x - player.getWidth() - 5;
            collisionRect.y = player.getPosition().y + player.getHeight() / 2;
        } else if(direction.y > 0) {
            //UP
            collisionRect.x = player.getPosition().x + player.getWidth() / 2 + 15;
            collisionRect.y = player.getPosition().y + player.getHeight() + 25;
        } else {
            //DOWN AND IDLE
            collisionRect.x = player.getPosition().x + player.getWidth() / 2 + 15;
            collisionRect.y = player.getPosition().y - player.getHeight() - 15;
        }
        collisionRect.setPosition(collisionRect.x, collisionRect.y);

        stateTime += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha){
        super.draw(batch, parentAlpha);
        if(direction.x > 0)
            rotation = 90;
        else if(direction.x < 0)
            rotation = 270;
        else if(direction.y > 0)
            rotation = 180;
        else{
            rotation = 0;
        }
        batch.draw(AssetManager.slash_animation.getKeyFrame(lifeTime), collisionRect.x, collisionRect.y, 0, 0, collisionRect.width * 2, collisionRect.height * 2, 1, 1, rotation);
    }

    public Rectangle getCollisionRect(){
        return collisionRect;
    }

    public boolean collides(Player player){
        return collisionRect.overlaps(player.getCollisionRect());
    }

    public Vector2 getDirection(){
        return direction;
    }

    public Player getPlayer(){
        return player;
    }
}
