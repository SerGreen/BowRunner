package com.sergreen.bowrunner.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.sergreen.bowrunner.Game.GameObjects.Bonus;
import com.sergreen.bowrunner.Game.GameObjects.Decoration;
import com.sergreen.bowrunner.Game.GameObjects.ForegroundDecoration;
import com.sergreen.bowrunner.Game.GameObjects.GameObject;
import com.sergreen.bowrunner.Game.GameObjects.Platforms.FallenTree;
import com.sergreen.bowrunner.Game.GameObjects.Platforms.Ground;
import com.sergreen.bowrunner.Game.GameObjects.Platforms.OneWayPlatform;
import com.sergreen.bowrunner.Game.GameObjects.Player;
import com.sergreen.bowrunner.Game.GameObjects.Targets.Chicken;
import com.sergreen.bowrunner.Utils.Global;

import java.util.ArrayList;

/**
 * Created on 01.03.2015 [SerGreen]
 */
public class WorldGenerator {

    public static enum Biome { StarterForest, AdvancedForest, HardForest, Village }
    private Vector2 lastPoint;
    private World world;
    private Player player;
    private Biome biomeToGenerate = Biome.StarterForest;
    private float villageStartPoint = 0;
    private float lastVillagePoint = 0;

    private boolean doLog = false;

    public WorldGenerator(Vector2 startPoint, World world, Player player) {
        this.lastPoint = startPoint;
        this.world = world;
        this.player = player;
    }

    public ArrayList<GameObject> generate() {
        ArrayList<GameObject> generated = new ArrayList<GameObject>();

        float distanceTravelled = player.getDistanceTravelled();
        if(biomeToGenerate != Biome.Village) {
            if (distanceTravelled >= 3000)
                biomeToGenerate = Biome.HardForest;
            else if (distanceTravelled > 400)
                biomeToGenerate = Biome.AdvancedForest;
            else
                biomeToGenerate = Biome.StarterForest;

            // Village can occur only beyond 1.1 km and only after 800 m from previous village
            if(distanceTravelled > 1100 && lastPoint.x - lastVillagePoint > 800)
                if(Global.random.nextFloat() < 0.03f) {     // 3% chance to start Village biome
                    biomeToGenerate = Biome.Village;
                    villageStartPoint = lastPoint.x;
                }
        }
        // after 100 m of Village there is
        // 9+% chance to end Village biome
        else if(lastPoint.x - villageStartPoint > 100 &&
                Global.random.nextFloat() < 0.09f + (lastPoint.x - villageStartPoint - 100)*0.0001f) {
            biomeToGenerate = Biome.StarterForest;
            lastVillagePoint = lastPoint.x;
        }

        switch (biomeToGenerate) {
            case StarterForest:
                generated.addAll(generateStarterForest());
                break;
            case AdvancedForest:
                generated.addAll(generateAdvancedForest(player.getPosition().y<0));
                break;
            case HardForest:
                generated.addAll(generateHardForest(player.getPosition().y < 0));
                break;
            case Village:
                generated.addAll(generateVillage());
                break;

            default: generated.addAll(generateStarterForest());
        }

        return generated;
    }

    public ArrayList<Ground> generateStartHut() {
        ArrayList<Ground> generated = new ArrayList<Ground>();

        for (int i = 0; i < 1; i++) {
            Vector2 position = lastPoint;
            float[] vertices = new float[]{ 0,     -2.5f,
                                            0,      0,
                                            50,     0,
                                            50,    -2.5f };

            lastPoint = new Vector2(lastPoint.x+vertices[vertices.length-4], lastPoint.y+vertices[vertices.length-3]);
            generated.add(new Ground(position, vertices, world, "starterhut"));
        }

        return generated;
    }

