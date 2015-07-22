package com.sergreen.bowrunner.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.WeldJointDef;
import com.sergreen.bowrunner.BowRunnerGame;
import com.sergreen.bowrunner.Game.Background;
import com.sergreen.bowrunner.Game.CollisionController;
import com.sergreen.bowrunner.Game.GameObjects.BloodSpray;
import com.sergreen.bowrunner.Game.GameObjects.Bonus;
import com.sergreen.bowrunner.Game.GameObjects.Decoration;
import com.sergreen.bowrunner.Game.GameObjects.ForegroundDecoration;
import com.sergreen.bowrunner.Game.GameObjects.GameObject;
import com.sergreen.bowrunner.Game.GameObjects.Targets.ArcheryTarget;
import com.sergreen.bowrunner.Game.GameObjects.Arrow;
import com.sergreen.bowrunner.Game.GameObjects.Targets.Bird;
import com.sergreen.bowrunner.Game.GameObjects.Targets.Chicken;
import com.sergreen.bowrunner.Game.GameObjects.Platforms.Ground;
import com.sergreen.bowrunner.Game.GameObjects.Player;
import com.sergreen.bowrunner.Game.GameObjects.Targets.Target;
import com.sergreen.bowrunner.Game.WorldGenerator;
import com.sergreen.bowrunner.Game.GameCamera;
import com.sergreen.bowrunner.Game.Particle;
import com.sergreen.bowrunner.Game.PopupScore;
import com.sergreen.bowrunner.Game.Skin;
import com.sergreen.bowrunner.Utils.Global;

import java.util.ArrayList;

public class GameScreen implements Screen {
    private World world;
    private BowRunnerGame game;
    private boolean paused = false;

    private boolean drawSprites = true;
    private boolean drawDebug = false;
    private Box2DDebugRenderer debugRenderer;
    private Matrix4 debugMatrix;
    private GameCamera camera;
    private BitmapFont font;
    private GameInputAndGUI gui;
    private CollisionController collisionController;
    private WorldGenerator worldGenerator;
    private Background background;

    private Player player;
    private ArrayList<Arrow> arrows;
    private ArrayList<Target> targets;
    private ArrayList<Ground> groundList;
    private ArrayList<Particle> particles;
    private ArrayList<Bonus> bonuses;
    private ArrayList<Decoration> decorations;
    private float platformWidth = 5;   //in meters

    private boolean gameOver = false;
    private int score = 0;
    private int currentHighScore;
    private float scoreMultiplier = 1;
    private int scoreMultiplierTimeLeft = 0;
    private int scoreMultiplierMaxTime = 1000;
    private ArrayList<Integer> targetHits;
    private float lastSynchedDistance = 0;
    private float speedUpEvery = 1000; //meters
    private float lastSpeedUpXPoint = 0;
    
    // Statistics
    private int statisticsArrows = 0,
                statisticsJumps = 0,
                statisticsInfiniteBonuses = 0,
                statisticsDoubleBonuses = 0,
                statisticsBirds = 0,
                statisticsChickens = 0,
                statisticsTargets = 0;

    public GameScreen(BowRunnerGame game) {
        Preferences settings = Global.getSettings();
        Global.timeState = 0;
        this.game = game;
        world = new World(new Vector2(0, -9.8f * 2), true);
        debugRenderer = new Box2DDebugRenderer();
        font = new BitmapFont();
        font.setColor(Color.BLACK);
        font.setScale(0.25f);
        currentHighScore = settings.getInteger("High score");
        player = new Player(new Vector2(0, 2), world, Skin.SkinType.values()[settings.getInteger("Skin")]);
        // width is constant and height depends on screen's aspect ratio
        camera = new GameCamera(16 * 2.5f, (Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth() * 16) * 2.5f, player);
        gui = new GameInputAndGUI(this, camera, player);
        Gdx.input.setInputProcessor(gui);
        collisionController = new CollisionController(this);
        world.setContactListener(collisionController);
        
        background = new Background(Global.assetManager.get("textures/backgrounds/forest_background.png", Texture.class), camera); 

        Vector2 lastPoint = new Vector2(-camera.viewportWidth / 2 + platformWidth, 0);
        groundList = new ArrayList<Ground>();
        worldGenerator = new WorldGenerator(lastPoint, world, player);
        groundList.addAll(worldGenerator.generateStartHut());

        arrows = new ArrayList<Arrow>();
        targets = new ArrayList<Target>();
        particles = new ArrayList<Particle>();
        targetHits = new ArrayList<Integer>();
        bonuses = new ArrayList<Bonus>();
        decorations = new ArrayList<Decoration>();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(gui);
        Gdx.input.setCatchBackKey(true);
        Global.soundManager.startGameMusic();
        Global.soundManager.setCamera(camera);
    }

