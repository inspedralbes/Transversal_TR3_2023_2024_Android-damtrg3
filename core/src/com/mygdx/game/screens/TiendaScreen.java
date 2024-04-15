package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.mygdx.game.Projecte3;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.utils.ApiService;
import com.mygdx.game.utils.Product;
import com.mygdx.game.utils.Settings;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TiendaScreen implements Screen {
    private Projecte3 game;
    private Stage stage;
    private TextButton backButton;

    private TextButton guardarCanvisButton;
    private ApiService apiService;
    private static final String BASE_URL = "http://" + Settings.IP_SERVER + ":" + Settings.PUERTO_PETICIONES + "/";
    private Table productsTable;;

    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private Batch batch;
    private Label moneyLabel;
    private int playerMoney;
    private Label noMoneyLabel;

    public TiendaScreen(Projecte3 game) {
        this.game = game;

        // Inicializa Retrofit y ApiService
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);
        productsTable = new Table();

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
        // Crear un nuevo SpriteBatch
        batch = new SpriteBatch();

        Gdx.input.setInputProcessor(stage); // Set InputProcessor after stage initialization

        Table wrapperTable = new Table(); // Table para envolver los campos
        wrapperTable.setSize(750, 900); // Establece el tamaño deseado para la tabla
        wrapperTable.setPosition((stage.getWidth() - wrapperTable.getWidth()) / 2,
                (stage.getHeight() - wrapperTable.getHeight()) / 2);
        Texture backgroundTexture = new Texture(Gdx.files.internal("frame6.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        wrapperTable.setBackground(backgroundDrawable);

        Table contentTable = new Table(); // Table para los campos de entrada y botones
        contentTable.center();

        //----------------------------- Label Dinero --------------------------------

        noMoneyLabel = new Label("Credit insuficient !!!", AssetManager.lava_skin);
        noMoneyLabel.setVisible(false);
        stage.addActor(noMoneyLabel);

        // Load the image for the button
        Texture myButtonTexture = new Texture(Gdx.files.internal("GameMode/torna.png"));

// Create a Drawable from the Texture
        Drawable myButtonDrawable = new TextureRegionDrawable(new TextureRegion(myButtonTexture));

// Create the ImageButton
        ImageButton myButton = new ImageButton(myButtonDrawable);

        myButton.getStyle().imageUp.setMinWidth(30);
        myButton.getStyle().imageUp.setMinHeight(30);

        moneyLabel = new Label("Credit total: ", AssetManager.lava_skin);
        contentTable.add(myButton).padRight(400);
        contentTable.row();
        contentTable.add(moneyLabel);
        contentTable.row();
        contentTable.add(noMoneyLabel).padBottom(10);

        Call<Double> userMoneyCall = apiService.getUserMoney(game.nomUsuari);
        userMoneyCall.enqueue(new Callback<Double>() {
            @Override
            public void onResponse(Call<Double> call, Response<Double> response) {
                if (response.isSuccessful()) {
                    double money = response.body();
                    playerMoney = (int) money;
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            moneyLabel.setText("Credit Total: " + money);
                        }
                    });
                } else {
                    Gdx.app.error("ERROR", "No se pudo obtener el dinero del usuario");
                }
            }

            @Override
            public void onFailure(Call<Double> call, Throwable t) {
                Gdx.app.error("error", "onFailure: " + t.getMessage());
            }
        });

        //----------------------------- Button Enrere --------------------------------

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


        //----------------------------- Button Guardar Canvis --------------------------------

        //Elements al table


        wrapperTable.add(contentTable); // Agrega el Table de contenido dentro del Table de envoltura

        //Obtenir els productes que té l'usuari
        Call<List<Integer>> userProductsCall = apiService.getUserProducts(game.nomUsuari);

        List<Integer> userProductIds = new ArrayList<>();

        userProductsCall.enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if (response.isSuccessful()) {
                    userProductIds.addAll(response.body());
                } else {
                    Gdx.app.error("ERROR", "No se pudieron obtener los productos del usuario");
                }
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                Gdx.app.error("error", "onFailure: " + t.getMessage());
            }
        });

        stage.addActor(wrapperTable); // Agrega el Table de envoltura al Stage
        Call<List<Product>> call = apiService.getProducts();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful()) {
                    List<Product> products = response.body();
                    for (Product product : products) {
                        System.out.println(product);
                    }
                    Iterator<Product> iterator = products.iterator();
                    while (iterator.hasNext()) {
                        Product product = iterator.next();
                        if (userProductIds.contains(product.getId()) || product.getId() == 1 || product.getId() == 2) {
                            iterator.remove();
                        }
                    }

                    // Itera sobre los productos y los agrega a la tabla
                    for (Product product : products) {
                        Label productLabel = new Label(product.getName(), AssetManager.lava_skin);
                        Label productPrice = new Label(String.valueOf(product.getList_price()), AssetManager.lava_skin);
                        Label productId = new Label("ID: " + product.getId(), AssetManager.lava_skin);
                        // Descarga la imagen a Pixmap
                        Pixmap pixmap = downloadImageToPixmap(product.getImage_1920());

                        // Comprueba si pixmap es null antes de crear la textura
                        if (pixmap != null) {
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    // Crea una imagen a partir del Pixmap
                                    Texture texture = new Texture(pixmap);
                                    TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(texture));
                                    drawable.setMinWidth(100);
                                    drawable.setMinHeight(100);
                                    Image productImage = new Image(drawable);


                                    // Crea una tabla para cada producto
                                    Table productTable = new Table();
                                    productTable.add(productLabel).padBottom(5); // Agrega el nombre del producto a la tabla del producto
                                    productTable.row(); // Crea una nueva fila en la tabla del producto
                                    productTable.add(productImage).padBottom(5); // Agrega la imagen del producto a la tabla del producto
                                    productTable.row(); // Crea una nueva fila en la tabla del producto
                                    productTable.add(productPrice); // Agrega el precio del producto a la tabla del producto

                                    // Agrega la tabla del producto a la tabla de productos
                                    productsTable.add(productTable).pad(5);

                                    // Añade un oyente de clics al producto
                                    productTable.addListener(new ClickListener() {
                                        @Override
                                        public void clicked(InputEvent event, float x, float y) {
                                            // Cuando se hace clic en el producto, compra el producto
                                            showConfirmationPopup(product);
                                        }
                                    });
                                }
                            });
                        } else {
                            Gdx.app.error("ERROR", "No se pudo descargar la imagen del producto: " + product.getName());
                        }
                    }

                    // Agrega productsTable a contentTable
                    contentTable.row();
                    contentTable.add(productsTable);

                } else {
                    Gdx.app.error("ERROR", "No se pudieron obtener los productos");
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Gdx.app.error("error", "onFailure: " + t.getMessage());
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
    public Pixmap downloadImageToPixmap(String imageUrl) {
        try {
            if (imageUrl.startsWith("data:")) {
                // Extrae la parte Base64 de la URL de datos
                String base64Image = imageUrl.split(",")[1];
                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                return new Pixmap(imageBytes, 0, imageBytes.length);
            } else {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                byte[] imageBytes = input.readAllBytes();
                return new Pixmap(imageBytes, 0, imageBytes.length);
            }
        } catch (Exception e) {
            Gdx.app.error("ERROR", "No se pudo descargar la imagen: " + e.getMessage());
            return null;
        }
    }

    private void showConfirmationPopup(Product product) {
        // Crear una tabla para contener los elementos del pop-up
        Table popupTable = new Table();
        popupTable.setSize(400, 320);

        // Agregar elementos al pop-up, como mensaje de confirmación y botones
        Label confirmationLabel = new Label("¿Confirmar la compra de " + product.getName() + "?", AssetManager.lava_skin);
        TextButton confirmButton = new TextButton("Confirmar", AssetManager.lava_skin);
        TextButton cancelButton = new TextButton("Cancelar", AssetManager.lava_skin);

        popupTable.setPosition((stage.getWidth() - popupTable.getWidth()) / 2,
                (stage.getHeight() - popupTable.getHeight()) / 2.5f);
        Texture popupBackgroundTexture = new Texture(Gdx.files.internal("frame6.png"));
        TextureRegionDrawable popupBackgroundDrawable = new TextureRegionDrawable(new TextureRegion(popupBackgroundTexture));
        popupTable.setBackground(popupBackgroundDrawable);

        // Agregar listeners a los botones
        confirmButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Ocultar el pop-up al confirmar la compra
                popupTable.setVisible(false);
                // Realizar la compra del producto
                buyProduct(product.getId(), game.nomUsuari, product.getList_price());
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Ocultar el pop-up al cancelar la compra
                popupTable.setVisible(false);
            }
        });

        // Agregar elementos al pop-up
        popupTable.add(confirmationLabel).colspan(2).padBottom(10).row();
        popupTable.add(confirmButton).padRight(20);
        popupTable.add(cancelButton);

        // Establecer la posición del pop-up en el centro de la pantalla
        popupTable.setPosition((stage.getWidth() - popupTable.getWidth()) / 2,
                (stage.getHeight() - popupTable.getHeight()) / 2);

        // Agregar el pop-up a la etapa
        stage.addActor(popupTable);

        // Hacer visible el pop-up
        popupTable.setVisible(true);
    }
    public void buyProduct(int productId, String username, double listPrice) {
        if(playerMoney < listPrice){
            noMoneyLabel.setVisible(true);
            return;
        } else {
            noMoneyLabel.setVisible(false);
        }

        Product product = new Product(productId);
        product.setName(username);
        product.setList_price(listPrice);  // Establecer el list_price

        Call<Void> call = apiService.buyProduct(product);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    playerMoney -= listPrice;
                    Gdx.app.postRunnable(new Runnable() {
                        @Override
                        public void run() {
                            game.setScreen(new TiendaScreen(game));
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("Error al comprar el producto: " + t.getMessage());
            }
        });
    }
}