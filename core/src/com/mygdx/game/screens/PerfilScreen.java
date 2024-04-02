package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.Projecte3;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.utils.Settings;

public class PerfilScreen implements Screen {
    private Projecte3 game;
    private Stage stage;
    private TextButton backButton;

    private TextButton guardarCanvisButton;
    private Batch batch;

    private ImageButton.ImageButtonStyle[] normalStyle, selectedStyle;

    private ImageButton selectedImageButton = null;

    public PerfilScreen(Projecte3 game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        setupButtonStyles();

        Table wrapperTable = new Table();
        wrapperTable.setSize(750, 880);
        wrapperTable.setPosition((Gdx.graphics.getWidth() - wrapperTable.getWidth()) / 2, (Gdx.graphics.getHeight() - wrapperTable.getHeight()) / 2);
        Texture backgroundTexture = new Texture(Gdx.files.internal("frame6.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        wrapperTable.setBackground(backgroundDrawable);

        Table contentTable = new Table();
        contentTable.pad(20);

        Table recyclerView = new Table();
        int numberOfRows = 2;
        for (int i = 0; i < numberOfRows; i++) {
            ImageButton[] imageButtons = new ImageButton[3];
            for (int j = 0; j < 3; j++) {
                final int index = j;
                ImageButton imageButton = new ImageButton(normalStyle[j]);
                imageButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (selectedImageButton != null) {
                            for (int k = 0; k < normalStyle.length; k++) {
                                if (selectedImageButton.getStyle().equals(selectedStyle[k])) {
                                    selectedImageButton.setStyle(normalStyle[k]);
                                    break;
                                }
                            }
                        }
                        selectedImageButton = imageButton;
                        for (int k = 0; k < imageButtons.length; k++) {
                            if (k == index) {
                                imageButtons[k].setStyle(selectedStyle[k]);
                            } else {
                                imageButtons[k].setStyle(normalStyle[k]);
                            }
                        }
                    }
                });
                imageButtons[j] = imageButton;
                recyclerView.add(imageButton).width(80).height(80).pad(10);
            }
            recyclerView.row();
        }

        // Crear el ScrollPane
        ScrollPane scrollPane = new ScrollPane(recyclerView);
        scrollPane.setFlickScroll(true);
        scrollPane.setScrollingDisabled(true, false); // Deshabilitar el desplazamiento horizontal

        // Limitar el alto del ScrollPane para mostrar solo dos filas
        scrollPane.setHeight(2 * 80 + 2 * 10); // Altura de dos filas (80 es la altura de un botón y 10 es el padding)

// Agregar el ScrollPane a la tabla principal
        contentTable.add(scrollPane).colspan(3).padBottom(10).width(750).height(2 * 80 + 2 * 14);

        TextButton backButton = new TextButton("Enrere", AssetManager.lava_skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameModeScreen(game));
            }
        });

        TextButton guardarCanvisButton = new TextButton("Guardar Canvis", AssetManager.lava_skin);
        guardarCanvisButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });

        contentTable.row();
        contentTable.add(backButton);
        contentTable.add(guardarCanvisButton).colspan(2);

        wrapperTable.add(contentTable).center();
        stage.addActor(wrapperTable);
    }

    private void setupButtonStyles() {
        normalStyle = new ImageButton.ImageButtonStyle[3];
        selectedStyle = new ImageButton.ImageButtonStyle[3];

        String[] imageFiles = {"GameMode/SoloLogo6.png", "GameMode/soloDos.png", "GameMode/soloTres.png"};

        for (int i = 0; i < 3; i++) {
            normalStyle[i] = new ImageButton.ImageButtonStyle();
            normalStyle[i].up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(imageFiles[i]))));

            selectedStyle[i] = new ImageButton.ImageButtonStyle();
            selectedStyle[i].up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(imageFiles[i]))));
            selectedStyle[i].imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("Perfil/Cuadre.png"))));
        }
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