    public ArrayList<Integer> getTargetHits() {
        return targetHits;
    }
    public void addTargetHits(int type) {
        targetHits.add(type);
    }

    public void addScore(int delta, Vector2 position) {
        score += delta*scoreMultiplier;

        if(position != null) {
            particles.add(new PopupScore(position, (int) (delta*scoreMultiplier)));
        }
    }
    public int getScore() {
        return score;
    }
    public boolean isHighscore() {
        return score > currentHighScore;
    }

    public float getScoreMultiplier() {
        return scoreMultiplier;
    }
    public int getScoreMultiplierTimeLeft() {
        return scoreMultiplierTimeLeft;
    }
    public int getScoreMultiplierMaxTime() {
        return scoreMultiplierMaxTime;
    }
    public void activateScoreMultiplier() {
        scoreMultiplierTimeLeft = scoreMultiplierMaxTime;
        scoreMultiplier = 2;
    }

    private void update(float timeStep)
    {
        world.step(timeStep, 6, 4);
        player.update();
        camera.update();
        updateGround();
        updateArrows();
        updateTargets();
        updateScore();
        updateSpeed();
        updateParticles();
        updateBonuses();
        updateDecorations();
        gui.update();

        if(player.isDead() && !gameOver)
            gameOver();
    }

    private void updateParticles() {
        for(int i = particles.size()-1; i>=0; i--) {
            particles.get(i).update();
            if(particles.get(i).isDead())
                particles.remove(i);
        }
    }

    private void updateScore() {
        float distance = player.getDistanceTravelled();
        // with greater speed comes faster score increase
        // default: +1 every 10 m       1.1 mod: +1 every 5 m
        // 1.2 mod: +1 every 3.3 m      1.3 mod: +1 every 2.5 m
        // 1.4 mod: +1 every 2 m        1.5 mod: +1 every 1.7 m
        float scoreEveryMeters = 10 / (player.getSpeedMod() * 10 - 10 + 1);
        if (distance - lastSynchedDistance > scoreEveryMeters) {
            addScore(1, null);
            lastSynchedDistance += scoreEveryMeters;
        }

        if (scoreMultiplierTimeLeft > 0) {
            scoreMultiplierTimeLeft--;
            if (scoreMultiplierTimeLeft == 0)
                scoreMultiplier = 1;
        }
    }

    private void updateSpeed() {
        float distance = player.getDistanceTravelled();
        if(distance - lastSpeedUpXPoint > speedUpEvery) {
            player.speedUd();
            lastSpeedUpXPoint += speedUpEvery;
        }
    }


    private void updateGround() {
        for (int i = groundList.size() - 1; i >= 0; i--) {
            if (groundList.get(i).isOutOfView(camera)) {
                world.destroyBody(groundList.get(i).getBody());
                groundList.remove(i);
            }
        }

        // if the last ground is close to show up on the screen, then generate new ground
        if(groundList.size() > 0) {
            if (groundList.get(groundList.size() - 1).isCloseToCameraRightEdge(camera)) {
                ArrayList<GameObject> objects;
                objects = worldGenerator.generate();

                for (GameObject go : objects) {
                    if (go instanceof Ground)
                        groundList.add((Ground) go);
                    else if (go instanceof Bonus)
                        bonuses.add((Bonus) go);
                    else if (go instanceof Target)
                        targets.add((Target) go);
                    else if (go instanceof Decoration)
                        decorations.add((Decoration) go);
                }

                Vector2 lastPoint = worldGenerator.getLastPoint();
                if (Global.random.nextInt(10) < 5 && countBirds() < 5) {
                    if (Global.random.nextInt(2) == 0)
                        targets.add(new Bird(new Vector2(camera.position.x - camera.viewportWidth / 1.5f, camera.position.y + 20), world, false, true));
                    else
                        targets.add(new Bird(new Vector2(camera.position.x + camera.viewportWidth * 1.5f, camera.position.y + 20), world, false, false));
                }
                if (Global.random.nextInt(10) < 6) {
                    targets.add(new ArcheryTarget(new Vector2(lastPoint.x + Global.random.nextFloat() * 6 - 3, lastPoint.y + 4 + Global.random.nextFloat() * 7), world));
                }
                if (Global.random.nextInt(10) < 3) {
                    createBonus(Bonus.getRandomWeightedType(player.getSpeedMod() > 1), new Vector2(lastPoint.x + Global.random.nextInt(4) - 2, lastPoint.y + 3f));
                }
            }
        }
    }

