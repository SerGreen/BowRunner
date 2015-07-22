package com.sergreen.bowrunner.Utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.sergreen.bowrunner.Utils.Global;

/**
 * Created on 16.03.2015 [SerGreen]
 */
public class GameFont {
    private Vector2 characterSize = new Vector2(50, 80);

    public Vector2 getCharacterSize() {
        return characterSize;
    }

    public void drawScore(int score, SpriteBatch batch, Vector2 position, OrthographicCamera camera, float angle, Color color) {
        Texture scoreTexture = Global.assetManager.get("textures/gui/score.png", Texture.class);
        float scale = (camera.viewportWidth / 2.5f) / scoreTexture.getWidth();
        float drawWidth = (scoreTexture.getWidth() + String.valueOf(score).length() * characterSize.x) * scale;

        float x = position.x - drawWidth / 2;
        float y = position.y;

        float yShift = characterSize.y / 2 * scale;
        Vector2 rotatePos = Global.rotatePointAroundOrigin(new Vector2(x, y), new Vector2(position.x, position.y + yShift), angle);
        batch.setColor(color);
        batch.draw(scoreTexture,
                rotatePos.x, rotatePos.y - yShift,
                0, yShift,
                scoreTexture.getWidth(), scoreTexture.getHeight(),
                scale, scale,
                angle,
                0, 0,
                scoreTexture.getWidth(), scoreTexture.getHeight(),
                false, false);
        batch.setColor(Color.WHITE);

        drawText(String.valueOf(score), batch, new Vector2(x + scoreTexture.getWidth() * scale + 1, y), scale, angle, new Vector2(position.x, position.y), color);
    }

    public void drawText(String text, SpriteBatch batch, Vector2 position, float scale) {
        drawText(text, batch, position, scale, 0, null, Color.WHITE);
    }

    public void drawText(String text, SpriteBatch batch, Vector2 position, float scale, Color color) {
        drawText(text, batch, position, scale, 0, null, color);
    }

    public void drawText(String text, SpriteBatch batch, Vector2 position, float scale, float angle, Vector2 origin, Color color) {
        Texture font = Global.assetManager.get("textures/gui/font.png", Texture.class);
        text = text.toUpperCase();
        Vector2 newPosition;
        float yShift = characterSize.y / 2 * scale;
        for (int i = 0; i < text.length(); i++) {
            int index = getCharacterIndex(text.charAt(i));
            if(index < 0)
                continue;

            if (angle != 0)
                newPosition = Global.rotatePointAroundOrigin(new Vector2(position.x + characterSize.x * i * scale, position.y), new Vector2(origin.x, origin.y + yShift), angle);
            else
                newPosition = new Vector2(position.x + characterSize.x * i * scale, position.y);
            if (color != Color.WHITE)
                batch.setColor(color);
            batch.draw(font,
                    newPosition.x, newPosition.y - yShift,
                    0, yShift,
                    characterSize.x, characterSize.y,
                    scale, scale,
                    angle,
                    (int) (index * characterSize.x), 0,
                    (int) characterSize.x, (int) characterSize.y,
                    false, false);
            if (color != Color.WHITE)
                batch.setColor(Color.WHITE);
        }
    }

    private int getCharacterIndex(char c) {
        switch (c) {
            case '.':
                return 36;
            case '!':
                return 37;
            case '?':
                return  38;
            case '-':
                return  39;
            case ':':
                return  40;
            case '+':
                return  41;
            case '/':
                return  42;
            case ' ':
                return -1;
            default:
                int letter = c - 65;
                if (letter < 0 || letter > 25) {
                    if (letter >= -17 && letter <= -8)
                        letter += 43;
                    else
                        letter = 38;
                }
                return letter;
        }
    }
}