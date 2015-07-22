package com.sergreen.bowrunner.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

/**
 * Created on 10.02.2015 [SerGreen]
 */
public class Global {
    public static final float PIXELS_TO_METERS = 1f;
    public static final short GROUND_BIT = 1;
    public static final short SEMISOLID_BIT = 2;
    public static final short PLAYER_BIT = 4;
    public static final short ARROW_BIT = 8;
    public static final short TARGET_BIT = 16;
    public static final short PICKUP_BIT = 32;

    // i don't fucking understand why is there 2 meters difference between world and render position
    public static final byte WEIRD_SHIT = 2;

    private static Preferences settings;
    private static Preferences stats;
    private static Preferences achievements;

    public static float timeState = 0;
    public static final GameFont font = new GameFont();
    public static AssetManager assetManager;
    public static SoundManager soundManager;
    public static final Random random = new Random();

    public static void initAssetManager() { assetManager = new AssetManager(); }
    public static void initSoundManager() {
        soundManager = new SoundManager();
    }

    public static float getAngleDegreesBetweenVectors(Vector2 v1, Vector2 v2) {
        float xDiff = v2.x - v1.x;
        float yDiff = v2.y - v1.y;
        return (float) Math.toDegrees(Math.atan2(yDiff, xDiff));
    }

    public static Vector2 rotatePointAroundOrigin(Vector2 point, Vector2 origin, float angle) {
        angle = (float) Math.toRadians(angle);
        return new Vector2((float) (Math.cos(angle) * (point.x - origin.x) - Math.sin(angle) * (point.y - origin.y) + origin.x),
                (float) (Math.sin(angle) * (point.x - origin.x) + Math.cos(angle) * (point.y - origin.y) + origin.y));
    }

    public static Color getRainbowColor(boolean fast) {
        float red, green, blue;
        float multiplier = fast ? 30 : 0.5f;
        float brightness = fast ? 0.75f : 1f;
        red = (float) (Math.sin(timeState * multiplier + 0) * 0.5f + brightness);
        green = (float) (Math.sin(timeState * multiplier + 2) * 0.5f + brightness);
        blue = (float) (Math.sin(timeState * multiplier + 4) * 0.5f + brightness);

        return new Color(red, green, blue, 1);
    }

    public static Preferences getSettings() {
        if (settings == null) {
            settings = Gdx.app.getPreferences("Settings");
            int test = settings.getInteger("High score", -1);
            if (test < 0) {
                settings.putInteger("High score", 0);
                settings.putInteger("Best distance", 0);
                settings.putInteger("Most jumps", 0);
                settings.putInteger("Most targets", 0);
                settings.putInteger("Most arrows", 0);
                settings.putInteger("Skin", 0);
                settings.putBoolean("Sound", true);
                settings.putBoolean("Music", true);
                settings.flush();
            }
        }

        return settings;
    }

    public static Preferences getStats() {
        if (stats == null) {
            stats = Gdx.app.getPreferences("Stats");
            int test = stats.getInteger("Games played", -1);
            if (test < 0) {
                stats.putInteger("Games played", 0);
                stats.putInteger("Distance travelled", 0);
                stats.putInteger("Arrows shot", 0);
                stats.putInteger("Birds killed", 0);
                stats.putInteger("Chickens killed", 0);
                stats.putInteger("Targets killed", 0);
                stats.putInteger("Jumps made", 0);
                stats.putInteger("Double bonuses", 0);
                stats.putInteger("Infinite bonuses", 0);
                stats.flush();
            }
        }

        return stats;
    }

    public static Preferences getAchievements() {
        if (achievements == null) {
            achievements = Gdx.app.getPreferences("Achievements");
            boolean test = achievements.getBoolean("Test", true);
            if (test) {
                achievements.putBoolean("Test", false);
                achievements.putBoolean("1 km", false);
                achievements.putBoolean("2 km", false);
                achievements.putBoolean("5 km", false);
                achievements.putBoolean("1000", false);
                achievements.putBoolean("2000", false);
                achievements.putBoolean("5000", false);
                achievements.putBoolean("10000", false);
                achievements.putBoolean("Bird", false);
                achievements.putBoolean("Arrow arrow", false);
                achievements.putBoolean("2 arrows 1 bird", false);
                achievements.putBoolean("100 in 1", false);
                achievements.putBoolean("200 in 1", false);
                achievements.putBoolean("Long shot", false);
                achievements.putBoolean("Full upgrade", false);
                achievements.putBoolean("12 arrows during infinite", false);
                achievements.putBoolean("Infinite and double", false);
                achievements.flush();
            }
        }

        return achievements;
    }
}