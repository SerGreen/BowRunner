package com.sergreen.bowrunner.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.sergreen.bowrunner.Screens.Buttons.GUIButton;
import com.sergreen.bowrunner.Screens.Buttons.GUIButtonRectangle;
import com.sergreen.bowrunner.Utils.Global;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created on 06.04.2015 [SerGreen]
 */
public class MenuInputAndGUI implements InputProcessor {

    private final int screenWidth;
    private final int screenHeight;

    private ArrayList<GUIButton> buttons;

    public MenuInputAndGUI(final MenuScreen menuScreen) {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();

        Texture texture = Global.assetManager.get("textures/gui/start_button.png", Texture.class);
        float radius = screenWidth * 0.159f;
        Vector2 position = new Vector2(screenWidth - screenWidth / 4, radius);
        buttons = new ArrayList<GUIButton>();
        buttons.add(new GUIButton(texture, position, radius, 1,
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        menuScreen.startGame();
                        return null;
                    }
                }, true));

        texture = Global.assetManager.get("textures/gui/settings_button.png", Texture.class);
        radius = screenWidth * 0.159f;
        position = new Vector2(screenWidth / 4, radius);
        buttons.add(new GUIButton(texture, position, radius, 1,
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        menuScreen.openSettings();
                        return null;
                    }
                }, true));

        texture = Global.assetManager.get("textures/gui/exit_button.png", Texture.class);
        radius = screenHeight * 0.12f;
        position = new Vector2(radius * 1.5f, screenHeight - radius);
        buttons.add(new GUIButton(texture, position, radius, 1,
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        menuScreen.exitGame();
                        return null;
                    }
                }, true));

        texture = Global.assetManager.get("textures/gui/stats_button.png", Texture.class);
        float width = screenWidth*0.263f;
        float height = screenHeight*0.354f;
        position = new Vector2(screenWidth*0.95f - width, screenHeight - height);
        buttons.add(new GUIButtonRectangle(texture, position, width, height, 1,
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        menuScreen.openStats();
                        return null;
                    }
                }, true));
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
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
        y = screenHeight - y;
        try {
            for (GUIButton b : buttons)
                b.touchDown(new Vector2(x, y), fingerID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int fingerID, int button) {
        y = screenHeight - y;
        try {
            for(GUIButton b : buttons)
                b.touchUp(new Vector2(x, y), fingerID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean touchDragged(int x, int y, int fingerID) {
        y = screenHeight - y;
        for(GUIButton b : buttons)
            b.touchDragged(new Vector2(x, y), fingerID);
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
        for(GUIButton b : buttons)
            b.render(batch);
    }
}