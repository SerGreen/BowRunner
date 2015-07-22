package com.sergreen.bowrunner.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.sergreen.bowrunner.BowRunnerGame;
import com.sergreen.bowrunner.Utils.Global;

/**
 * Created on 21.04.2015 [SerGreen]
 */
public class StatsScreen implements Screen {
    private final BowRunnerGame game;
    private final MenuScreen menuScreen;
    private OrthographicCamera camera;
    private StatsInputAndGUI gui;
    private int tab = 0;    // 0 = best, 1 = total
    private Texture bestBackground = Global.assetManager.get("textures/backgrounds/stats_best_background.png", Texture.class);
    private Texture totalBackground = Global.assetManager.get("textures/backgrounds/stats_total_background.png", Texture.class);

    public StatsScreen(BowRunnerGame game, MenuScreen menuScreen) {
        this.game = game;
        this.menuScreen = menuScreen;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gui = new StatsInputAndGUI(this);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(gui);
        Gdx.input.setCatchBackKey(true);
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

    }
    @Override
    public void hide() {

    }
    @Override
    public void dispose() {

    }
    @Override
    public void render(float delta) {
        Global.timeState += delta;
        int screenHeight = Gdx.graphics.getHeight();
        int screenWidth = Gdx.graphics.getWidth();

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();

        // background
        if(tab == 0) {
            game.batch.draw(bestBackground,
                    0, 0,
                    0, 0,
                    Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                    1, 1,
                    0,
                    0, 0,
                    bestBackground.getWidth(), bestBackground.getHeight(),
                    false, false);

            int distance = Global.getSettings().getInteger("Best distance");
            String distanceText = String.valueOf(distance < 1000 ? distance : distance / 1000 + " " + distance % 1000) + " m";

            Global.font.drawText(String.valueOf(Global.getSettings().getInteger("High score")), game.batch,
                    new Vector2(screenWidth * 0.69f, screenHeight * 0.846f), screenHeight * 0.104f / Global.font.getCharacterSize().y);
            Global.font.drawText(distanceText, game.batch,
                    new Vector2(screenWidth * 0.69f, screenHeight * 0.668f), screenHeight * 0.104f / Global.font.getCharacterSize().y);
            Global.font.drawText(String.valueOf(Global.getSettings().getInteger("Most jumps")), game.batch,
                    new Vector2(screenWidth * 0.69f, screenHeight * 0.483f), screenHeight * 0.104f / Global.font.getCharacterSize().y);
            Global.font.drawText(String.valueOf(Global.getSettings().getInteger("Most targets")), game.batch,
                    new Vector2(screenWidth * 0.69f, screenHeight * 0.304f), screenHeight * 0.104f / Global.font.getCharacterSize().y);
            Global.font.drawText(String.valueOf(Global.getSettings().getInteger("Most arrows")), game.batch,
                    new Vector2(screenWidth * 0.69f, screenHeight * 0.118f), screenHeight * 0.104f / Global.font.getCharacterSize().y);
        }
        else {
            game.batch.draw(totalBackground,
                    0, 0,
                    0, 0,
                    Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                    1, 1,
                    0,
                    0, 0,
                    totalBackground.getWidth(), totalBackground.getHeight(),
                    false, false);

            int distance = Global.getStats().getInteger("Distance travelled");
            String distanceText = String.valueOf(distance < 1000 ? distance : distance / 1000 + " " + distance % 1000) + " m";

            Global.font.drawText(distanceText, game.batch,
                    new Vector2(screenWidth * 0.562f, screenHeight * 0.845f), screenHeight * 0.104f / Global.font.getCharacterSize().y);
            Global.font.drawText(String.valueOf(Global.getStats().getInteger("Jumps made")), game.batch,
                    new Vector2(screenWidth * 0.562f, screenHeight * 0.7f), screenHeight * 0.104f / Global.font.getCharacterSize().y);
            Global.font.drawText(String.valueOf(Global.getStats().getInteger("Arrows shot")), game.batch,
                    new Vector2(screenWidth * 0.562f, screenHeight * 0.552f), screenHeight * 0.104f / Global.font.getCharacterSize().y);
            Global.font.drawText(String.valueOf(Global.getStats().getInteger("Birds killed")), game.batch,
                    new Vector2(screenWidth * 0.562f, screenHeight * 0.408f), screenHeight * 0.104f / Global.font.getCharacterSize().y);
            Global.font.drawText(String.valueOf(Global.getStats().getInteger("Targets killed")), game.batch,
                    new Vector2(screenWidth * 0.562f, screenHeight * 0.262f), screenHeight * 0.104f / Global.font.getCharacterSize().y);
            Global.font.drawText(String.valueOf(Global.getStats().getInteger("Chickens killed")), game.batch,
                    new Vector2(screenWidth * 0.562f, screenHeight * 0.117f), screenHeight * 0.104f / Global.font.getCharacterSize().y);
        }

        gui.render(game.batch);

        game.batch.end();
    }

    public void closeAction() {
        game.setScreen(menuScreen);
    }

    public void bestTabAction() {
        tab = 0;
    }

    public void totalTabAction() {
        tab = 1;
    }
}
