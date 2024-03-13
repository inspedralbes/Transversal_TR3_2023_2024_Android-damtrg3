package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.Projecte3;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.utils.Settings;

public class GameScreen implements Screen {

    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private Projecte3 game;
    private Stage stage = new Stage();


    public GameScreen(Projecte3 game) {
        this.game = game;

        camera = new OrthographicCamera(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
        camera.setToOrtho(false);
        StretchViewport viewport = new StretchViewport(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT, camera);
        stage = new Stage(viewport);
        mapRenderer = new OrthogonalTiledMapRenderer(AssetManager.tiledMap);

        mapRenderer.setView(camera);
        mapRenderer.render();


    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.setView(camera);
        mapRenderer.render();

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
        mapRenderer.dispose();
    }
}
