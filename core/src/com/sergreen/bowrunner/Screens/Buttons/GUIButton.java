package com.sergreen.bowrunner.Screens.Buttons;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.sergreen.bowrunner.Game.GameCamera;
import com.sergreen.bowrunner.Utils.AnimatedSprite;
import com.sergreen.bowrunner.Utils.Global;

import java.util.concurrent.Callable;

/**
 * Created on 11.04.2015 [SerGreen]
 */
public class GUIButton {
    protected Vector2 position;
    protected float radius;
    protected AnimatedSprite sprite;
    protected boolean pressed = false;
    private int fingerID = -1;
    private Callable<Void> actionDown;
    private Callable<Void> actionUp;
    private boolean actionUpOverButton;
    private boolean playSound = true;

    public GUIButton(Texture sprite, Vector2 onScreenPosition, float radius, float pixelToMeter, Callable<Void> actionFunctionUp, boolean playSound) {
        this(sprite, onScreenPosition, onScreenPosition, radius, pixelToMeter, null, actionFunctionUp, playSound);
    }

    public GUIButton(Texture sprite, Vector2 inWorldPosition, Vector2 onScreenPosition, float radius, float pixelToMeter, Callable<Void> actionFunctionDown, Callable<Void> actionFunctionUp, boolean playSound) {
        this(sprite, inWorldPosition, onScreenPosition, radius, pixelToMeter, actionFunctionDown, actionFunctionUp, true, playSound);
    }

    public GUIButton(Texture sprite, Vector2 inWorldPosition, Vector2 onScreenPosition, float radius, float pixelToMeter, Callable<Void> actionFunctionDown, Callable<Void> actionFunctionUp, boolean playSound, boolean actionUpOnlyWhenTouchReleasedOverButton) {
        this.position = onScreenPosition;
        this.radius = radius;
        initSprite(sprite, inWorldPosition, pixelToMeter);
        this.actionDown = actionFunctionDown;
        this.actionUp = actionFunctionUp;
        this.actionUpOverButton = actionUpOnlyWhenTouchReleasedOverButton;
        this.playSound = playSound;
    }

    protected void initSprite(Texture sprite, Vector2 inWorldPosition, float pixelToMeter) {
        this.sprite = new AnimatedSprite(sprite, 2, 1, 1, Animation.PlayMode.NORMAL);
        this.sprite.setPosition(inWorldPosition);
        this.sprite.setScaleXY(radius * 2 * pixelToMeter / this.sprite.getWidth());
    }

    public void touchDown(Vector2 touchPoint, int fingerID) throws Exception {
        if (isButtonTouched(touchPoint)) {
            actionDown(fingerID);
        }
    }

    protected void actionDown(int fingerID) throws Exception {
        soundDown();
        pressed = true;
        this.fingerID = fingerID;
        if (actionDown != null)
            actionDown.call();
    }

    protected void soundDown() {
        if(playSound)
            Global.soundManager.playButtonDownSound();
    }

    public void touchUp(Vector2 touchPoint, int fingerID) throws Exception {
        if (fingerID == this.fingerID) {
            this.fingerID = -1;

            if (isButtonTouched(touchPoint) || !actionUpOverButton) {
                actionUp();
            }
        }
    }

    protected void actionUp() throws Exception {
        soundUp();
        pressed = false;
        if (actionUp != null)
            actionUp.call();
    }

    protected void soundUp() {
        if(playSound)
            Global.soundManager.playButtonUpSound();
    }

    public void touchDragged(Vector2 touchPoint, int fingerID) {
        // if action can be fired even if finger was moved out of button, we may not to update pressed state
        if (actionUpOverButton && fingerID == this.fingerID) {
            if (isButtonTouched(touchPoint)) {
                if (!pressed) {
                    pressed = true;
                    soundDown();
                }
            }
            else {
                if (pressed) {
                    pressed = false;
                    soundUp();
                }
            }
        }
    }

    public void updateInWorldPosition(GameCamera camera) {
        sprite.setPosition(camera.screenToWorld(position));
    }

    protected boolean isButtonTouched(Vector2 touchPoint) {
        float distanceToButton = position.dst(touchPoint);
        return distanceToButton < radius;
    }

    public Vector2 getOnScreenPosition() {
        return position;
    }

    public int getFingerID() {
        return fingerID;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void render(SpriteBatch batch) {
        sprite.draw(batch, pressed ? 1 : 0);
    }
}
