package com.sergreen.bowrunner.Game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.sergreen.bowrunner.Utils.Global;

/**
 * Created on 31.03.2015 [SerGreen]
 */
public class PopupScore extends Particle {
    private int score;
    private float scale = 0.015f;
    private Color color;

    public PopupScore(Vector2 position, int score) {
        super("popup");
        this.position = new Vector2(position);
        this.score = score;
        speed = new Vector2(0, +0.04f);
        maxLifetime = 2f;
        float scaleMultiplier = score / 150f + 1;
        scale *= scaleMultiplier;

        if(score >= 90)
            color = Color.ORANGE;
        else if(score >= 50)
            color = Color.YELLOW;
        else if(score < 0)
            color = Color.BLACK;
        else
            color = Color.WHITE;
    }

    @Override
    public void render(SpriteBatch batch) {
        Global.font.drawText(String.valueOf(score), batch, position, scale, color);
    }
}
