package com.mygdx.game.actors;

import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MultiPlayerPlayer extends Player{
    private String user;
    private boolean isCurrentUser;

    public MultiPlayerPlayer(String user){
        super();
        this.user = user;
        isCurrentUser = false;
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
