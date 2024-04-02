package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.Projecte3;
import com.mygdx.game.actors.Player;
import com.mygdx.game.actors.PlayerSlash;
import com.mygdx.game.actors.obstacles.SpinLog;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.helpers.GameInputHandler;
import com.mygdx.game.utils.Settings;


public class GameScreen implements Screen {

    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private Projecte3 game;
    private Stage stage = new Stage();
    private Player player;
    private ShapeRenderer shapeRenderer;

    private TiledMapTileLayer plataformaLayer;

    private Label forceLabel;
    private Label scoreLabel;

    public GameScreen(Projecte3 game) {
        shapeRenderer = new ShapeRenderer();

        this.game = game;

        camera = new OrthographicCamera(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
        camera.setToOrtho(false);
        StretchViewport viewport = new StretchViewport(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT, camera);
        stage = new Stage(viewport);
        mapRenderer = new OrthogonalTiledMapRenderer(AssetManager.tiledMap);

        mapRenderer.setView(camera);
        mapRenderer.render();

        SpinLog spinLog = new SpinLog();
        stage.addActor(spinLog);

        // Crear la etiqueta de fuerza y añadirla al escenario
        forceLabel = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        forceLabel.setPosition(10, Gdx.graphics.getHeight() - 10);
        stage.addActor(forceLabel);

        scoreLabel = new Label("", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel.setPosition(10, Gdx.graphics.getHeight() - 30);
        stage.addActor(scoreLabel);

        //Carregar el jugador
        player = new Player();
        stage.addActor(player);

        plataformaLayer = (TiledMapTileLayer) AssetManager.tiledMap.getLayers().get("plataforma");

        Gdx.input.setInputProcessor(new GameInputHandler(player));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(new GameInputHandler(player));
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

        scoreLabel.setText("Puntuación: " + player.getScore());

        stage.act(delta);
        stage.draw();

        drawHitboxes();
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
            if(!player.isJumping() && player.isAlive()){
                sendScore(player.getScore());
                player.setAlive(false);
                player.remove();
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

    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
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

    public void sendScore(float score){
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
    }
}
