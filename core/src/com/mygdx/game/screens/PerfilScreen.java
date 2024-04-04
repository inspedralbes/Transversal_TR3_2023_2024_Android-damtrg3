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
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
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

    private ImageButton.ImageButtonStyle lockedStyle;

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
            for (int j = 0; j < 3; j++) {
                final int index = i * 3 + j; // Cambiar a esto para usar las imágenes correctas
                ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(normalStyle[index]); // Usar 'index' en lugar de 'j'
                ImageButton imageButton = new ImageButton(style);
                Stack stack = new Stack(); // Crear un Stack para superponer las imágenes

                if (i == 0 && j < 2) {
                    stack.add(imageButton); // Agregar la skin al Stack
                } else {
                    ImageButton lockButton = new ImageButton(new ImageButton.ImageButtonStyle(lockedStyle)); // Crear un botón con la imagen del candado
                    lockButton.setDisabled(true); // Deshabilitar el botón del candado

                    stack.add(imageButton); // Agregar la skin al Stack
                    stack.add(lockButton); // Agregar el candado encima de la skin
                }

                imageButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (selectedImageButton != null) {
                            selectedImageButton.getStyle().imageUp = null;
                        }
                        selectedImageButton = imageButton;
                        imageButton.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("Perfil/Cuadre.png"))));
                    }
                });

                recyclerView.add(stack).width(80).height(80).pad(10); // Agregar el Stack al RecyclerView en lugar del ImageButton
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
        contentTable.add(scrollPane).colspan(3).width(750).height(2 * 80 + 2 * 14).padBottom(20);

        TextButton backButton = new TextButton("<-", AssetManager.lava_skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameModeScreen(game));
            }
        });

        TextButton guardarCanvisButton = new TextButton("Guardar", AssetManager.lava_skin);
        guardarCanvisButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            }
        });


        contentTable.add(backButton).colspan(2).padRight(400);
        //contentTable.row();
        //contentTable.add(guardarCanvisButton).padLeft(500);


        wrapperTable.add(contentTable).center();
        stage.addActor(wrapperTable);
    }

    private void setupButtonStyles() {
        normalStyle = new ImageButton.ImageButtonStyle[6]; // Cambiar a 6 para incluir todas las imágenes
        selectedStyle = new ImageButton.ImageButtonStyle[6]; // Cambiar a 6 para incluir todas las imágenes
        lockedStyle = new ImageButton.ImageButtonStyle(); // Este es el nuevo estilo para las skins bloqueadas

        String[] imageFiles = {"GameMode/SoloLogo6.png", "GameMode/soloDos.png", "GameMode/soloTres.png", "GameMode/personaje4.png", "GameMode/personaje5.png", "GameMode/personaje6.png"};

        for (int i = 0; i < 6; i++) { // Cambiar a 6 para incluir todas las imágenes
            normalStyle[i] = new ImageButton.ImageButtonStyle();
            normalStyle[i].up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(imageFiles[i]))));

            selectedStyle[i] = new ImageButton.ImageButtonStyle();
            selectedStyle[i].up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(imageFiles[i]))));
            selectedStyle[i].imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("Perfil/Cuadre.png"))));
        }

        lockedStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("Perfil/candoado6.png"))));
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
