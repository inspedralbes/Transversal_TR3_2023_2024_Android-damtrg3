package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Projecte3;
import com.mygdx.game.helpers.AssetManager;

public class OptionsScreen implements Screen {
    private Projecte3 game;
    private Stage stage;
    private TextButton toggleMusicButton;
    private Slider volumeSlider;
    private TextButton backButton;

    public OptionsScreen(Projecte3 game) {
        this.game = game;
        stage = new Stage();

        toggleMusicButton = new TextButton("Toggle Music", AssetManager.neon_skin);
        volumeSlider = new Slider(0, 1, 0.01f, false, AssetManager.neon_skin);
        backButton = new TextButton("Back", AssetManager.neon_skin);

        toggleMusicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Implement music toggle logic here
            }
        });

        volumeSlider.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Implement volume control logic here
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MenuScreen(game));
            }
        });

        stage.addActor(toggleMusicButton);
        stage.addActor(volumeSlider);
        stage.addActor(backButton);

        // Center the actors
        toggleMusicButton.setPosition(Gdx.graphics.getWidth() / 2 - toggleMusicButton.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        volumeSlider.setPosition(Gdx.graphics.getWidth() / 2 - volumeSlider.getWidth() / 2, Gdx.graphics.getHeight() / 2 - toggleMusicButton.getHeight());
        backButton.setPosition(Gdx.graphics.getWidth() / 2 - backButton.getWidth() / 2, Gdx.graphics.getHeight() / 2 - toggleMusicButton.getHeight() - volumeSlider.getHeight());

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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

    }
}
