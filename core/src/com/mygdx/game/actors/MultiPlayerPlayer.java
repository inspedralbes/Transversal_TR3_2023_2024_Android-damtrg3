package com.mygdx.game.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
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

    public MultiPlayerPlayer(String user){
        super();
        this.user = user;
        isCurrentUser = false;
        socket = MenuSalasScreen.socket;
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
