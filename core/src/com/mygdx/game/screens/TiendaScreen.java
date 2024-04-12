package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
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
    }

    @Override
    public void show() {
        // Crear un nuevo SpriteBatch
        batch = new SpriteBatch();

        stage = new Stage(); // Initialize stage first
        Gdx.input.setInputProcessor(stage); // Set InputProcessor after stage initialization

        Table wrapperTable = new Table(); // Table para envolver los campos
        wrapperTable.setSize(750, 880); // Establece el tamaño deseado para la tabla
        wrapperTable.setPosition((Gdx.graphics.getWidth() - wrapperTable.getWidth()) / 2, (Gdx.graphics.getHeight() - wrapperTable.getHeight()) / 2); // Centra la tabla en la pantalla

        Texture backgroundTexture = new Texture(Gdx.files.internal("frame6.png"));
        TextureRegionDrawable backgroundDrawable = new TextureRegionDrawable(new TextureRegion(backgroundTexture));
        wrapperTable.setBackground(backgroundDrawable);

        Table contentTable = new Table(); // Table para los campos de entrada y botones
        contentTable.pad(20); // Agrega un relleno de 20 píxeles alrededor del contenido

        //----------------------------- Label Dinero --------------------------------

        noMoneyLabel = new Label("No tens prou diners", AssetManager.lava_skin);
        contentTable.add(noMoneyLabel).align(Align.top).pad(10);
        noMoneyLabel.setVisible(false);
        stage.addActor(noMoneyLabel);

        moneyLabel = new Label("Diners: ", AssetManager.lava_skin);
        contentTable.add(moneyLabel).align(Align.top).pad(10);

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
                            moneyLabel.setText("Diners: " + money);
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

        backButton = new TextButton("Enrere", AssetManager.lava_skin);

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameModeScreen(game));
            }
        });

        //----------------------------- Button Guardar Canvis --------------------------------

        //Elements al table
        contentTable.add(backButton).pad(10);


        wrapperTable.add(contentTable).center(); // Agrega el Table de contenido dentro del Table de envoltura

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
                                    Image productImage = new Image(texture);

                                    // Crea una tabla para cada producto
                                    Table productTable = new Table();
                                    productTable.add(productLabel).padBottom(10); // Agrega el nombre del producto a la tabla del producto
                                    productTable.row(); // Crea una nueva fila en la tabla del producto
                                    productTable.add(productImage).padBottom(10); // Agrega la imagen del producto a la tabla del producto
                                    productTable.row(); // Crea una nueva fila en la tabla del producto
                                    productTable.add(productPrice); // Agrega el precio del producto a la tabla del producto
                                    productTable.row(); // Crea una nueva fila en la tabla del producto
                                    productTable.add(productId);
                                    // Agrega la tabla del producto a la tabla de productos
                                    productsTable.add(productTable).pad(10);

                                    // Añade un oyente de clics al producto
                                    productTable.addListener(new ClickListener() {
                                        @Override
                                        public void clicked(InputEvent event, float x, float y) {
                                            // Cuando se hace clic en el producto, compra el producto
                                            buyProduct(product.getId(), game.nomUsuari,product.getList_price());
                                        }
                                    });
                                }
                            });
                        } else {
                            Gdx.app.error("ERROR", "No se pudo descargar la imagen del producto: " + product.getName());
                        }
                    }

                    // Agrega productsTable a contentTable
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
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                System.out.println("Error al comprar el producto: " + t.getMessage());
            }
        });
    }
}