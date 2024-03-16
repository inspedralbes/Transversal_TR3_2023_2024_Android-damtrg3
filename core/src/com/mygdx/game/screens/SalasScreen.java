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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.mygdx.game.Projecte3;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.utils.Settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class SalasScreen implements Screen {

    private Projecte3 game;
    private Stage stage;
    private Batch batch;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;

    private Label textSala;
    private Label textSalaCreador;

    private Label textSalaJugadors;
    public SalasScreen(Projecte3 game) {
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


        textSala = new Label("Sala id:"+ game.SalaActual , AssetManager.lava_skin);
        textSalaCreador = new Label("Creador:" + game.nomUsuari , AssetManager.lava_skin);
        textSalaJugadors = new Label("Jugadors:"+ game.nomUsuari , AssetManager.lava_skin);

        //Sockets
        MenuSalasScreen.socket.on("actualitzarSala", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        if (args.length > 0) {
                            JSONObject salaInfo = (JSONObject) args[0];
                            System.out.println(salaInfo.toString());

                            // Acceder directamente a las claves en el objeto salaInfo
                            try {
                                String idSala = salaInfo.getString("idSala");
                                String creador = salaInfo.getString("creador");
                                JSONArray jugadores = salaInfo.getJSONArray("jugadores");

                                textSala.setText("Sala id: " + idSala);
                                textSalaCreador.setText("Creador: " + creador);
                                textSalaJugadors.setText("Jugadores: " + jugadores.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                // Manejar la excepción si alguna de las claves no está presente en el objeto salaInfo
                            }
                        }
                    }
                });




        contentTable.add(textSala);
        contentTable.row();
        contentTable.add(textSalaCreador).padBottom(20);
        contentTable.row();
        contentTable.add(textSalaJugadors).padBottom(30);;

        wrapperTable.add(contentTable); // Agrega el Table de contenido dentro del Table de envoltura
        stage.addActor(wrapperTable); // Agrega el Table de envoltura al Stage

        // Crear una tabla para contener los elementos del pop-up
        Table popupTable = new Table();
        popupTable.setSize(300, 240);
        popupTable.setVisible(false); // Inicialmente, hacer que la tabla no sea visible


        // Establecer la posición del pop-up en el centro de la pantalla
        popupTable.setPosition((Gdx.graphics.getWidth() - popupTable.getWidth()) / 2, (float) ((Gdx.graphics.getHeight() - popupTable.getHeight()) / 2.5));

        Texture popupBackgroundTexture = new Texture(Gdx.files.internal("frame6.png"));
        TextureRegionDrawable popupBackgroundDrawable = new TextureRegionDrawable(new TextureRegion(popupBackgroundTexture));
        popupTable.setBackground(popupBackgroundDrawable);


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
}