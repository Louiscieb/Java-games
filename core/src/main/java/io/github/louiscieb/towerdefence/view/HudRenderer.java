package io.github.louiscieb.towerdefence.view;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.louiscieb.towerdefence.model.GameState;

public class HudRenderer {

    private final BitmapFont font;
    private final Texture whitePixel;

    public HudRenderer() {
        font = new BitmapFont();
        font.getData().setScale(3f);

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1, 1, 1, 1);
        pm.fill();
        whitePixel = new Texture(pm);
        pm.dispose();
    }

    public void renderTopLeft(SpriteBatch batch, float camLeft, float camTop, int gold, int enemyLevel, int maxEnemyLevel) {
        font.draw(batch,
            "Gold: " + gold + " | Enemy Lv: " + enemyLevel + "/" + maxEnemyLevel,
            camLeft + 20,
            camTop - 20
        );
    }

    public void renderBaseHp(SpriteBatch batch, Vector2 basePosition, int hp, int maxHp) {
        float hpPercent = (float) hp / maxHp;

        float barWidth = 180;
        float barHeight = 16;

        float x = basePosition.x - barWidth / 2f;
        float y = basePosition.y + 40;

        batch.setColor(1, 0, 0, 1);
        batch.draw(whitePixel, x, y, barWidth, barHeight);

        batch.setColor(0, 1, 0, 1);
        batch.draw(whitePixel, x, y, barWidth * hpPercent, barHeight);

        batch.setColor(1, 1, 1, 1);
        font.draw(batch, "BASE", basePosition.x - 28, y + 22);
    }

    public void renderState(SpriteBatch batch, float camX, float camY, GameState state) {
        if (state == GameState.GAME_OVER) {
            font.draw(batch, "GAME OVER", camX - 120, camY);
        } else if (state == GameState.WIN) {
            font.draw(batch, "YOU WIN!", camX - 120, camY);
        }
    }

    public void dispose() {
        whitePixel.dispose();
        font.dispose();
    }
}