    public ArrayList<GameObject> generateStarterForest() {
        ArrayList<GameObject> generated = new ArrayList<GameObject>();

        int dice = Global.random.nextInt(30);

        if(dice < 12)
            generated.addAll(generatePlain());
        else if(dice < 18)
            generated.addAll(generateUphill());
        else if(dice < 23)
            generated.addAll(generateDownhill());
        else if(dice < 25)
            generated.addAll(generateCliffUp());
        else if(dice < 29)
            generated.addAll(generateCliffDown());
        else if(dice < 30)
            generated.addAll(generateHole());

        return generated;
    }

    public ArrayList<GameObject> generateAdvancedForest(boolean moreProbabilityForUpward) {
        ArrayList<GameObject> generated = new ArrayList<GameObject>();
        int upwardFix = moreProbabilityForUpward ? 1 : 0;

        int dice = Global.random.nextInt(30);

        if(dice < 2+upwardFix)
            generated.addAll(generateCliffUp());
        else if(dice < 4+upwardFix*2)
            generated.addAll(generateCliffUpLarge());
        else if(dice < 10+upwardFix*3)
            generated.addAll(generateUphill());
        else if(dice < 12)
            generated.addAll(generateCliffDown());
        else if(dice < 14)
            generated.addAll(generateCliffDownLarge());
        else if(dice < 20)
            generated.addAll(generateDownhill());
        else if(dice < 21)
            generated.addAll(generateHole());
        else if(dice < 23)
            generated.addAll(generateLargeHole());
        else if(dice < 27)
            generated.addAll(generateFallenTree());
        else if(dice < 30)
            generated.addAll(generatePlain());

        return generated;
    }

    private ArrayList<GameObject> generateHardForest(boolean moreProbabilityForUpward) {
        ArrayList<GameObject> generated = new ArrayList<GameObject>();
        int upwardFix = moreProbabilityForUpward ? 1 : 0;

        int dice = Global.random.nextInt(30);

        if(dice < 4+upwardFix)
            generated.addAll(generateCliffUp());
        else if(dice < 7+upwardFix*2)
            generated.addAll(generateCliffUpLarge());
        else if(dice < 9+upwardFix*3)
            generated.addAll(generateUphill());
        else if(dice < 13)
            generated.addAll(generateCliffDown());
        else if(dice < 16)
            generated.addAll(generateCliffDownLarge());
        else if(dice < 18)
            generated.addAll(generateDownhill());
        else if(dice < 20)
            generated.addAll(generateHole());
        else if(dice < 25)
            generated.addAll(generateLargeHole());
        else if(dice < 29)
            generated.addAll(generateFallenTree());
        else if(dice < 30)
            generated.addAll(generatePlain());

        return generated;
    }

    private int previousObject = 0; //auxiliary variable, 0=nothingSpecial, 1=lampPost, 2=well, 3=barn
    public ArrayList<GameObject> generateVillage() {
        ArrayList<GameObject> generated = new ArrayList<GameObject>();

        int dice = Global.random.nextInt(30);

        if(dice < 5 && previousObject!= 2) {
            generated.addAll(generateWell());
            previousObject = 2;
        }
        else if(dice < 10 && previousObject!= 1) {
            generated.addAll(generateLampPost());
            previousObject = 1;
        }
        else if(dice < 15 && previousObject!=3) {
            generated.addAll(generateBarn());
            previousObject = 3;
            int dice2 = Global.random.nextInt(10);
            if(dice2 < 7) {
                if(dice2 < 4)
                    generated.addAll(generateHouse());
                else
                    generated.addAll(generateLongHouse());
            }
        }
        else if(dice < 24) {
            generated.addAll(generateHouse());
            int dice2 = Global.random.nextInt(10);
            if(dice2 < 4 && previousObject != 1) {
                generated.addAll(generateLampPost());
                previousObject = 1;
            }
            else previousObject = 0;
        }
        else if(dice < 30) {
            generated.addAll(generateLongHouse());
            int dice2 = Global.random.nextInt(10);
            if(dice2 < 4 && previousObject != 1) {
                generated.addAll(generateLampPost());
                previousObject = 1;
            }
            else previousObject = 0;
        }

        return generated;
    }

