package com.sergreen.bowrunner.Game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Joint;
import com.sergreen.bowrunner.Game.GameObjects.Player;
import com.sergreen.bowrunner.Utils.AnimatedSprite;
import com.sergreen.bowrunner.Utils.Global;

/**
 * Created on 24.03.2015 [SerGreen]
 */
public class Skin {

    public float spriteScale;
    public static enum SkinType { STANDARD, NIGGER, NAKED}

    private Player player;

    private AnimatedSprite runLegs;
    private AnimatedSprite runTorso;
    private AnimatedSprite jumpLegs;
    private AnimatedSprite jumpTorso;
    private AnimatedSprite aimHands;
    private AnimatedSprite aimTorso;
    private Texture feather;
    private float featherScale;
    public Vector2 featherAttachPoint;

    public Skin(SkinType type, Player player, float scale) {
        this.player = player;
        this.spriteScale = scale;
        loadSprites(type.toString().toLowerCase());
        
        switch (type) {
            case STANDARD:
                featherAttachPoint = new Vector2(-player.getBodyHalfSize().x + 0.5f, player.getBodyHalfSize().y + 0.45f);
                break;

            case NIGGER:
                featherAttachPoint = new Vector2(-player.getBodyHalfSize().x + 0.5f, player.getBodyHalfSize().y + 0.45f);
                break;

            case NAKED:
                featherAttachPoint = new Vector2(-player.getBodyHalfSize().x + 0.5f, player.getBodyHalfSize().y + 0.45f);
                break;

            default:
                featherAttachPoint = new Vector2(0.0f, 0.1f);
                break;
        }

        featherScale = 0.2f / feather.getWidth();
    }

    private void loadSprites(String type) {
        feather = Global.assetManager.get("textures/skins/" + type + "/feather.png", Texture.class);

        runLegs = new AnimatedSprite(
                Global.assetManager.get("textures/skins/" + type + "/run_legs.png", Texture.class)
                , 9, 1, 0.05f, Animation.PlayMode.LOOP);
        runLegs.setScaleXY(spriteScale);

        runTorso = new AnimatedSprite(
                Global.assetManager.get("textures/skins/" + type + "/run_torso.png", Texture.class),
                9, 1, 0.05f, Animation.PlayMode.LOOP);
        runTorso.setScaleXY(spriteScale);

        jumpLegs = new AnimatedSprite(
                Global.assetManager.get("textures/skins/" + type + "/jump_legs.png", Texture.class),
                5, 1, 4f, Animation.PlayMode.NORMAL);
        jumpLegs.setScaleXY(spriteScale);

        jumpTorso = new AnimatedSprite(
                Global.assetManager.get("textures/skins/" + type + "/jump_torso.png", Texture.class),
                5, 1, 4f, Animation.PlayMode.NORMAL);
        jumpTorso.setScaleXY(spriteScale);

        aimTorso = new AnimatedSprite(
                Global.assetManager.get("textures/skins/" + type + "/aim_torso.png", Texture.class),
                1, 1, 4f, Animation.PlayMode.NORMAL);
        aimTorso.setScaleXY(spriteScale);

        aimHands = new AnimatedSprite(
                Global.assetManager.get("textures/skins/" + type + "/aim_hands.png", Texture.class),
                1, 6, 0.05f, Animation.PlayMode.NORMAL);
        aimHands.setScaleXY(spriteScale);
    }

    public void setAnimate(boolean value) {
        runLegs.animate = value;
        runTorso.animate = value;
    }

    public void update() {
        if(player.isLoadingArrow()) {
            aimTorso.setPosition(player.getBody().getPosition());
            aimTorso.setRotation((float) Math.toDegrees(player.getBody().getAngle()));

            aimHands.setPosition((player.getBody().getPosition().x) + 0.52f,
                                 (player.getBody().getPosition().y) + 0.55f);
            aimHands.setRotation(player.getAimingAngle());
        }

        if (player.isOnGround()) {
            if(!player.isAiming()) {
                runTorso.setPosition(player.getBody().getPosition());
                runTorso.setRotation((float) Math.toDegrees(player.getBody().getAngle()));
            }

            runLegs.setPosition(player.getBody().getPosition());
            runLegs.setRotation((float) Math.toDegrees(player.getBody().getAngle()));
        } else {
            if(!player.isAiming()) {
                jumpTorso.setPosition(player.getBody().getPosition());
                jumpTorso.setRotation((float) Math.toDegrees(player.getBody().getAngle()));
            }

            jumpLegs.setPosition(player.getBody().getPosition());
            jumpLegs.setRotation((float) Math.toDegrees(player.getBody().getAngle()));
        }
    }

    public void render(SpriteBatch batch) {
        renderBody(batch);
        renderTail(batch);
    }

    private void renderBody(SpriteBatch batch) {
        if(player.isLoadingArrow()) {
            aimTorso.draw(batch, 0);
            aimHands.draw(batch, Global.timeState - player.getAimStartTimeState());
        }

        if (player.isOnGround()) {
            if (!player.isAiming())
                runTorso.draw(batch, Global.timeState);
            runLegs.draw(batch, Global.timeState);
        } else {
            // converting vertical speed from [-inf .. 9.34] to [0 .. 20+] where 0 is max ySpeed and 20 is min
            float ySpeedTimeState = Math.max(player.getBody().getLinearVelocity().y * -1 + 9.4f, 0);
            if (!player.isAiming())
                jumpTorso.draw(batch, ySpeedTimeState);
            jumpLegs.draw(batch, ySpeedTimeState);
        }
    }

    private void renderTail(SpriteBatch batch) {
        Joint[] tail = player.getTail();
        for(int i=1; i<tail.length-1; i++)
        {
            float x = tail[i-1].getAnchorA().x;
            float y = tail[i-1].getAnchorA().y;
            float angle = Global.getAngleDegreesBetweenVectors(tail[i - 1].getAnchorA(), tail[i].getAnchorA());
            float w = tail[i-1].getAnchorA().dst(tail[i].getAnchorA());
            float scale = w/feather.getWidth();
            batch.draw(feather,
                    x, y - Global.WEIRD_SHIT,
                    0, feather.getHeight()/2,
                    feather.getWidth(), feather.getHeight(),
                    scale, featherScale,
                    angle,
                    0, 0,
                    feather.getWidth(),
                    feather.getHeight(),
                    false, false);
        }
    }
}
