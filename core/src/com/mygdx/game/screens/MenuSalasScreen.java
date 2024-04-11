package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
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
import com.mygdx.game.utils.Settings;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Random;

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
        camera = new OrthographicCamera(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);
        camera.setToOrtho(false);

        StretchViewport viewport = new StretchViewport(Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT, camera);
        stage = new Stage(viewport);
        camera.setToOrtho(false, Settings.SCREEN_WIDTH, Settings.SCREEN_HEIGHT);

        try {
            connectToSocketServer();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        //mapRenderer.setView(camera);
        //mapRenderer.render();
    }
    @Override
    public void show() {

        Gdx.input.setInputProcessor(stage);

        batch = stage.getBatch();

        Table wrapperTable = new Table(); // Table para envolver los campos
        wrapperTable.setSize(500, 680); // Establece el tamaño deseado para la tabla
        wrapperTable.setPosition((stage.getWidth() - wrapperTable.getWidth()) / 2,
                (stage.getHeight() - wrapperTable.getHeight()) / 2);

        Texture backgroundTexture = new Texture(Gdx.files.internal("frame6.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        wrapperTable.setBackground(backgroundDrawable);


        // Load the image for the button
        Texture myButtonTexture = new Texture(Gdx.files.internal("GameMode/torna.png"));

// Create a Drawable from the Texture
        Drawable myButtonDrawable = new TextureRegionDrawable(new TextureRegion(myButtonTexture));

// Create the ImageButton
        ImageButton myButton = new ImageButton(myButtonDrawable);

        myButton.getStyle().imageUp.setMinWidth(30);
        myButton.getStyle().imageUp.setMinHeight(30);

// Add a ClickListener to the ImageButton

        myButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        game.setScreen(new GameModeScreen(game));
                    }
                });
            }
        });

// Add the ImageButton to the contentTable


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
        contentTable.add(myButton).padBottom(20);
        contentTable.add(crearSalaLabel).padBottom(10).padLeft(70);
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
        popupTable.setPosition((stage.getWidth() - popupTable.getWidth()) / 2,
                (stage.getHeight() - popupTable.getHeight()) / 2.5f);
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

        // Actualizar el tamaño del fondo para que se ajuste al nuevo tamaño de la pantalla
        batch.setProjectionMatrix(camera.combined);
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
        String salaId = generarSalaId();
        salaExiste(salaId, new SalaExisteCallback() {
            @Override
            public void onSalaExisteChecked(boolean salaExiste) {
                if (!salaExiste) {
                    // Si la sala no existe, puedes continuar con la creación de la sala
                    JSONObject roomJSON = new JSONObject();
                    try {
                        roomJSON.put("idSala", salaId);
                        roomJSON.put("creadorSala", game.nomUsuari);
                        roomJSON.put("estatSala", "En espera");
                        JSONArray jugadores = new JSONArray();
                        JSONObject jugador = new JSONObject();
                        jugador.put("nom", game.nomUsuari);
                        jugador.put("wins", 0);
                        jugador.put("skin", game.Skin);
                        jugadores.put(jugador);

                        roomJSON.put("jugadores", jugadores);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                    game.SalaActual = salaId;

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
                } else {
                    // Si la sala existe, genera un nuevo ID y verifica de nuevo
                    crearNovaSala();
                }
            }
        });

        socket.emit("join room", salaId);
    }


    public void unirseASala(final String idSala) {
        game.SalaActual = idSala;
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("idSala", idSala);
            Gdx.app.error("Usuari", game.nomUsuari);
            requestData.put("nomUsuari", game.nomUsuari);
            requestData.put("skin", game.Skin);
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
                if (httpResponse.getStatus().getStatusCode() == 200) {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Te has unido a la sala!");

                            //Connexió al servidor de Socket.IO

                            socket.emit("unirSala", idSala);
                            game.setScreen(new SalasScreen(game));
                        }
                    });
                }else {
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Error al unirse a la sala: La sala no existe");
                            showToastMessage("La sala " + idSala + " no existe");
                        }
                    });
                }
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

        socket.emit("join room", idSala);
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

    public void showToastMessage(String message) {
        Label.LabelStyle style = new Label.LabelStyle();
        style.font = new BitmapFont();
        style.fontColor = Color.WHITE;

        Label label = new Label(message, style);
        label.setPosition(Gdx.graphics.getWidth() / 2 - label.getWidth() / 2, Gdx.graphics.getHeight() / 2 - label.getHeight() / 2);
        label.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.5f), Actions.delay(2), Actions.fadeOut(0.5f), Actions.removeActor()));

        stage.addActor(label);
    }


    public String generarSalaId() {
        Random random = new Random();
        String salaId = "";

        for (int i = 0; i < 3; i++) {
            salaId += random.nextInt(10); // Generar un dígito aleatorio
        }

        for (int i = 0; i < 3; i++) {
            char letra = (char) ('A' + random.nextInt(26)); // Generar una letra mayúscula aleatoria
            salaId += letra;
        }

        return salaId;
    }
    public interface SalaExisteCallback {
        void onSalaExisteChecked(boolean salaExiste);
    }

    public void salaExiste(String salaId, final SalaExisteCallback callback) {
        // Crear una solicitud HTTP GET para verificar si la sala existe
        Net.HttpRequest httpRequest = new Net.HttpRequest(Net.HttpMethods.GET);
        httpRequest.setUrl("http://" + Settings.IP_SERVER + ":" + Settings.PUERTO_PETICIONES + "/salaExiste/" + salaId);
        httpRequest.setHeader("Content-Type", "application/json");

        // Enviar la solicitud y obtener la respuesta
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                // La sala existe si la respuesta es "true"
                boolean salaExiste = httpResponse.getResultAsString().equals("true");
                callback.onSalaExisteChecked(salaExiste);
            }

            @Override
            public void failed(Throwable t) {
                throw new RuntimeException("Error al verificar la sala: " + t.getMessage());
            }

            @Override
            public void cancelled() {
                throw new RuntimeException("Verificación de sala cancelada");
            }
        });
    }

}
