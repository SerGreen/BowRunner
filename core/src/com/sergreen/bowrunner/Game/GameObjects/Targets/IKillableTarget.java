package com.sergreen.bowrunner.Game.GameObjects.Targets;

import com.badlogic.gdx.math.Vector2;

/**
 * Created on 09.03.2015 [SerGreen]
 */
public interface IKillableTarget {
    public void kill();
    public boolean isDead();
    public boolean isTouchedGround();
    public void setTouchedGround(boolean value);

    public Vector2 getPosition();
}
