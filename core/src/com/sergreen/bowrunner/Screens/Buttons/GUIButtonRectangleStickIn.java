package com.sergreen.bowrunner.Screens.Buttons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.concurrent.Callable;

/**
 * Created on 21.04.2015 [SerGreen]
 */
public class GUIButtonRectangleStickIn extends GUIButtonRectangle {
    public boolean stateOn;
    private GUIButtonRectangleStickIn linkedButton;

    public GUIButtonRectangleStickIn(Texture sprite, Vector2 onScreenPosition, float width, float height, float pixelToMeter, Callable<Void> actionFunctionDown, boolean playSound, boolean stateOn) {
        super(sprite, onScreenPosition, onScreenPosition, width, height, pixelToMeter, actionFunctionDown, null, playSound);
        this.stateOn = stateOn;
    }

    public void setLinkedButton(GUIButtonRectangleStickIn button) {
        this.linkedButton = button;
    }

    @Override
    public void touchDown(Vector2 touchPoint, int fingerID) throws Exception {
        if (!stateOn)
            super.touchDown(touchPoint, fingerID);
    }

    @Override
    protected void actionDown(int fingerID) throws Exception {
        super.actionDown(fingerID);
        stateOn = true;
        linkedButton.stateOn = false;
    }

    @Override
    protected void soundUp() { }

    @Override
    protected void soundDown() {
        if (!stateOn)
            super.soundDown();
    }

    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch, stateOn || pressed ? 1 : 0);
    }
}
