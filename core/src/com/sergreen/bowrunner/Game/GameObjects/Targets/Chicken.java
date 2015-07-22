package com.sergreen.bowrunner.Game.GameObjects.Targets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.sergreen.bowrunner.Utils.AnimatedSprite;
import com.sergreen.bowrunner.Utils.Global;

/**
 * Created on 09.03.2015 [SerGreen]
 */
public class Chicken extends Target implements IKillableTarget {
    private final Vector2 bodyHalfSize = new Vector2(0.7f, 0.4f);
    private float maxSpeed;
    private AnimatedSprite sprite;
    private int facing;
    private boolean dead = false;
    public boolean touchedSomething = false;
    private float spawnTimeState = Global.timeState;

    public Chicken(Vector2 position, World world, boolean facingRight) {
        super("chicken", 0);
        scoreCost = 30;

        this.facing = facingRight ? 1 : -1;
        maxSpeed = 0.9f + Global.random.nextFloat()*0.8f;
        sprite = new AnimatedSprite(
                Global.assetManager.get("textures/targets/chicken.png", Texture.class),
                3, 1, 0.1f, Animation.PlayMode.LOOP_PINGPONG);
        sprite.setScaleXY(bodyHalfSize.x * 2 / sprite.getWidth());
        sprite.setScaleX(sprite.getScaleX() * this.facing);

        createBody(position, world);
    }

    private void createBody(Vector2 position, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);
        bodyDef.angularDamping = 1.5f;
        body = world.createBody(bodyDef);
        body.setUserData(this);
        body.setFixedRotation(true);

        PolygonShape shape = new PolygonShape();
        shape.set(new float[]{ -bodyHalfSize.x,       bodyHalfSize.y*0.5f,
                               -bodyHalfSize.x*0.6f,  bodyHalfSize.y,
                                bodyHalfSize.x*0.6f,  bodyHalfSize.y,
                                bodyHalfSize.x,       bodyHalfSize.y*0.5f,
                                bodyHalfSize.x,      -bodyHalfSize.y*0.5f,
                                bodyHalfSize.x*0.6f, -bodyHalfSize.y,
                               -bodyHalfSize.x*0.6f, -bodyHalfSize.y,
                               -bodyHalfSize.x,      -bodyHalfSize.y*0.5f});

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.5f;
        fixtureDef.density = 1.2f;
        fixtureDef.filter.categoryBits = Global.TARGET_BIT;
        fixtureDef.filter.maskBits = Global.GROUND_BIT | Global.ARROW_BIT;
        body.createFixture(fixtureDef);

        shape.setAsBox(bodyHalfSize.x*0.1f, bodyHalfSize.y*0.5f, new Vector2(0, -bodyHalfSize.y), 0);
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);

        shape.dispose();
    }

    @Override
    public boolean isDead() {
        return dead;
    }

    @Override
    public boolean isTouchedGround() {
        return touchedSomething;
    }

    @Override
    public void setTouchedGround(boolean value) {
        touchedSomething = value;
    }

    @Override
    public void kill() {
        dead = true;
        body.setFixedRotation(false);
        sprite.animate = false;
    }

    @Override
    public int getScoreCost() {
        return scoreCost;
    }

    @Override
    public void update() {
        if(!dead) {
            if(Math.abs(body.getLinearVelocity().x) < maxSpeed)
                body.applyForceToCenter(100 * facing, 0, true);
        }

        sprite.setPosition(body.getPosition());
        sprite.setRotation((float)Math.toDegrees(body.getAngle()));
    }

    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch, Global.timeState-spawnTimeState);

        /*
        batch.draw( sprite,
                    sprite.getX(), sprite.getY(),
                    sprite.getOriginX(), sprite.getOriginY(),
                    sprite.getWidth(), sprite.getHeight(),
                    sprite.getScaleX()*facing, sprite.getScaleY(),
                    sprite.getRotation());
        */
    }
}
