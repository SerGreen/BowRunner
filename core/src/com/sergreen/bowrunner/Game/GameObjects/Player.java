package com.sergreen.bowrunner.Game.GameObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.sergreen.bowrunner.Game.Skin;
import com.sergreen.bowrunner.Utils.Global;

/**
 * Created on 09.02.2015 [SerGreen]
 */
public class Player extends GameObject {
    private final Vector2 bodyHalfSize = new Vector2(0.25f, 0.65f);

    public boolean realHumanBean = true;

    private Skin skin;
    private Joint[] tail;

    private Body legs;
    private float legsAngularSpeed = 55f;
    private float speedModifier = 1f;
    private RevoluteJoint legsJoint;

    private boolean dead = false;
    private float prevSpeed = 0;

    public int groundTouching = 0;
    private boolean jumping = false;
    private int jumpMaxFrames = 8;
    private int jumpCurFrames;

    private boolean aiming = false;
    private boolean loadingArrow = false;
    private float aimStartTimeState = 0;
    private float aimChargeTime = 0.3f;
    private float aimAngle = 0;
    private int quiverSize = 15, quiverMaxSize = 50, arrows = quiverSize;
    private boolean infiniteArrows = false;

    private int infiniteArrowsTimeLeft = 0;
    private int infiniteArrowsMaxTime = 400;

    public float initialXPos;
    private boolean enableAutoRun = true;
    private boolean enableDeath = true;

    private int stepInterval = 15;
    private int stepCurrent = 0;


    public Player(Vector2 position, World world, Skin.SkinType skinType) {
        this(position, world, skinType, 0.07f);
    }

    public Player(Vector2 position, World world, Skin.SkinType skinType, float spriteScale) {
        super("player");

        setSkin(skinType, spriteScale);
        createBody(position, world);
        createLegs(position, world);
        attachLegsToBody(position, world);
        attachTailToBody(world);

        initialXPos = position.x;

        //Gdx.app.log("P;ayer", "Mass: " + body.getMass());
    }

    public void setSkin(Skin.SkinType skinType, float spriteScale) {
        if(skinType == null)
            skinType = Skin.SkinType.STANDARD;

        skin = new Skin(skinType, this, spriteScale);
    }

    private void createBody(Vector2 position, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);
        body = world.createBody(bodyDef);
        body.setUserData(this);
        //body.setBullet(true);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(bodyHalfSize.x, bodyHalfSize.y);

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = shape;
        fixDef.density = 100f;
        fixDef.friction = 0f;
        fixDef.restitution = 0f;
        fixDef.filter.categoryBits = Global.PLAYER_BIT;
        fixDef.filter.maskBits = Global.GROUND_BIT | Global.PICKUP_BIT;

        body.createFixture(fixDef).setUserData("body");
        body.setFixedRotation(true);

