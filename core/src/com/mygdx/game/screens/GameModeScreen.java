package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.Projecte3;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.utils.Settings;

public class GameModeScreen implements Screen {

    private Projecte3 game;
    private Stage stage;
    private Batch batch;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    public GameModeScreen(Projecte3 game) {
        this.game = game;
        camera = new OrthographicCamera();
        mapRenderer = new OrthogonalTiledMapRenderer(AssetManager.tiledMap);
        camera.setToOrtho(false, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);

        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    @Override
    public void show() {

        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        batch = stage.getBatch();

        Table wrapperTable = new Table(); // Table para envolver los campos
        wrapperTable.setSize(700, 820); // Establece el tamaño deseado para la tabla
        wrapperTable.setPosition((Gdx.graphics.getWidth() - wrapperTable.getWidth()) / 2, (Gdx.graphics.getHeight() - wrapperTable.getHeight()) / 2); // Centra la tabla en la pantalla

        Texture backgroundTexture = new Texture(Gdx.files.internal("frame6.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        wrapperTable.setBackground(backgroundDrawable);

        //Crear table que tindra el contingut
        Table contentTable = new Table();
        contentTable.center();


        //----------------------------- Button Solo i Multi --------------------------------


        Texture myTextureSolo = new Texture(Gdx.files.internal("soloLogo6.png"));
        Drawable myTexRegionDrawable1 = new TextureRegionDrawable(new TextureRegion(myTextureSolo));

        Texture myTextureMulti = new Texture(Gdx.files.internal("multiLogo4.png"));
        Drawable myTexRegionDrawable2 = new TextureRegionDrawable(new TextureRegion(myTextureMulti));

        // Crear un Image con la imagen
        Image myImageSolo = new Image(myTexRegionDrawable1);
        Image myImageMulti = new Image(myTexRegionDrawable2);

        // Crear un contenedor para la imagen y añadir relleno
        Container<Image> imageContainer = new Container<Image>(myImageSolo);
        imageContainer.padLeft(35);

        Container<Image> imageContainer2 = new Container<Image>(myImageMulti);
        imageContainer2.padLeft(10);

        Skin skin = AssetManager.lava_skin;

        // Crear un TextButton con texto
        TextButton SoloButton = new TextButton("Inidividual", skin);
        TextButton MultiButton = new TextButton("Multijugador", skin);

        // Añadir la imagen (dentro del contenedor) como actor al botón
        SoloButton.add(imageContainer);
        MultiButton.add(imageContainer2);


        //----------------------------- Button Config --------------------------------

        // Cargar las imágenes para los estados normal y presionado del ImageButton
        Texture myButtonTexture = new Texture(Gdx.files.internal("ruedita.png"));
        Drawable myButtonTexRegionDrawable = new TextureRegionDrawable(new TextureRegion(myButtonTexture));
        Texture myButtonPressedTexture = new Texture(Gdx.files.internal("ruedita2.png"));
        Drawable myButtonPressedTexRegionDrawable = new TextureRegionDrawable(new TextureRegion(myButtonPressedTexture));

        // Crear un ButtonStyle y establecer los Drawable para los estados normal y presionado
        ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
        buttonStyle.imageUp = myButtonTexRegionDrawable;
        buttonStyle.imageDown = myButtonPressedTexRegionDrawable;

        // Crear un ImageButton con el ButtonStyle
        ImageButton myImageButton = new ImageButton(buttonStyle);

        //ClickListener
        myImageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Aquí va el código que se ejecutará cuando se haga clic en el ImageButton
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new OptionsScreen(game));
                    }
                });
            }
        });

        // Crear un contenedor para el ImageButton
        Container<ImageButton> imageButtonContainer = new Container<ImageButton>(myImageButton);
        imageButtonContainer.padBottom(300).padRight(230);
        imageButtonContainer.width(38);
        imageButtonContainer.height(38);


        //----------------------------- Button Perfil --------------------------------


