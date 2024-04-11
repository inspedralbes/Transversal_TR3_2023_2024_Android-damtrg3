package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
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
import com.badlogic.gdx.utils.viewport.StretchViewport;
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

    private OrthographicCamera camera;

    private OrthogonalTiledMapRenderer mapRenderer;
    public class AudioManager {
        private Music music;
        private boolean isMusicEnabled = true;


        public void toggleMusic() {
            if (isMusicEnabled) {
                music.pause();
            } else {
                music.play();
            }
            isMusicEnabled = !isMusicEnabled;
        }

        public void setVolume(float volume) {
            music.setVolume(volume);
        }

        public void setMusicEnabled(boolean isEnabled) {
            isMusicEnabled = isEnabled;
            if (isMusicEnabled) {
                music.play();
            } else {
                music.pause();
            }
        }

        // Asegúrate de llamar a este método cuando cargues tu música
        public void setMusic(Music music) {
            this.music = music;
        }
    }

    private AudioManager audioManager = new AudioManager();
    public OptionsScreen(Projecte3 game) {
        this.game = game;
        camera = new OrthographicCamera(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
        camera.setToOrtho(false);

        StretchViewport viewport = new StretchViewport(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT, camera);
        mapRenderer = new OrthogonalTiledMapRenderer(AssetManager.tiledMap);
        stage = new Stage(viewport);
        camera.setToOrtho(false, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);

        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    @Override
    public void show() {
        // Crear un nuevo SpriteBatch
        batch = new SpriteBatch();

        Gdx.input.setInputProcessor(stage); // Set InputProcessor after stage initialization

        Table wrapperTable = new Table(); // Table para envolver los campos
        wrapperTable.setSize(500, 600); // Establece el tamaño deseado para la tabla
        wrapperTable.setPosition((stage.getWidth() - wrapperTable.getWidth()) / 2,
                (stage.getHeight() - wrapperTable.getHeight()) / 2); // Centra la tabla en la pantalla

        Texture backgroundTexture = new Texture(Gdx.files.internal("frame6.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        wrapperTable.setBackground(backgroundDrawable);

        Table contentTable = new Table(); // Table para los campos de entrada y botones
        contentTable.center();

        toggleMusicButton = new TextButton("Toggle Music", AssetManager.lava_skin);
        volumeSlider = new Slider(0, 1, 0.01f, false, AssetManager.lava_skin);
        backButton = new TextButton("Back", AssetManager.lava_skin);
        musicCheckBox = new CheckBox("Music On/Off", AssetManager.lava_skin);

        // Agrega los actores al contentTable
        contentTable.add(toggleMusicButton).pad(10);
        contentTable.row();
        contentTable.add(volumeSlider).pad(10);
        contentTable.row();
        contentTable.add(musicCheckBox).pad(10);
        contentTable.row();
        contentTable.add(backButton).pad(10);


        wrapperTable.add(contentTable).center(); // Agrega el Table de contenido dentro del Table de envoltura

        stage.addActor(wrapperTable); // Agrega el Table de envoltura al Stage
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
                // Implementación de la lógica de activar/desactivar música
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

        Music music = Gdx.audio.newMusic(Gdx.files.internal("GameMode/musica.mp3"));
        Projecte3.audioManager.setMusic(music);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(AssetManager.menu_bg2, 0, 0, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
        batch.end();

        stage.draw();
        stage.act(delta);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);

        // Actualizar el tamaño del fondo para que se ajuste al nuevo tamaño de la pantalla
        batch.setProjectionMatrix(camera.combined);
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
