package com.sergreen.bowrunner.Screens.Buttons;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import java.util.concurrent.Callable;

/**
 * Created on 18.04.2015 [SerGreen]
 */
public class GUIButtonRectangle extends GUIButton {
    private float width;
    private float height;

    public GUIButtonRectangle(Texture sprite, Vector2 onScreenPosition, float width, float height, float pixelToMeter, Callable<Void> actionFunctionUp, boolean playSound) {
        super(sprite, onScreenPosition, 0, pixelToMeter, actionFunctionUp, playSound);
        this.width = width;
        this.height = height;
        this.sprite.setOriginX(0);
        this.sprite.setOriginY(0);
        this.sprite.setScaleX(width * pixelToMeter / this.sprite.getWidth());
        this.sprite.setScaleY(height * pixelToMeter / this.sprite.getHeight());
    }

    public GUIButtonRectangle(Texture sprite, Vector2 inWorldPosition, Vector2 onScreenPosition, float width, float height, float pixelToMeter, Callable<Void> actionFunctionDown, Callable<Void> actionFunctionUp, boolean playSound) {
        super(sprite, inWorldPosition, onScreenPosition, 0, pixelToMeter, actionFunctionDown, actionFunctionUp, playSound);
        this.width = width;
        this.height = height;
        this.sprite.setOriginX(0);
        this.sprite.setOriginY(0);
        this.sprite.setScaleX(width * pixelToMeter / this.sprite.getWidth());
        this.sprite.setScaleY(height * pixelToMeter / this.sprite.getHeight());
    }

    @Override
    protected boolean isButtonTouched(Vector2 touchPoint) {
        return  touchPoint.x > position.x &&
                touchPoint.x < position.x + width &&
                touchPoint.y > position.y &&
                touchPoint.y < position.y + height;
    }
}
