package com.sergreen.bowrunner.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.sergreen.bowrunner.Screens.Buttons.GUIButton;
import com.sergreen.bowrunner.Screens.Buttons.GUIButtonRectangle;
import com.sergreen.bowrunner.Screens.Buttons.GUIButtonRectangleStickIn;
import com.sergreen.bowrunner.Utils.Global;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Created on 21.04.2015 [SerGreen]
 */
public class StatsInputAndGUI implements InputProcessor {
    private final ArrayList<GUIButton> buttons;
    private StatsScreen statsScreen;

    public StatsInputAndGUI(final StatsScreen statsScreen) {
        this.statsScreen = statsScreen;
        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();
        buttons = new ArrayList<GUIButton>();

        buttons.add(new GUIButtonRectangle(
                Global.assetManager.get("textures/gui/stats_close_button.png", Texture.class),
                new Vector2(0, screenHeight-screenHeight*0.208f), screenWidth *0.117f, screenHeight*0.208f, 1,
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        statsScreen.closeAction();
                        return null;
                    }
                }, true));

        buttons.add(new GUIButtonRectangleStickIn(
                Global.assetManager.get("textures/gui/stats_best_button.png", Texture.class),
                new Vector2(0, screenHeight-screenHeight*(0.208f + 0.395f)),
                screenWidth *0.117f, screenHeight*0.395f, 1,
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        statsScreen.bestTabAction();
                        return null;
                    }
                }, true, true));

        buttons.add(new GUIButtonRectangleStickIn(
                Global.assetManager.get("textures/gui/stats_total_button.png", Texture.class),
                new Vector2(0, 0),
                screenWidth *0.117f, screenHeight*0.395f, 1,
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        statsScreen.totalTabAction();
                        return null;
                    }
                }, true, false));

        ((GUIButtonRectangleStickIn)buttons.get(1)).setLinkedButton((GUIButtonRectangleStickIn) buttons.get(2));
        ((GUIButtonRectangleStickIn)buttons.get(2)).setLinkedButton((GUIButtonRectangleStickIn) buttons.get(1));
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK) {
            statsScreen.closeAction();
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
