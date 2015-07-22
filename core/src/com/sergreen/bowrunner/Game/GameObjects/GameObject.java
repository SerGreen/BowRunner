package com.sergreen.bowrunner.Game.GameObjects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by SerGreen on 20.02.2015.
 */
public abstract class GameObject {
    public final String id;
    protected Body body;

    protected GameObject(String id) {
        this.id = id;
    }
    public Vector2 getPosition() {
        return body.getPosition();
    }
    public abstract void update();
    public  boolean isOutOfView(OrthographicCamera camera) {
        return getPosition().x < camera.position.x - camera.viewportWidth * 2 ||
               getPosition().x > camera.position.x + camera.viewportWidth * 5 ||
               getPosition().y < camera.position.y - camera.viewportHeight * 2;
    }

    public Body getBody() {
        return body;
    }

    public abstract void render(SpriteBatch batch);
}
