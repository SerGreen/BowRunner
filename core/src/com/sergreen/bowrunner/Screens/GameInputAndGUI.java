package com.sergreen.bowrunner.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.sergreen.bowrunner.Game.GameObjects.Player;
import com.sergreen.bowrunner.Game.GameCamera;
import com.sergreen.bowrunner.Screens.Buttons.GUIButton;
import com.sergreen.bowrunner.Utils.AnimatedSprite;
import com.sergreen.bowrunner.Utils.Global;

import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Created on 16.02.2015 [SerGreen]
 */
public class GameInputAndGUI implements InputProcessor {
    private Random random;
    private GameScreen game;
    private GameCamera camera;
    private Player player;

    private AnimatedSprite quiver;
    private AnimatedSprite aimHalo;

    private float maxScoreTiltAngle = 6;
    private float finalScoreTiltAngle = 0;
    private int finalScoreToDraw = 0;
    private int scoreToDraw = 0;
    private int targetResultsToDraw = 0;
    private int targetResultFrame = 0;
    private int targetResultDelayFrames = 10;
    private boolean tiltIncrease = true;

    private boolean aiming = false;     //are we currently aiming
    private boolean queueAim = false;   //if we tried to aim before previous arrow was released
    private Vector2 aimStartPoint;      //touch point, when we start aiming
    private Vector2 aimCurrentPoint;    //point, we aiming at
    private float aimRadius;            //radius of aim circle graphics to draw
    private int aimFinger = -1;         //ID of finger that does aim

    private GUIButton jumpButton;
    private boolean jumping = false;
    private boolean queueJump = false;

    private int delayBetweenGameOverAndButtons = 200;
    private boolean showGameOverButtons = false;

    private GUIButton retryButton;
    private GUIButton homeButton;

    private GUIButton pauseRetryButton;
    private GUIButton pauseHomeButton;
    private GUIButton pauseResumeButton;