// Cargar las imágenes para los estados normal y presionado del ImageButton
        Texture myButtonPerfilTexture = new Texture(Gdx.files.internal("perfilNou.png")); // Cambia "perfil.png" a la imagen que quieras usar
        Drawable myButtonPerfilTexRegionDrawable = new TextureRegionDrawable(new TextureRegion(myButtonPerfilTexture));
        Texture myButtonPerfilPressedTexture = new Texture(Gdx.files.internal("perfil2Nou.png")); // Cambia "perfil2.png" a la imagen que quieras usar cuando se presione el botón
        Drawable myButtonPerfilPressedTexRegionDrawable = new TextureRegionDrawable(new TextureRegion(myButtonPerfilPressedTexture));

// Crear un ButtonStyle y establecer los Drawable para los estados normal y presionado
        ImageButton.ImageButtonStyle buttonPerfilStyle = new ImageButton.ImageButtonStyle();
        buttonPerfilStyle.imageUp = myButtonPerfilTexRegionDrawable;
        buttonPerfilStyle.imageDown = myButtonPerfilPressedTexRegionDrawable;

// Crear un ImageButton con el ButtonStyle
        ImageButton myImageButtonPerfil = new ImageButton(buttonPerfilStyle);

//ClickListener
        myImageButtonPerfil.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Aquí va el código que se ejecutará cuando se haga clic en el ImageButton
                System.out.println("¡ImageButtonPerfil clickeado!");
            }
        });

        // Crear un contenedor para el ImageButton
        Container<ImageButton> imageButtonPerfilContainer = new Container<ImageButton>(myImageButtonPerfil);
        imageButtonPerfilContainer.padBottom(300).padLeft(460);
        imageButtonPerfilContainer.width(35);
        imageButtonPerfilContainer.height(35);


        //----------------------------- Button Tienda --------------------------------

        // Cargar las imágenes para los estados normal y presionado del ImageButton
        Texture myButtonTiendaTexture = new Texture(Gdx.files.internal("tiendaNou.png")); // Cambia "tienda.png" a la imagen que quieras usar
        Drawable myButtonTiendaTexRegionDrawable = new TextureRegionDrawable(new TextureRegion(myButtonTiendaTexture));
        Texture myButtonTiendaPressedTexture = new Texture(Gdx.files.internal("tienda2Nou.png")); // Cambia "tienda2.png" a la imagen que quieras usar cuando se presione el botón
        Drawable myButtonTiendaPressedTexRegionDrawable = new TextureRegionDrawable(new TextureRegion(myButtonTiendaPressedTexture));

        // Crear un ButtonStyle y establecer los Drawable para los estados normal y presionado
        ImageButton.ImageButtonStyle buttonTiendaStyle = new ImageButton.ImageButtonStyle();
        buttonTiendaStyle.imageUp = myButtonTiendaTexRegionDrawable;
        buttonTiendaStyle.imageDown = myButtonTiendaPressedTexRegionDrawable;

        // Crear un ImageButton con el ButtonStyle
        ImageButton myImageButtonTienda = new ImageButton(buttonTiendaStyle);

        //ClickListener
        myImageButtonTienda.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Aquí va el código que se ejecutará cuando se haga clic en el ImageButton
                System.out.println("¡ImageButtonTienda clickeado!");
            }
        });

        // Crear un contenedor para el ImageButton
        Container<ImageButton> imageButtonTiendaContainer = new Container<ImageButton>(myImageButtonTienda);
        imageButtonTiendaContainer.padBottom(300).padLeft(550);
        imageButtonTiendaContainer.width(35);
        imageButtonTiendaContainer.height(35);








        //-------------------------------------Botons al Table-------------------------------------
        contentTable.addActor(imageButtonTiendaContainer);
        contentTable.addActor(imageButtonPerfilContainer);
        contentTable.addActor(imageButtonContainer);
        contentTable.add(SoloButton).left().padBottom(10);
        contentTable.row();
        contentTable.add(MultiButton).left().padBottom(10);




        wrapperTable.add(contentTable); // Agrega el Table de contenido dentro del Table de envoltura
        stage.addActor(wrapperTable); // Agrega el Table de envoltura al Stage


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
