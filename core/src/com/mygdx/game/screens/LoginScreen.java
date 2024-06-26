package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.Projecte3;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.utils.ApiService;
import com.mygdx.game.utils.Resposta;
import com.mygdx.game.utils.Settings;
import com.mygdx.game.utils.UsuariLocalitzat;

import java.awt.Menu;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginScreen implements Screen {
    private Projecte3 game;
    private Stage stage;
    private TextField correuField;
    private TextField contrasenyaField;
    private TextButton registrarButton;
    private TextButton loginButton;
    private Batch batch;

    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private ApiService apiService;
    private static final String URL = "http://" + Settings.IP_SERVER + ":" + Settings.PUERTO_PETICIONES + "/";

    public LoginScreen(Projecte3 game) {
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
        Gdx.input.setInputProcessor(stage);

        batch = stage.getBatch();

        Table wrapperTable = new Table(); // Table para envolver los campos
        wrapperTable.setSize(500, 600); // Establece el tamaño deseado para la tabla
        wrapperTable.setPosition((stage.getWidth() - wrapperTable.getWidth()) / 2,
                (stage.getHeight() - wrapperTable.getHeight()) / 2);

        Texture backgroundTexture = new Texture(Gdx.files.internal("frame6.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        wrapperTable.setBackground(backgroundDrawable);

        Table contentTable = new Table(); // Table para los campos de entrada y botones
        contentTable.pad(20); // Agrega un relleno de 20 píxeles alrededor del contenido

        Label correuLabel = new Label("Usuari:", AssetManager.lava_skin);
        correuField = new TextField("", AssetManager.lava_skin);
        Label contrasenyaLabel = new Label("Contrasenya:", AssetManager.lava_skin);
        contrasenyaField = new TextField("", AssetManager.lava_skin);
        contrasenyaField.setPasswordMode(true);
        contrasenyaField.setPasswordCharacter('*');
        loginButton = new TextButton("Login", AssetManager.lava_skin);
        registrarButton = new TextButton("Register", AssetManager.lava_skin);
        Label registraLabel = new Label("No tens compte?", AssetManager.lava_skin);

        contentTable.add(correuLabel).left().padBottom(10); // Alinea la etiqueta a la izquierda y agrega un espacio en la parte inferior
        contentTable.add(correuField).fillX().uniformX().padBottom(10); // Llena horizontalmente y uniformemente, agrega un espacio en la parte inferior
        contentTable.row();
        contentTable.add(contrasenyaLabel).left().padBottom(10);
        contentTable.add(contrasenyaField).fillX().uniformX().padBottom(10);
        contentTable.row();
        contentTable.add(loginButton).fillX().uniformX().padBottom(10);
        contentTable.row();
        contentTable.add(registraLabel).padBottom(10);
        contentTable.add(registrarButton).fillX().uniformX().right().padBottom(10); // Alinea el botón de registro a la derecha

        wrapperTable.add(contentTable); // Agrega el Table de contenido dentro del Table de envoltura

        stage.addActor(wrapperTable); // Agrega el Table de envoltura al Stage



        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String nomUsuari = correuField.getText().toString().trim();
                String contrasenya = contrasenyaField.getText().toString().trim();
                if (correuField.getText().isEmpty() || contrasenyaField.getText().isEmpty()) {
                    showToastMessage("Falten camps per omplir");
                } else {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    apiService = retrofit.create(ApiService.class);
                    UsuariLocalitzat usuariTrobat = new UsuariLocalitzat(nomUsuari, contrasenya);

                    Call<Resposta> call = apiService.EnviarUsuari(usuariTrobat);

                    call.enqueue(new Callback<Resposta>() {
                        @Override
                        public void onResponse(Call<Resposta> call, Response<Resposta> response) {
                            if (response.isSuccessful()) {
                                Gdx.app.error("CONEXION", "CONEXION SERVIDOR CONECTADO");
                                Resposta r = response.body();
                                System.out.println(r.isAutoritzacio());
                                if (r.isAutoritzacio()) {
                                    Gdx.app.error("nomUsuari", nomUsuari);
                                    game.nomUsuari = nomUsuari;
                                    Gdx.app.postRunnable(new Runnable() {
                                        @Override
                                        public void run() {
                                            game.setScreen(new GameModeScreen(game));
                                        }
                                    });
                                } else {
                                    Gdx.app.postRunnable(new Runnable() {
                                        @Override
                                        public void run() {
                                            showToastMessage("Error: Datos incorrectos. Por favor, inténtalo de nuevo.");
                                        }
                                    });
                                }
                            } else {
                                Gdx.app.postRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        showToastMessage("Error: No se pudo iniciar sesión. Por favor, inténtalo de nuevo.");
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<Resposta> call, Throwable t) {
                            Gdx.app.error("error", "onFailure: " + t.getMessage());
                        }
                    });
                }
            }
        });

        registrarButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new RegisterScreen(game));
                    }
                });
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
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();

    }
    public void showToastMessage(String message) {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont();
        style.fontColor = Color.WHITE;

        Label label = new Label(message, style);
        label.setPosition(Gdx.graphics.getWidth() / 2 - label.getWidth() / 2, Gdx.graphics.getHeight() / 2 - label.getHeight() / 2);
        label.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.5f), Actions.delay(2), Actions.fadeOut(0.5f), Actions.removeActor()));

        stage.addActor(label);
    }
}