    private ArrayList<GameObject> generateUphill() {
        ArrayList<GameObject> generated = new ArrayList<GameObject>();
        Vector2 position = lastPoint;
        float[] vertices = new float[] { 0,      -2.5f,
                                         0,       0,
                                         5,       0.75f,
                                         10,      2f,
                                         15,      3.25f,
                                         20,      4.25f,
                                         25,      5,
                                         25,     -2.5f };

        lastPoint = new Vector2(lastPoint.x+vertices[vertices.length-4], lastPoint.y+vertices[vertices.length-3]);
        
        generated.add(new Ground(position, vertices, world, "uphill"));

        int decorAmount = Global.random.nextInt(3)+1;
        float spacing = 25 / decorAmount;
        for(int i=0; i<decorAmount; i++) {
            float x = i*spacing + Global.random.nextFloat()*spacing;
            generated.add(new Decoration(new Vector2(position.x + x, position.y + x/5), "flowers"));
        }

        if(0.05f > Global.random.nextFloat()) {
            float x = 25*Global.random.nextFloat();
            generated.add(new ForegroundDecoration(
                    new Vector2(position.x + x, position.y + x / 5), "forest_fore"));
        }
        
        return generated;
    }

    private ArrayList<GameObject> generatePlain() {
        ArrayList<GameObject> generated = new ArrayList<GameObject>();
        Vector2 position = lastPoint;
        float[] vertices = new float[] {    0,    -2.5f,
                                            0,     0,
                                            8,    -0.2f,
                                            12,   -0.1f,
                                            20,    0.2f,
                                            28,    0f,
                                            32,    0f,
                                            32,   -2.5f};

        lastPoint = new Vector2(lastPoint.x+vertices[vertices.length-4], lastPoint.y+vertices[vertices.length-3]);

        generated.add(new Ground(position, vertices, world, "plain"));

        int decorAmount = Global.random.nextInt(4)+2;
        float spacing = 32 / decorAmount;
        for(int i=0; i<decorAmount; i++) {
            generated.add(new Decoration(new Vector2(position.x + i*spacing + Global.random.nextFloat()*spacing, position.y), "flowers"));
        }

        if(0.05f > Global.random.nextFloat()) {
            float x = 32*Global.random.nextFloat();
            generated.add(new ForegroundDecoration(new Vector2(position.x + x, position.y), "forest_fore"));
        }

        return generated;
    }

    private ArrayList<GameObject> generateDownhill() {
        ArrayList<GameObject> generated = new ArrayList<GameObject>();
        Vector2 position = lastPoint;
        float[] vertices = new float[] {    0,    -7.5f,
                                            0,     0,
                                            5,    -0.75f,
                                            10,   -1.75f,
                                            15,   -3.25f,
                                            20,   -4.25f,
                                            25,   -5,
                                            25,   -7.5f};

        lastPoint = new Vector2(lastPoint.x+vertices[vertices.length-4], lastPoint.y+vertices[vertices.length-3]);

        generated.add(new Ground(position, vertices, world, "downhill"));

        int decorAmount = Global.random.nextInt(3)+1;
        float spacing = 25 / decorAmount;
        for(int i=0; i<decorAmount; i++) {
            float x = i*spacing + Global.random.nextFloat()*spacing;
            generated.add(new Decoration(new Vector2(position.x + x, position.y - x/5), "flowers"));
        }

        if(0.05f > Global.random.nextFloat()) {
            float x = 25*Global.random.nextFloat();
            generated.add(new ForegroundDecoration(
                    new Vector2(position.x + x, position.y - x / 5), "forest_fore"));
        }

        return generated;
    }

