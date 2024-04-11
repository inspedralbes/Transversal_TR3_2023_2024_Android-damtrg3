package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.Projecte3;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.utils.Settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameEndedScreen implements Screen {

    private Projecte3 game;
    private Stage stage;

    public GameEndedScreen(Projecte3 game) {
        this.game = game;
        this.stage = new Stage();

        fetchAndDisplayLobbyRanking();
    }

    private void fetchAndDisplayLobbyRanking() {
        final String URL = "http://" + Settings.IP_SERVER + ":" + Settings.PUERTO_PETICIONES + "/getInfoSala";
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(URL);
        request.setHeader("Content-Type", "application/json");

        String lobbyId = game.SalaActual;
        String json = "{\"idSala\":\"" + lobbyId + "\"}";
        request.setContent(json);

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String response = httpResponse.getResultAsString();
                try {
                    System.out.println("Response: " + response);
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray playersArray = jsonResponse.getJSONArray("jugadores");

                    Map<String, Integer> players = new HashMap<>();
                    for (int i = 0; i < playersArray.length(); i++) {
                        JSONObject playerObject = playersArray.getJSONObject(i);
                        String name = playerObject.getString("nom");
                        int wins = playerObject.getInt("wins");
                        players.put(name, wins);
                    }

                    List<Map.Entry<String, Integer>> sortedPlayers = new ArrayList<>(players.entrySet());
                    Collections.sort(sortedPlayers, new Comparator<Map.Entry<String, Integer>>() {
                        @Override
                        public int compare(Map.Entry<String, Integer> e1, Map.Entry<String, Integer> e2) {
                            return Integer.compare(e2.getValue(), e1.getValue());
                        }
                    });

                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            displayPlayerRankings(sortedPlayers);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failed(Throwable t) {
                System.out.println("Error: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                System.out.println("Cancelled");
            }
        });
    }

    private void displayPlayerRankings(List<Map.Entry<String, Integer>> sortedPlayers) {
        stage.getBatch().begin();
        stage.getBatch().draw(AssetManager.menu_bg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage.getBatch().end();

        Table rankingsTable = new Table(); // Table para envolver los campos
        rankingsTable.setSize(700, 820); // Establece el tamaño deseado para la tabla
        rankingsTable.setPosition((Gdx.graphics.getWidth() - rankingsTable.getWidth()) / 2, (Gdx.graphics.getHeight() - rankingsTable.getHeight()) / 2); // Centra la tabla en l

        Texture backgroundTexture = new Texture(Gdx.files.internal("frame6.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        rankingsTable.setBackground(backgroundDrawable);

        Table contentTable = new Table();
        contentTable.center();


        Label nameLabel = new Label("Jugadors", AssetManager.lava_skin);
        Label winsLabel = new Label("Victories", AssetManager.lava_skin);

        contentTable.add(nameLabel).padRight(20);
        contentTable.add(winsLabel).padLeft(20);
        contentTable.row();

        for (Map.Entry<String, Integer> player : sortedPlayers) {
            Label playerNameLabel = new Label(player.getKey(), AssetManager.lava_skin);
            Label playerWinsLabel = new Label(String.valueOf(player.getValue()), AssetManager.lava_skin);
            contentTable.add(playerNameLabel).padRight(20);
            contentTable.add(playerWinsLabel).padLeft(20);
            contentTable.row();

        }

        TextButton returnButton = new TextButton("Menu principal", AssetManager.lava_skin);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameModeScreen(game));
            }
        });

        contentTable.add(returnButton).colspan(2).padTop(40);

        // Add the table to the stage
        rankingsTable.add(contentTable);
        stage.addActor(rankingsTable);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);

        stage.getBatch().begin();

        stage.getBatch().draw(AssetManager.menu_bg2, 0, 0, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);

        stage.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}