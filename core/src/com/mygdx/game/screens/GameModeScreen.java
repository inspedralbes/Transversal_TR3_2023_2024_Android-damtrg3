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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.Projecte3;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.utils.Settings;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class GameModeScreen implements Screen {
    private Projecte3 game;
    private Stage stage;
    private Batch batch;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    public GameModeScreen(Projecte3 game) {
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
        OkHttpClient client = new OkHttpClient();
        String url = "http://" + Settings.IP_SERVER + ":" + Settings.PUERTO_PETICIONES + "/ruta/datos";
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    // Aquí puedes procesar la respuesta del servidor
                    String responseData = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseData);
                        // Extraer los datos del JSON
                        Settings.PLAYER_SPEED = json.getInt("velocidadPersonaje");
                        Settings.PLAYER_DAMAGE_RECIEVED = (float)json.getDouble("danoPersonaje");
                        Settings.SPINLOG_ACCEL = Float.parseFloat(json.getString("aceleracionTronco").replace("f", ""));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        batch = stage.getBatch();

        Table wrapperTable = new Table(); // Table para envolver los campos
        wrapperTable.setSize(700, 820); // Establece el tamaño deseado para la tabla
        wrapperTable.setPosition((stage.getWidth() - wrapperTable.getWidth()) / 2,
                (stage.getHeight() - wrapperTable.getHeight()) / 2);

        Texture backgroundTexture = new Texture(Gdx.files.internal("frame6.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        wrapperTable.setBackground(backgroundDrawable);

        //Crear table que tindra el contingut
        Table contentTable = new Table();
        contentTable.center();


        //----------------------------- Button Solo i Multi --------------------------------


        Texture myTextureSolo = new Texture(Gdx.files.internal("GameMode/soloLogo6.png"));
        Drawable myTexRegionDrawable1 = new TextureRegionDrawable(new TextureRegion(myTextureSolo));

        Texture myTextureMulti = new Texture(Gdx.files.internal("GameMode/multiLogo4.png"));
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

        SoloButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Aquí va el código que se ejecutará cuando se haga clic en el TextButton
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new GameScreen(game, Projecte3.audioManager));
                    }
                });
            }
        });

        MultiButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Aquí va el código que se ejecutará cuando se haga clic en el TextButton
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new MenuSalasScreen(game));
                    }
                });
            }
        });

        // Añadir la imagen (dentro del contenedor) como actor al botón
        SoloButton.add(imageContainer);
        MultiButton.add(imageContainer2);


        //----------------------------- Button Config --------------------------------

        // Cargar las imágenes para los estados normal y presionado del ImageButton
        Texture myButtonTexture = new Texture(Gdx.files.internal("GameMode/ruedita.png"));
        Drawable myButtonTexRegionDrawable = new TextureRegionDrawable(new TextureRegion(myButtonTexture));
        Texture myButtonPressedTexture = new Texture(Gdx.files.internal("GameMode/ruedita2.png"));
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
        imageButtonContainer.width(43);
        imageButtonContainer.height(43);


        //----------------------------- Button Ranking --------------------------------

        // Cargar las imágenes para los estados normal y presionado del ImageButton
        Texture myButtonRankingTexture = new Texture(Gdx.files.internal("GameMode/ranking1.png"));
        Drawable myButtonRankingTexRegionDrawable = new TextureRegionDrawable(new TextureRegion(myButtonRankingTexture));
        Texture myButtonRankingPressedTexture = new Texture(Gdx.files.internal("GameMode/ranking2.png"));
        Drawable myButtonRankingPressedTexRegionDrawable = new TextureRegionDrawable(new TextureRegion(myButtonRankingPressedTexture));

        // Crear un ButtonStyle y establecer los Drawable para los estados normal y presionado
        ImageButton.ImageButtonStyle buttonRankingStyle = new ImageButton.ImageButtonStyle();
        buttonRankingStyle.imageUp = myButtonRankingTexRegionDrawable;
        buttonRankingStyle.imageDown = myButtonRankingPressedTexRegionDrawable;

        // Crear un ImageButton con el ButtonStyle
        ImageButton myImageButtonRanking = new ImageButton(buttonRankingStyle);

        //ClickListener
        myImageButtonRanking.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Aquí va el código que se ejecutará cuando se haga clic en el ImageButton
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new RankingSreen(game));
                    }
                });
            }
        });

        // Crear un contenedor para el ImageButton
        Container<ImageButton> imageButtonRankingContainer = new Container<ImageButton>(myImageButtonRanking);
        imageButtonRankingContainer.padBottom(300).padLeft(460);
        imageButtonRankingContainer.width(40);
        imageButtonRankingContainer.height(40);


        //----------------------------- Button Perfil --------------------------------


        // Cargar las imágenes para los estados normal y presionado del ImageButton
        Texture myButtonPerfilTexture = new Texture(Gdx.files.internal("GameMode/perfilNou2.png")); // Cambia "perfil.png" a la imagen que quieras usar
        Drawable myButtonPerfilTexRegionDrawable = new TextureRegionDrawable(new TextureRegion(myButtonPerfilTexture));
        Texture myButtonPerfilPressedTexture = new Texture(Gdx.files.internal("GameMode/perfil4Nou.png")); // Cambia "perfil2.png" a la imagen que quieras usar cuando se presione el botón
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
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new PerfilScreen(game));
                    }
                });
            }
        });

        // Crear un contenedor para el ImageButton
        Container<ImageButton> imageButtonPerfilContainer = new Container<ImageButton>(myImageButtonPerfil);
        imageButtonPerfilContainer.padBottom(300).padRight(130);
        imageButtonPerfilContainer.width(40);
        imageButtonPerfilContainer.height(40);


        //----------------------------- Button Tienda --------------------------------

        // Cargar las imágenes para los estados normal y presionado del ImageButton
        Texture myButtonTiendaTexture = new Texture(Gdx.files.internal("GameMode/tienda3.png")); // Cambia "tienda.png" a la imagen que quieras usar
        Drawable myButtonTiendaTexRegionDrawable = new TextureRegionDrawable(new TextureRegion(myButtonTiendaTexture));
        Texture myButtonTiendaPressedTexture = new Texture(Gdx.files.internal("GameMode/tienda3Nou.png")); // Cambia "tienda2.png" a la imagen que quieras usar cuando se presione el botón
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
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new TiendaScreen(game));
                    }
                });
            }
        });

        // Crear un contenedor para el ImageButton
        Container<ImageButton> imageButtonTiendaContainer = new Container<ImageButton>(myImageButtonTienda);
        imageButtonTiendaContainer.padBottom(300).padLeft(550);
        imageButtonTiendaContainer.width(40);
        imageButtonTiendaContainer.height(40);








        //-------------------------------------Botons al Table-------------------------------------
        contentTable.addActor(imageButtonTiendaContainer);
        contentTable.addActor(imageButtonPerfilContainer);
        contentTable.addActor(imageButtonContainer);
        contentTable.addActor(imageButtonRankingContainer);
        contentTable.add(SoloButton).left().padBottom(10);
        contentTable.row();
        contentTable.add(MultiButton).left().padBottom(10);
        contentTable.row();




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
        Projecte3.audioManager.setMusicEnabled(false);
    }

    @Override
    public void dispose() {

    }
}
