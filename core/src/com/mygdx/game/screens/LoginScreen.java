package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.game.Projecte3;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.utils.ApiService;
import com.mygdx.game.utils.Resposta;
import com.mygdx.game.utils.Settings;
import com.mygdx.game.utils.UsuariLocalitzat;

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
    private static final String URL = "http://localhost:3327/";

    public LoginScreen(Projecte3 game) {
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
        Table table = new Table();
        table.setFillParent(true);


        stage.addActor(table);
        Label correuLabel = new Label("Correu:", AssetManager.neon_skin);
        correuField = new TextField("", AssetManager.neon_skin);
        Label contrasenyaLabel = new Label("Contrasenya:", AssetManager.neon_skin);
        contrasenyaField = new TextField("", AssetManager.neon_skin);
        contrasenyaField.setPasswordMode(true);
        contrasenyaField.setPasswordCharacter('*');
        loginButton = new TextButton("Login", AssetManager.neon_skin);
        registrarButton = new TextButton("Register", AssetManager.neon_skin);
        Label registraLabel = new Label("No tens compte?", AssetManager.neon_skin);


        loginButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String correu = correuField.getText().toString().trim();
                String contrasenya = contrasenyaField.getText().toString().trim();
                if (correuField.getText().isEmpty() || contrasenyaField.getText().isEmpty()) {
                    showToastMessage("Falten camps per omplir");
                } else {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    apiService = retrofit.create(ApiService.class);
                    UsuariLocalitzat usuariTrobat = new UsuariLocalitzat( correu, contrasenya);

                    Call<Resposta> call = apiService.EnviarUsuari(usuariTrobat);

                    call.enqueue(new Callback<Resposta>() {
                        @Override
                        public void onResponse(Call<Resposta> call, Response<Resposta> response) {
                            if (response.isSuccessful()) {
                                Gdx.app.error("CONEXION", "CONEXION SERVIDOR CONECTADO");
                                Resposta r = response.body();
                                System.out.println(r.isAutoritzacio());
                                if (r.isAutoritzacio()) {
                                    Gdx.app.postRunnable(new Runnable() {
                                        @Override
                                        public void run() {
                                            game.setScreen(new GameScreen(game));
                                        }
                                    });
                                }
                            } else {
                                Gdx.app.error("ERROR", "Gorda");
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
        table.add(correuLabel);
        table.add(correuField).fillX().uniformX();
        table.row();
        table.add(contrasenyaLabel);
        table.add(contrasenyaField).fillX().uniformX();
        table.row();
        table.add(loginButton).fillX().uniformX();
        table.row();
        table.add(registraLabel);
        table.row();
        table.add(registrarButton).fillX().uniformX();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(AssetManager.menu_bg, 0, 0, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
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
