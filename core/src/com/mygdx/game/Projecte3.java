package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.screens.LoginScreen;
import com.mygdx.game.screens.MultiplayerGameScreen;
import com.mygdx.game.utils.Broadcast;
import com.mygdx.game.utils.Settings;

public class Projecte3 extends Game {

	public static String nomUsuari;

	public static int Skin;
	public static String SalaActual;
	private Broadcast broadcast;
	private SpriteBatch batch;

	public static Texture cat_spritesheet;


	@Override
	public void create () {
		Settings.setHeight(Gdx.graphics.getHeight());
		Settings.setWidth(Gdx.graphics.getWidth());
		Skin = 1;
		cat_spritesheet = new Texture(Gdx.files.internal("characters/Sprite2.png"));
		AssetManager.load();
		setScreen(new LoginScreen(this));
		broadcast = new Broadcast();
		batch = new SpriteBatch();

	}
	@Override
	public void render () {
		super.render();
		batch.begin();
		broadcast.draw(batch, Gdx.graphics.getDeltaTime());
		batch.end();
	}
	@Override
	public void dispose () {
		super.dispose();
		AssetManager.dispose();
		batch.dispose();
	}
}