    private ArrayList<GameObject> generateCliffUp() {
        ArrayList<GameObject> generated = new ArrayList<GameObject>();
        if (doLog) Gdx.app.log("Generate", "Cliff Up");
        Vector2 position = lastPoint;
        float height = 1.75f;
        float[] vertices = new float[] { 0,     -5,
                                         0,      0,
                                         5,      0,
                                         5.3f,   height*0.6f,
                                         5,      height,
                                         10,     height+0.3f,
                                         15,     height+0.5f,
                                         15,    -5 };

        lastPoint = new Vector2(lastPoint.x+vertices[vertices.length-4], lastPoint.y+vertices[vertices.length-3]);

        generated.add(new Ground(position, vertices, world, "cliffup"));

        boolean[] decorPlaces = new boolean[] {    Global.random.nextBoolean(),
                false,
                false,
                Global.random.nextBoolean(),
                Global.random.nextBoolean() };
        for(int i=0; i<5; i++) {
            if (decorPlaces[i])
                generated.add(new Decoration(
                        new Vector2(position.x + vertices[2+i*2] + 0.5f + Global.random.nextFloat()*4f, position.y + vertices[3+i*2]),
                        "flowers"));
        }

        return generated;
    }

    private ArrayList<GameObject> generateCliffUpLarge() {
        ArrayList<GameObject> generated = new ArrayList<GameObject>();
        if (doLog) Gdx.app.log("Generate", "Cliff Up Large");
        Vector2 position = lastPoint;
        float height = 2.5f;
        float[] vertices = new float[] {    0,     -5,
                                            0,      0,
                                            5,      0,
                                            5.3f,   height*0.6f,
                                            5,      height,
                                            10,     height+0.3f,
                                            15,     height+0.5f,
                                            15,    -5 };

        lastPoint = new Vector2(lastPoint.x+vertices[vertices.length-4], lastPoint.y+vertices[vertices.length-3]);

        generated.add(new Ground(position, vertices, world, "cliffuplarge"));

        boolean[] decorPlaces = new boolean[] {    Global.random.nextBoolean(),
                                                    false,
                                                    false,
                                                    Global.random.nextBoolean(),
                                                    Global.random.nextBoolean() };
        for(int i=0; i<5; i++) {
            if (decorPlaces[i])
                generated.add(new Decoration(
                        new Vector2(position.x + vertices[2+i*2] + 0.5f + Global.random.nextFloat()*4f, position.y + vertices[3+i*2]),
                        "flowers"));
        }

        return generated;
    }

    private ArrayList<GameObject> generateCliffDown() {
        ArrayList<GameObject> generated = new ArrayList<GameObject>();
        if (doLog) Gdx.app.log("Generate", "Cliff Down");
        Vector2 position = lastPoint;
        float height = 2.5f;
        float[] vertices = new float[] { 0,     -height-1f,
                                         0,      0,
                                         5,      0,
                                         4.7f,  -height*0.4f,
                                         5,     -height,
                                         10,    -height-0.3f,
                                         15,    -height-0.5f,
                                         15,    -height-1f };

        lastPoint = new Vector2(lastPoint.x+vertices[vertices.length-4], lastPoint.y+vertices[vertices.length-3]);

        generated.add(new Ground(position, vertices, world, "cliffdown"));

        boolean[] decorPlaces = new boolean[] {    Global.random.nextBoolean(),
                                                    false,
                                                    false,
                                                    Global.random.nextBoolean(),
                                                    Global.random.nextBoolean() };
        for(int i=0; i<5; i++) {
            if (decorPlaces[i])
                generated.add(new Decoration(
                        new Vector2(position.x + vertices[4+i*2] -0.5f - Global.random.nextFloat()*4f, position.y + vertices[5+i*2]),
                        "flowers"));
        }

        return generated;
    }

    private ArrayList<GameObject> generateCliffDownLarge() {
        ArrayList<GameObject> generated = new ArrayList<GameObject>();
        if (doLog) Gdx.app.log("Generate", "Cliff Down Large");
        Vector2 position = lastPoint;
        float height = 4.5f;
        float[] vertices = new float[] {    0,     -height-1f,
                                            0,      0,
                                            5,      0,
                                            4.7f,  -height*0.4f,
                                            5,     -height,
                                            10,    -height-0.3f,
                                            15,    -height-0.5f,
                                            15,    -height-1f };

        lastPoint = new Vector2(lastPoint.x+vertices[vertices.length-4], lastPoint.y+vertices[vertices.length-3]);

        generated.add(new Ground(position, vertices, world, "cliffdownlarge"));

        boolean[] decorPlaces = new boolean[] {    Global.random.nextBoolean(),
                                                    false,
                                                    false,
                                                    Global.random.nextBoolean(),
                                                    Global.random.nextBoolean() };
        for(int i=0; i<5; i++) {
            if (decorPlaces[i])
                generated.add(new Decoration(
                        new Vector2(position.x + vertices[4+i*2] -0.5f - Global.random.nextFloat()*4f, position.y + vertices[5+i*2]),
                        "flowers"));
        }

        return generated;
    }

