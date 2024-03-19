package com.mygdx.game.actors;

import com.badlogic.gdx.graphics.g2d.Batch;

public class MultiPlayerPlayer extends Player{
    private String user;
    private boolean isCurrentUser;

    public MultiPlayerPlayer(String user){
        super();
        this.user = user;
        isCurrentUser = false;

    }

    @Override
    public void act(float delta){
        super.act(delta);

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
