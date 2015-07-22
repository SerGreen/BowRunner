package com.sergreen.bowrunner.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.sergreen.bowrunner.BowRunnerGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Bow Runner";

        boolean largeScreen = false;
        boolean smallScreen = false;
        if(!largeScreen && !smallScreen) {
            config.width = 854;
            config.height = 480;
        }
        else if(largeScreen) {
            config.width = 1280;
            config.height = 800;
        }
        else {
            config.width = 320;
            config.height = 180;
        }
		new LwjglApplication(new BowRunnerGame(), config);
	}
}
