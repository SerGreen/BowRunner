package com.sergreen.bowrunner.Screens;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.sergreen.bowrunner.BowRunnerGame;
import com.sergreen.bowrunner.Utils.Global;

import java.util.ArrayList;

/**
 * Created on 12.04.2015 [SerGreen]
 */
public class SplashLoadScreen implements Screen, AssetErrorListener {

    private BowRunnerGame game;
    private OrthographicCamera camera;
    private boolean doneLoading = false;

    public SplashLoadScreen(BowRunnerGame game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Global.initAssetManager();
        Global.assetManager.setErrorListener(this);

        Global.assetManager.load("textures/backgrounds/loading_background.png", Texture.class);
        Global.assetManager.load("textures/gui/loading_bar_back.png", Texture.class);
        Global.assetManager.load("textures/gui/loading_bar_start.png", Texture.class);
        Global.assetManager.load("textures/gui/loading_bar_end.png", Texture.class);
        Global.assetManager.load("textures/gui/loading_bar_filler.png", Texture.class);
        Global.assetManager.load("textures/gui/loading_bar_filler_end.png", Texture.class);

        //* This should work, but it does not
        // [UPD] Ok, it does
        FileHandle root = Gdx.files.internal("textures");
        ArrayList<FileHandle> texturePaths = getFileHandles(root);

        root = Gdx.files.internal("sounds");
        ArrayList<FileHandle> soundsPaths = getFileHandles(root);

        for(FileHandle texture : texturePaths)
            Global.assetManager.load(texture.path(), Texture.class);
        for(FileHandle sound : soundsPaths)
            Global.assetManager.load(sound.path(), Sound.class);
        //*/


        // Okay, i'll comment in english
        // Somehow this crap of a framework can't find the folder via Gdx.files.internal, so instead of those couple lines above we will have this fucking hell of a crunch:
        // [UPD 14.02.2017] Hm, ok, it actually works with code above now. I'll leave all these lines in place just because
        /*
        Global.assetManager.load("sounds/arrow_hit1.mp3", Sound.class);
        Global.assetManager.load("sounds/arrow_shoot1.mp3", Sound.class);
        Global.assetManager.load("sounds/arrow_shoot2.mp3", Sound.class);
        Global.assetManager.load("sounds/bird_flap1.mp3", Sound.class);
        Global.assetManager.load("sounds/bird_flap2.mp3", Sound.class);
        Global.assetManager.load("sounds/bird_flap3.mp3", Sound.class);
        Global.assetManager.load("sounds/bird_flap4.mp3", Sound.class);
        Global.assetManager.load("sounds/bow_load.mp3", Sound.class);
        Global.assetManager.load("sounds/bow_load2.mp3", Sound.class);
        Global.assetManager.load("sounds/bubble.mp3", Sound.class);
        Global.assetManager.load("sounds/button_click2.mp3", Sound.class);
        Global.assetManager.load("sounds/button_click3.wav", Sound.class);
        Global.assetManager.load("sounds/flesh_hit1.mp3", Sound.class);
        Global.assetManager.load("sounds/flesh_hit2.mp3", Sound.class);
        Global.assetManager.load("sounds/flesh_hit3.mp3", Sound.class);
        Global.assetManager.load("sounds/flesh_hit4.mp3", Sound.class);
        Global.assetManager.load("sounds/flesh_hit5.mp3", Sound.class);
        Global.assetManager.load("sounds/flesh_hit6.mp3", Sound.class);
        Global.assetManager.load("sounds/flesh_hit7.mp3", Sound.class);
        Global.assetManager.load("sounds/flesh_hit8.mp3", Sound.class);
        Global.assetManager.load("sounds/game over.mp3", Sound.class);
        Global.assetManager.load("sounds/jump1.mp3", Sound.class);
        Global.assetManager.load("sounds/jump2.mp3", Sound.class);
        Global.assetManager.load("sounds/jump3.mp3", Sound.class);
        Global.assetManager.load("sounds/jump4.mp3", Sound.class);
        Global.assetManager.load("sounds/jump5.mp3", Sound.class);
        Global.assetManager.load("sounds/jump6.mp3", Sound.class);
        Global.assetManager.load("sounds/land1.mp3", Sound.class);
        Global.assetManager.load("sounds/land2.mp3", Sound.class);
        Global.assetManager.load("sounds/land3.mp3", Sound.class);
        Global.assetManager.load("sounds/step1.mp3", Sound.class);
        Global.assetManager.load("sounds/step2.mp3", Sound.class);
        Global.assetManager.load("sounds/target_hit1.mp3", Sound.class);
        Global.assetManager.load("sounds/target_hit2.mp3", Sound.class);
        Global.assetManager.load("sounds/target_hit3.mp3", Sound.class);
        
        Global.assetManager.load("textures/arrow.png", Texture.class);
        Global.assetManager.load("textures/blood.png", Texture.class);
        Global.assetManager.load("textures/blood2.png", Texture.class);
        Global.assetManager.load("textures/backgrounds/forest_background.png", Texture.class);
        Global.assetManager.load("textures/backgrounds/loading_background.png", Texture.class);
        Global.assetManager.load("textures/backgrounds/menu_background_animated.png", Texture.class);
        Global.assetManager.load("textures/backgrounds/radial_rays.png", Texture.class);
        Global.assetManager.load("textures/backgrounds/settings_background.png", Texture.class);
        Global.assetManager.load("textures/backgrounds/stats_best_background.png", Texture.class);
        Global.assetManager.load("textures/backgrounds/stats_total_background.png", Texture.class);
        Global.assetManager.load("textures/decorations/flowers.png", Texture.class);
        Global.assetManager.load("textures/decorations/forest_fore.png", Texture.class);
        Global.assetManager.load("textures/decorations/fungi.png", Texture.class);
        Global.assetManager.load("textures/decorations/village_fore.png", Texture.class);
        Global.assetManager.load("textures/decorations/well_front.png", Texture.class);
        Global.assetManager.load("textures/ground/barn.png", Texture.class);
        Global.assetManager.load("textures/ground/barn_ground.png", Texture.class);
        Global.assetManager.load("textures/ground/box.png", Texture.class);
        Global.assetManager.load("textures/ground/cliffdown.png", Texture.class);
        Global.assetManager.load("textures/ground/cliffdownlarge.png", Texture.class);
        Global.assetManager.load("textures/ground/cliffup.png", Texture.class);
        Global.assetManager.load("textures/ground/cliffuplarge.png", Texture.class);
        Global.assetManager.load("textures/ground/downhill.png", Texture.class);
        Global.assetManager.load("textures/ground/fallen_tree.png", Texture.class);
        Global.assetManager.load("textures/ground/fallen_tree_ground.png", Texture.class);
        Global.assetManager.load("textures/ground/hay_bale.png", Texture.class);
        Global.assetManager.load("textures/ground/hole.png", Texture.class);
        Global.assetManager.load("textures/ground/holelarge.png", Texture.class);
        Global.assetManager.load("textures/ground/house.png", Texture.class);
        Global.assetManager.load("textures/ground/house_ground.png", Texture.class);
        Global.assetManager.load("textures/ground/lamp_ground.png", Texture.class);
        Global.assetManager.load("textures/ground/lamp_post.png", Texture.class);
        Global.assetManager.load("textures/ground/large_hay_bale.png", Texture.class);
        Global.assetManager.load("textures/ground/long_house_a.png", Texture.class);
        Global.assetManager.load("textures/ground/long_house_b.png", Texture.class);
        Global.assetManager.load("textures/ground/long_house_ground.png", Texture.class);
        Global.assetManager.load("textures/ground/plain.png", Texture.class);
        Global.assetManager.load("textures/ground/starterhut.png", Texture.class);
        Global.assetManager.load("textures/ground/uphill.png", Texture.class);
        Global.assetManager.load("textures/ground/well_back.png", Texture.class);
        Global.assetManager.load("textures/ground/well_ground.png", Texture.class);
        Global.assetManager.load("textures/gui/aim_halo.png", Texture.class);
        Global.assetManager.load("textures/gui/aim_sprite.png", Texture.class);
        Global.assetManager.load("textures/gui/arrow_ammo.png", Texture.class);
        Global.assetManager.load("textures/gui/back_button.png", Texture.class);
        Global.assetManager.load("textures/gui/bonus_bar_filler.png", Texture.class);
        Global.assetManager.load("textures/gui/bonus_bar_frame.png", Texture.class);
        Global.assetManager.load("textures/gui/continue_button.png", Texture.class);
        Global.assetManager.load("textures/gui/distance.png", Texture.class);
        Global.assetManager.load("textures/gui/exit_button.png", Texture.class);
        Global.assetManager.load("textures/gui/font.png", Texture.class);
        Global.assetManager.load("textures/gui/font_num.png", Texture.class);
        Global.assetManager.load("textures/gui/home_button.png", Texture.class);
        Global.assetManager.load("textures/gui/jump_button.png", Texture.class);
        Global.assetManager.load("textures/gui/left_button.png", Texture.class);
        Global.assetManager.load("textures/gui/loading_bar_back.png", Texture.class);
        Global.assetManager.load("textures/gui/loading_bar_end.png", Texture.class);
        Global.assetManager.load("textures/gui/loading_bar_filler.png", Texture.class);
        Global.assetManager.load("textures/gui/loading_bar_filler_end.png", Texture.class);
        Global.assetManager.load("textures/gui/loading_bar_start.png", Texture.class);
        Global.assetManager.load("textures/gui/music_button.png", Texture.class);
        Global.assetManager.load("textures/gui/paused.png", Texture.class);
        Global.assetManager.load("textures/gui/quiver.png", Texture.class);
        Global.assetManager.load("textures/gui/retry_button.png", Texture.class);
        Global.assetManager.load("textures/gui/right_button.png", Texture.class);
        Global.assetManager.load("textures/gui/score.png", Texture.class);
        Global.assetManager.load("textures/gui/settings_button.png", Texture.class);
        Global.assetManager.load("textures/gui/sound_button.png", Texture.class);
        Global.assetManager.load("textures/gui/start_button.png", Texture.class);
        Global.assetManager.load("textures/gui/stats_best_button.png", Texture.class);
        Global.assetManager.load("textures/gui/stats_button.png", Texture.class);
        Global.assetManager.load("textures/gui/stats_close_button.png", Texture.class);
        Global.assetManager.load("textures/gui/stats_total_button.png", Texture.class);
        Global.assetManager.load("textures/gui/target_medals.png", Texture.class);
        Global.assetManager.load("textures/pickups/base.png", Texture.class);
        Global.assetManager.load("textures/pickups/bonus_5arrows.png", Texture.class);
        Global.assetManager.load("textures/pickups/bonus_arrow.png", Texture.class);
        Global.assetManager.load("textures/pickups/bonus_bubble.png", Texture.class);
        Global.assetManager.load("textures/pickups/bonus_infinite.png", Texture.class);
        Global.assetManager.load("textures/pickups/bonus_multiplier.png", Texture.class);
        Global.assetManager.load("textures/pickups/bonus_quiver.png", Texture.class);
        Global.assetManager.load("textures/pickups/bonus_score.png", Texture.class);
        Global.assetManager.load("textures/pickups/bonus_slowdown.png", Texture.class);
        Global.assetManager.load("textures/skins/naked/aim_hands.png", Texture.class);
        Global.assetManager.load("textures/skins/naked/aim_torso.png", Texture.class);
        Global.assetManager.load("textures/skins/naked/feather.png", Texture.class);
        Global.assetManager.load("textures/skins/naked/jump_legs.png", Texture.class);
        Global.assetManager.load("textures/skins/naked/jump_torso.png", Texture.class);
        Global.assetManager.load("textures/skins/naked/run_legs.png", Texture.class);
        Global.assetManager.load("textures/skins/naked/run_torso.png", Texture.class);
        Global.assetManager.load("textures/skins/nigger/aim_hands.png", Texture.class);
        Global.assetManager.load("textures/skins/nigger/aim_torso.png", Texture.class);
        Global.assetManager.load("textures/skins/nigger/feather.png", Texture.class);
        Global.assetManager.load("textures/skins/nigger/jump_legs.png", Texture.class);
        Global.assetManager.load("textures/skins/nigger/jump_torso.png", Texture.class);
        Global.assetManager.load("textures/skins/nigger/run_legs.png", Texture.class);
        Global.assetManager.load("textures/skins/nigger/run_torso.png", Texture.class);
        Global.assetManager.load("textures/skins/standard/aim_hands.png", Texture.class);
        Global.assetManager.load("textures/skins/standard/aim_torso.png", Texture.class);
        Global.assetManager.load("textures/skins/standard/feather.png", Texture.class);
        Global.assetManager.load("textures/skins/standard/jump_legs.png", Texture.class);
        Global.assetManager.load("textures/skins/standard/jump_torso.png", Texture.class);
        Global.assetManager.load("textures/skins/standard/run_legs.png", Texture.class);
        Global.assetManager.load("textures/skins/standard/run_torso.png", Texture.class);
        Global.assetManager.load("textures/targets/bird.png", Texture.class);
        Global.assetManager.load("textures/targets/chicken.png", Texture.class);
        Global.assetManager.load("textures/targets/target.png", Texture.class);
        //*/

        // Isn't it WONDERFUL?!
        

        Global.assetManager.load("music/bomberguy-short.mp3", Music.class);
    }

