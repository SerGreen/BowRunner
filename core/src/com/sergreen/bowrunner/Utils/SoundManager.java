package com.sergreen.bowrunner.Utils;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Created on 24.04.2015 [SerGreen]
 */
public class SoundManager {
    private OrthographicCamera camera;
    private boolean sound = Global.getSettings().getBoolean("Sound");
    private boolean music = Global.getSettings().getBoolean("Music");

    private Music gameMusic = Global.assetManager.get("music/bomberguy-short.mp3", Music.class);

    private Sound[] soundStep = new Sound[]{
            Global.assetManager.get("sounds/step1.mp3", Sound.class),
            Global.assetManager.get("sounds/step2.mp3", Sound.class)};
    private Sound[] soundJump = new Sound[]{
            Global.assetManager.get("sounds/jump1.mp3", Sound.class),
            Global.assetManager.get("sounds/jump2.mp3", Sound.class),
            Global.assetManager.get("sounds/jump3.mp3", Sound.class),
            Global.assetManager.get("sounds/jump4.mp3", Sound.class),
            Global.assetManager.get("sounds/jump5.mp3", Sound.class),
            Global.assetManager.get("sounds/jump6.mp3", Sound.class)};
    private Sound[] soundLand = new Sound[]{
            Global.assetManager.get("sounds/land1.mp3", Sound.class),
            Global.assetManager.get("sounds/land2.mp3", Sound.class),
            Global.assetManager.get("sounds/land3.mp3", Sound.class)};
    private Sound soundLoad = Global.assetManager.get("sounds/bow_load2.mp3", Sound.class);
    private Sound[] soundShoot = new Sound[]{
            Global.assetManager.get("sounds/arrow_shoot1.mp3", Sound.class),
            Global.assetManager.get("sounds/arrow_shoot2.mp3", Sound.class)};
    private Sound soundGameOver = Global.assetManager.get("sounds/game over.mp3", Sound.class);
    private Sound[] soundArrowHitTarget = new Sound[] {
            Global.assetManager.get("sounds/target_hit1.mp3", Sound.class),
            Global.assetManager.get("sounds/target_hit2.mp3", Sound.class),
            Global.assetManager.get("sounds/target_hit3.mp3", Sound.class) };
    private Sound[] soundArrowHitFlesh = new Sound[] {
            Global.assetManager.get("sounds/flesh_hit1.mp3", Sound.class),
            Global.assetManager.get("sounds/flesh_hit2.mp3", Sound.class),
            Global.assetManager.get("sounds/flesh_hit3.mp3", Sound.class),
            Global.assetManager.get("sounds/flesh_hit4.mp3", Sound.class),
            Global.assetManager.get("sounds/flesh_hit5.mp3", Sound.class),
            Global.assetManager.get("sounds/flesh_hit6.mp3", Sound.class),
            Global.assetManager.get("sounds/flesh_hit7.mp3", Sound.class),
            Global.assetManager.get("sounds/flesh_hit8.mp3", Sound.class) };
    private Sound[] soundArrowHitArrow = new Sound[] {
            Global.assetManager.get("sounds/arrow_hit1.mp3", Sound.class) };
    private Sound[] soundFlap = new Sound[] {
            Global.assetManager.get("sounds/bird_flap1.mp3", Sound.class),
            Global.assetManager.get("sounds/bird_flap2.mp3", Sound.class),
            Global.assetManager.get("sounds/bird_flap3.mp3", Sound.class),
            Global.assetManager.get("sounds/bird_flap4.mp3", Sound.class) };
    private Sound[] soundBubble = new Sound[] {
            Global.assetManager.get("sounds/bubble.mp3", Sound.class) };
    private Sound soundClick = Global.assetManager.get("sounds/button_click3.wav", Sound.class);

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }

    public void updateSoundSettings() {
        sound = Global.getSettings().getBoolean("Sound");
        music = Global.getSettings().getBoolean("Music");
    }

    public void startGameMusic() {
        if (music) {
            gameMusic.setLooping(true);
            gameMusic.setVolume(0.1f);
            gameMusic.play();
        }
    }

    public void stopGameMusic() {
        if (music) {
            gameMusic.stop();
            soundGameOver.play(0.45f);
        }
    }

    public void playLandSound(float vSpeed) {
        if (sound) {
            float volume = Math.max(0, Math.min(vSpeed * -0.08f, 0.7f));
            soundLand[Global.random.nextInt(soundLand.length)].play(volume, 0.8f + Global.random.nextFloat() * 0.4f, 0);
        }
    }

    public void playStepSound() {
        if (sound)
            soundStep[Global.random.nextInt(soundStep.length)].play(0.7f, 0.8f + Global.random.nextFloat() * 0.4f, 0);
    }

    public void playJumpSound() {
        if (sound)
            soundJump[Global.random.nextInt(soundJump.length)].play(1f, 0.8f + Global.random.nextFloat() * 0.4f, 0);
    }

    public void playLoadSound() {
        if(sound)
            soundLoad.play(0.5f, 0.8f + Global.random.nextFloat() * 0.4f, 0);
    }

    public void stopLoadSound() {
        soundLoad.stop();
    }

    public void playShootSound() {
        if (sound)
            soundShoot[Global.random.nextInt(soundShoot.length)].play(0.7f, 0.8f + Global.random.nextFloat() * 0.4f, 0);
    }

    public void playArrowHitTargetSound(float hitXPosition, float playerXPosition) {
        if (sound) {
            // further from source - quieter sound and pan is set to right side
            float volumeMod = 1;
            float pan = 0;
            if (camera != null) {
                volumeMod = Math.min(camera.viewportWidth / (hitXPosition - playerXPosition) / 4, 1);
                pan = Math.min((hitXPosition - playerXPosition) / camera.viewportWidth, 1);
            }
            soundArrowHitTarget[Global.random.nextInt(soundArrowHitTarget.length)].play(0.8f * volumeMod, 0.8f + Global.random.nextFloat() * 0.4f, pan);
        }
    }

    public void playArrowHitFleshSound(float hitXPosition, float playerXPosition) {
        if (sound) {
            // further from source - quieter sound and pan is set to right side
            float volumeMod = 1;
            float pan = 0;
            if (camera != null) {
                volumeMod = Math.min(camera.viewportWidth / (hitXPosition - playerXPosition) / 4, 1);
                pan = Math.min((hitXPosition - playerXPosition) / camera.viewportWidth, 1);
            }
            soundArrowHitFlesh[Global.random.nextInt(soundArrowHitFlesh.length)].play(0.8f * volumeMod, 0.8f + Global.random.nextFloat() * 0.4f, pan);
        }
    }

    public void playArrowHitArrowSound(float hitXPosition, float arrowSpeed, float playerXPosition) {
        if (sound) {
        // further from source - quieter sound and pan is set to right side
            float volumeMod = arrowSpeed / 90;
            float pan = 0;
            if (camera != null) {
                volumeMod *= Math.min(camera.viewportWidth / (hitXPosition - playerXPosition) / 4, 1);
                pan = Math.min((hitXPosition - playerXPosition) / camera.viewportWidth, 1);
            }
            soundArrowHitArrow[Global.random.nextInt(soundArrowHitArrow.length)].play(0.5f * volumeMod, 0.8f + Global.random.nextFloat() * 0.4f, pan);
        }
    }

    public void playFlapSound() {
        //float volume = (float) Math.max(0.01f / Math.pow(getPosition().dst2(camera.position.x, camera.position.y), 2), 0.02f);
        if (sound)
            soundFlap[Global.random.nextInt(soundFlap.length)].play(0.01f, 0.8f + Global.random.nextFloat() * 0.4f, 0);
    }

    public void playBubbleSound() {
        if(sound)
            soundBubble[Global.random.nextInt(soundBubble.length)].play(0.5f, 0.8f + Global.random.nextFloat() * 0.4f, 0);
    }

    public void playButtonDownSound() {
        if(sound)
            soundClick.play(0.5f);
    }
    public void playButtonUpSound() {
        if(sound)
            soundClick.play(0.4f, 0.75f, 0);
    }
}
