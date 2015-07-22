package com.sergreen.bowrunner;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sergreen.bowrunner.Screens.SplashLoadScreen;
import com.sergreen.bowrunner.Utils.Global;

/**
 * Created on 06.04.2015 [SerGreen]
 */
public class BowRunnerGame extends Game {
    public SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        this.setScreen(new SplashLoadScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
