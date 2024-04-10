package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.Projecte3;
import com.mygdx.game.actors.MultiPlayerPlayer;
import com.mygdx.game.actors.Player;
import com.mygdx.game.actors.PlayerSlash;
import com.mygdx.game.actors.obstacles.SpinLog;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.helpers.MultiplayerGameInputHandler;
import com.mygdx.game.stats.PlayerStats;
import com.mygdx.game.utils.Settings;
import com.sun.org.apache.xpath.internal.operations.Mult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.socket.emitter.Emitter;

public class MultiplayerGameScreen implements Screen {

    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private Projecte3 game;
    private Stage stage;
    private Player player;
    private ShapeRenderer shapeRenderer;

    private TiledMapTileLayer plataformaLayer;
    private MapObjects spawnLayer;
    private MultiPlayerPlayer[] players;
    private BitmapFont font;
    private SpriteBatch batch;
    private TextButton playAgainButton, endGameButton;
    private boolean scoreSent;
    private boolean leftPressed, rightPressed, upPressed, downPressed;
    private String[] jugadorsIn;
    private MultiPlayerPlayer ganadorRonda;
    private String creadorSala;
    private ArrayList<PlayerStats> player_stats;
    private int position;
    private int[] skins;
    private OptionsScreen.AudioManager audioManager;

