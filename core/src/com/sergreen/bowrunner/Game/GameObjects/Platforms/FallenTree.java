package com.sergreen.bowrunner.Game.GameObjects.Platforms;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.sergreen.bowrunner.Utils.Global;

/**
 * Created on 03.03.2015 [SerGreen]
 */
public class FallenTree extends Ground {
    float radius = 0.8f;

    public FallenTree(Vector2 position, World world) {
        super("tree");
        this.position = position;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(position);
        body = world.createBody(bodyDef);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 1f;
        fixtureDef.filter.categoryBits = Global.GROUND_BIT;
        fixtureDef.filter.maskBits = Global.PLAYER_BIT | Global.ARROW_BIT | Global.TARGET_BIT;

        body.createFixture(fixtureDef);
        shape.dispose();

        sprite = new Sprite(Global.assetManager.get("textures/ground/fallen_tree.png", Texture.class));
        sprite.setPosition(position.x, position.y);
        sprite.setScale(0.035f);
    }

    @Override
    public Vector2 getRightmostPoint() {
        return new Vector2(position.x + radius, position.y);
    }

    @Override
    public float getTopMostYPosition() {
        return position.y + radius;
    }
}
