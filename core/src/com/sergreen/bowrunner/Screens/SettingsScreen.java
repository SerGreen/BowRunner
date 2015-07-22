package com.sergreen.bowrunner.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.sergreen.bowrunner.BowRunnerGame;
import com.sergreen.bowrunner.Game.GameObjects.Player;
import com.sergreen.bowrunner.Game.Skin;
import com.sergreen.bowrunner.Utils.Global;

/**
 * Created on 18.04.2015 [SerGreen]
 */
public class SettingsScreen implements Screen {
    private final BowRunnerGame game;
    private MenuScreen menu;
    private final OrthographicCamera camera;
    private SettingsInputAndGUI gui;
    private Texture background = Global.assetManager.get("textures/backgrounds/settings_background.png", Texture.class);
    private World world;
    private Player player;

    public SettingsScreen(final BowRunnerGame game, final MenuScreen menu) {
        this.game = game;
        this.menu = menu;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gui = new SettingsInputAndGUI(this);
        initSkinWindow();
    }

    protected void initSkinWindow() {
        world = new World(Vector2.Zero, true);

        player = new Player(new Vector2(Gdx.graphics.getWidth()*0.75f, Gdx.graphics.getHeight()*0.6f),
                world,
                Skin.SkinType.values()[Global.getSettings().getInteger("Skin", 0)],
                Gdx.graphics.getHeight() / 160f);
        player.groundTouching++;
        player.realHumanBean = false;
        player.update();
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
        world.dispose();
    }

    @Override
    public void render(float delta) {
        Global.timeState += delta;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // background
        game.batch.draw(background,
                0, 0,
                0, 0,
                Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                1, 1,
                0,
                0, 0,
                background.getWidth(), background.getHeight(),
                false, false);

        gui.render(game.batch);
        player.render(game.batch);

        game.batch.end();
    }

    public void backAction() {
        game.setScreen(menu);
    }

    public void leftAction() {
        int value = Global.getSettings().getInteger("Skin", 0);
        if (value > 0) {
            Global.getSettings().putInteger("Skin", --value);
            Global.getSettings().flush();
            player.setSkin(Skin.SkinType.values()[value], Gdx.graphics.getHeight() / 160f);
            player.update();
        }
    }

    public void rightAction() {
        int value = Global.getSettings().getInteger("Skin", 0);
        if (value < Skin.SkinType.values().length - 1) {
            Global.getSettings().putInteger("Skin", ++value);
            Global.getSettings().flush();
            player.setSkin(Skin.SkinType.values()[value], Gdx.graphics.getHeight() / 160f);
            player.update();
        }
    }

    public void soundAction() {
        Global.getSettings().putBoolean("Sound", !Global.getSettings().getBoolean("Sound"));
        Global.getSettings().flush();
        Global.soundManager.updateSoundSettings();
    }

    public void musicAction() {
        Global.getSettings().putBoolean("Music", !Global.getSettings().getBoolean("Music"));
        Global.getSettings().flush();
        Global.soundManager.updateSoundSettings();
    }
}