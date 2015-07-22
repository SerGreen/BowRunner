package com.sergreen.bowrunner.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.sergreen.bowrunner.Game.GameObjects.Arrow;
import com.sergreen.bowrunner.Game.GameObjects.Bonus;
import com.sergreen.bowrunner.Game.GameObjects.GameObject;
import com.sergreen.bowrunner.Game.GameObjects.Targets.Bird;
import com.sergreen.bowrunner.Game.GameObjects.Targets.IKillableTarget;
import com.sergreen.bowrunner.Game.GameObjects.Platforms.OneWayPlatform;
import com.sergreen.bowrunner.Game.GameObjects.Player;
import com.sergreen.bowrunner.Game.GameObjects.Targets.Target;
import com.sergreen.bowrunner.Screens.GameScreen;
import com.sergreen.bowrunner.Utils.Global;

import java.util.ArrayList;

/**
 * Created on 20.02.2015 [SerGreen]
 */
public class CollisionController implements ContactListener {
    GameScreen gameScreen;

    public CollisionController(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public ArrayList<Body> weldList = new ArrayList<Body>();

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        GameObject bodyA = (GameObject) fa.getBody().getUserData();
        GameObject bodyB = (GameObject) fb.getBody().getUserData();

        String fixDataA = (String) fa.getUserData();

        byte iteration = 0;

        // here i swap fixture A and fixture B and do while{} code again
        // in order to not write the same code for A and B twice
        while (iteration < 2) {
            if (bodyA != null) {
                if (bodyA.id.equals("arrow")) {
                    Arrow arrow = (Arrow) bodyA;
                    arrowBeginContact(arrow, fa, fb, fixDataA, bodyB);
                }
                if (bodyA.id.equals("bird") || bodyA.id.equals("chicken")) {
                    //checking if dead bird fell on ground
                    IKillableTarget killable = (IKillableTarget) bodyA;
                    killableBeginContact(killable, bodyB);
                }
                // player collisions with ground
                if (bodyA.id.equals("player")) {
                    Player player = (Player) bodyA;
                    playerBeginContact(player, fixDataA, bodyB);
                }
            }

            // here i swap data from fixture A and fixture B
            fa = contact.getFixtureB();
            fb = contact.getFixtureA();
            bodyA = (GameObject) fa.getBody().getUserData();
            bodyB = (GameObject) fb.getBody().getUserData();
            fixDataA = (String) fa.getUserData();
            iteration++;
        }
    }

    private void killableBeginContact(IKillableTarget killable, GameObject bodyB) {
        if (bodyB != null && bodyB.id.equals("ground")) {
            if (!killable.isTouchedGround()) {
                killable.setTouchedGround(true);
                if (killable instanceof Bird) {
                    if (!killable.isDead())
                        killable.kill();
                    Global.soundManager.playArrowHitFleshSound(killable.getPosition().x, gameScreen.getPlayer().getPosition().x);
                }
            }
        }
    }

    private void playerBeginContact(Player player, String fixDataA, GameObject bodyB) {
        // if ground sensor touching ground
        if (fixDataA.equals("sensor")) {
            // one-way platforms logic
            boolean groundIsSolid = true;
            if (bodyB != null && bodyB.id.equals("one way")) {
                OneWayPlatform platform = (OneWayPlatform) bodyB;
                if (player.getCenterBottomPoint().y >= platform.getCenterTopPoint().y)
                    platform.solid = true;

                groundIsSolid = platform.solid;
            }

            // increase ground touched only if platform is solid (ground or solid one-way)
            if (groundIsSolid) {
                if (!player.isOnGround())
                    Global.soundManager.playLandSound(player.getVelocity().y);
                player.groundTouching++;
            }
        }

        // picking up bonuses
        if(bodyB != null && bodyB.id.equals("pickup")) {
            Bonus bonus = (Bonus) bodyB;
            bonus.pickUp();
            switch (bonus.type) {
                case ARROW:
                    player.addArrows(1);
                    break;
                case FIVE_ARROWS:
                    player.addArrows(5);
                    break;
                case QUIVER_UPGRADE:
                    player.increaseMaxArrows(5);
                    player.addArrows(player.getQuiverSize());
                    break;
                case SCORE:
                    gameScreen.addScore(100, bonus.getPosition());
                    break;
                case SCORE_MULTIPLIER:
                    gameScreen.activateScoreMultiplier();
                    gameScreen.statisticsAddBonusDouble();
                    break;
                case SLOWDOWN:
                    player.slowDown();
                    break;
                case INFINITE_ARROWS:
                    player.activateInfiniteArrows();
                    gameScreen.statisticsAddBonusInfinite();
                    break;
                default:
                    gameScreen.addScore(-1, bonus.getPosition());
                    break;
            }
        }
    }