    private int countBirds() {
        int n = 0;

        for(int i=0; i<targets.size(); i++)
            if(targets.get(i) instanceof Bird)
                n++;

        return n;
    }

    private void updateArrows() {
        for (int i=arrows.size()-1; i>=0; i--) {
            Arrow arrow = arrows.get(i);
            arrow.update();
            if(arrow.isOutOfView(camera)) {
                world.destroyBody(arrow.getBody());
                arrows.remove(i);
            }
        }

        if(collisionController.weldList.size() > 0) {
            ArrayList<Body> list = collisionController.weldList;
            for(int i=0; i<list.size()-1; i+=2) {
                WeldJointDef jointDef = new WeldJointDef();
                jointDef.initialize(list.get(i), list.get(i+1), list.get(i).getPosition());
                world.createJoint(jointDef);
            }
            list.clear();
        }
    }

    public void createArrow(float angle) {
        statisticsAddArrow();
        arrows.add(new Arrow(world,
                             new Vector2(player.getPosition().x + 0.5f, player.getPosition().y + 0.5f),
                             angle,
                             45, !player.isOnGround()));
                             //player.getVelocity()));
    }

    public void createBonus(Bonus.BonusType type, Vector2 position) {
        bonuses.add(new Bonus(type, position, world));
    }

    public void createBlood(Vector2 position, float angle) {
        particles.add(new BloodSpray(position, angle));
    }

    public void updateBonuses() {
        for(int i=bonuses.size()-1; i>=0; i--) {
            if(bonuses.get(i).isDead()) {
                world.destroyBody(bonuses.get(i).getBody());
                bonuses.remove(i);
            }
        }
    }

    public void updateDecorations() {
        for(int i=decorations.size()-1; i>=0; i--) {
            if(decorations.get(i).isOutOfView(camera)) {
                decorations.remove(i);
            }
        }
    }

    private void updateTargets() {
        for (int i=targets.size()-1; i>=0; i--) {
            Target target = targets.get(i);

            if(target.isOutOfView(camera)) {
                world.destroyBody(target.getBody());
                targets.remove(i);
            }
            else {
                target.update();
                if(target instanceof Bird)
                    //((Bird)target).updateMaxFlightHeight(camera.position.y + 4f);
                    //((Bird)target).updateMaxFlightHeight(worldGenerator.getLastPoint().y + 6f);
                    ((Bird)target).updateMaxFlightHeight(groundList);
            }
        }
    }

    private void gameOver() {
        gameOver = true;
        Global.soundManager.stopGameMusic();
        if(score > currentHighScore)
            Global.getSettings().putInteger("High score", score).flush();
        if(statisticsArrows > Global.getSettings().getInteger("Most arrows"))
            Global.getSettings().putInteger("Most arrows", statisticsArrows).flush();
        if(statisticsTargets+statisticsBirds+statisticsChickens > Global.getSettings().getInteger("Most targets"))
            Global.getSettings().putInteger("Most targets", statisticsTargets+statisticsBirds+statisticsChickens).flush();
        if(statisticsJumps > Global.getSettings().getInteger("Most jumps"))
            Global.getSettings().putInteger("Most jumps", statisticsJumps).flush();
        if(player.getDistanceTravelled() > Global.getSettings().getInteger("Best distance"))
            Global.getSettings().putInteger("Best distance", (int) player.getDistanceTravelled()).flush();

        Preferences stats = Global.getStats();
        stats.putInteger("Games played", stats.getInteger("Games played") + 1);
        stats.putInteger("Distance travelled", (int) (stats.getInteger("Distance travelled") + player.getDistanceTravelled()));
        stats.putInteger("Arrows shot", stats.getInteger("Arrows shot") + statisticsArrows);
        stats.putInteger("Birds killed", stats.getInteger("Birds killed") + statisticsBirds);
        stats.putInteger("Chickens killed", stats.getInteger("Chickens killed") + statisticsChickens);
        stats.putInteger("Targets killed", stats.getInteger("Targets killed") + statisticsTargets);
        stats.putInteger("Jumps made", stats.getInteger("Jumps made") + statisticsJumps);
        stats.putInteger("Double bonuses", stats.getInteger("Double bonuses") + statisticsDoubleBonuses);
        stats.putInteger("Infinite bonuses", stats.getInteger("Infinite bonuses") + statisticsInfiniteBonuses);
        stats.flush();
    }

