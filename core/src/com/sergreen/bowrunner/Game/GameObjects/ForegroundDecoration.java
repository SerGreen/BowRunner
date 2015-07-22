package com.sergreen.bowrunner.Game.GameObjects;

import com.badlogic.gdx.math.Vector2;

/**
 * Created on 30.04.2015 [SerGreen]
 */
public class ForegroundDecoration extends Decoration {

    public ForegroundDecoration(Vector2 position, String type) {
        super(position, type);
    }

    @Override
    protected void setPosition(Vector2 position) {
        float shiftY = -1f;
        sprite.setPosition(position.x, position.y + shiftY);
    }
}
