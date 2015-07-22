package com.sergreen.bowrunner.Game.GameObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.sergreen.bowrunner.Utils.AnimatedSprite;
import com.sergreen.bowrunner.Utils.Global;

/**
 * Created on 30.04.2015 [SerGreen]
 */
public class Decoration extends GameObject {
    private final Vector2 position;
    protected AnimatedSprite sprite;
    private int spriteFrame;

    public Decoration(Vector2 position, String type) {
        super("decoration");

        this.position = position;

        int widthFrames = 1;
        int heightFrames = 1;
        float scale = 0.1f;
        int originY = 0;
        if(type.equals("flowers")) {
            widthFrames = 10;
            heightFrames = 5;
            scale = 0.05f;
            originY = 0;
        }
        else if(type.equals("fungi")) {
            widthFrames = 5;
            heightFrames = 3;
            scale = 0.05f;
            originY = 3;
        }
        else if(type.equals("forest_fore")) {
            widthFrames = 3;
            heightFrames = 1;
            scale = 0.07f;
            originY = 0;
        }
        else if(type.equals("village_fore")) {
            widthFrames = 4;
            heightFrames = 2;
            scale = 0.08f;
            originY = 0;
        }
        else if(type.equals("well_front")) {
            widthFrames = 1;
            heightFrames = 1;
            scale = 0.1f;
            originY = 147;
        }

        sprite = new AnimatedSprite(
                Global.assetManager.get("textures/decorations/" + type + ".png", Texture.class),
                widthFrames, heightFrames, 1, Animation.PlayMode.NORMAL);
        setPosition(position);
        sprite.setOriginY(originY);
        sprite.setScaleXY(scale);
        spriteFrame = Global.random.nextInt(sprite.getFramesCount());
    }

    protected void setPosition(Vector2 position) {
        sprite.setPosition(position.x, position.y + 1f);
    }

    @Override
    public Body getBody() {
        return null;
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public void update() {

    }

    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch, spriteFrame);
    }
}
