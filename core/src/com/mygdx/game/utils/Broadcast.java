package com.mygdx.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONObject;

public class Broadcast {
    private Socket socket;
    private BitmapFont font;
    private GlyphLayout layout;
    private float messageTime = 0;

    public Broadcast() {
        connectSocket();
        font = new BitmapFont();
        layout = new GlyphLayout();
    }

    public void connectSocket() {
        try {
            socket = IO.socket("http://" + Settings.IP_SERVER + ":" + Settings.PUERTO_PETICIONES);
            socket.connect();
            configSocketEvents();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void configSocketEvents() {
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Connected to server");
            }
        }).on("broadcast", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject)args[0];
                try {
                    String title = obj.getString("title");
                    String message = obj.getString("message");
                    System.out.println("Received broadcast message:\nTitle: " + title + "\nMessage: " + message);
                    layout.setText(font,"Received broadcast message:\nTitle: " + title + "\nMessage: " + message);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }
    public void draw(SpriteBatch batch, float deltaTime) {
        messageTime += deltaTime; // Incrementa el contador de tiempo

        if (messageTime <= 5) { // Si el contador es menor o igual a 5 segundos
            float x = (Gdx.graphics.getWidth() - layout.width) / 2;
            float y = (Gdx.graphics.getHeight() + layout.height) -100;
            font.draw(batch, layout, x, y);
        } else {
            layout.setText(font, ""); // Restablece el texto del layout
            messageTime = 0; // Restablece el contador de tiempo
        }
    }
}