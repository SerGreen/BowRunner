package com.sergreen.bowrunner.Game.GameObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.sergreen.bowrunner.Utils.Global;

/**
 * Created on 19.02.2015 [SerGreen]
 */
public class Arrow extends GameObject {
    private Sprite sprite;
    public boolean hitSomething = false;
    private final float dragConstant = 0.02f;
    private final Vector2 arrowHalfSize = new Vector2(0.7f, 0.06f);
    private boolean stickIn = false;
    private float scoreMultiplier = 0.4f;
    private float softcapScoreMultiplier = 2f;
    private float airShotBonusMultiplier = 1.2f;
    private boolean airShot;

    public Arrow(World world, Vector2 position, float angle, float speed, boolean airShot) {
        this(world, position, angle, speed, new Vector2(0, 0), airShot);
    }

    public Arrow(World world, Vector2 position, float angle, float speed, Vector2 speedModifier, boolean airShot) {
        super("arrow");
        this.airShot = airShot;

        createArrowBody(world, position, angle);
        body.setLinearVelocity((float)Math.cos(Math.toRadians(angle))*speed + speedModifier.x,
                               (float)Math.sin(Math.toRadians(angle))*speed + speedModifier.y);

        sprite = new Sprite(Global.assetManager.get("textures/arrow.png", Texture.class));

        //Gdx.app.log("Arrow mass", String.valueOf(body.getMass()));
    }

    private void createArrowBody(World world, Vector2 position, float angle) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(position);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.angle = angle* MathUtils.degreesToRadians;
        bodyDef.angularDamping = 1.5f;
        body = world.createBody(bodyDef);
        body.setUserData(this);
        //body.setBullet(true);

        PolygonShape shape = new PolygonShape();
        shape.set(new float[]{ -arrowHalfSize.x,         0,
                                arrowHalfSize.x * 0.5f,  arrowHalfSize.y,
                                arrowHalfSize.x,         0,
                                arrowHalfSize.x * 0.5f, -arrowHalfSize.y});

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.3f;
        //fixtureDef.restitution = 0.5f;
        fixtureDef.density = 0.8f;
        fixtureDef.filter.categoryBits = Global.ARROW_BIT;
        fixtureDef.filter.maskBits = Global.GROUND_BIT | Global.ARROW_BIT;

        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(arrowHalfSize.y / 6);
        circleShape.setPosition(new Vector2(arrowHalfSize.x, 0));

        FixtureDef arrowTip = new FixtureDef();
        arrowTip.shape = circleShape;
        arrowTip.filter.categoryBits = Global.ARROW_BIT;
        arrowTip.filter.maskBits = Global.GROUND_BIT | Global.TARGET_BIT | Global.ARROW_BIT;
        arrowTip.isSensor = true;

        body.createFixture(arrowTip).setUserData("tip");
        body.createFixture(fixtureDef).setUserData("stick");

        shape.dispose();
        circleShape.dispose();
    }

    @Override
    public void update() {
        if(!hitSomething) {
            body.setTransform(body.getPosition(), getMovingAngle());
            //applyDrag();
            if (scoreMultiplier < softcapScoreMultiplier)
                scoreMultiplier += 0.02f;
            else
                scoreMultiplier += 0.01f;
        }

        if(stickIn) {
            stickIn = false;
            moveForward(arrowHalfSize.x);
        }

        sprite.setPosition((body.getPosition().x) - sprite.getWidth()/2,
                           (body.getPosition().y) - sprite.getHeight()/2);
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
    }

    // applies air drag to arrow's tail
    private void applyDrag() {
        float flightSpeed = body.getLinearVelocity().len();
        float bodyAngle = body.getAngle();
        Vector2 pointingDirection = new Vector2((float)Math.cos(bodyAngle), (float)-Math.sin(bodyAngle));
        float flyingAngle = body.getLinearVelocity().angleRad();
        Vector2 flightDirection = new Vector2((float)Math.cos(flyingAngle), (float)Math.sin(flyingAngle));
        float dot = flightDirection.dot(pointingDirection);
        float dragForceMagnitude = (1 - Math.abs(dot))*flightSpeed*flightSpeed*dragConstant*body.getMass();
        Vector2 arrowTailPosition = body.getWorldPoint(new Vector2(-arrowHalfSize.x, 0));
        body.applyForce(new Vector2(dragForceMagnitude * -flightDirection.x, dragForceMagnitude * -flightDirection.y), arrowTailPosition, true);
    }

    public float getMovingAngle() {
        return body.getLinearVelocity().angleRad();
    }

    public float getScoreMultiplier() {
        if(airShot)
            scoreMultiplier *= airShotBonusMultiplier;
        return scoreMultiplier;
    }

    public void moveForward(float distance) {
        float angle = body.getAngle();
        float dx = (float) Math.cos(angle) * distance;
        float dy = (float) Math.sin(angle) * distance;
        body.setTransform(body.getPosition().x + dx, body.getPosition().y + dy, angle);
    }

    @Override
    public void render(SpriteBatch batch) {
        float scale = 0.04f;    //TODO Костыль

        batch.draw(sprite,
                sprite.getX(), sprite.getY(),
                sprite.getOriginX(), sprite.getOriginY(),
                sprite.getWidth(), sprite.getHeight(),
                scale, scale,
                sprite.getRotation());

        //Global.font.drawText(String.valueOf(getLinearVelocity().len()), batch, getPosition(), 0.02f);
    }

    public Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
    }
}