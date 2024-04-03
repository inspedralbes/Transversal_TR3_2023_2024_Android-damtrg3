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
    private Stage stage;
    private TextButton button;


    public Broadcast() {
        connectSocket();
        font = new BitmapFont();
        stage = new Stage();
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        button = new TextButton("", textButtonStyle);
        button.setPosition(Gdx.graphics.getWidth() / 2 - button.getWidth() / 2, Gdx.graphics.getHeight() - button.getHeight()-100);
        stage.addActor(button);
        Gdx.input.setInputProcessor(stage);
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
                    button.setText("Received broadcast message:\nTitle: " + title + "\nMessage: " + message);
                    button.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            button.setVisible(false);
                        }
                    });
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }
    public void draw(SpriteBatch batch) {
        stage.draw();
    }
}