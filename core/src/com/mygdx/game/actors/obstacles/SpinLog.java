package com.mygdx.game.actors.obstacles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.actors.Player;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.utils.Settings;

public class SpinLog extends Actor {
    private Vector2 position;
    private float speed;
    private float rotation;
    private TextureRegion logTexture;
    private Vector2 shadowOffset;
    private Color shadowColor;
    private Polygon collisionPolygon;
    private float acceleration;
    public SpinLog(){
        position = new Vector2(0, 0);
        speed = 1.0f;
        rotation = 0.0f;
        logTexture = AssetManager.spinLog;
        shadowOffset = new Vector2(5, -5);
        shadowColor = new Color(0, 0, 0, 0.5f);
        collisionPolygon = new Polygon(new float[]{0, 0, logTexture.getRegionWidth(), 0, logTexture.getRegionWidth(), logTexture.getRegionHeight(), 0, logTexture.getRegionHeight()});
        collisionPolygon.setOrigin(logTexture.getRegionWidth() / 2, logTexture.getRegionHeight() / 2);
        acceleration = 0.1f;
    }

    @Override
    public void act(float delta){
        super.act(delta);

        //Rotation
        rotation += speed;
        if(rotation > 360){
            rotation -= 360;
        }

        //Position
        position.x = Settings.SCREEN_WIDTH / 2 + (float)Math.cos(Math.toRadians(rotation)) * 100;
        position.y = Settings.SCREEN_HEIGHT / 2 + (float)Math.sin(Math.toRadians(rotation)) * 100;

        collisionPolygon.setPosition(position.x - logTexture.getRegionWidth() / 2, position.y - logTexture.getRegionHeight() / 2);
        collisionPolygon.setRotation(rotation);

        speed += acceleration * delta;
    }

    public void draw(Batch batch, float parentAlpha){
        super.draw(batch, parentAlpha);

        float originX = logTexture.getRegionWidth() / 2;
        float originY = logTexture.getRegionHeight() / 2;

        batch.setColor(shadowColor);
        batch.draw(logTexture, position.x - originX + shadowOffset.x, position.y - originY + shadowOffset.y, originX, originY, logTexture.getRegionWidth(), logTexture.getRegionHeight(), 1, 1, rotation);

        batch.setColor(Color.WHITE);
        batch.draw(logTexture, position.x - originX, position.y - originY, originX, originY, logTexture.getRegionWidth(), logTexture.getRegionHeight(), 1, 1, rotation);
    }

    public Vector2 getPosition(){
        return position;
    }

    public Polygon getCollisionPolygon(){
        return collisionPolygon;
    }

    public boolean collides(Player player){
        Rectangle playerRect = player.getCollisionRect();
        Polygon playerPolygon = new Polygon(new float[]{0, 0, playerRect.width, 0, playerRect.width, playerRect.height, 0, playerRect.height});
        playerPolygon.setPosition(playerRect.x, playerRect.y);

        return Intersector.overlapConvexPolygons(collisionPolygon, playerPolygon);
    }

    public float getRotation(){
        return rotation;
    }
}
