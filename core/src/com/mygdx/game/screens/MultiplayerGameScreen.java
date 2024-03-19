package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.Projecte3;
import com.mygdx.game.actors.MultiPlayerPlayer;
import com.mygdx.game.actors.Player;
import com.mygdx.game.actors.obstacles.SpinLog;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.helpers.MultiplayerGameInputHandler;
import com.mygdx.game.utils.Settings;

import org.json.JSONObject;

import java.util.HashMap;

import io.socket.emitter.Emitter;

public class MultiplayerGameScreen implements Screen {

    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private Projecte3 game;
    private Stage stage = new Stage();
    private Player player;
    private ShapeRenderer shapeRenderer;

    private TiledMapTileLayer plataformaLayer;
    private MapObjects spawnLayer;
    private MultiPlayerPlayer[] players;

    public MultiplayerGameScreen(Projecte3 game, String[] jugadors) {
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

        //Carregar els jugadors
        spawnLayer = AssetManager.tiledMap.getLayers().get("spawn").getObjects();

        RectangleMapObject spawnObject = (RectangleMapObject) spawnLayer.get("spawn");
        Rectangle spawnRectangle = spawnObject.getRectangle();

        players = new MultiPlayerPlayer[jugadors.length];
        for (int i = 0; i < jugadors.length; i++) {
            players[i] = new MultiPlayerPlayer(jugadors[i]);
            stage.addActor(players[i]);

            float randomX = MathUtils.random(spawnRectangle.x, spawnRectangle.x + spawnRectangle.width);
            float randomY = MathUtils.random(spawnRectangle.y, spawnRectangle.y + spawnRectangle.height);

            System.out.println("X: " + randomX + " Y: " + randomY);

            Vector2 position = new Vector2(randomX, randomY);

            players[i].setPosition(position);

            if (jugadors[i].equals(game.nomUsuari)) {
                players[i].setIsCurrentUser(true);
                Gdx.input.setInputProcessor(new MultiplayerGameInputHandler(players[i], this.game));
            }
        }
        System.out.println("Jugadors: " + players.length);
        plataformaLayer = (TiledMapTileLayer) AssetManager.tiledMap.getLayers().get("plataforma");

        for(Actor actor : stage.getActors()){
            if (actor instanceof MultiPlayerPlayer){
                MultiPlayerPlayer player = (MultiPlayerPlayer) actor;
                System.out.println(player.getPosition());
            }
        }
        
    }

    @Override
    public void show() {
        MenuSalasScreen.socket.on("key_event", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try{
                            String player = data.getString("player");
                            String type = data.getString("type");
                            String keycode = data.getString("keycode");

                            for (MultiPlayerPlayer currentPlayer : players) {
                                if(currentPlayer.getUser().equals(player)){
                                    if(type.equals("keyDown")){
                                        switch (keycode) {
                                            case "up":
                                                currentPlayer.getDirection().y = 1;
                                                break;
                                            case "down":
                                                currentPlayer.getDirection().y = -1;
                                                break;
                                            case "left":
                                                currentPlayer.getDirection().x = -1;
                                                break;
                                            case "right":
                                                currentPlayer.getDirection().x = 1;
                                                break;
                                            case "space":
                                                currentPlayer.jump();
                                                break;
                                        }
                                    } else if(type.equals("keyUp")){
                                        switch (keycode) {
                                            case "up":
                                                currentPlayer.getDirection().y = 0;
                                                break;
                                            case "down":
                                                currentPlayer.getDirection().y = 0;
                                                break;
                                            case "left":
                                                currentPlayer.getDirection().x = 0;
                                                break;
                                            case "right":
                                                currentPlayer.getDirection().x = 0;
                                                break;
                                        }
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
    }

    @Override
    public void render(float delta) {
        checkCollisions();
        checkInPlatform();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setView(camera);
        mapRenderer.render();

        stage.act(delta);
        stage.draw();

        drawHitboxes();
    }
    public void checkCollisions(){
        for(Actor actor : stage.getActors()){
            if(actor instanceof SpinLog){
                SpinLog spinLog = (SpinLog) actor;
                for (MultiPlayerPlayer currentPlayer : players) {
                    if(spinLog.collides(currentPlayer)){
                        if(!currentPlayer.isJumping()){
                            float logRotation = spinLog.getRotation();
                            currentPlayer.updatePosition(logRotation);
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
                    currentPlayer.remove();
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

    }

    @Override
    public void dispose() {
        mapRenderer.dispose();
    }
    public void drawHitboxes(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for(Actor actor : stage.getActors()){
            if (actor instanceof MultiPlayerPlayer){
                MultiPlayerPlayer player = (MultiPlayerPlayer) actor;
                shapeRenderer.rect(player.getCollisionRect().x, player.getCollisionRect().y, player.getCollisionRect().width, player.getCollisionRect().height);
            } else if (actor instanceof SpinLog){
                SpinLog spinLog = (SpinLog) actor;
                shapeRenderer.polygon(spinLog.getCollisionPolygon().getTransformedVertices());
            }
        }
        shapeRenderer.end();
    }

}