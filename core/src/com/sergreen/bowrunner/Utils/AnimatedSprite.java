package com.sergreen.bowrunner.Utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Created on 16.03.2015 [SerGreen]
 */
public class AnimatedSprite {
    private Animation animation;

    private int width, height, originX, originY;
    private float scaleX, scaleY, rotation;
    private Vector2 position;
    private float prevTimeState = 0;
    public boolean animate = true;

    public AnimatedSprite(Texture spriteSheet, int frameCols, int frameRows, float frameTime, Animation.PlayMode playMode) {
        width = spriteSheet.getWidth() / frameCols;
        height = spriteSheet.getHeight() / frameRows;
        this.position = new Vector2(0, 0);
        originX = width/2;
        originY = height/2;
        scaleX = scaleY = 1;
        rotation = 0;

        TextureRegion[][] tempFrames = TextureRegion.split(spriteSheet, width, height);
        TextureRegion[] frames = new TextureRegion[frameCols * frameRows];
        int index = 0;
        for (int i = 0; i < frameRows; i++) {
            for (int j = 0; j < frameCols; j++) {
                frames[index++] = tempFrames[i][j];
            }
        }

        if(frameTime <= 0)
            frameTime = 0.05f;

        animation = new Animation(frameTime, frames);
        animation.setPlayMode(playMode);
    }

    public void draw(SpriteBatch batch, float timeState) {
        if (!animate)
            timeState = prevTimeState;
        else
            prevTimeState = timeState;

        TextureRegion currentFrame = animation.getKeyFrame(timeState);
        batch.draw(currentFrame,
                position.x-originX, position.y-originY,
                originX, originY,
                width, height,
                scaleX, scaleY,
                rotation);
    }

    public int getFramesCount() {
        return animation.getKeyFrames().length;
    }

    public float getAnimationDuration() {
        return animation.getAnimationDuration();
    }

    public float getFrameDuration() {
        return animation.getFrameDuration();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getOriginX() {
        return originX;
    }

    public int getOriginY() {
        return originY;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setOriginX(int originX) {
        this.originX = originX;
    }

    public void setOriginY(int originY) {
        this.originY = originY;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public void setScaleXY(float scaleXY) {
        this.scaleX = this.scaleY = scaleXY;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getRotation() {
        return rotation;
    }

    public void setPosition(float x, float y) {
        this.position = new Vector2(x,  y);
    }

    public void setPosition(Vector2 pos) {
        this.position = pos;
    }

    public Vector2 getPosition() {
        return position;
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }
}
