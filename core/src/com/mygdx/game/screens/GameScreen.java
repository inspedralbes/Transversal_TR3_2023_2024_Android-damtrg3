package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.Projecte3;
import com.mygdx.game.actors.Player;
import com.mygdx.game.actors.PlayerSlash;
import com.mygdx.game.actors.obstacles.SpinLog;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.helpers.GameInputHandler;
import com.mygdx.game.utils.Settings;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.Texture;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


import java.util.concurrent.TimeUnit;


public class GameScreen implements Screen {
    private boolean isElapsedTimeSent;
    private long elapsedTimeWhenPlayerDied;
    private boolean isPlayerAlive;
    private Label timerLabel;
    private long startTime;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private Projecte3 game;
    private Stage stage;
    private Player player;
    private ShapeRenderer shapeRenderer;

    private TiledMapTileLayer plataformaLayer;
    private Sound sound;
    private Label forceLabel;
    //private Label scoreLabel;
    private FitViewport viewport;
    private Touchpad touchpad;
    private InputMultiplexer inputMultiplexer;
    private Projecte3.AudioManager audioManager;
    private Music music;
    private boolean debeReproducirSonidoDeLava;
    private Sound lavaSound;
    public GameScreen(Projecte3 game, Projecte3.AudioManager audioManager) {
        this.audioManager = audioManager;
        lavaSound = Gdx.audio.newSound(Gdx.files.internal("GameMode/acid.mp3"));
        // Crear la música aquí en lugar de en show()
        this.music = Gdx.audio.newMusic(Gdx.files.internal("GameMode/musica.mp3"));
        audioManager.setMusic(music);

        shapeRenderer = new ShapeRenderer();
        isPlayerAlive = true;
        elapsedTimeWhenPlayerDied = 0;
        isElapsedTimeSent = false;

        this.game = game;

        Settings.setHeight(Gdx.graphics.getHeight());
        Settings.setWidth(Gdx.graphics.getWidth());

        camera = new OrthographicCamera();
        viewport = new FitViewport(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT, camera);
        camera.setToOrtho(false);
        camera.update();

        mapRenderer = new OrthogonalTiledMapRenderer(AssetManager.tiledMap);
        mapRenderer.setView(camera);
        mapRenderer.render();

        stage = new Stage(viewport);

        mapRenderer.setView(camera);
        mapRenderer.render();

        SpinLog spinLog = new SpinLog();
        stage.addActor(spinLog);

        // Crear la etiqueta de fuerza y añadirla al escenario
        forceLabel = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        forceLabel.setPosition(10, Gdx.graphics.getHeight() - 10);
        stage.addActor(forceLabel);

        /*scoreLabel = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel.setPosition(10, Gdx.graphics.getHeight() - 30);
        stage.addActor(scoreLabel);*/

        //Carregar el jugador
        player = new Player();
        stage.addActor(player);

        plataformaLayer = (TiledMapTileLayer) AssetManager.tiledMap.getLayers().get("plataforma");

        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(stage);
        inputMultiplexer.addProcessor(new GameInputHandler(player));
        Gdx.input.setInputProcessor(inputMultiplexer);

        startTime = TimeUtils.millis();

        // Crear la etiqueta del cronómetro y añadirla al escenario
        timerLabel = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timerLabel.setPosition(10, Gdx.graphics.getHeight() - 30);
        stage.addActor(timerLabel);

        TouchpadStyle touchpadStyle = new TouchpadStyle();

        Drawable touchBackground = AssetManager.clean_skin.getDrawable("touchpad");
        Drawable touchKnob = AssetManager.clean_skin.getDrawable("touchpad-knob");

        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;

        touchpad = new Touchpad(10, touchpadStyle);
        touchpad.setBounds(50, 50, 150, 150);

        touchpad.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float knobPercentX = touchpad.getKnobPercentX();
                float knobPercentY = touchpad.getKnobPercentY();
                player.getDirection().x = knobPercentX;
                player.getDirection().y = knobPercentY;
            }
        });

        stage.addActor(touchpad);

        TextButton jumpButton = new TextButton("Jump", AssetManager.clean_skin);
        jumpButton.setSize(200, 100);
        jumpButton.setPosition(Gdx.graphics.getWidth() - 300, 50);
        jumpButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                player.jump();
            }
        });
        stage.addActor(jumpButton);

        TextButton slashButton = new TextButton("Slash", AssetManager.clean_skin);
        slashButton.setSize(200, 100);
        slashButton.setPosition(Gdx.graphics.getWidth() - 300, 200);
        slashButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                player.slash();
            }
        });
        stage.addActor(slashButton);
    }

    @Override
    public void show() {
        if(isPlayerAlive){
            lavaSound.play();
        }
        // Inicializar el sonido de la lava
        Gdx.input.setInputProcessor(new GameInputHandler(player));
        isPlayerAlive = true;
        Preferences prefs = Gdx.app.getPreferences("MyPreferences");
        float volume = prefs.getFloat("volume", 1.0f); // 1.0f es el valor predeterminado
        boolean musicEnabled = prefs.getBoolean("musicEnabled", true); // true es el valor predeterminado
        // Aplicar las preferencias
        Projecte3.audioManager.setVolume(volume);
        Projecte3.audioManager.setMusicEnabled(musicEnabled);
        // Aquí solo debes iniciar la música si está habilitada
        if (audioManager.isMusicEnabled()) {
            audioManager.getMusic().play();
        }
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    @Override
    public void render(float delta) {
        checkCollisions();
        checkInPlatform();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setView(camera);
        mapRenderer.render();

        float currentForce = player.getPushForce(); // La fuerza actual es igual a pushForce
        forceLabel.setText("Fuerza: " + currentForce); // Muestra la fuerza directamente

        //scoreLabel.setText("Puntuación: " + player.getScore());

        stage.act(delta);
        stage.draw();

        drawHitboxes();

        if (isPlayerAlive) {
            long timeElapsedMillis = TimeUtils.timeSinceMillis(startTime);
            long hours = TimeUnit.MILLISECONDS.toHours(timeElapsedMillis);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(timeElapsedMillis) % 60;
            long seconds = TimeUnit.MILLISECONDS.toSeconds(timeElapsedMillis) % 60;
            long milliseconds = timeElapsedMillis % 1000;
            lavaSound.play();
            // Actualizar la etiqueta del cronómetro
            timerLabel.setText(String.format("Tiempo: %02d:%02d:%02d.%03d", hours, minutes, seconds, milliseconds));
        }
        player.increaseScore(delta);
    }

    public void checkCollisions(){
        for(Actor actor : stage.getActors()){
            if(actor instanceof SpinLog){
                SpinLog spinLog = (SpinLog) actor;
                if(spinLog.collides(player)){
                    if(!player.isJumping()){
                        float logRotation = spinLog.getRotation();
                        player.updatePosition(logRotation);
                    }
                }
            }
        }
    }

    public void checkInPlatform(){
        int tileSize = 32;
        int playerTileX = (int) ((player.getPosition().x + 20) / tileSize);
        int playerTileY = (int) (player.getPosition().y / tileSize);
        TiledMapTileLayer.Cell cell = plataformaLayer.getCell(playerTileX, playerTileY);
        if (cell == null) {
            if(!player.isJumping() && isPlayerAlive){
                player.remove();
                // Cuando el jugador "muere", detén el cronómetro y guarda el tiempo transcurrido
                isPlayerAlive = false;
                // Reproducir el sonido de la lava
                //sendScore(player.getScore());
                // Guardar el tiempo transcurrido cuando el jugador muere
                elapsedTimeWhenPlayerDied = TimeUtils.timeSinceMillis(startTime);
                game.setScreen(new soloGameEndedScreen(game, elapsedTimeWhenPlayerDied));
                // Enviar el tiempo transcurrido al servidor solo si aún no se ha enviado
                if (!isElapsedTimeSent) {
                    sendElapsedTimeToServer(elapsedTimeWhenPlayerDied);
                    isElapsedTimeSent = true;
                }
            }
        }
    }

    public void sendElapsedTimeToServer(long elapsedTime) {
        JSONObject dataJSON = new JSONObject();
        try {
            dataJSON.put("elapsedTime", elapsedTime);
            dataJSON.put("username", game.nomUsuari);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
        httpRequest.setUrl("http://" + Settings.IP_SERVER + ":" + Settings.PUERTO_PETICIONES + "/cronometreYuser");
        String data = dataJSON.toString();
        httpRequest.setContent(data);
        httpRequest.setHeader("Content-Type", "application/json");

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Elapsed time and username sent!");
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                System.out.println("Error sending elapsed time and username: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                System.out.println("Elapsed time and username send cancelled");
            }
        });
    }



    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        camera.position.set(Settings.SCREEN_WIDTH / 2f, Settings.SCREEN_HEIGHT / 2f, 0);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Projecte3.audioManager.setMusicEnabled(false);
    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
        stage.dispose();
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

    /*public void sendScore(float score){
        final String URL = "http://" + Settings.IP_SERVER + ":" + Settings.PUERTO_PETICIONES + "/score";
        Net.HttpRequest request = new Net.HttpRequest(Net.HttpMethods.POST);
        request.setUrl(URL);
        request.setHeader("Content-Type", "application/json");
        request.setContent("{\"score\":" + score + ", \"username\":\"" + Projecte3.nomUsuari + "\"}");

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
    }*/
}
