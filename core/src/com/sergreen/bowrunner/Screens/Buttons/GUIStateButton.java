package com.sergreen.bowrunner.Screens.Buttons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.sergreen.bowrunner.Utils.AnimatedSprite;

import java.util.concurrent.Callable;

/**
 * Created on 19.04.2015 [SerGreen]
 */
public class GUIStateButton extends GUIButton {
    private boolean stateOn;

    public GUIStateButton(Texture sprite, Vector2 onScreenPosition, float radius, float pixelToMeter, Callable<Void> actionFunctionUp, boolean playSound, boolean stateOn) {
        super(sprite, onScreenPosition, radius, pixelToMeter, actionFunctionUp, playSound);
        this.stateOn = stateOn;
    }

    @Override
    protected void initSprite(Texture sprite, Vector2 inWorldPosition, float pixelToMeter) {
        this.sprite = new AnimatedSprite(sprite, 4, 1, 1, Animation.PlayMode.NORMAL);
        this.sprite.setPosition(inWorldPosition);
        this.sprite.setScaleXY(radius * 2 * pixelToMeter / this.sprite.getWidth());
    }

    @Override
    protected void actionUp() throws Exception {
        super.actionUp();
        stateOn = !stateOn;
    }

    @Override
    public void render(SpriteBatch batch) {
        int frame = (pressed ? 1 : 0) | (stateOn ? 0 : 2);
        sprite.draw(batch, frame);
    }
}
