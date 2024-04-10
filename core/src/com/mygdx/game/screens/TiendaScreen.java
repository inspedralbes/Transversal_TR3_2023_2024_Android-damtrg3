package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.Projecte3;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.utils.Settings;

public class TiendaScreen implements Screen {
    private Projecte3 game;
    private Stage stage;
    private TextButton backButton;

    private TextButton guardarCanvisButton;
    private Batch batch;

    public TiendaScreen(Projecte3 game) {
        this.game = game;
    }

    @Override
    public void show() {
        // Crear un nuevo SpriteBatch
        batch = new SpriteBatch();

        stage = new Stage(); // Initialize stage first
        Gdx.input.setInputProcessor(stage); // Set InputProcessor after stage initialization

        Table wrapperTable = new Table(); // Table para envolver los campos
        wrapperTable.setSize(750, 880); // Establece el tamaño deseado para la tabla
        wrapperTable.setPosition((Gdx.graphics.getWidth() - wrapperTable.getWidth()) / 2, (Gdx.graphics.getHeight() - wrapperTable.getHeight()) / 2); // Centra la tabla en la pantalla

        Texture backgroundTexture = new Texture(Gdx.files.internal("frame6.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        wrapperTable.setBackground(backgroundDrawable);

        Table contentTable = new Table(); // Table para los campos de entrada y botones
        contentTable.pad(20); // Agrega un relleno de 20 píxeles alrededor del contenido



        //----------------------------- Button Enrere --------------------------------

        backButton = new TextButton("Enrere", AssetManager.lava_skin);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                OptionsScreen.AudioManager audioManager = new OptionsScreen.AudioManager();
                game.setScreen(new GameModeScreen(game, audioManager));
            }
        });

        //----------------------------- Button Guardar Canvis --------------------------------

        //Elements al table
        contentTable.add(backButton).pad(10);


        wrapperTable.add(contentTable).center(); // Agrega el Table de contenido dentro del Table de envoltura

        stage.addActor(wrapperTable); // Agrega el Table de envoltura al Stage

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

    }

    @Override
    public void dispose() {

    }
}