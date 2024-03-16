package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.Projecte3;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.utils.Settings;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class MenuSalasScreen implements Screen {

    private Projecte3 game;
    private Stage stage;
    private Batch batch;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;

    //Text
    private Label crearSalaLabel;
    private Label unirSalaLabel;


    //Text Fields
    private TextField codiSalaField;


    //Buttons
    private TextButton crearSalaBtn;
    private TextButton unirSalaBtn;

    public static Socket socket;


    public MenuSalasScreen(Projecte3 game) {
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
        wrapperTable.setSize(500, 680); // Establece el tamaño deseado para la tabla
        wrapperTable.setPosition((Gdx.graphics.getWidth() - wrapperTable.getWidth()) / 2, (Gdx.graphics.getHeight() - wrapperTable.getHeight()) / 2); // Centra la tabla en la pantalla

        Texture backgroundTexture = new Texture(Gdx.files.internal("frame6.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        wrapperTable.setBackground(backgroundDrawable);

        //Crear table que tindra el contingut
        Table contentTable = new Table();
        contentTable.center();

        //Assignar elements (Labels, btn, textField)
        //Labels
        crearSalaLabel = new Label("Sales", AssetManager.lava_skin);
        //Btns
        crearSalaBtn = new TextButton("Crear sala", AssetManager.lava_skin);
        unirSalaBtn = new TextButton("Unir-se sala", AssetManager.lava_skin);
        codiSalaField = new TextField("", AssetManager.lava_skin);
        //TextField

        //Afegir contingut a la table
        contentTable.add(crearSalaLabel).padBottom(10).padLeft(100);
        contentTable.row();
        contentTable.add(crearSalaBtn).padBottom(100).colspan(2);
        contentTable.add(unirSalaBtn).padBottom(100).padRight(40).colspan(2);

        crearSalaBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                crearNovaSala();
            }
        });

        wrapperTable.add(contentTable); // Agrega el Table de contenido dentro del Table de envoltura
        stage.addActor(wrapperTable); // Agrega el Table de envoltura al Stage

        // Crear una tabla para contener los elementos del pop-up
        Table popupTable = new Table();
        popupTable.setSize(300, 240);
        popupTable.setVisible(false); // Inicialmente, hacer que la tabla no sea visible

        Label codiLabel = new Label("Codi:", AssetManager.lava_skin);
        TextField codiSalaField = new TextField("", AssetManager.lava_skin);
        popupTable.add(codiLabel);
        popupTable.add(codiSalaField);

        // Crear un botón "Accedir" para el pop-up
        TextButton accedirButton = new TextButton("Accedir", AssetManager.lava_skin);
        popupTable.row(); // Crea una nueva fila en la tabla
        popupTable.add(accedirButton).colspan(2); // Añade el botón a la nueva fila

        // Establecer la posición del pop-up en el centro de la pantalla
        popupTable.setPosition((Gdx.graphics.getWidth() - popupTable.getWidth()) / 2, (float) ((Gdx.graphics.getHeight() - popupTable.getHeight()) / 2.5));

        Texture popupBackgroundTexture = new Texture(Gdx.files.internal("frame6.png"));
        TextureRegionDrawable popupBackgroundDrawable = new TextureRegionDrawable(new TextureRegion(popupBackgroundTexture));
        popupTable.setBackground(popupBackgroundDrawable);

        unirSalaBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                popupTable.setVisible(!popupTable.isVisible());
            }
        });

        accedirButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                unirseASala(codiSalaField.getText());
            }
        });

        stage.addActor(popupTable); // Añadir la tabla del pop-up al escenario después de la tabla de envoltura


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


    public void crearNovaSala() {
        JSONObject roomJSON = new JSONObject();
        String salaId = "123ABC";
        roomJSON.put("idSala", salaId);
        game.SalaActual = salaId;
        roomJSON.put("creadorSala", game.nomUsuari);
        roomJSON.put("estatSala", "En espera");

        JSONArray jugadores = new JSONArray();
        jugadores.put(game.nomUsuari);

        roomJSON.put("jugadores", jugadores);

        Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
        httpRequest.setUrl("http://" + Settings.IP_SERVER + ":" + Settings.PUERTO_PETICIONES + "/crearSala");
        String data = roomJSON.toString();
        httpRequest.setContent(data);
        httpRequest.setHeader("Content-Type", "application/json");

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Sala creada!");
                        try {
                            connectToSocketServer();
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                        game.setScreen(new SalasScreen(game));
                    }
                });
            }
            @Override
            public void failed(Throwable t) {
                System.out.println("Error al crear sala: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                System.out.println("Creació de sala cancelada");
            }
        });
    }

    public void unirseASala(final String idSala) {
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("idSala", idSala);
            Gdx.app.error("Usuari", game.nomUsuari);
            requestData.put("nomUsuari", game.nomUsuari);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.POST);
        httpRequest.setUrl("http://" + Settings.IP_SERVER + ":" + Settings.PUERTO_PETICIONES + "/unirSala");
        httpRequest.setContent(requestData.toString());
        httpRequest.setHeader("Content-Type", "application/json");

        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("Te has unido a la sala!");

                        //Connexió al servidor de Socket.IO
                        try {
                            connectToSocketServer();
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }

                        socket.emit("unirSala", idSala);
                        game.setScreen(new SalasScreen(game));
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                System.out.println("Error al unirse a la sala: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                System.out.println("Unión a la sala cancelada");
            }
        });
    }

    private void connectToSocketServer() throws URISyntaxException {
        socket = IO.socket("http://" + Settings.IP_SERVER + ":" + Settings.PUERTO_PETICIONES);
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("Conectado al servidor de Socket.IO");
            }
        });

        // Conectar al servidor
        socket.connect();
    }
}