package com.sergreen.bowrunner.Game.GameObjects.Targets;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.sergreen.bowrunner.Game.GameObjects.Platforms.Ground;
import com.sergreen.bowrunner.Utils.AnimatedSprite;
import com.sergreen.bowrunner.Utils.Global;

import java.util.ArrayList;

/**
 * Created on 22.02.2015 [SerGreen]
 */
public class Bird extends Target implements IKillableTarget {
    private final Vector2 bodyHalfSize = new Vector2(0.6f, 0.3f);

    private AnimatedSprite sprite;
    private int currentTimeout = 0;
    private float maxFlightHeight;
    private float heightAboveGround = 5 + Global.random.nextFloat()*6;
    private boolean sitting;
    private int facing;
    private boolean dead = false;
    private boolean touchedGround = false;
    private float flapStartTimeState = Global.timeState;

    public Bird(Vector2 position, World world, boolean sitting, boolean rightFaced) {
        super("bird", 1);
        scoreCost = 50;

        facing = rightFaced ? 1 : -1;
        sprite = new AnimatedSprite(
                Global.assetManager.get("textures/targets/bird.png", Texture.class),
                5, 1, 0.08f, Animation.PlayMode.NORMAL);
        sprite.setScaleX(0.065f * facing);
        sprite.setScaleY(0.065f);

        createBody(position,  world);
        this.sitting = sitting;
        maxFlightHeight = position.y;

        //Gdx.app.log("Bird mass", String.valueOf(body.getMass()));
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

        shape.dispose();
    }


    public void updateMaxFlightHeight(ArrayList<Ground> groundList) {
        if (groundList.size() > 0) {
            // find nearest ground object
            int nearestIndex = 0;
            float nearestDistance = Float.MAX_VALUE;
            for (int i = 0; i < groundList.size(); i++) {
                float distance = getPosition().dst2(groundList.get(i).getPosition());
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestIndex = i;
                }
            }

            maxFlightHeight = groundList.get(nearestIndex).getTopMostYPosition() + heightAboveGround;
        }
    }
    //*/

    public void updateMaxFlightHeight(float cameraY) {
        maxFlightHeight = cameraY;
    }

    @Override
    public boolean isDead() {
        return dead;
    }

    @Override
    public boolean isTouchedGround() {
        return touchedGround;
    }

    @Override
    public void setTouchedGround(boolean value) {
        touchedGround = value;
    }

    @Override
    public void kill() {
        dead = true;
        body.setFixedRotation(false);
        sprite.animate = false;
    }

    @Override
    public int getScoreCost() {
        return facing < 0 ? (int) (scoreCost * 1.4f) : scoreCost;
    }

    @Override
    public void update() {
        if(!dead && !sitting) {
            if (body.getPosition().y < maxFlightHeight && currentTimeout == 0) {
                float flapPowerUp = 250f;
                body.setLinearVelocity(body.getLinearVelocity().x, 0);
                body.applyForceToCenter(0, flapPowerUp, true);
                currentTimeout = 15;
                flapStartTimeState = Global.timeState;
                Global.soundManager.playFlapSound();
            }
            else if (currentTimeout > 0)
                currentTimeout--;

            float flapPowerHorizontal = 80f;
            // applying different speeds for birds moving right and left
            if (Math.abs(body.getLinearVelocity().x) < 14 + 4 * facing)
                body.applyForceToCenter(flapPowerHorizontal * facing, 0, true);
        }

        sprite.setPosition((body.getPosition().x),
                           (body.getPosition().y));
        sprite.setRotation((float)Math.toDegrees(body.getAngle()));
    }

    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch, Global.timeState- flapStartTimeState);
        //Global.font.drawText(String.valueOf(maxFlightHeight), batch, getPosition(), 0.03f);
    }
}
