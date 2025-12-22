package io.github.louiscieb.towerdefence;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Enemy {

    // =====================
    // VISUAL
    // =====================
    Texture texture = new Texture("libgdx.png");

    // =====================
    // MOVEMENT
    // =====================
    Vector2 position;
    Array<Vector2> path;
    int targetIndex = 0;
    float speed = 80;

    // =====================
    // HEALTH
    // =====================
    float maxHp = 100;
    float hp = 100;

    public Enemy(Array<Vector2> path) {
        this.path = path;
        position = path.first().cpy();
    }

    public void update(float delta) {
        if (targetIndex >= path.size) return;

        Vector2 target = path.get(targetIndex);
        Vector2 dir = target.cpy().sub(position);

        if (dir.len() < 2f) {
            targetIndex++;
            return;
        }

        dir.nor();
        position.mulAdd(dir, speed * delta);
    }

    public void damage(float dmg) {
        hp -= dmg;
        if (hp < 0) hp = 0;
    }

    public boolean isDead() {
        return hp <= 0 || targetIndex >= path.size;
    }

    public void draw(SpriteBatch batch) {

        // ===== DRAW ENEMY =====
        batch.draw(texture, position.x - 16, position.y - 16);

        // ===== DRAW HP BAR (5× BIGGER) =====
        float barWidth = 120;   // was ~24
        float barHeight = 20;   // was ~4
        float hpPercent = hp / maxHp;

        float barX = position.x - barWidth / 2;
        float barY = position.y + 40; // move up so it doesn't overlap enemy

        // background (red)
        batch.setColor(1f, 0f, 0f, 1f);
        batch.draw(texture, barX, barY, barWidth, barHeight);

        // foreground (green)
        batch.setColor(0f, 1f, 0f, 1f);
        batch.draw(texture, barX, barY, barWidth * hpPercent, barHeight);

        // reset color
        batch.setColor(1f, 1f, 1f, 1f);
    }

    public void dispose() {
        texture.dispose();
    }
}
