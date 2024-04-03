package com.mygdx.game.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.screens.LoginScreen;
import com.mygdx.game.screens.MenuSalasScreen;
import com.mygdx.game.utils.Settings;

import io.socket.client.Socket;

public class Player extends Actor {
    private Vector2 position;
    private int width, height;
    private Vector2 direction;
    private float stateTime;
    private boolean jumping;
    private float jumpHeight;
    private float velocity;
    private float originalY;
    private ShapeRenderer shapeRenderer;
    private float peakShadowSize;
    private float jumpStartTime; // the time when the jump started
    private float jumpDuration = 1;
    private float jumpCooldown = 0;
    private float slashCooldown = 0;
    private Rectangle collisionRect;
    private Vector2 pushVelocity;
    private float damageTaken;
    private boolean isAlive;
    private float pushForce;
    private Vector2 previousPosition;
    private float score;
    private boolean isInvulnerable;
    private float invulnerabilityTime;
    protected float logHitCooldown;

    public Player() {
        position = Settings.PLAYER_START;
        width = Settings.PLAYER_WIDTH;
        height = Settings.PLAYER_HEIGHT;
        direction = new Vector2(0, 0);
        stateTime = 0;
        jumping = false;
        jumpHeight = 30;
        velocity = 0;
        originalY = position.y;
        peakShadowSize = 0;
        collisionRect = new Rectangle(position.x, position.y, width, height);
        pushVelocity = new Vector2(0, 0);
        damageTaken = 0;
        isAlive = true;
        previousPosition = position;
        score = 0;
        isInvulnerable = false;
        invulnerabilityTime = 0;
        logHitCooldown = 0;

        shapeRenderer = new ShapeRenderer();
    }

    public void act(float delta){
        this.position.x += direction.x * Settings.PLAYER_SPEED * delta;

        if (jumping) {
            float elapsedTime = stateTime - jumpStartTime;
            if (elapsedTime < jumpDuration) {
                float progress = elapsedTime / jumpDuration;
                this.position.y = originalY + (float)Math.sin(progress * Math.PI) * jumpHeight;
                originalY += direction.y * Settings.PLAYER_SPEED * delta;
            } else {
                jumping = false;
                jumpCooldown = 0;
                this.position.y = originalY;
            }
        } else {
            this.position.y += direction.y * Settings.PLAYER_SPEED * delta;
        }

        this.position.x += pushVelocity.x * delta;
        this.position.y += pushVelocity.y * delta;

        pushVelocity.scl(0.9f);

        collisionRect.set(position.x + 20, position.y + 10, width - 40, height - 50);

        if (isInvulnerable) {
            invulnerabilityTime -= delta;
            if (invulnerabilityTime <= 0) {
                isInvulnerable = false;
            }
        }

        stateTime += delta;
        jumpCooldown += delta;
        slashCooldown += delta;
        logHitCooldown += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);

        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.5f);
        float size1 = width / 2 * 0.5f;
        float size2 = size1 * 0.5f;
        float shadowSize;
        float shadowY = originalY + 10;
        if (jumping) {
            if (velocity > 0) {
                shadowSize = size1 - (size1 - size2) * (jumpHeight - velocity) / jumpHeight;
                peakShadowSize = shadowSize;
            } else {
                float progress = (originalY - position.y) / jumpHeight;
                shadowSize = (peakShadowSize + (size1 - peakShadowSize) * progress) + 15;
            }
        } else {
            shadowSize = size1;
            shadowY = position.y + 10;
        }
        shapeRenderer.circle(position.x + width / 2, shadowY, shadowSize);
        shapeRenderer.end();
        batch.begin();

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

    public void jump() {
        if (!jumping && jumpCooldown >= 1) {
            jumping = true;
            originalY = position.y;
            jumpStartTime = stateTime;
        }
    }

    public void updatePosition(float rotation) {
        if(logHitCooldown < 1){
            return;
        }
        damageTaken += 1;

        pushForce = damageTaken * 250;
        float pushDirectionX = (float) Math.cos(Math.toRadians(rotation + 90));
        float pushDirectionY = (float) Math.sin(Math.toRadians(rotation + 90));

        this.pushVelocity.set(pushForce * pushDirectionX, pushForce * pushDirectionY);

        logHitCooldown = 0;
    }

    public void updatePosition(Vector2 direction){
        if(isInvulnerable){
            return;
        }

        float angle = direction.angleRad();

        damageTaken += Settings.PLAYER_DAMAGE_RECIEVED;
        pushForce = damageTaken * 250;

        float pushDirectionX = (float) Math.cos(angle);
        float pushDirectionY = (float) Math.sin(angle);

        this.pushVelocity.set(pushForce * pushDirectionX, pushForce * pushDirectionY);

        isInvulnerable = true;
        invulnerabilityTime = 1;
    }

    public void slash(){
        if(isAlive && slashCooldown >= 1.5){
            PlayerSlash slash = new PlayerSlash(this);
            getStage().addActor(slash);
            slashCooldown = 0;
        }
    }

    public void increaseScore(float delta){
        this.score += delta;
    }

    public Rectangle getCollisionRect() {
        return collisionRect;
    }

    public boolean isJumping() {
        return jumping;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public float getPushForce() {
        return pushForce;
    }

    public void setAlive(boolean isAlive){
        this.isAlive = isAlive;
    }

    public boolean isAlive(){
        return isAlive;
    }

    public Vector2 getPreviousPosition() {
        return previousPosition;
    }

    public void setPreviousPosition(Vector2 previousPosition) {
        this.previousPosition = previousPosition;
    }

    public float getDamageTaken() {
        return damageTaken;
    }

    public float getScore() {
        return score;
    }
}
