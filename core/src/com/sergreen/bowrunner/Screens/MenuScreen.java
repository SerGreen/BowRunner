package com.sergreen.bowrunner.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.sergreen.bowrunner.BowRunnerGame;
import com.sergreen.bowrunner.Utils.AnimatedSprite;
import com.sergreen.bowrunner.Utils.Global;

/**
 * Created on 06.04.2015 [SerGreen]
 */
public class MenuScreen implements Screen {
    private final BowRunnerGame game;
    private OrthographicCamera camera;
    private MenuInputAndGUI gui;
    private AnimatedSprite background;
    private boolean moveBackgroundRight = true;
    private int backgroundMoveEveryXTick = 4;
    private int backgroundTick = 0;

    public MenuScreen(final BowRunnerGame game) {
        Global.timeState = 0;
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gui = new MenuInputAndGUI(this);
        background = new AnimatedSprite(
                Global.assetManager.get("textures/backgrounds/menu_background_animated.png", Texture.class),
                1, 7, 0.1f, Animation.PlayMode.LOOP);
        background.setOriginX(0);
        background.setOriginY(0);
        float screenHeight = Gdx.graphics.getHeight();
        background.setScaleXY(screenHeight / background.getHeight());
        background.setPosition(-background.getWidth() * background.getScaleX() + Gdx.graphics.getWidth(), 0);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(gui);
        Gdx.input.setCatchBackKey(false);
    }

    public void startGame() {
        game.setScreen(new GameScreen(game));
        dispose();
    }

    public void openSettings() {
        game.setScreen(new SettingsScreen(game, this));
    }


    public void openStats() {
        game.setScreen(new StatsScreen(game, this));
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {
        if(Global.assetManager.getQueuedAssets() > 0) {
            Global.assetManager.finishLoading();
            Gdx.app.log("Menu", "Resume");
        }
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    private void update() {
        camera.update();
        if (moveBackgroundRight) {
            if (background.getPosition().x < 0) {
                backgroundTick++;
                if (backgroundTick >= backgroundMoveEveryXTick) {
                    backgroundTick = 0;
                    background.setPosition(background.getX() + 1, background.getY());
                }
            } else
                moveBackgroundRight = false;
        } else {
            if (background.getPosition().x + background.getWidth() > Gdx.graphics.getWidth()) {
                backgroundTick++;
                if (backgroundTick >= backgroundMoveEveryXTick) {
                    backgroundTick = 0;
                    background.setPosition(background.getX() - 1, background.getY());
                }
            } else
                moveBackgroundRight = true;
        }
    }

    @Override
    public void render(float delta) {
        Global.timeState += delta;
        update();

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        background.draw(game.batch, Global.timeState);

        gui.render(game.batch);

        game.batch.end();
    }

    public void exitGame() {
        Gdx.app.exit();
    }
}
