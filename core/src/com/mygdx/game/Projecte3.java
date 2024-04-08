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
import com.mygdx.game.utils.User;

public class Projecte3 extends Game {

	public static String nomUsuari;

	public static int Skin;
	public static String SalaActual;

	public static Texture cat_spritesheet;


	@Override
	public void create () {
		cat_spritesheet = new Texture(Gdx.files.internal("characters/Sprite2.png"));
		AssetManager.load();
		setScreen(new LoginScreen(this));


	}
	@Override
	public void dispose () {
		super.dispose();
		AssetManager.dispose();
	}
}