    private ArrayList<GameObject> generateHole() {
        ArrayList<GameObject> generated = new ArrayList<GameObject>();
        if (doLog) Gdx.app.log("Generate", "Hole");
        Vector2 position = lastPoint;
        float[] vertices = new float[] { 0,       -5,
                                         0,        0,
                                         7.5f,     0,
                                         7.75f,   -2,
                                         8f,      -2.25f,
                                         10,      -2.25f,
                                         10.25f,  -2,
                                         10.5f,    0.25f,
                                         15,       0,
                                         15,      -5 };

        lastPoint = new Vector2(lastPoint.x+vertices[vertices.length-4], lastPoint.y+vertices[vertices.length-3]);

        generated.add(new Ground(position, vertices, world, "hole"));

        boolean[] decorPlaces = new boolean[] {    Global.random.nextBoolean(),
                                                    Global.random.nextBoolean() };
        if (decorPlaces[0])
                generated.add(new Decoration(
                        new Vector2(position.x + vertices[2] + Global.random.nextFloat()*7f, position.y + vertices[3]),
                        "flowers"));
        if (decorPlaces[1])
            generated.add(new Decoration(
                    new Vector2(position.x + vertices[vertices.length-4] - Global.random.nextFloat()*4.5f, position.y + vertices[vertices.length-3]),
                    "flowers"));

        return generated;
    }

    private ArrayList<GameObject> generateLargeHole() {
        ArrayList<GameObject> generated = new ArrayList<GameObject>();
        if (doLog) Gdx.app.log("Generate", "Large Hole");
        Vector2 position = lastPoint;
        float[] vertices = new float[]{ 0, -10f,
                                        0, 0,
                                        7.5f, 0,
                                        7.5f, -3f,
                                        8.5f, -4f,
                                        20, -4f,
                                        20.5f, -3f,
                                        21f, 0.25f,
                                        26.5f, 0,
                                        26.5f, -10f};

        lastPoint = new Vector2(lastPoint.x + vertices[vertices.length - 4], lastPoint.y + vertices[vertices.length - 3]);
        generated.add(new Ground(position, vertices, world, "holelarge"));

        boolean[] decorPlaces = new boolean[]{Global.random.nextBoolean(),
                Global.random.nextBoolean(),
                Global.random.nextBoolean()};
        if (decorPlaces[0])
            generated.add(new Decoration(
                    new Vector2(position.x + vertices[2] + Global.random.nextFloat() * 7f, position.y + vertices[3]),
                    "flowers"));
        if (decorPlaces[1])
            if (Global.random.nextBoolean())
                generated.add(new Decoration(
                        new Vector2(position.x + vertices[8] + 0.25f + Global.random.nextFloat() * 11f, position.y + vertices[9]),
                        "flowers"));
            else
                generated.add(new Decoration(
                        new Vector2(position.x + vertices[8] + 0.25f + Global.random.nextFloat() * 11f, position.y + vertices[9]),
                        "fungi"));
        if (decorPlaces[2])
            generated.add(new Decoration(
                    new Vector2(position.x + vertices[vertices.length - 4] - Global.random.nextFloat() * 4.5f, position.y + vertices[vertices.length - 3]),
                    "flowers"));

        return generated;
    }