    private void arrowBeginContact(Arrow arrow, Fixture fa, Fixture fb, String fixDataA, GameObject bodyB) {
        // if arrow hit something with a tip - attach arrow to body B
        if (fixDataA.equals("tip") && !arrow.hitSomething) {
            weldList.add(fa.getBody());
            weldList.add(fb.getBody());
            //arrow.moveInsideTarget();

            if (bodyB != null) {
                // kill bird or chicken
                if ((bodyB.id.equals("bird") || bodyB.id.equals("chicken"))) {
                    IKillableTarget killable = ((IKillableTarget) bodyB);
                    int scoreToAdd = (int) (((Target) bodyB).getScoreCost() * arrow.getScoreMultiplier());
                    // if target is already dead but did not fell on ground, then double the score
                    // but if already fell down, then no +score at all
                    if (killable.isDead()) {
                        if (!killable.isTouchedGround())
                            scoreToAdd *= 2;
                        else
                            scoreToAdd = 0;
                    } else {
                        if(bodyB.id.equals("bird"))
                            gameScreen.statisticsAddBird();
                        if(bodyB.id.equals("chicken"))
                            gameScreen.statisticsAddChicken();
                        killable.kill();
                        gameScreen.createBlood(new Vector2(killable.getPosition().x, killable.getPosition().y), arrow.getMovingAngle()* MathUtils.radiansToDegrees);
                        gameScreen.addTargetHits(((Target) bodyB).type);
                    }

                    Global.soundManager.playArrowHitFleshSound(arrow.getPosition().x, gameScreen.getPlayer().getPosition().x);

                    if (scoreToAdd > 0)
                        gameScreen.addScore(scoreToAdd, arrow.getPosition());
                }
                // hit archery target
                if (bodyB.id.equals("target")) {
                    gameScreen.statisticsAddTarget();
                    Global.soundManager.playArrowHitTargetSound(arrow.getPosition().x, gameScreen.getPlayer().getPosition().x);
                    gameScreen.addScore((int) (((Target) bodyB).getScoreCost() * arrow.getScoreMultiplier()), arrow.getPosition());
                    gameScreen.addTargetHits(((Target) bodyB).type);
                }
            }
        }
        // hit another arrow in air, that did not hit anything yet
        if(bodyB != null && bodyB.id.equals("arrow")) {
            Arrow arrow2 = (Arrow) bodyB;
            if (!arrow2.hitSomething && !arrow.hitSomething) {
                arrow2.hitSomething = true;
                //Gdx.app.log("Arrows hit", String.valueOf(arrow.getPosition().y - gameScreen.getPlayer().getPosition().y));
                if(arrow.getPosition().y - gameScreen.getPlayer().getPosition().y < 32)
                    gameScreen.addScore((int) (50 * arrow.getScoreMultiplier() * arrow2.getScoreMultiplier()), arrow.getPosition());
            }
            if (!arrow.hitSomething || !arrow2.hitSomething)
                Global.soundManager.playArrowHitArrowSound(arrow.getPosition().x, arrow.getLinearVelocity().len(), gameScreen.getPlayer().getPosition().x);
        }
        arrow.hitSomething = true;
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        GameObject bodyA = (GameObject) fa.getBody().getUserData();
        GameObject bodyB = (GameObject) fb.getBody().getUserData();

        String fixDataA = (String) fa.getUserData();

        byte iteration = 0;

        while (iteration < 2) {
            if (bodyA != null) {
                if (bodyA.id.equals("player")) {
                    Player player = (Player) bodyA;

                    // if player ceased to touch ground
                    if (fixDataA.equals("sensor")) {
                        // one-way logic
                        boolean groundIsSolid = true;
                        if (bodyB != null && bodyB.id.equals("one way")) {
                            OneWayPlatform platform = (OneWayPlatform) bodyB;
                            groundIsSolid = platform.solid;
                            platform.solid = false;
                        }

                        // decrease ground touched only if platform was solid (ground or solid one-way)
                        if (groundIsSolid)
                            player.groundTouching--;
                    }
                }
            }

            fa = contact.getFixtureB();
            fb = contact.getFixtureA();
            bodyA = (GameObject) fa.getBody().getUserData();
            bodyB = (GameObject) fb.getBody().getUserData();
            fixDataA = (String) fa.getUserData();
            iteration++;
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        GameObject bodyA = (GameObject) fa.getBody().getUserData();
        GameObject bodyB = (GameObject) fb.getBody().getUserData();

        String fixDataB = (String) fb.getUserData();

        byte iteration = 0;

        while (iteration < 2) {
            if (bodyA != null) {
                if (bodyA.id.equals("one way")) {
                    OneWayPlatform platform = (OneWayPlatform) bodyA;

                    if (bodyB != null && bodyB.id.equals("player")) {
                        if (fixDataB.equals("sensor")) {
                            Player player = (Player) bodyB;
                            // if player's bottom is upper than platform's top, then make platform solid
                            if (player.getCenterBottomPoint().y > platform.getCenterTopPoint().y)
                                platform.solid = true;
                        }

                        // cancel collision if platform is not solid
                        if (!platform.solid)
                            contact.setEnabled(false);
                    }
                }
            }

            fa = contact.getFixtureB();
            fb = contact.getFixtureA();
            bodyA = (GameObject) fa.getBody().getUserData();
            bodyB = (GameObject) fb.getBody().getUserData();
            fixDataB = (String) fb.getUserData();
            iteration++;
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) { }
}