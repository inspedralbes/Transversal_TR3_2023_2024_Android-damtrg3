package com.mygdx.game;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.mygdx.game.Projecte3;
import com.mygdx.game.utils.Settings;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useImmersiveMode = true;
		initialize(new Projecte3(), config);

		getWindow().getAttributes().width = Settings.SCREEN_WIDTH;
		getWindow().getAttributes().height = Settings.SCREEN_HEIGHT;
	}
}