    public ArrayList<FileHandle> getFileHandles(FileHandle startDir) {
        ArrayList<FileHandle> handles = new ArrayList<FileHandle>();
        FileHandle[] newHandles = startDir.list();
        for (FileHandle f : newHandles) {
            if (f.isDirectory()) {
                handles.addAll(getFileHandles(f));
            }
            else {
                handles.add(f);
            }
        }

        return handles;
    }

    @Override
    public void show() {

    }
    @Override
    public void resize(int width, int height) {

    }
    @Override
    public void pause() {

    }
    @Override
    public void resume() {

    }
    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        Global.assetManager.unload("textures/backgrounds/loading_background.png");
        Global.assetManager.unload("textures/gui/loading_bar_back.png");
        Global.assetManager.unload("textures/gui/loading_bar_start.png");
        Global.assetManager.unload("textures/gui/loading_bar_end.png");
        Global.assetManager.unload("textures/gui/loading_bar_filler.png");
        Global.assetManager.unload("textures/gui/loading_bar_filler_end.png");
    }

    private void update() {
        if (Global.assetManager.update()) {
            Global.assetManager.setErrorListener(null);
            Gdx.app.log("Loading", "Done!");
            doneLoading = true;
            Global.initSoundManager();
            game.setScreen(new MenuScreen(game));
            dispose();
        }
    }

    @Override
    public void render(float delta) {
        Global.timeState += delta;
        update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float progress = Global.assetManager.getProgress();
        int progressInt = (int)(progress*100);
        if(doneLoading) {
            progressInt = 100;
            progress = 1;
        }

        if(!doneLoading)
            Gdx.app.log("Loading", String.valueOf(progressInt));


        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        // background
        if(Global.assetManager.isLoaded("textures/backgrounds/loading_background.png")) {
            Texture bg = Global.assetManager.get("textures/backgrounds/loading_background.png", Texture.class);
            game.batch.draw(bg,
                    0, 0,
                    0, 0,
                    Gdx.graphics.getWidth(), Gdx.graphics.getHeight(),
                    1, 1,
                    0,
                    0, 0,
                    bg.getWidth(), bg.getHeight(),
                    false, false);
        }

        drawLoadingBar(progress);

        game.batch.end();
        //*/
    }

    private void drawLoadingBar(float progress) {
        if(Global.assetManager.isLoaded("textures/gui/loading_bar_back.png") &&
           Global.assetManager.isLoaded("textures/gui/loading_bar_start.png") &&
           Global.assetManager.isLoaded("textures/gui/loading_bar_end.png") &&
           Global.assetManager.isLoaded("textures/gui/loading_bar_filler.png") &&
           Global.assetManager.isLoaded("textures/gui/loading_bar_filler_end.png")) {
            Texture back = Global.assetManager.get("textures/gui/loading_bar_back.png", Texture.class);
            Texture start = Global.assetManager.get("textures/gui/loading_bar_start.png", Texture.class);
            Texture filler = Global.assetManager.get("textures/gui/loading_bar_filler.png", Texture.class);
            Texture fillerEnd = Global.assetManager.get("textures/gui/loading_bar_filler_end.png", Texture.class);
            Texture end = Global.assetManager.get("textures/gui/loading_bar_end.png", Texture.class);
            int indent = (int) (Gdx.graphics.getWidth() * 0.11f);   //11% from screen width = ~100px at 854x480
            int loadingBarWidth = Gdx.graphics.getWidth() - indent*2 - start.getWidth() - end.getWidth();
            game.batch.draw(start,
                    indent, Gdx.graphics.getHeight() / 5,
                    0, 0,
                    start.getWidth(), start.getHeight(),
                    1, 1,
                    0,
                    0, 0,
                    start.getWidth(), start.getHeight(),
                    false, false);

            game.batch.draw(back,
                    indent+start.getWidth(), Gdx.graphics.getHeight() / 5,
                    0, 0,
                    loadingBarWidth, back.getHeight(),
                    1, 1,
                    0,
                    0, 0,
                    back.getWidth(), back.getHeight(),
                    false, false);

            game.batch.draw(end,
                    indent+start.getWidth()+loadingBarWidth, Gdx.graphics.getHeight() / 5,
                    0, 0,
                    end.getWidth(), end.getHeight(),
                    1, 1,
                    0,
                    0, 0,
                    end.getWidth(), end.getHeight(),
                    false, false);

            game.batch.draw(filler,
                    indent+start.getWidth(), Gdx.graphics.getHeight() / 5 + 4,
                    0, 0,
                    loadingBarWidth*progress, filler.getHeight(),
                    1, 1,
                    0,
                    0, 0,
                    filler.getWidth(), filler.getHeight(),
                    false, false);

            game.batch.draw(fillerEnd,
                    indent+start.getWidth()+loadingBarWidth*progress, Gdx.graphics.getHeight() / 5 + 4,
                    0, 0,
                    fillerEnd.getWidth(), fillerEnd.getHeight(),
                    1, 1,
                    0,
                    0, 0,
                    fillerEnd.getWidth(), fillerEnd.getHeight(),
                    false, false);
        }
    }

    @Override
    public void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.log("Loading error", asset.fileName + " is not a " + asset.type.getSimpleName().toLowerCase() + " file!");
    }
}
