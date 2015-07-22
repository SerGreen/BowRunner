package com.sergreen.bowrunner.Game.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.sergreen.bowrunner.Game.Particle;
import com.sergreen.bowrunner.Utils.AnimatedSprite;
import com.sergreen.bowrunner.Utils.Global;

/**
 * Created on 29.04.2015 [SerGreen]
 */
public class BloodSpray extends Particle {
    private AnimatedSprite sprite;

    public BloodSpray(Vector2 position, float angle) {
        super("blood");

        if(Global.random.nextBoolean())
            sprite = new AnimatedSprite(Global.assetManager.get("textures/blood.png", Texture.class), 8, 1, 0.02f, Animation.PlayMode.NORMAL);
        else
            sprite = new AnimatedSprite(Global.assetManager.get("textures/blood2.png", Texture.class), 5, 1, 0.02f, Animation.PlayMode.NORMAL);
        sprite.setPosition(position);
        sprite.setRotation(angle);
        sprite.setScaleXY(0.05f);
        sprite.setOriginX(0);
    }

    @Override
    public boolean isDead() {
        return Global.timeState > startTimeState + sprite.getAnimationDuration();
    }

    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch, Global.timeState - startTimeState);
    }
}
