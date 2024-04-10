package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.Projecte3;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.utils.Settings;

public class OptionsScreen implements Screen {
    private Projecte3 game;
    private Stage stage;
    private TextButton toggleMusicButton;
    private Slider volumeSlider;
    private TextButton backButton;
    private CheckBox musicCheckBox;
    private Batch batch;

    public OptionsScreen(Projecte3 game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        Table wrapperTable = new Table();
        wrapperTable.setSize(500, 600);
        wrapperTable.setPosition((Gdx.graphics.getWidth() - wrapperTable.getWidth()) / 2, (Gdx.graphics.getHeight() - wrapperTable.getHeight()) / 2);

        Texture backgroundTexture = new Texture(Gdx.files.internal("frame6.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        wrapperTable.setBackground(backgroundDrawable);

        Table contentTable = new Table();
        contentTable.pad(20);

        toggleMusicButton = new TextButton("Toggle Music", AssetManager.lava_skin);
        volumeSlider = new Slider(0, 1, 0.01f, false, AssetManager.lava_skin);
        backButton = new TextButton("Back", AssetManager.lava_skin);
        musicCheckBox = new CheckBox("Music On/Off", AssetManager.lava_skin);

        contentTable.add(toggleMusicButton).pad(10);
        contentTable.row();
        contentTable.add(volumeSlider).pad(10);
        contentTable.row();
        contentTable.add(musicCheckBox).pad(10);
        contentTable.row();
        contentTable.add(backButton).pad(10);

        wrapperTable.add(contentTable).center();

        stage.addActor(wrapperTable);

        toggleMusicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Projecte3.audioManager.toggleMusic();
            }
        });

        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Projecte3.audioManager.setVolume(volumeSlider.getValue());

                // Guardar el volumen en las preferencias
                Preferences prefs = Gdx.app.getPreferences("MyPreferences");
                prefs.putFloat("volume", volumeSlider.getValue());
                prefs.flush();
            }
        });

        musicCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean isChecked = musicCheckBox.isChecked();
                Projecte3.audioManager.setMusicEnabled(isChecked);

                // Guardar el estado de la música en las preferencias
                Preferences prefs = Gdx.app.getPreferences("MyPreferences");
                prefs.putBoolean("musicEnabled", isChecked);
                prefs.flush();
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameModeScreen(game));
            }
        });

        Music music = Gdx.audio.newMusic(Gdx.files.internal("GameMode/lean.mp3"));
        Projecte3.audioManager.setMusic(music);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(AssetManager.menu_bg2, 0, 0, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
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
    }
}

