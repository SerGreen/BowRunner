package com.sergreen.bowrunner.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
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

        FileHandle root = Gdx.files.internal("textures");
        ArrayList<FileHandle> texturePaths = getFileHandles(root);

        root = Gdx.files.internal("sounds");
        ArrayList<FileHandle> soundsPaths = getFileHandles(root);

        for(FileHandle texture : texturePaths)
            Global.assetManager.load(texture.path(), Texture.class);
        for(FileHandle sound : soundsPaths)
            Global.assetManager.load(sound.path(), Sound.class);

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
