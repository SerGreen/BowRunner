package com.sergreen.bowrunner.Game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.sergreen.bowrunner.Game.GameObjects.Player;

/**
 * Created by SerGreen on 18.02.2015.
 */
public class GameCamera extends OrthographicCamera {
    private boolean initComplete = false;   //костыли из-за реализации супер-конструктора
    private Player player;
    private float pixelToMeter;     //how many meters in one pixel
    private float meterToPixel;     //how many pixels in one meter


    public GameCamera(float viewportWidth, float viewportHeight, Player player) {
        super(viewportWidth, viewportHeight);
        this.player = player;
        pixelToMeter = viewportWidth / Gdx.graphics.getWidth();
        meterToPixel = 1 / pixelToMeter;
        initComplete = true;
    }

    @Override
    public void update() {
        super.update();

        if (initComplete) {
            if (!player.isDead())
                position.set(player.getPosition().x + viewportWidth * 0.35f,
                             player.getPosition().y + viewportHeight * 0.1f, 0);

            //if (player.isDead()) {
            //    if (zoom > 0.5)
            //        zoom -= 0.001f;

                //if(getAngle() > 70)
                //    rotate(5 * MathUtils.degreesToRadians);
            //}
        }
    }

    public void setAngle(float angle /*in degrees*/) {
        up.x = (float) Math.sin(angle * MathUtils.degreesToRadians);
        up.y = (float) Math.cos(angle * MathUtils.degreesToRadians);
    }

    public float getAngle()
    {
        return (float) Math.toDegrees(Math.atan2(up.y, up.x));
    }

    public Vector2 worldToScreen(Vector2 worldCoordinates) {
        return worldToScreen(worldCoordinates.x, worldCoordinates.y);
    }

    public Vector2 worldToScreen(float worldX, float worldY) {
        return new Vector2((viewportWidth/2*zoom - (position.x - worldX)) * meterToPixel,
                (viewportHeight/2*zoom - (position.y - worldY)) * meterToPixel);
    }

    public Vector2 screenToWorld(Vector2 screenCoordinates){
        return screenToWorld(screenCoordinates.x, screenCoordinates.y);
    }

    public Vector2 screenToWorld(float screenX,  float screenY) {
        return new Vector2(position.x - viewportWidth/2*zoom + screenX * getPixelToMeter(),
                position.y - viewportHeight/2*zoom + screenY * getPixelToMeter());
    }

    public float getPixelToMeter() {
        return pixelToMeter * zoom;
    }

    public float getMeterToPixel() {
        return meterToPixel * zoom;
    }
}