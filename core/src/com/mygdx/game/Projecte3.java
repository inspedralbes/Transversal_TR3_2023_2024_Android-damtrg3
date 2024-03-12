package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.mygdx.game.helpers.AssetManager;
import com.mygdx.game.screens.GameScreen;
import com.mygdx.game.screens.MenuScreen;

public class Projecte3 extends Game {

	
	@Override
	public void create () {
		AssetManager.load();

		setScreen(new MenuScreen(this));
	}
	
	@Override
	public void dispose () {
		super.dispose();
		AssetManager.dispose();
	}
}
