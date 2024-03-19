package com.mygdx.game.helpers;

import com.mygdx.game.Projecte3;
import com.mygdx.game.actors.MultiPlayerPlayer;
import com.mygdx.game.actors.Player;
import com.badlogic.gdx.Input;
import com.mygdx.game.screens.MenuSalasScreen;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;

public class MultiplayerGameInputHandler extends GameInputHandler{
    private MultiPlayerPlayer player;
    private Projecte3 game;
    private Socket socket = MenuSalasScreen.socket;
    public MultiplayerGameInputHandler(Player player, Projecte3 game) {
        super(player);
        this.player = (MultiPlayerPlayer) player;
        this.game = game;
    }

    @Override
    public boolean keyDown(int keycode) {
        JSONObject data = new JSONObject();
        super.keyDown(keycode);
        try {
            data.put("player", this.player.getUser());
            data.put("salaId", this.game.SalaActual);
            data.put("type", "keyDown");
            switch (keycode) {
                case Input.Keys.UP:
                    data.put("keycode", "up");
                    socket.emit("key", data.toString());
                    break;
                case Input.Keys.DOWN:
                    data.put("keycode", "down");
                    socket.emit("key", data.toString());
                    break;
                case Input.Keys.LEFT:
                    data.put("keycode", "left");
                    socket.emit("key", data.toString());
                    break;
                case Input.Keys.RIGHT:
                    data.put("keycode", "right");
                    socket.emit("key", data.toString());
                    break;
                case Input.Keys.SPACE:
                    data.put("keycode", "space");
                    socket.emit("key", data.toString());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        JSONObject data = new JSONObject();
        super.keyUp(keycode);
        try {
            data.put("player", this.player.getUser());
            data.put("salaId", this.game.SalaActual);
            data.put("type", "keyUp");
            switch (keycode) {
                case Input.Keys.UP:
                    data.put("keycode", "up");
                    socket.emit("key", data.toString());
                    break;
                case Input.Keys.DOWN:
                    data.put("keycode", "down");
                    socket.emit("key", data.toString());
                    break;
                case Input.Keys.LEFT:
                    data.put("keycode", "left");
                    socket.emit("key", data.toString());
                    break;
                case Input.Keys.RIGHT:
                    data.put("keycode", "right");
                    socket.emit("key", data.toString());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
