package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

public class PerfilScreen implements Screen {
    private Projecte3 game;
    private Stage stage;
    private TextButton backButton;

    private TextButton guardarCanvisButton;
    private Batch batch;

    private ImageButton.ImageButtonStyle[] normalStyle, selectedStyle;

    private ImageButton.ImageButtonStyle lockedStyle;

    private ImageButton selectedImageButton = null;


    private List<Integer> productIds = new ArrayList<>();

    private boolean inventarioCargado = false;

    public PerfilScreen(Projecte3 game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        setupButtonStyles();
        getInventari(game.nomUsuari);
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

    public void getInventari(final String nomUsuari) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("nomUsuari", nomUsuari);
            Gdx.app.error("Usuari", game.nomUsuari);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
        httpRequest.setUrl("http://" + Settings.IP_SERVER + ":" + Settings.PUERTO_PETICIONES + "/getInventari");
        httpRequest.setContent(requestData.toString());
        httpRequest.setHeader("Content-Type", "application/json");

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                final String responseResult = httpResponse.getResultAsString();
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONArray jsonArray = new JSONArray(responseResult);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int productId = jsonObject.getInt("product_id");
                                productIds.add(productId);
                            }
                            inventarioCargado = true;
                            setupRecyclerView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                System.out.println("Error al recibir la información del inventario: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                System.out.println("Operación Cancelada");
            }
        });
    }

    private void setupRecyclerView() {
        if (!inventarioCargado) {
            return;
        }

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
                final int index = i * 3 + j;
                ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle(normalStyle[index]);
                ImageButton imageButton = new ImageButton(style);
                Stack stack = new Stack();

                stack.add(imageButton);

                if (!(i == 0 && j < 2) && !productIds.contains(index + 1)) {
                    ImageButton lockButton = new ImageButton(new ImageButton.ImageButtonStyle(lockedStyle));
                    lockButton.setDisabled(true);
                    stack.add(lockButton);
                } else {
                    // Agregar el comportamiento de selección solo si el ítem está desbloqueado
                    imageButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            // Lógica de selección del botón
                            if (selectedImageButton != null) {
                                selectedImageButton.getStyle().imageUp = null;
                            }
                            selectedImageButton = imageButton;
                            imageButton.getStyle().imageUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("Perfil/Cuadre.png"))));
                            game.Skin = index + 1;
                            cargarTexturasPersonaje();
                            System.out.println(game.cat_spritesheet);
                            System.out.println(game.Skin);
                            AssetManager.load();
                        }
                    });
                }

                recyclerView.add(stack).width(80).height(80).pad(10);
            }
            recyclerView.row();
        }

        ScrollPane scrollPane = new ScrollPane(recyclerView);
        scrollPane.setFlickScroll(true);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setHeight(2 * 80 + 2 * 10);

        contentTable.add(scrollPane).colspan(3).width(750).height(2 * 80 + 2 * 14).padBottom(20);

        TextButton backButton = new TextButton("<-", AssetManager.lava_skin);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                OptionsScreen.AudioManager audioManager = new OptionsScreen.AudioManager();
                game.setScreen(new GameModeScreen(game, audioManager));
            }
        });

        contentTable.add(backButton).colspan(2).padRight(400);

        wrapperTable.add(contentTable).center();
        stage.addActor(wrapperTable);
    }

    private void cargarTexturasPersonaje() {
        switch (game.Skin) {
            case 1:
                game.cat_spritesheet = new Texture(Gdx.files.internal("characters/Sprite2.png"));
                break;
            case 2:
                game.cat_spritesheet = new Texture(Gdx.files.internal("characters/Sprite1.png"));
                break;
            case 3:
                game.cat_spritesheet = new Texture(Gdx.files.internal("characters/Sprite3.png"));
                break;
            case 4:
                game.cat_spritesheet = new Texture(Gdx.files.internal("characters/Sprite4.png"));
                break;
            case 5:
                game.cat_spritesheet = new Texture(Gdx.files.internal("characters/Sprite5.png"));
                break;
            case 6:
                game.cat_spritesheet = new Texture(Gdx.files.internal("characters/Sprite6.png"));
                break;
            default:
                game.cat_spritesheet = new Texture(Gdx.files.internal("/Sprite2.png"));
                break;
        }

        // Luego, carga las texturas de animación como lo haces actualmente
        // ...
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