    public MultiplayerGameScreen(Projecte3 game, String[] jugadors, String creador, int[] skins,  OptionsScreen.AudioManager audioManager) {
        creadorSala = creador;
        jugadorsIn = jugadors;
        stage = new Stage();
        scoreSent = false;
        font = new BitmapFont();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        player_stats = new ArrayList<>();
        position = jugadors.length;
        this.skins = skins;

        this.game = game;
        this.audioManager = audioManager;
        camera = new OrthographicCamera(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
        camera.setToOrtho(false);
        StretchViewport viewport = new StretchViewport(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT, camera);
        stage = new Stage(viewport);
        mapRenderer = new OrthogonalTiledMapRenderer(AssetManager.tiledMap);

        mapRenderer.setView(camera);
        mapRenderer.render();

        SpinLog spinLog = new SpinLog();
        stage.addActor(spinLog);

        //Carregar els jugadors
        spawnLayer = AssetManager.tiledMap.getLayers().get("spawn").getObjects();

        RectangleMapObject spawnObject = (RectangleMapObject) spawnLayer.get("spawn");
        Rectangle spawnRectangle = spawnObject.getRectangle();

        InputMultiplexer inputMultiplexer = new InputMultiplexer();

        players = new MultiPlayerPlayer[jugadors.length];
        for (int i = 0; i < jugadors.length; i++) {
            players[i] = new MultiPlayerPlayer(jugadors[i], skins[i]);
            stage.addActor(players[i]);

            float randomX = MathUtils.random(spawnRectangle.x, spawnRectangle.x + spawnRectangle.width);
            float randomY = MathUtils.random(spawnRectangle.y, spawnRectangle.y + spawnRectangle.height);

            System.out.println("X: " + randomX + " Y: " + randomY);

            Vector2 position = new Vector2(randomX, randomY);

            players[i].setPosition(position);

            if (jugadors[i].equals(game.nomUsuari)) {
                players[i].setIsCurrentUser(true);
                inputMultiplexer.addProcessor(new MultiplayerGameInputHandler(players[i], this.game));
            }
        }
        inputMultiplexer.addProcessor(stage);

        Gdx.input.setInputProcessor(inputMultiplexer);

        System.out.println("Jugadors: " + players.length);
        plataformaLayer = (TiledMapTileLayer) AssetManager.tiledMap.getLayers().get("plataforma");

        for(Actor actor : stage.getActors()){
            if (actor instanceof MultiPlayerPlayer){
                MultiPlayerPlayer player = (MultiPlayerPlayer) actor;
                System.out.println(player.getPosition());
            }
        }

        for (MultiPlayerPlayer player : players) {
            if (player.isCurrentUser() && player.isAlive()) {
                String lobby = game.SalaActual;
                String username = player.getUser();
                Vector2 position = player.getPosition();

                JSONObject data = new JSONObject();
                try {
                    data.put("salaId", lobby);
                    data.put("user", username);
                    data.put("positionX", position.x);
                    data.put("positionY", position.y);

                    MenuSalasScreen.socket.emit("user_position", data);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        playAgainButton = new TextButton("Play Again", AssetManager.neon_skin);
        playAgainButton.setPosition(Settings.SCREEN_WIDTH / 2 - playAgainButton.getWidth() / 2, Settings.SCREEN_HEIGHT / 2 - playAgainButton.getHeight() / 2);
        playAgainButton.setVisible(false);
        stage.addActor(playAgainButton);
        playAgainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                JSONObject data = new JSONObject();
                try {
                    data.put("salaId", game.SalaActual);
                    data.put("user", ganadorRonda.getUser());
                    MenuSalasScreen.socket.emit("playerDead", data.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject salaInfo = new JSONObject();
                try {
                    salaInfo.put("idSala", game.SalaActual);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                System.out.println(game.SalaActual);
                System.out.println(salaInfo.toString());
                MenuSalasScreen.socket.emit("playAgain", salaInfo);
            }
        });

        endGameButton = new TextButton("End Game", AssetManager.neon_skin);
        endGameButton.setPosition(Settings.SCREEN_WIDTH / 2 - endGameButton.getWidth() / 2, Settings.SCREEN_HEIGHT / 2 - endGameButton.getHeight() / 2 - 100);
        endGameButton.setVisible(false);
        stage.addActor(endGameButton);
        endGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                JSONObject data = new JSONObject();
                JSONObject data2 = new JSONObject();
                try {
                    data.put("salaId", game.SalaActual);
                    data.put("user", ganadorRonda.getUser());
                    MenuSalasScreen.socket.emit("playerDead", data.toString());
                    data2.put("salaId", game.SalaActual);
                    MenuSalasScreen.socket.emit("endGame", data2.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void show() {
        Music music = Gdx.audio.newMusic(Gdx.files.internal("GameMode/lean.mp3"));
        audioManager.setMusic(music);
        audioManager.setMusicEnabled(true);
        MenuSalasScreen.socket.on("key_down", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try{
                            String player = data.getString("player");
                            int keycode = data.getInt("keycode");

                            for (MultiPlayerPlayer currentPlayer : players) {
                                if(currentPlayer.getUser().equals(player)){
                                    switch (keycode) {
                                        case Input.Keys.UP:
                                            upPressed = true;
                                            currentPlayer.getDirection().y = 1;
                                            break;
                                        case Input.Keys.DOWN:
                                            downPressed = true;
                                            currentPlayer.getDirection().y = -1;
                                            break;
                                        case Input.Keys.LEFT:
                                            leftPressed = true;
                                            currentPlayer.getDirection().x = -1;
                                            break;
                                        case Input.Keys.RIGHT:
                                            rightPressed = true;
                                            currentPlayer.getDirection().x = 1;
                                            break;
                                        case Input.Keys.SPACE:
                                            currentPlayer.jump();
                                            break;
                                        case Input.Keys.C:
                                            currentPlayer.slash();
                                            break;
                                    }
                                }
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        MenuSalasScreen.socket.on("key_up", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try{
                            String player = data.getString("player");
                            int keycode = data.getInt("keycode");

                            for (MultiPlayerPlayer currentPlayer : players) {
                                if(currentPlayer.getUser().equals(player)){
                                    switch (keycode) {
                                        case Input.Keys.UP:
                                            upPressed = false;
                                            if(!downPressed){
                                                currentPlayer.getDirection().y = 0;
                                            }
                                            break;
                                        case Input.Keys.DOWN:
                                            downPressed = false;
                                            if(!upPressed){
                                                currentPlayer.getDirection().y = 0;
                                            }
                                            break;
                                        case Input.Keys.LEFT:
                                            leftPressed = false;
                                            if(!rightPressed){
                                                currentPlayer.getDirection().x = 0;
                                            }
                                            break;
                                        case Input.Keys.RIGHT:
                                            rightPressed = false;
                                            if(!leftPressed){
                                                currentPlayer.getDirection().x = 0;
                                            }
                                            break;
                                    }
                                }
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                for (MultiPlayerPlayer player : players) {
                    if (player.isCurrentUser() && player.isAlive()) {
                        String lobby = game.SalaActual;
                        String username = player.getUser();
                        Vector2 position = player.getPosition();

                        JSONObject data = new JSONObject();
                        try {
                            data.put("salaId", lobby);
                            data.put("user", username);
                            data.put("positionX", position.x);
                            data.put("positionY", position.y);

                            MenuSalasScreen.socket.emit("user_position", data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, 0, 1, TimeUnit.SECONDS);

        MenuSalasScreen.socket.on("update_positions", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            String user = data.getString("user");
                            float positionX = (float) data.getDouble("positionX");
                            float positionY = (float) data.getDouble("positionY");

                            for (MultiPlayerPlayer player : players) {
                                if (player.getUser().equals(user)) {
                                    player.setPosition(new Vector2(positionX, positionY));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        MenuSalasScreen.socket.on("hit_by_log", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            String user = data.getString("player");
                            float positionX = (float) data.getDouble("x");
                            float positionY = (float) data.getDouble("y");
                            float rotation = (float) data.getDouble("rotation");

                            for (MultiPlayerPlayer player : players) {
                                if (player.getUser().equals(user)) {
                                    player.setPosition(new Vector2(positionX, positionY));
                                    player.updatePosition(rotation);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        MenuSalasScreen.socket.on("hit_by_player", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            String user = data.getString("player");
                            float positionX = (float) data.getDouble("x");
                            float positionY = (float) data.getDouble("y");
                            float directionX = (float) data.getDouble("directionX");
                            float directionY = (float) data.getDouble("directionY");

                            for (MultiPlayerPlayer player : players) {
                                if (player.getUser().equals(user)) {
                                    player.setPosition(new Vector2(positionX, positionY));
                                    player.updatePosition(new Vector2(directionX, directionY));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        MenuSalasScreen.socket.on("player_dead", new Emitter.Listener(){
            @Override
            public void call(Object... args) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            String user = data.getString("user");
                            for (MultiPlayerPlayer player : players) {
                                if (player.getUser().equals(user) && player.isAlive()) {
                                    player.setAlive(false);
                                    PlayerStats playerStats = new PlayerStats(player.getUser(), position, player.getDamageTaken());
                                    --position;
                                    player_stats.add(playerStats);
                                    player.remove();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        MenuSalasScreen.socket.on("PLAY_AGAIN", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        dispose();
                        OptionsScreen.AudioManager audioManager = new OptionsScreen.AudioManager();
                        game.setScreen(new MultiplayerGameScreen(game, jugadorsIn, creadorSala, skins, audioManager));
                    }
                });
            }
        });

        MenuSalasScreen.socket.on("GAME_ENDED", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        dispose();
                        game.setScreen(new GameEndedScreen(game));
                    }
                });
            }
        });
    }

    @Override
    public void render(float delta) {
        checkCollisions();
        checkInPlatform();
        checkAllDead();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setView(camera);
        mapRenderer.render();

        stage.act(delta);
        stage.draw();

        drawHitboxes();

        batch.begin();
        for(MultiPlayerPlayer player : players){
            if(player.isAlive()){
                font.draw(batch, player.getUser() + " " + player.getDamageTaken(), player.getPosition().x + 15, player.getPosition().y + 50);
            }
        }
        batch.end();

    }

    public void checkAllDead(){
        int alive = 0;
        MultiPlayerPlayer winner = null;
        MultiPlayerPlayer currentUser = null;
        for(MultiPlayerPlayer player : players){
            if(player.isAlive()){
                ++alive;
                winner = player;
            }
            if(player.isCurrentUser()){
                currentUser = player;
            }
        }

        if(alive == 1){
            if(winner.isCurrentUser() && !scoreSent){
                sendScore(winner.getUser(), game.SalaActual);
                PlayerStats playerStats = new PlayerStats(winner.getUser(), position, winner.getDamageTaken());
                player_stats.add(playerStats);
                sendStats();
                scoreSent = true;
            }

            ganadorRonda = winner;
            if(currentUser.getUser().equals(creadorSala)){
                playAgainButton.setVisible(true);
                endGameButton.setVisible(true);
            }
        }
    }

    public void sendStats(){
        final String URL = "http://" + Settings.IP_SERVER + ":" + Settings.PUERTO_PETICIONES + "/stats";
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(URL);
        request.setHeader("Content-Type", "application/json");

        JSONArray playerStatsArray = new JSONArray();

        for (PlayerStats playerStats : player_stats) {
            JSONObject playerStatsObject = new JSONObject();
            try {
                playerStatsObject.put("playerName", playerStats.getPlayerName());
                playerStatsObject.put("position", playerStats.getPosition());
                playerStatsObject.put("damageReceived", playerStats.getDamageReceived());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            playerStatsArray.put(playerStatsObject);
        }

        Date currentTime = new Date();

        JSONObject data = new JSONObject();
        try {
            data.put("playerStats", playerStatsArray);
            data.put("time", currentTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        request.setContent(data.toString());

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Gdx.app.log("STATS", "Stats sent successfully");
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("STATS", "Failed to send stats: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                Gdx.app.error("STATS", "Request cancelled");
            }
        });
    }

    public void sendScore(String user, String lobbyId){
        final String URL = "http://" + Settings.IP_SERVER + ":" + Settings.PUERTO_PETICIONES + "/score";
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(URL);
        request.setHeader("Content-Type", "application/json");
        request.setContent("{\"score\":" + 50 + ", \"username\":\"" + user + "\", \"lobbyId\":\"" + lobbyId + "\"}");

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Gdx.app.log("SCORE", "Score sent successfully");
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.error("SCORE", "Failed to send score: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                Gdx.app.error("SCORE", "Request cancelled");
            }
        });
    }

    public void checkCollisions(){
        for(Actor actor : stage.getActors()){
            if(actor instanceof SpinLog){
                SpinLog spinLog = (SpinLog) actor;
                for (MultiPlayerPlayer currentPlayer : players) {
                    if(spinLog.collides(currentPlayer)){
                        if(!currentPlayer.isJumping()){
                            float logRotation = spinLog.getRotation();
                            currentPlayer.updatePosition(logRotation, game.SalaActual);
                        }
                    }
                }
            } else if(actor instanceof PlayerSlash){
                PlayerSlash playerSlash = (PlayerSlash) actor;
                for (MultiPlayerPlayer currentPlayer : players) {
                    if(playerSlash.collides(currentPlayer)){
                        if(!currentPlayer.isJumping() && !playerSlash.getPlayer().equals(currentPlayer)){
                            currentPlayer.updatePosition(playerSlash.getDirection(), game.SalaActual);
                        }
                    }
                }
            }
        }
    }

    public void checkInPlatform(){
        int tileSize = 32;
        for (MultiPlayerPlayer currentPlayer : players) {
            int playerTileX = (int) ((currentPlayer.getPosition().x + 20) / tileSize);
            int playerTileY = (int) (currentPlayer.getPosition().y / tileSize);
            TiledMapTileLayer.Cell cell = plataformaLayer.getCell(playerTileX, playerTileY);
            if (cell == null) {
                if(!currentPlayer.isJumping()){
                    if(currentPlayer.isCurrentUser() && currentPlayer.isAlive()){
                        JSONObject data = new JSONObject();
                        try {
                            data.put("salaId", game.SalaActual);
                            data.put("user", currentPlayer.getUser());
                            MenuSalasScreen.socket.emit("playerDead", data.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
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
        audioManager.setMusicEnabled(false);
    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
        font.dispose();
        batch.dispose();
        stage.dispose();
        MenuSalasScreen.socket.off("key_down");
        MenuSalasScreen.socket.off("key_up");
        MenuSalasScreen.socket.off("update_positions");
        MenuSalasScreen.socket.off("hit_by_log");
        MenuSalasScreen.socket.off("hit_by_player");
        MenuSalasScreen.socket.off("player_dead");
        MenuSalasScreen.socket.off("PLAY_AGAIN");
    }
    public void drawHitboxes(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for(Actor actor : stage.getActors()){
            if (actor instanceof Player){
                Player player = (Player) actor;
                shapeRenderer.rect(player.getCollisionRect().x, player.getCollisionRect().y, player.getCollisionRect().width, player.getCollisionRect().height);
            } else if (actor instanceof SpinLog){
                SpinLog spinLog = (SpinLog) actor;
                shapeRenderer.polygon(spinLog.getCollisionPolygon().getTransformedVertices());
            } else if(actor instanceof PlayerSlash){
                PlayerSlash playerSlash = (PlayerSlash) actor;
                shapeRenderer.rect(playerSlash.getCollisionRect().x, playerSlash.getCollisionRect().y, playerSlash.getCollisionRect().width, playerSlash.getCollisionRect().height);
            }
        }
        shapeRenderer.end();
    }

}