package com.sergreen.bowrunner.Game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.sergreen.bowrunner.Game.GameObjects.GameObject;
import com.sergreen.bowrunner.Utils.Global;

/**
 * Created on 31.03.2015 [SerGreen]
 */
public abstract class Particle extends GameObject {
    protected float rotation = 0;
    protected float deltaRotation = 0;
    protected Vector2 position = Vector2.Zero;
    protected Vector2 speed = Vector2.Zero;
    protected float maxLifetime;
    protected float startTimeState;

    protected Particle(String id) {
        super(id);
        startTimeState = Global.timeState;
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    public boolean isDead() {
        return startTimeState + maxLifetime < Global.timeState;
    }

    @Override
    public Body getBody() {
        return null;
    }

    @Override
    public void update() {
        position.x += speed.x;
        position.y += speed.y;
        rotation += deltaRotation;
    }
}
