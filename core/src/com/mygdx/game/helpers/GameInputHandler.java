package com.mygdx.game.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Timer;
import com.mygdx.game.actors.Player;
import com.mygdx.game.screens.GameScreen;

public class GameInputHandler implements InputProcessor {

    private Player player;
    private boolean leftPressed, rightPressed, upPressed, downPressed;

    public GameInputHandler(Player player){
        this.player = player;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch (keycode) {
            case Input.Keys.UP:
                upPressed = true;
                player.getDirection().y = 1;
                break;
            case Input.Keys.DOWN:
                downPressed = true;
                player.getDirection().y = -1;
                break;
            case Input.Keys.LEFT:
                leftPressed = true;
                player.getDirection().x = -1;
                break;
            case Input.Keys.RIGHT:
                rightPressed = true;
                player.getDirection().x = 1;
                break;
            case Input.Keys.SPACE:
                player.jump();
                break;
            case Input.Keys.C:
                player.slash();
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.UP:
                upPressed = false;
                if (!downPressed) {
                    player.getDirection().y = 0;
                }
                break;
            case Input.Keys.DOWN:
                downPressed = false;
                if (!upPressed) {
                    player.getDirection().y = 0;
                }
                break;
            case Input.Keys.LEFT:
                leftPressed = false;
                if (!rightPressed) {
                    player.getDirection().x = 0;
                }
                break;
            case Input.Keys.RIGHT:
                rightPressed = false;
                if (!leftPressed) {
                    player.getDirection().x = 0;
                }
                break;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