    public GameInputAndGUI(final GameScreen game, GameCamera camera, Player player) {
        this.game = game;
        this.camera = camera;
        this.player = player;
        random = new Random();

        int screenWidth = Gdx.graphics.getWidth();
        int screenHeight = Gdx.graphics.getHeight();

        //size and location of jump button in pixels
        float radius = screenWidth / 10f;
        Vector2 position = new Vector2(radius, radius);
        jumpButton = new GUIButton(
                Global.assetManager.get("textures/gui/jump_button.png", Texture.class),
                new Vector2(0, 0), position, radius, camera.getPixelToMeter(),
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        jumpButtonPressed();
                        return null;
                    }
                },
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        jumpButtonReleased();
                        return null;
                    }
                }, false, false);

        //aim circle
        aimRadius = screenWidth / 20f;
        aimStartPoint = new Vector2(0, 0);
        aimCurrentPoint = new Vector2(-1, 0);

        //retry button
        radius = screenWidth / 10f;
        position = new Vector2(screenWidth / 2 + radius * 2, radius * 1.5f);
        retryButton = new GUIButton(
                Global.assetManager.get("textures/gui/retry_button.png", Texture.class),
                position, radius, camera.getPixelToMeter(),
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        game.retry();
                        return null;
                    }
                }, true);

        //home button
        position = new Vector2(screenWidth / 2 - radius * 2, radius * 1.5f);
        homeButton = new GUIButton(
                Global.assetManager.get("textures/gui/home_button.png", Texture.class),
                position,  radius, camera.getPixelToMeter(),
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        game.goToMenu();
                        return null;
                    }
                }, true);

        //==================PAUSE BUTTONS=================
        //pause retry button
        radius = screenWidth / 12f;
        position = new Vector2(screenWidth / 2 + radius * 3f, screenHeight / 2);
        pauseRetryButton = new GUIButton(
                Global.assetManager.get("textures/gui/retry_button.png", Texture.class),
                position, radius, camera.getPixelToMeter(),
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        game.retry();
                        return null;
                    }
                }, true);

        //pause home button
        position = new Vector2(screenWidth / 2 - radius * 3f, screenHeight / 2);
        pauseHomeButton = new GUIButton(
                Global.assetManager.get("textures/gui/home_button.png", Texture.class),
                position,  radius, camera.getPixelToMeter(),
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        game.goToMenu();
                        return null;
                    }
                }, true);

        //pause continue button
        radius = screenWidth / 8f;
        position = new Vector2(screenWidth / 2, screenHeight / 2);
        pauseResumeButton = new GUIButton(
                Global.assetManager.get("textures/gui/continue_button.png", Texture.class),
                position,  radius, camera.getPixelToMeter(),
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        game.unpause();
                        return null;
                    }
                }, true);

        loadSprites();
    }

    private void loadSprites() {

        quiver = new AnimatedSprite(
                Global.assetManager.get("textures/gui/quiver.png", Texture.class),
                6, 1, 1f, Animation.PlayMode.NORMAL);
        quiver.setScaleXY(Gdx.graphics.getHeight() * 0.13f * camera.getPixelToMeter() / quiver.getHeight());

        aimHalo = new AnimatedSprite(
                Global.assetManager.get("textures/gui/aim_halo.png", Texture.class),
                1, 1, 1f, Animation.PlayMode.NORMAL);
        aimHalo.setScaleXY(0.07f);
    }

    @Override
    public boolean touchDown(int x, int y, int fingerID, int button) {

        y = Gdx.graphics.getHeight() - y;
        if(!game.isGameOver()) {
            if(!game.isPaused()) {
                try {
                    jumpButton.touchDown(new Vector2(x, y), fingerID);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //if jump button not touched then do AIM
                if (!aiming && !(jumpButton.isPressed() && jumpButton.getFingerID() == fingerID)) {
                    startAim(x, y, fingerID);
                }
            }
            else {
                try {
                    pauseRetryButton.touchDown(new Vector2(x, y), fingerID);
                    pauseHomeButton.touchDown(new Vector2(x, y), fingerID);
                    pauseResumeButton.touchDown(new Vector2(x, y), fingerID);
                } catch (Exception e) { e.printStackTrace(); }
            }
        }
        // Retry & Menu buttons
        if(showGameOverButtons) {
            try {
                retryButton.touchDown(new Vector2(x, y), fingerID);
                homeButton.touchDown(new Vector2(x, y), fingerID);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public boolean touchUp(int x, int y, int fingerID, int button) {
        y = Gdx.graphics.getHeight() - y;

        if(!game.isPaused()) {
            // if we were aiming and released the finger
            if (fingerID == aimFinger) {
                if (aiming)
                    stopAim();
                else if (queueAim)
                    queueAim = false;
            }

            // Jump, Retry and Menu buttons
            try {
                jumpButton.touchUp(new Vector2(x, y), fingerID);

                if (showGameOverButtons) {
                    retryButton.touchUp(new Vector2(x, y), fingerID);
                    homeButton.touchUp(new Vector2(x, y), fingerID);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                pauseRetryButton.touchUp(new Vector2(x, y), fingerID);
                pauseHomeButton.touchUp(new Vector2(x, y), fingerID);
                pauseResumeButton.touchUp(new Vector2(x, y), fingerID);
            } catch (Exception e) { e.printStackTrace(); }
        }

        return true;
    }

    @Override
    public boolean touchDragged(int x, int y, int fingerID) {
        y = Gdx.graphics.getHeight() - y;

        if(!game.isPaused()) {
            // adjust aim direction
            if (aiming && fingerID == aimFinger) {
                changeAimAngle(x, y);
            }

            jumpButton.touchDragged(new Vector2(x, y), fingerID);
            if (showGameOverButtons) {
                retryButton.touchDragged(new Vector2(x, y), fingerID);
                homeButton.touchDragged(new Vector2(x, y), fingerID);
            }
        }
        else {
            pauseRetryButton.touchDragged(new Vector2(x, y), fingerID);
            pauseHomeButton.touchDragged(new Vector2(x, y), fingerID);
            pauseResumeButton.touchDragged(new Vector2(x, y), fingerID);
        }

        return true;
    }

    // action function for Jump button
    private void jumpButtonPressed() {
        if (!jumping) {
            if (player.isOnGround()) {
                jumping = true;
                player.jumpStart();
                game.statisticsAddJump();
            } else
                queueJump = true;
        }
    }

    // action function for Jump button
    private void jumpButtonReleased() {
        if (jumping) {
            jumping = false;
            player.jumpEnd();
        }

        queueJump = false;
    }

    // when finger touched any space but jump button
    private void startAim(int x, int y, int fingerID) {
        if (player.getArrows() > 0 || player.isInfiniteArrows()) {
            if (player.isLoadingArrow()) {
                queueAim = true;
                aimStartPoint.set(x, y);
                changeAimAngle(x - 1, y - 1);
                aimFinger = fingerID;   //so we can track the right finger while aiming
            } else {
                aiming = true;
                aimStartPoint.set(x, y);
                changeAimAngle(x - 1, y - 1);
                aimFinger = fingerID;   //so we can track the right finger while aiming
                player.aimStart(getAimingAngle());
            }
        }
    }

    // when finger released
    private void stopAim() {
        aiming = false;
        if (player.isArrowLoaded()) {
            game.createArrow(player.getAimingAngle());
            player.shootArrow();
        }
        player.aimStop();
    }

    // when finger moved
    private void changeAimAngle(int x, int y) {
        aimCurrentPoint.set(x, y);
        if (aiming)
            player.aimChangeAngle(getAimingAngle());
    }

    private float getAimingAngle() {
        return 180 + Global.getAngleDegreesBetweenVectors(aimStartPoint, aimCurrentPoint);
    }

    @Override
    public boolean keyDown(int keycode) {
        //Gdx.app.log("Key", String.valueOf(keycode));
        if (!game.isPaused() && (keycode == Input.Keys.SPACE || keycode == Input.Keys.MENU))
            try {
                jumpButton.touchDown(jumpButton.getOnScreenPosition(), 666);
            } catch (Exception e) {
                e.printStackTrace();
            }
        if (keycode == Input.Keys.A)
            player.startMovingLeft();
        if (keycode == Input.Keys.D)
            player.startMovingRight();
        if (!game.isGameOver() &&
                (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.BACK || keycode == Input.Keys.HOME)) {
            game.pause();
        }

        //TODO debug stuff, remove later
        if (keycode == Input.Keys.O)
            game.activateScoreMultiplier();
        if (keycode == Input.Keys.P)
            player.activateInfiniteArrows();
        if (keycode == Input.Keys.LEFT_BRACKET)
            player.slowDown();
        if (keycode == Input.Keys.RIGHT_BRACKET)
            player.speedUd();
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.SPACE || keycode == Input.Keys.MENU) {
            try {
                jumpButton.touchUp(jumpButton.getOnScreenPosition(), 666);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //TODO debug stuff, remove later
        if (keycode == Input.Keys.A)
            player.stopMoving();
        if (keycode == Input.Keys.D)
            player.stopMoving();
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }
    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }
    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void update() {
        if(!game.isPaused()) {
            if (!game.isGameOver()) {
                jumpButton.updateInWorldPosition(camera);
            }

            // when we released aim finger before end of arrow loading animation
            // we need to finish loading and then shoot an arrow
            if (!aiming && player.isLoadingArrow()) {
                if (player.isArrowLoaded()) {
                    game.createArrow(player.getAimingAngle());
                    player.shootArrow();
                }
            }

            // if we started new aim before previous arrow was shot, we set next aim to queue
            // so if there queued new aim and arrow is shot, we can start to load new arrow
            if (queueAim && !player.isLoadingArrow()) {
                queueAim = false;
                aiming = true;
                player.aimStart(getAimingAngle());
            }

            // same with queued jump - if we pressed jump again before landing on ground
            // so the moment character touches ground and there queued jump, we do jump again
            if (queueJump && player.isOnGround() && !jumping) {
                jumping = true;
                player.jumpStart();
                game.statisticsAddJump();
            }
        }
        else {
            pauseRetryButton.updateInWorldPosition(camera);
            pauseHomeButton.updateInWorldPosition(camera);
            pauseResumeButton.updateInWorldPosition(camera);
        }

        // if game is over then:
        if (game.isGameOver()) {
            //calculate tilt for final score animation
            if (tiltIncrease) {
                finalScoreTiltAngle += 0.2;
                if (finalScoreTiltAngle >= maxScoreTiltAngle)
                    tiltIncrease = false;
            } else {
                finalScoreTiltAngle -= 0.2;
                if (finalScoreTiltAngle <= -maxScoreTiltAngle)
                    tiltIncrease = true;
            }

            // calculate retry and home buttons appearance time
            if (delayBetweenGameOverAndButtons > 0)
                delayBetweenGameOverAndButtons--;

            if (delayBetweenGameOverAndButtons == 0 && finalScoreToDraw == game.getScore()) {
                showGameOverButtons = true;
                retryButton.updateInWorldPosition(camera);
                homeButton.updateInWorldPosition(camera);
            }
        }

        updateSprites();
    }

    private void updateSprites() {
        int screenHeight = Gdx.graphics.getHeight();
        if (player.isLoadingArrow())
            aimHalo.setPosition(player.getBody().getPosition().x + 0.52f, player.getBody().getPosition().y + 0.55f);

        quiver.setPosition(camera.screenToWorld(screenHeight*0.067f, screenHeight - screenHeight*0.067f));
    }

    //========================== MAIN RENDER METHOD ============================
    public void render(SpriteBatch batch) {
        if (!game.isGameOver()) {
            if (aiming || queueAim)
                renderAimCircle(batch);

            jumpButton.render(batch);

            if (player.isLoadingArrow())
                aimHalo.draw(batch, 0);

            renderAmmo(batch);
            renderScore(batch);
            renderDistance(batch);

            if (game.getScoreMultiplierTimeLeft() > 0)
                renderScoreMultiplierBar(batch);
            if (player.isInfiniteArrows())
                renderInfiniteArrowsBar(batch);
            if (game.isPaused()) {
                pauseRetryButton.render(batch);
                pauseHomeButton.render(batch);
                pauseResumeButton.render(batch);
                renderPausedLabel(batch);
            }
        } else {
            renderFinalScore(batch);

            if (showGameOverButtons) {
                retryButton.render(batch);
                homeButton.render(batch);
            }
        }

        //TODO debug stuff
        //Global.font.drawText("Y:" + player.getPosition().y, batch, new Vector2(camera.position.x-10, camera.position.y+4), 0.04f);
        //Global.font.drawText("V:" + player.getVelocity().x, batch, new Vector2(camera.position.x-12, camera.position.y+4), 0.04f);
        //Global.font.drawText("D:" + (int)player.getDistanceTravelled(), batch, new Vector2(camera.position.x-12, camera.position.y+4), 0.04f);
        //Global.font.drawText("Mod:" + player.getSpeedMod(), batch, new Vector2(camera.position.x-12, camera.position.y+6), 0.04f);
    }
    //==========================================================================

    private void renderPausedLabel(SpriteBatch batch) {
        Texture pausedTexture = Global.assetManager.get("textures/gui/paused.png", Texture.class);
        float scale = 0.05f;
        batch.draw(pausedTexture,
                camera.position.x - pausedTexture.getWidth() * scale/2, camera.position.y + camera.viewportHeight/2 - pausedTexture.getHeight()*scale - 2,
                0, 0,
                pausedTexture.getWidth(), pausedTexture.getHeight(),
                scale, scale,
                0,
                0, 0,
                pausedTexture.getWidth(), pausedTexture.getHeight(),
                false, false);
    }

    private void renderAimCircle(SpriteBatch batch) {
        Vector2 worldPosition =
                camera.screenToWorld(aimStartPoint.x - aimRadius, aimStartPoint.y - aimRadius);

        Texture aimSprite = Global.assetManager.get("textures/gui/aim_sprite.png", Texture.class);
        batch.draw(aimSprite,
                worldPosition.x, worldPosition.y,
                aimRadius * camera.getPixelToMeter(), aimRadius * camera.getPixelToMeter(),
                aimRadius * 2 * camera.getPixelToMeter(), aimRadius * 2 * camera.getPixelToMeter(),
                1f, 1f,
                0,
                0, 0, aimSprite.getWidth(), aimSprite.getHeight(), false, false);
    }

    private void renderAmmo(SpriteBatch batch) {
        int screenHeight = Gdx.graphics.getHeight();

        //translate position of ammo-arrow to world coordinates
        Vector2 worldPosition =
                camera.screenToWorld(screenHeight*0.168f, screenHeight - screenHeight*0.067f);

        // draw quiver icon and number of arrows left, like 13/25
        quiver.draw(batch, player.getArrows());
        Global.font.drawText(player.getArrows() + "/" + player.getQuiverSize(), batch,
                new Vector2(quiver.getPosition().x - 1.3f, quiver.getPosition().y - 1.5f), 0.012f);

        // draw arrows array, displaying amount of arrows left
        Texture arrow = Global.assetManager.get("textures/gui/arrow_ammo.png", Texture.class);
        for (int i = 0; i < player.getArrows(); i++) {
            batch.draw(arrow,
                    worldPosition.x - arrow.getWidth() / 2 + 0.5f * i,
                    worldPosition.y - arrow.getHeight() / 2 - 0.2f,
                    arrow.getWidth() / 2, arrow.getHeight() / 2,
                    arrow.getWidth(), arrow.getHeight(),
                    quiver.getScaleX(), quiver.getScaleY(),
                    -10,
                    0, 0, arrow.getWidth(), arrow.getHeight(), false, false);
        }
    }

    private void renderScore(SpriteBatch batch) {
        int screenHeight = Gdx.graphics.getHeight();
        int screenWidth = Gdx.graphics.getWidth();

        Vector2 characterSize = Global.font.getCharacterSize();
        float scale = camera.viewportHeight * 0.1f / characterSize.y;
        float drawWidth = (String.valueOf(game.getScore()).length() * characterSize.x) * scale;
        Vector2 topRightScreenCorner = camera.screenToWorld(screenWidth, screenHeight);
        Vector2 drawLocation = new Vector2(topRightScreenCorner.x - drawWidth - 0.2f, topRightScreenCorner.y - characterSize.y * scale - 0.2f);

        if (scoreToDraw < game.getScore())
            scoreToDraw++;
        Global.font.drawText(String.valueOf(scoreToDraw), batch, drawLocation, scale);
    }

    private void renderDistance(SpriteBatch batch) {
        int screenHeight = Gdx.graphics.getHeight();
        int screenWidth = Gdx.graphics.getWidth();
        String distanceString = String.valueOf((int) game.getPlayer().getDistanceTravelled());

        Vector2 characterSize = Global.font.getCharacterSize();
        float scale = camera.viewportHeight * 0.035f / characterSize.y;
        float drawWidth = (distanceString.length() + 2) * characterSize.x * scale;
        Vector2 topRightScreenCorner = camera.screenToWorld(screenWidth, screenHeight);
        Vector2 drawLocation = new Vector2(topRightScreenCorner.x - drawWidth - 0.4f, topRightScreenCorner.y - characterSize.y * scale - 2.5f);

        Global.font.drawText(distanceString + " m", batch, drawLocation, scale);
    }

    private void renderFinalScore(SpriteBatch batch) {
        int screenHeight = Gdx.graphics.getHeight();
        int screenWidth = Gdx.graphics.getWidth();

        if (finalScoreToDraw < game.getScore()) {
            int dScore = Math.max((game.getScore() - finalScoreToDraw) / 100, 1);
            finalScoreToDraw += dScore;
        }
        Vector2 finalScorePosition = camera.screenToWorld(screenWidth / 2, screenHeight - screenHeight * 0.3f);
        Global.font.drawScore(finalScoreToDraw, batch, finalScorePosition, camera, finalScoreTiltAngle, Global.getRainbowColor(game.isHighscore()));
        renderFinalDistance(batch, new Vector2(finalScorePosition.x, finalScorePosition.y - 2f));
        renderTargetResults(batch, new Vector2(finalScorePosition.x, finalScorePosition.y - 4.2f));
    }

    private void renderFinalDistance(SpriteBatch batch, Vector2 position) {
        String distanceString = String.valueOf((int) game.getPlayer().getDistanceTravelled());
        Texture distanceTexture = Global.assetManager.get("textures/gui/distance.png", Texture.class);
        float scale = (camera.viewportWidth / 5.3f) / distanceTexture.getWidth();
        float fontScale = distanceTexture.getHeight() * scale / Global.font.getCharacterSize().y;
        float drawWidth = (distanceString.length() * Global.font.getCharacterSize().x * fontScale) + distanceTexture.getWidth() * scale;

        batch.draw(distanceTexture,
                position.x - drawWidth/2, position.y,
                0, 0,
                distanceTexture.getWidth(), distanceTexture.getHeight(),
                scale, scale,
                0,
                0, 0,
                distanceTexture.getWidth(), distanceTexture.getHeight(),
                false, false);
        Global.font.drawText(distanceString + " m", batch, new Vector2(position.x - drawWidth/2 + distanceTexture.getWidth() * scale + 0.5f, position.y), fontScale);
    }

    private void renderTargetResults(SpriteBatch batch, Vector2 position) {
        if (targetResultsToDraw < game.getTargetHits().size()) {
            targetResultFrame++;
            if (targetResultFrame >= targetResultDelayFrames) {
                targetResultFrame = 0;
                targetResultsToDraw++;
            }
        }

        float scale = 0.06f;
        int maxMedalsInRow = 30;
        int rows = 0;
        float yShift = 0;
        float drawWidth = Math.min(targetResultsToDraw, maxMedalsInRow) * 16 * scale;

        for (int i = 0; i < targetResultsToDraw; i++) {
            int index = game.getTargetHits().get(i);
            if (i + 1 - rows * maxMedalsInRow > maxMedalsInRow) {
                rows++;
                yShift -= 16 * scale;
            }
            batch.draw(Global.assetManager.get("textures/gui/target_medals.png", Texture.class),
                    position.x - drawWidth / 2 + ((i - rows * maxMedalsInRow) * 16) * scale, position.y + yShift,
                    0, 0,
                    32, 32,
                    scale, scale,
                    0,
                    index * 32, 0,
                    32, 32,
                    false, false);
        }
    }

    private void renderScoreMultiplierBar(SpriteBatch batch) {
        int screenWidth = Gdx.graphics.getWidth();

        Texture bonusBarFrame = Global.assetManager.get("textures/gui/bonus_bar_frame.png", Texture.class);
        Texture bonusBarFiller = Global.assetManager.get("textures/gui/bonus_bar_filler.png", Texture.class);
        float scale = camera.viewportWidth/2 / bonusBarFrame.getWidth();
        float screenScale = screenWidth/2 / bonusBarFrame.getWidth();
        float barScale = game.getScoreMultiplierTimeLeft() / (float)game.getScoreMultiplierMaxTime() * (bonusBarFrame.getWidth()-10);
        Vector2 positionFrame = camera.screenToWorld(screenWidth/2 - bonusBarFrame.getWidth()*screenScale/2, bonusBarFrame.getHeight()*screenScale);
        Vector2 positionBar = camera.screenToWorld(screenWidth/2 - bonusBarFrame.getWidth()*screenScale/2 + 5*screenScale, bonusBarFrame.getHeight()*screenScale + 2*screenScale);

        batch.setColor(new Color(1, 1, random.nextFloat(), 1));
        batch.draw(bonusBarFiller,
                positionBar.x, positionBar.y,
                0, 0,
                bonusBarFiller.getWidth(), bonusBarFiller.getHeight(),
                barScale * scale, scale,
                0,
                0, 0, bonusBarFiller.getWidth(), bonusBarFiller.getHeight(), false, false);
        batch.setColor(Color.WHITE);

        batch.draw(bonusBarFrame,
                positionFrame.x, positionFrame.y,
                0, 0,
                bonusBarFrame.getWidth(), bonusBarFrame.getHeight(),
                scale, scale,
                0,
                0, 0, bonusBarFrame.getWidth(), bonusBarFrame.getHeight(), false, false);
    }

    private void renderInfiniteArrowsBar(SpriteBatch batch) {
        int screenWidth = Gdx.graphics.getWidth();

        Texture bonusBarFrame = Global.assetManager.get("textures/gui/bonus_bar_frame.png", Texture.class);
        Texture bonusBarFiller = Global.assetManager.get("textures/gui/bonus_bar_filler.png", Texture.class);
        float scale = camera.viewportWidth/2 / bonusBarFrame.getWidth();
        float screenScale = screenWidth/2 / bonusBarFrame.getWidth();
        float barScale = player.getInfiniteArrowsTimeLeft() / (float)player.getInfiniteArrowsMaxTime() * (bonusBarFrame.getWidth()-10);
        Vector2 positionFrame = camera.screenToWorld(screenWidth/2 - bonusBarFrame.getWidth()*screenScale/2, bonusBarFrame.getHeight()*2*screenScale);
        Vector2 positionBar = camera.screenToWorld(screenWidth/2 - bonusBarFrame.getWidth()*screenScale/2 + 5*screenScale, bonusBarFrame.getHeight()*2*screenScale + 2*screenScale);

        float rndF = random.nextFloat()*0.5f;
        batch.setColor(new Color(1, rndF, rndF, 1));
        batch.draw(bonusBarFiller,
                positionBar.x, positionBar.y,
                0, 0,
                bonusBarFiller.getWidth(), bonusBarFiller.getHeight(),
                barScale * scale, scale,
                0,
                0, 0, bonusBarFiller.getWidth(), bonusBarFiller.getHeight(), false, false);
        batch.setColor(Color.WHITE);

        batch.draw(bonusBarFrame,
                positionFrame.x, positionFrame.y,
                0, 0,
                bonusBarFrame.getWidth(), bonusBarFrame.getHeight(),
                scale, scale,
                0,
                0, 0, bonusBarFrame.getWidth(), bonusBarFrame.getHeight(), false, false);
    }
}