package com.sergreen.bowrunner.Game.GameObjects.Targets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.sergreen.bowrunner.Utils.Global;

/**
 * Created on 27.02.2015 [SerGreen]
 */
public class ArcheryTarget extends Target {
    Sprite sprite;

    public ArcheryTarget(Vector2 position, World world) {
        super("target", 2);
        scoreCost = 10;

        sprite = new Sprite(Global.assetManager.get("textures/targets/target.png", Texture.class));

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(position);
        body = world.createBody(bodyDef);
        body.setUserData(this);

        CircleShape shape = new CircleShape();
        shape.setRadius(0.8f);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 2f;
        fixtureDef.filter.categoryBits = Global.TARGET_BIT;
        fixtureDef.filter.maskBits = Global.GROUND_BIT | Global.ARROW_BIT;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    @Override
    public void update() {
        sprite.setPosition((body.getPosition().x) - sprite.getWidth()/2,
                (body.getPosition().y) - sprite.getHeight()/2);
        sprite.setRotation((float)Math.toDegrees(body.getAngle()));
    }

    @Override
    public void render(SpriteBatch batch) {
        float scale = 0.03f;    //TODO Костыль

        batch.draw(sprite,
                sprite.getX(), sprite.getY(),
                sprite.getOriginX(), sprite.getOriginY(),
                sprite.getWidth(), sprite.getHeight(),
                scale, scale,
                sprite.getRotation());
    }
}