    private ArrayList<GameObject> generateFallenTree() {
        if (doLog) Gdx.app.log("Generate", "Fallen Tree");
        ArrayList<GameObject> generated = new ArrayList<GameObject>();

        Vector2 position = lastPoint;
        float[] vertices = new float[] {    0,   -5f,
                                            0,    0,
                                            8f,   0.1f,
                                            12f,  0,
                                            17f,  0.1f,
                                            17f, -5f };

        lastPoint = new Vector2(lastPoint.x+vertices[vertices.length-4], lastPoint.y+vertices[vertices.length-3]);
        generated.add(new Ground(position, vertices, world, "fallen_tree_ground"));
        generated.add(new FallenTree(new Vector2(position.x + 8f, position.y + 0.5f), world));

        return generated;
    }

    private ArrayList<GameObject> generateBarn() {
        if (doLog) Gdx.app.log("Generate", "Barn");
        ArrayList<GameObject> generated = new ArrayList<GameObject>();

        Vector2 position = lastPoint;
        float[] vertices = new float[] {    0,      -5f,
                                            0,       0,
                                            20f,     0,
                                            40f,     0,
                                            40f,    -5f };

        lastPoint = new Vector2(lastPoint.x+vertices[vertices.length-4], lastPoint.y+vertices[vertices.length-3]);
        generated.add(new Ground(position, vertices, world, "barn_ground"));
        generated.add(new OneWayPlatform("barn", new Vector2(position.x + 12f, lastPoint.y+6.5f), 20f, 13f, world));
        generated.add(new OneWayPlatform("hay_bale", new Vector2(position.x + 4f, lastPoint.y+1f), 4f, 2f, world));
        generated.add(new OneWayPlatform("large_hay_bale", new Vector2(position.x + 19f, lastPoint.y+1.75f), 7f, 3.5f, world));
        generated.add(new OneWayPlatform("lamp_post", new Vector2(position.x + 34f, lastPoint.y+2.75f), 4f, 5.5f, world));

        if(Global.random.nextInt(10) < 5)
            generated.addAll(generateChickens(new Vector2(position.x + 12, lastPoint.y + 1)));

        return generated;
    }

    private ArrayList<GameObject> generateLampPost() {
        if (doLog) Gdx.app.log("Generate", "Lamp post");
        ArrayList<GameObject> generated = new ArrayList<GameObject>();

        Vector2 position = lastPoint;
        float[] vertices = new float[] {    0,      -5f,
                0,       0,
                7f,      0,
                7f,     -5f };

        lastPoint = new Vector2(lastPoint.x+vertices[vertices.length-4], lastPoint.y+vertices[vertices.length-3]);
        generated.add(new Ground(position, vertices, world, "lamp_ground"));
        generated.add(new OneWayPlatform("lamp_post", new Vector2(position.x + 3.5f, lastPoint.y+2.75f), 4f, 5.5f, world));

        if(Global.random.nextInt(5) < 3)
            generated.add(new Bonus(Bonus.getRandomRareType(player.getSpeedMod()>1), new Vector2(position.x + 3.5f, lastPoint.y+7f), world));

        if(Global.random.nextInt(5) < 2)
            generated.add(new ForegroundDecoration(
                    new Vector2(position.x + Global.random.nextFloat() * 7f, position.y),
                    "village_fore"));

        return generated;
    }

    private ArrayList<GameObject> generateWell() {
        if (doLog) Gdx.app.log("Generate", "Well");
        ArrayList<GameObject> generated = new ArrayList<GameObject>();

        Vector2 position = lastPoint;
        float[] vertices = new float[] {    0,         -20f,
                                            0,          0,
                                            10f,        0,
                                            10f,        1.5f,
                                            10.4f,      1.5f,
                                            10.4f,     -15f,
                                            12.6f,     -15f,
                                            12.6f,      1.5f,
                                            13f,        1.5f,
                                            13f,        0,
                                            18f,        0,
                                            18f,       -20f };


        lastPoint = new Vector2(lastPoint.x+vertices[vertices.length-4], lastPoint.y+vertices[vertices.length-3]);
        generated.add(new Ground(position, vertices, world, "well_ground"));
        generated.add(new OneWayPlatform("well_back", new Vector2(position.x + 11.5f, lastPoint.y+2.4f), 3.2f, 4.8f, world));
        generated.add(new ForegroundDecoration(new Vector2(position.x + 11.5f, lastPoint.y + 1f), "well_front"));

        generated.add(new Bonus(Bonus.getRandomRareType(player.getSpeedMod()>1), new Vector2(position.x + 11.5f, lastPoint.y+6.5f), world));
        return generated;
    }

