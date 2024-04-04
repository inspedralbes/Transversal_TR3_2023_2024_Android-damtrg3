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
            data.put("keycode", keycode);
            if(player.isAlive()){
                socket.emit("keyDown", data.toString());
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
            data.put("keycode", keycode);
            if(player.isAlive()){
                socket.emit("keyUp", data.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
