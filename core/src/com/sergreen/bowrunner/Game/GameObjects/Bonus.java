package com.sergreen.bowrunner.Game.GameObjects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.sergreen.bowrunner.Utils.AnimatedSprite;
import com.sergreen.bowrunner.Utils.Global;

/**
 * Created on 02.04.2015 [SerGreen]
 */
public class Bonus extends GameObject {
    public static enum BonusType { ARROW, FIVE_ARROWS, QUIVER_UPGRADE, SCORE, SCORE_MULTIPLIER, INFINITE_ARROWS, SLOWDOWN}

    private AnimatedSprite bubble;
    private AnimatedSprite sprite;
    public final BonusType type;
    private boolean picked = false;
    private float pickedTimeState;
    private float bodyRadius = 1f;

    public Bonus(BonusType type, Vector2 position, World world) {
        super("pickup");
        this.type = type;
        loadSprite();
        sprite.setPosition(position);
        sprite.setScaleXY(0.06f);

        bubble = new AnimatedSprite(
                Global.assetManager.get("textures/pickups/bonus_bubble.png", Texture.class),
                7, 1, 0.02f, Animation.PlayMode.NORMAL);
        bubble.setPosition(position);
        bubble.setScaleXY(0.06f);
        createBody(position, world);
    }

    private void loadSprite() {
        switch (type) {
            case ARROW:
                sprite = new AnimatedSprite(
                        Global.assetManager.get("textures/pickups/bonus_arrow.png", Texture.class),
                        1, 1, 0.2f, Animation.PlayMode.LOOP);
                break;
            case FIVE_ARROWS:
                sprite = new AnimatedSprite(
                        Global.assetManager.get("textures/pickups/bonus_5arrows.png", Texture.class),
                        1, 1, 0.2f, Animation.PlayMode.LOOP);
                break;
            case QUIVER_UPGRADE:
                sprite = new AnimatedSprite(
                        Global.assetManager.get("textures/pickups/bonus_quiver.png", Texture.class),
                        1, 1, 0.2f, Animation.PlayMode.LOOP);
                break;
            case SCORE:
                sprite = new AnimatedSprite(
                        Global.assetManager.get("textures/pickups/bonus_score.png", Texture.class),
                        1, 1, 0.2f, Animation.PlayMode.LOOP);
                break;
            case SCORE_MULTIPLIER:
                sprite = new AnimatedSprite(
                        Global.assetManager.get("textures/pickups/bonus_multiplier.png", Texture.class),
                        1, 1, 0.2f, Animation.PlayMode.LOOP);
                break;
            case SLOWDOWN:
                sprite = new AnimatedSprite(
                        Global.assetManager.get("textures/pickups/bonus_slowdown.png", Texture.class),
                        1, 1, 0.2f, Animation.PlayMode.LOOP);
                break;
            case INFINITE_ARROWS:
                sprite = new AnimatedSprite(
                        Global.assetManager.get("textures/pickups/bonus_infinite.png", Texture.class),
                        1, 1, 0.2f, Animation.PlayMode.LOOP);
                break;
            default:
                sprite = new AnimatedSprite(
                        Global.assetManager.get("textures/pickups/base.png", Texture.class),
                        1, 1, 0.2f, Animation.PlayMode.LOOP);
                break;
        }
    }

    private void createBody(Vector2 position, World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(position);
        body = world.createBody(bodyDef);
        body.setUserData(this);

        CircleShape shape = new CircleShape();
        shape.setRadius(bodyRadius);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = Global.PICKUP_BIT;
        fixtureDef.filter.maskBits = Global.PLAYER_BIT;

        body.createFixture(fixtureDef);
        shape.dispose();
    }

    // generator of (weighted) random bonus. Slowdown bonus can appear only after speed is increased
    public static BonusType getRandomWeightedType(boolean generateSlowdown) {
        // weights of bonuses. ORDER IS IMPORTANT, should be the same as in enum
        int[] weights = new int[] { 25,     // 1 arrow
                                    12,     // 5 arrows
                                    1,      // quiver upgrade
                                    9,      // +100 score
                                    2,      // score 2x multiplier
                                    1,      // infinite arrows
                                    4};     // slowdown

        int sumOfWeights = 0;
        for (int i = 0; i < weights.length - (generateSlowdown ? 0 : 1); i++)
            sumOfWeights += weights[i];

        int dice = Global.random.nextInt(sumOfWeights);
        int tempSum = 0;
        for (int i = 0; i < weights.length; i++) {
            tempSum += weights[i];
            if(dice < tempSum)
                return BonusType.values()[i];
        }

        return BonusType.ARROW;
    }

    public static BonusType getRandomRareType(boolean generateSlowdown) {
        switch (Global.random.nextInt(5)) {
            case 0:
                return BonusType.INFINITE_ARROWS;
            case 1:
                return BonusType.QUIVER_UPGRADE;
            case 2:
                return BonusType.SCORE_MULTIPLIER;
            case 3:
                return generateSlowdown ? BonusType.SLOWDOWN : BonusType.SCORE;
            case 4:
            default:
                return BonusType.SCORE;
        }
    }

    public void pickUp() {
        picked = true;
        pickedTimeState = Global.timeState;
        Global.soundManager.playBubbleSound();
    }

    public boolean isDead() {
        return picked && Global.timeState - pickedTimeState > bubble.getAnimationDuration();

    }

    @Override
    public void update() { }

    @Override
    public void render(SpriteBatch batch) {
        if(!picked)
            sprite.draw(batch, 0);

        bubble.draw(batch, picked ? Global.timeState - pickedTimeState : 0);
    }
}
