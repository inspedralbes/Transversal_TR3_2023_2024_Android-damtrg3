package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.Projecte3;
import com.mygdx.game.actors.Player;
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

        //Carregar el jugador
        player = new Player();
        stage.addActor(player);

        plataformaLayer = (TiledMapTileLayer) AssetManager.tiledMap.getLayers().get("plataforma");

        Gdx.input.setInputProcessor(new GameInputHandler(player));
    }

    @Override
    public void show() {

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
                if(spinLog.collides(player)){
                    if(!player.isJumping()){
                        player.remove();
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
            if(!player.isJumping()){
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
            }
        }
        shapeRenderer.end();
    }
}
