package com.sergreen.bowrunner.Game.GameObjects.Platforms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.sergreen.bowrunner.Utils.Global;

/**
 * Created on 04.03.2015 [SerGreen]
 */
public class OneWayPlatform extends Ground {

    public Vector2 halfSize;
    public boolean solid = false;

    public OneWayPlatform(String type, Vector2 position, float width, float height, World world) {
        super("one way");

        this.halfSize = new Vector2(width/2, height/2);
        this.position = position;

        sprite = new Sprite(Global.assetManager.get("textures/ground/" + type + ".png", Texture.class));
        sprite.setScale(height / sprite.getHeight());
        sprite.setPosition(position.x, position.y);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(position);
        body = world.createBody(bodyDef);
        body.setUserData(this);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(halfSize.x, halfSize.y);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.5f;
        fixtureDef.filter.categoryBits = Global.SEMISOLID_BIT;
        fixtureDef.filter.maskBits = Global.PLAYER_BIT;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public Vector2 getRightmostPoint() {
        return new Vector2(body.getPosition().x + halfSize.x, body.getPosition().y + halfSize.y);
    }

    @Override
    public float getTopMostYPosition() {
        return position.y + halfSize.y;
    }

    public Vector2 getCenterTopPoint() {
        return new Vector2(body.getPosition().x, body.getPosition().y + halfSize.y);
    }
}
