package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.screens.LoginScreen;

public class Projecte3 extends Game {

	public static String nomUsuari;
	public static String SalaActual;

	@Override
	public void create () {
		AssetManager.load();
		setScreen(new LoginScreen(this));

	}
	@Override
	public void dispose () {
		super.dispose();
		AssetManager.dispose();
	}
}