	@Override
	public void render (float delta) {
        //long time = System.currentTimeMillis();
        if (!paused) {
            Global.timeState += Gdx.graphics.getDeltaTime();

            float timeStep = player.isDead() ? 1 / 240f : 1 / 60f;
            update(timeStep);
        } else {
            camera.update();
            gui.update();
        }

        Gdx.gl.glClearColor(0.16f, 0.19f, 0.13f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.setProjectionMatrix(camera.combined);
        debugMatrix = game.batch.getProjectionMatrix().cpy().scale(Global.PIXELS_TO_METERS, Global.PIXELS_TO_METERS, 0);
        game.batch.begin();

        if (drawSprites) {
            background.render(game.batch, player.getDistanceTravelled(), isHighscore());

            for (Ground ground : groundList)
                ground.render(game.batch);

            for (Decoration decoration : decorations)
                if (!(decoration instanceof ForegroundDecoration))
                    decoration.render(game.batch);

            for (Bonus bonus : bonuses)
                bonus.render(game.batch);

            player.render(game.batch);

            for (Arrow arrow : arrows)
                arrow.render(game.batch);

            for (Target target : targets)
                target.render(game.batch);

            for (Decoration decoration : decorations)
                if (decoration instanceof ForegroundDecoration) decoration.render(game.batch);

            for (Particle particle : particles)
                particle.render(game.batch);
        }


        /*
        font.draw(game.batch,
                //"vX: " + player.getVelocity().x,
                "[B:J] = [" + (world.getBodyCount()-22) + ":" + (world.getJointCount()-21) + "]",
                //"DST = " + player.getDistanceTravelled(),
                //"SCORE = " + score,
                //"ANGLE = " + player.getAimingAngle(),
                camera.position.x - camera.viewportWidth/2 + 1f,
                camera.position.y + camera.viewportHeight/2 - 1f);
        //*/

        gui.render(game.batch);

        game.batch.end();

        try {
            if (!drawSprites || drawDebug)
                debugRenderer.render(world, debugMatrix);
        } catch (NullPointerException e) {
            Gdx.app.log("DebugRenderer", "Something is wrong desu.");
        }
        //Gdx.app.log("Elapsed", String.valueOf(System.currentTimeMillis() - time));
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, 16 * 2.5f, (height / (float) width * 16) * 2.5f);
    }
    @Override
    public void pause() {
        paused = true;
    }
    public void unpause() {
        paused = false;
    }
    public boolean isPaused() {
        return paused;
    }
    @Override
    public void resume() {
        if(gameOver)
            paused = false;
    }
    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
    }

    public void goToMenu() {
        game.setScreen(new MenuScreen(game));
        dispose();
    }
    public void retry() {
        game.setScreen(new GameScreen(game));
        dispose();
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void statisticsAddArrow() {
        statisticsArrows++;
    }
    public void statisticsAddJump() {
        statisticsJumps++;
    }
    public void statisticsAddBonusInfinite() {
        statisticsInfiniteBonuses++;
    }
    public void statisticsAddBonusDouble() {
        statisticsDoubleBonuses++;
    }
    public void statisticsAddBird() {
        statisticsBirds++;
    }
    public void statisticsAddChicken() {
        statisticsChickens++;
    }
    public void statisticsAddTarget() {
        statisticsTargets++;
    }

    public Player getPlayer() {
        return player;
    }
}
