package com.sergreen.bowrunner.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.sergreen.bowrunner.Screens.Buttons.GUIButton;
import com.sergreen.bowrunner.Screens.Buttons.GUIButtonRectangle;
import com.sergreen.bowrunner.Screens.Buttons.GUIStateButton;
import com.sergreen.bowrunner.Utils.Global;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created on 18.04.2015 [SerGreen]
 */
public class SettingsInputAndGUI implements InputProcessor {
    private ArrayList<GUIButton> buttons;
    private SettingsScreen settingsScreen;

    public SettingsInputAndGUI(final SettingsScreen settingsScreen) {
        this.settingsScreen = settingsScreen;
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        buttons = new ArrayList<GUIButton>();

        buttons.add(new GUIButtonRectangle(
                Global.assetManager.get("textures/gui/back_button.png", Texture.class),
                new Vector2(0, 0), screenWidth *0.176f, screenHeight, 1,
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        settingsScreen.backAction();
                        return null;
                    }
                }, true));

        buttons.add(new GUIButton(
                Global.assetManager.get("textures/gui/left_button.png", Texture.class),
                new Vector2(screenWidth *0.65f, screenHeight *0.275f), screenHeight *0.105f, 1,
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        settingsScreen.leftAction();
                        return null;
                    }
                }, true));

        buttons.add(new GUIButton(
                Global.assetManager.get("textures/gui/right_button.png", Texture.class),
                new Vector2(screenWidth *0.855f, screenHeight *0.275f), screenHeight *0.105f, 1,
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        settingsScreen.rightAction();
                        return null;
                    }
                }, true));

        buttons.add(new GUIStateButton(
                Global.assetManager.get("textures/gui/sound_button.png", Texture.class),
                new Vector2(screenWidth *0.377f, screenHeight *0.275f), screenWidth *0.1f, 1,
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        settingsScreen.soundAction();
                        return null;
                    }
                }, true, Global.getSettings().getBoolean("Sound")));

        buttons.add(new GUIStateButton(
                Global.assetManager.get("textures/gui/music_button.png", Texture.class),
                new Vector2(screenWidth *0.377f, screenHeight *0.72f), screenWidth *0.1f, 1,
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        settingsScreen.musicAction();
                        return null;
                    }
                }, true, Global.getSettings().getBoolean("Music")));
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
            settingsScreen.backAction();
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int x, int y, int fingerID, int button) {
        y = Gdx.graphics.getHeight() - y;
        try {
            for(GUIButton b : buttons) {
                b.touchDown(new Vector2(x, y), fingerID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int fingerID, int button) {
        y = Gdx.graphics.getHeight() - y;
        try {
            for(GUIButton b : buttons) {
                b.touchUp(new Vector2(x, y), fingerID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean touchDragged(int x, int y, int fingerID) {
        y = Gdx.graphics.getHeight() - y;
        for(GUIButton b : buttons) {
            b.touchDragged(new Vector2(x, y), fingerID);
        }
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void render(SpriteBatch batch) {
        for(GUIButton b : buttons) {
            b.render(batch);
        }
    }
}
