package com.mygdx.game.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.helpers.AssetManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import org.json.JSONObject;

public class Broadcast {
    private Socket socket;
    private BitmapFont font;
    private GlyphLayout layout;
    private float messageTime = 0;
    Table popupTable;
    Label titleLabel;
    Label messageLabel;
    boolean showMessage = false;

    public Broadcast() {
        connectSocket();
        font = new BitmapFont();
        layout = new GlyphLayout();

        popupTable = new Table();
        popupTable.setSize(300, 240);
        popupTable.setVisible(false); // Inicialmente, hacer que la tabla no sea visible

        // Establecer la posición del pop-up en el centro de la pantalla
        popupTable.setPosition((Gdx.graphics.getWidth() - popupTable.getWidth()) / 2, (Gdx.graphics.getHeight() - popupTable.getHeight()) - 20);
        Texture popupBackgroundTexture = new Texture(Gdx.files.internal("frame6.png"));
        TextureRegionDrawable popupBackgroundDrawable = new TextureRegionDrawable(new TextureRegion(popupBackgroundTexture));
        popupTable.setBackground(popupBackgroundDrawable);

        // Crear etiquetas para el título y el mensaje
        titleLabel = new Label("", new Label.LabelStyle(font, Color.RED));
        messageLabel = new Label("", new Label.LabelStyle(font, Color.WHITE));

        titleLabel.setFontScale(1.2f);
        messageLabel.setFontScale(1f);

        // Agregar etiquetas a la tabla
        popupTable.add(titleLabel).padBottom(10).row();
        popupTable.add(messageLabel);
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
                    System.out.println(title + "\n" + message);

                    // Mostrar el pop-up y establecer el título y el mensaje
                    titleLabel.setText(title);
                    messageLabel.setText(message);
                    showMessage = true;
                    messageTime = 0;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    public void draw(SpriteBatch batch, float deltaTime) {
        if (showMessage) {
            messageTime += deltaTime; // Incrementa el contador de tiempo

            // Si el contador es menor o igual a 5 segundos
            if (messageTime <= 5) {
                // Dibuja el pop-up
                popupTable.draw(batch, 1);
            } else {
                // Restablecer el pop-up y el contador de tiempo
                popupTable.setVisible(false);
                showMessage = false;
            }
        }
    }
}
