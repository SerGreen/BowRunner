package com.sergreen.bowrunner.Game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.sergreen.bowrunner.Utils.AnimatedSprite;
import com.sergreen.bowrunner.Utils.Global;

/**
 * Created on 06.05.2015 [SerGreen]
 */
public class Background {
    private Texture image;
    private AnimatedSprite rays;
    private OrthographicCamera camera;
    public float scale = 1f;
    private float anchorY = 0;
    float maxShiftY;
    private float Y_SHIFT_CONSTANT;
    private float raysAngle = 0;

    public Background(Texture image, OrthographicCamera camera) {
        this.image = image;
        this.camera = camera;
        scale = camera.viewportHeight / image.getHeight() * 2;
        maxShiftY = image.getHeight() * 0.25f * scale;
        Y_SHIFT_CONSTANT = -image.getHeight() / 2 * scale - 2;

        rays = new AnimatedSprite(Global.assetManager.get("textures/backgrounds/radial_rays.png", Texture.class),
                1, 1, 1, Animation.PlayMode.NORMAL);
        rays.setScaleXY(scale);
    }

    public void render(SpriteBatch batch, float playerDistance, boolean isHighscore) {
        if (isHighscore)
            renderRays(batch);

        float shiftX = (playerDistance / 5) % (image.getWidth() * scale);
        float shiftY = (anchorY - camera.position.y) / 5;

        if (shiftY > maxShiftY) {
            anchorY += maxShiftY - shiftY;
            shiftY = maxShiftY;
        }
        if (shiftY < -maxShiftY) {
            anchorY -= maxShiftY + shiftY;
            shiftY = -maxShiftY;
        }

        for (float i = camera.position.x - camera.viewportWidth / 2 - shiftX;
             i < camera.position.x + camera.viewportWidth / 2;
             i += image.getWidth() * scale) {

            batch.draw(image,
                    i, camera.position.y + Y_SHIFT_CONSTANT + shiftY,
                    0, 0,
                    image.getWidth(), image.getHeight(),
                    scale, scale,
                    0,
                    0, 0,
                    image.getWidth(),
                    image.getHeight(),
                    false, false);
        }

        //Global.font.drawText(String.valueOf(anchorY), batch, new Vector2(camera.position.x, camera.position.y), 0.02f);
    }

    private void renderRays(SpriteBatch batch) {
        rays.setPosition(camera.position.x, camera.position.y - 8);
        rays.setRotation(raysAngle++);
        rays.draw(batch, 0);
    }
}