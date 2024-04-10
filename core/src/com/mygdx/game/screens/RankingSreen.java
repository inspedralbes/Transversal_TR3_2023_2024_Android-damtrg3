package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.mygdx.game.Projecte3;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.utils.Settings;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import javax.swing.text.View;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class RankingSreen implements Screen {
    private Table recyclerView;
    private Projecte3 game;
    private Stage stage;
    private TextButton backButton;

    private TextButton actualizar;
    private Batch batch;
    public static Socket socket;
    public RankingSreen(Projecte3 game) {
        this.game = game;
        try {
            connectToSocketServer();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void show() {

        actualizar = new TextButton("Actualitzar", AssetManager.lava_skin);
        actualizar.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                socket.emit("reqRanking");
            }
        });

        socket.on("actualitzarRanking", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                if (args.length > 0) {
                    JSONArray rankingInfo = (JSONArray) args[0];
                    System.out.println(rankingInfo.toString());

                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            // Limpiar el RecyclerView
                            recyclerView.clearChildren();

                            try {
                                for (int i = 0; i < rankingInfo.length(); i++) {
                                    JSONObject jugador = rankingInfo.getJSONObject(i);
                                    String nombre = jugador.getString("username");
                                    int puntuacion = jugador.getInt("elapsedTime");

                                    Table table = new Table();

                                    if (i < 3) { // Solo para las tres primeras posiciones
                                        String rutaImagen;
                                        if (i == 0) {
                                            rutaImagen = "GameMode/numero1.png"; // Reemplaza esto con la ruta a tu imagen de oro
                                        } else if (i == 1) {
                                            rutaImagen = "GameMode/numero2.png"; // Reemplaza esto con la ruta a tu imagen de plata
                                        } else {
                                            rutaImagen = "GameMode/numero3.png"; // Reemplaza esto con la ruta a tu imagen de bronce
                                        }
                                        Texture texture = new Texture(Gdx.files.internal(rutaImagen));
                                        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(texture));
                                        drawable.setMinWidth(30); // Ajusta el ancho mínimo de la imagen
                                        drawable.setMinHeight(30); // Ajusta la altura mínima de la imagen
                                        Image imagen = new Image(drawable);
                                        table.add(imagen); // Añade la imagen a la tabla con un padding a la derecha
                                    } else {
                                        String posicionStr = String.format("%3s", i + 1 + " ");
                                        Label posicion = new Label(posicionStr, AssetManager.lava_skin); // Muestra el número de la posición
                                        table.add(posicion); // Añade la etiqueta de la posición a la tabla
                                    }

                                    Label label = new Label("| " + nombre + ": " + puntuacion, AssetManager.lava_skin);
                                    table.add(label); // Añade la etiqueta a la tabla

                                    recyclerView.add(table).pad(10).left(); // Alinea a la izquierda
                                    recyclerView.row(); // Esto crea una nueva fila, por lo que cada texto estará en su propia línea.
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                // Manejar la excepción si alguna de las claves no está presente en el objeto salaInfo
                            }
                        }
                    });
                }
            }
        });

        batch = new SpriteBatch();

        stage = new Stage(); // Initialize stage first
        Gdx.input.setInputProcessor(stage); // Set InputProcessor after stage initialization

        Table wrapperTable = new Table(); // Table para envolver los campos
        wrapperTable.setSize(750, 980); // Establece el tamaño deseado para la tabla
        wrapperTable.setPosition((Gdx.graphics.getWidth() - wrapperTable.getWidth()) / 2, (Gdx.graphics.getHeight() - wrapperTable.getHeight()) / 2); // Centra la tabla en la pantalla

        Texture backgroundTexture = new Texture(Gdx.files.internal("frame6.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        wrapperTable.setBackground(backgroundDrawable);

        Table contentTable = new Table(); // Table para los campos de entrada y botones
        contentTable.pad(20); // Agrega un relleno de 20 píxeles alrededor del contenido

        Label title = new Label("Ranking Individual", AssetManager.lava_skin); // Define el título
        title.setFontScale(2); // Ajusta el tamaño de la fuente del título
        contentTable.add(title).padBottom(-80).center().padLeft(200); // Añade el título al centro de la tabla
        contentTable.row();

        backButton = new TextButton("Back", AssetManager.lava_skin);
// Crear un nuevo Table que actuará como nuestro RecyclerView.
        recyclerView = new Table();

// Crear un ScrollPane que contenga nuestro recyclerView.
        ScrollPane scrollPane = new ScrollPane(recyclerView);
        scrollPane.setScrollingDisabled(true, false); // Deshabilita el desplazamiento horizontal
        scrollPane.setFadeScrollBars(false); // Deshabilita el desvanecimiento de las barras de desplazamiento
        scrollPane.setHeight(200); // Establece la altura del ScrollPane

// Añadir el ScrollPane a tu contentTable.
        contentTable.add(scrollPane).expandX().fillX().height(200).left().padRight(10).padTop(70); // Establece la altura del contenedor del ScrollPane, alinea a la izquierda y añade espacio a la derecha

// Agrega los actores al contentTable
        contentTable.add(backButton).pad(10).right().padRight(130); // Alinea el botón de retroceso a la derecha y añade espacio a la izquierda
        contentTable.row();
        contentTable.add(actualizar).pad(-100).right().padRight(-110).padBottom(140); // Alinea el botón de actualizar a la derecha y añade espacio a la izquierda
        contentTable.row();

        wrapperTable.add(contentTable).expand().fill(); // Agrega el Table de contenido dentro del Table de envoltura

        stage.addActor(wrapperTable); // Agrega el Table de envoltura al Stage

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                OptionsScreen.AudioManager audioManager = new OptionsScreen.AudioManager();
                game.setScreen(new GameModeScreen(game, audioManager));
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
