package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mygdx.game.Projecte3;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.utils.Settings;

public class RankingSreen implements Screen {
    private Projecte3 game;
    private Stage stage;
    private TextButton backButton;
    private Batch batch;
    public RankingSreen(Projecte3 game) {
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

        backButton = new TextButton("Back", AssetManager.lava_skin);

        // Crear un nuevo Table que actuará como nuestro RecyclerView.
        Table recyclerView = new Table();

        // Añadir algunos elementos de texto al recyclerView.
        for (int i = 0; i < 20; i++) {
            Label label = new Label("Texto " + (i + 1), AssetManager.lava_skin);
            recyclerView.add(label).pad(10);
            recyclerView.row(); // Esto crea una nueva fila, por lo que cada texto estará en su propia línea.
        }

        // Crear un ScrollPane que contenga nuestro recyclerView.
        ScrollPane scrollPane = new ScrollPane(recyclerView);
        scrollPane.setScrollingDisabled(true, false); // Deshabilita el desplazamiento horizontal
        scrollPane.setFadeScrollBars(false); // Deshabilita el desvanecimiento de las barras de desplazamiento
        scrollPane.setHeight(200); // Establece la altura del ScrollPane

        // Añadir el ScrollPane a tu contentTable.
        contentTable.add(scrollPane).expandX().fillX().height(200).left().padRight(180); // Establece la altura del contenedor del ScrollPane, alinea a la izquierda y añade espacio a la derecha

        // Agrega los actores al contentTable
        contentTable.add(backButton).pad(10).right().padRight(130); // Alinea el botón de retroceso a la derecha y añade espacio a la izquierda
        contentTable.row();

        wrapperTable.add(contentTable).expand().fill(); // Agrega el Table de contenido dentro del Table de envoltura

        stage.addActor(wrapperTable); // Agrega el Table de envoltura al Stage

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameModeScreen(game));
            }
        });
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