    private ArrayList<GameObject> generateHouse() {
        if (doLog) Gdx.app.log("Generate", "House");
        ArrayList<GameObject> generated = new ArrayList<GameObject>();

        Vector2 position = lastPoint;
        float[] vertices = new float[] {    0,      -5f,
                0,       0,
                22f,     0,
                22f,    -5f };

        lastPoint = new Vector2(lastPoint.x+vertices[vertices.length-4], lastPoint.y+vertices[vertices.length-3]);
        generated.add(new Ground(position, vertices, world, "house_ground"));
        generated.add(new OneWayPlatform("house", new Vector2(position.x + 10f, lastPoint.y+3.75f), 15f, 7.5f, world));

        int dice = Global.random.nextInt(10);
        if(dice < 5) {
            Bonus.BonusType bonusType = dice < 2 ? Bonus.getRandomRareType(player.getSpeedMod()>1) : Bonus.getRandomWeightedType(false);
            generated.add(new Bonus(bonusType, new Vector2(position.x + 10f, lastPoint.y + 9f), world));
        }

        if(Global.random.nextInt(10) < 4)
            generated.addAll(generateChickens(new Vector2(position.x + 10, lastPoint.y + 1)));

        if(Global.random.nextInt(5) < 2)
            generated.add(new ForegroundDecoration(
                    new Vector2(position.x + 1 + Global.random.nextFloat() * 20f, position.y),
                    "village_fore"));

        return generated;
    }

    private ArrayList<GameObject> generateLongHouse() {
        if (doLog) Gdx.app.log("Generate", "House");
        ArrayList<GameObject> generated = new ArrayList<GameObject>();

        Vector2 position = lastPoint;
        float[] vertices = new float[] {    0,      -5f,
                0,       0,
                30f,     0,
                30f,    -5f };

        lastPoint = new Vector2(lastPoint.x+vertices[vertices.length-4], lastPoint.y+vertices[vertices.length-3]);
        generated.add(new Ground(position, vertices, world, "long_house_ground"));
        generated.add(new OneWayPlatform("long_house_a", new Vector2(position.x + 10f, lastPoint.y+3.75f), 14.5f, 7.5f, world));
        generated.add(new OneWayPlatform("long_house_b", new Vector2(position.x + 20.4f, lastPoint.y+3.4f), 6.5f, 6.9f, world));

        int dice = Global.random.nextInt(9);
        if(dice < 5) {
            Bonus.BonusType bonusType = dice < 2 ? Bonus.getRandomRareType(player.getSpeedMod()>1) : Bonus.getRandomWeightedType(false);
            generated.add(new Bonus(bonusType, new Vector2(position.x + 15f, lastPoint.y + 9f), world));
        }

        if(Global.random.nextInt(10) < 6)
            generated.addAll(generateChickens(new Vector2(position.x + 15, lastPoint.y + 1)));

        if(Global.random.nextInt(5) < 2)
            generated.add(new ForegroundDecoration(
                    new Vector2(position.x + 2 + Global.random.nextFloat() * 25f, position.y),
                    "village_fore"));

        return generated;
    }

    private ArrayList<Chicken> generateChickens(Vector2 position) {
        ArrayList<Chicken> chickens = new ArrayList<Chicken>();

        for(int i=0; i<Global.random.nextInt(3) + 2; i++) {
            chickens.add(new Chicken(new Vector2(position.x + Global.random.nextFloat()*5 - 2.5f, position.y), world, Global.random.nextBoolean()));
        }

        return chickens;
    }

    public Vector2 getLastPoint() {
        return lastPoint;
    }
}
