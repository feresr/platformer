package com.feresr.platformer.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.feresr.platformer.Main;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 1300;
		config.height = 800;
		//config.resizable = false;
		//config.fullscreen = true;
		new LwjglApplication(new Main(), config);
	}
}