        shape.dispose();
        createGroundSensor();
    }

    private void createLegs(Vector2 position, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(position.x, position.y - bodyHalfSize.y));
        legs = world.createBody(bodyDef);
        legs.setUserData(this);

        CircleShape shape = new CircleShape();
        shape.setRadius(bodyHalfSize.x);

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = shape;
        fixDef.density = 40f;
        fixDef.friction = 1f;
        fixDef.restitution = 0f;
        fixDef.filter.categoryBits = Global.PLAYER_BIT;
        fixDef.filter.maskBits = Global.GROUND_BIT | Global.SEMISOLID_BIT;

        legs.createFixture(fixDef).setUserData("legs");

        shape.dispose();
    }

    private void createGroundSensor() {
        //PolygonShape shape = new PolygonShape();
        //shape.setAsBox(bodyHalfSize.x / 2, bodyHalfSize.x / 2, new Vector2(0, -bodyHalfSize.y - bodyHalfSize.x), 0);
        CircleShape shape = new CircleShape();
        shape.setRadius(bodyHalfSize.x / 2);
        shape.setPosition(new Vector2(0, -bodyHalfSize.y - bodyHalfSize.x));

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = shape;
        fixDef.isSensor = true;
        fixDef.filter.categoryBits = Global.PLAYER_BIT;
        fixDef.filter.maskBits = Global.GROUND_BIT | Global.SEMISOLID_BIT;

        body.createFixture(fixDef).setUserData("sensor");
        shape.dispose();
    }

    private void attachLegsToBody(Vector2 position, World world) {
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.initialize(legs, body, new Vector2(position.x, position.y - bodyHalfSize.y));
        jointDef.enableMotor = true;
        jointDef.maxMotorTorque = 1000f;
        if(enableAutoRun)
            jointDef.motorSpeed = legsAngularSpeed;

        legsJoint = (RevoluteJoint) world.createJoint(jointDef);
    }

    public void attachTailToBody(World world) {
        float radius = 0.1f;
        int amount = 20;

        tail = new Joint[amount];

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);
        shape.setPosition(new Vector2(0, 0));

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.01f;
        fixtureDef.filter.categoryBits = Global.PLAYER_BIT;
        fixtureDef.filter.maskBits = Global.GROUND_BIT;

        Body prevBody = body;

        for (int i=0; i<amount; i++) {
            float newX = body.getPosition().x + skin.featherAttachPoint.x - i * radius * 2;
            float newY = body.getPosition().y + skin.featherAttachPoint.y;

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(newX, newY);
            bodyDef.allowSleep = false;
            Body body = world.createBody(bodyDef);
            body.createFixture(fixtureDef);
            body.setGravityScale(0.0005f);
            body.setLinearDamping(0.99f);

            RevoluteJointDef jointDef = new RevoluteJointDef();
            jointDef.initialize(prevBody, body, new Vector2(newX, newY));
            Joint revoluteJoint = world.createJoint(jointDef);
            tail[i] = revoluteJoint;

            prevBody = body;
        }

        shape.dispose();
    }

    private void jump(float force) {
        float mod = Math.max(1, speedModifier*0.77f);
        body.applyForceToCenter(0, force*mod, true);
    }

    public void jumpStart() {
        jumping = true;
        jumpCurFrames = 0;
        body.setLinearVelocity(body.getLinearVelocity().x, 0);
        jump(20000f);
        Global.soundManager.playJumpSound();
    }

    private void jumpContinue() {
        jump(4000f);
    }

    public void jumpEnd() {
        jumping = false;
    }

    public void aimStart(float angle) {
        Global.soundManager.playLoadSound();
        aiming = true;
        loadingArrow = true;
        aimStartTimeState = Global.timeState;
        aimAngle = angle;
        if (!infiniteArrows)
            arrows = Math.max(--arrows, 0);
    }

    public void aimChangeAngle(float angle) {
        if (aiming) {
            if (70 < angle && angle < 180)
                angle = 70;
            if (180 <= angle && angle < 320)
                angle = 320;

            aimAngle = angle;
        }
    }

    public void aimStop() {
        aiming = false;
    }

    public void addArrows(int amount) {
        arrows = Math.min(arrows + amount, quiverSize);
    }

    public void increaseMaxArrows(int amount) {
        quiverSize = Math.min(quiverSize + amount, quiverMaxSize);
    }

    public void shootArrow() {
        loadingArrow = false;
        Global.soundManager.stopLoadSound();
        Global.soundManager.playShootSound();
    }

    public void startMovingLeft() {
        legsJoint.setMotorSpeed(-legsAngularSpeed);
        legsJoint.enableMotor(true);
    }
    public void startMovingRight() {
        legsJoint.setMotorSpeed(legsAngularSpeed);
        legsJoint.enableMotor(true);
    }
    public void stopMoving() {
        legsJoint.setMotorSpeed(0);
        legsJoint.enableMotor(false);
    }

    public void speedUd() {
        if (speedModifier < 3f)
            speedModifier += 0.1f;
        legsJoint.setMotorSpeed(legsAngularSpeed*speedModifier);
    }
    public void slowDown() {
        if (speedModifier > 1f)
            speedModifier -= 0.1f;
        legsJoint.setMotorSpeed(legsAngularSpeed*speedModifier);
    }

    public float getDistanceTravelled() {
        return body.getPosition().x - initialXPos;
    }

    @Override
    public void update() {
        skin.update();

        if (!dead) {
            if (enableDeath)
                checkDeathCondition();


            // if jump button is held then make long jump
            // long jump has a limited duration in frames
            if (jumping)
                if (jumpCurFrames < jumpMaxFrames) {
                    jumpContinue();
                    jumpCurFrames++;
                } else {
                    jumpEnd();
                    //Gdx.app.log("Jump", "Exhausted");
                }

            if(isOnGround()) {
                stepCurrent++;
                if(stepCurrent >= stepInterval*speedModifier) {
                    stepCurrent = 0;
                    Global.soundManager.playStepSound();
                }
            }
        }

        if(infiniteArrows) {
            infiniteArrowsTimeLeft--;
            if(infiniteArrowsTimeLeft <= 0)
                infiniteArrows = false;
        }
    }

    private void checkDeathCondition() {
        // if player suddenly stopped moving then he is dead
        float speed = body.getLinearVelocity().x;
        if (prevSpeed >= 0.1 && speed < 0.1)
            kill();
        prevSpeed = speed;
    }

    private void kill() {
        dead = true;
        body.setFixedRotation(false);
        body.getFixtureList().first().setFriction(0.5f);
        body.getFixtureList().first().setRestitution(0.5f);
        legsJoint.setMotorSpeed(0);
        skin.setAnimate(false);
        aimStop();
        for(Joint joint : tail) {
            joint.getBodyB().setGravityScale(0.9f);
        }
    }

    public Vector2 getCenterBottomPoint() {
        return new Vector2(body.getPosition().x, legs.getPosition().y - bodyHalfSize.x / 2);
    }
    public float getRotation() { return body.getAngle(); }
    public Vector2 getVelocity() { return body.getLinearVelocity(); }
    public float getAimingAngle() {
        return aimAngle;
    }
    public boolean isArrowLoaded() {
        return (Global.timeState - aimStartTimeState) >= aimChargeTime;
    }
    public boolean isDead() { return dead; }
    public boolean isOnGround() {
        return groundTouching > 0;
    }
    public boolean isAiming() {
        return aiming;
    }
    public boolean isLoadingArrow() {
        return loadingArrow;
    }
    public Joint[] getTail() {
        return tail;
    }
    public int getArrows() {
        return arrows;
    }
    public int getQuiverSize() {
        return quiverSize;
    }
    public Vector2 getBodyHalfSize() {
        return bodyHalfSize;
    }
    public float getAimStartTimeState() {
        return aimStartTimeState;
    }

    @Override
    public void render(SpriteBatch batch) {
        skin.render(batch);
        //Global.font.drawText(String.valueOf(getPosition().y), batch,  getPosition(), 0.02f);
    }

    public boolean isInfiniteArrows() {
        return infiniteArrows;
    }
    public int getInfiniteArrowsTimeLeft() {
        return infiniteArrowsTimeLeft;
    }
    public int getInfiniteArrowsMaxTime() {
        return infiniteArrowsMaxTime;
    }
    public void activateInfiniteArrows() {
        infiniteArrows = true;
        infiniteArrowsTimeLeft = infiniteArrowsMaxTime;
    }

    public float getSpeedMod() {
        return speedModifier;
    }
}
