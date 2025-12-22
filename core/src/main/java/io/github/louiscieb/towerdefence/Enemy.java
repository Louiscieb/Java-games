package io.github.louiscieb.towerdefence;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Enemy {

    // =====================
    // CONFIG
    // =====================
    private static final float SCALE = 5f;   // ✅ 1/3 of 15x size

    // =====================
    // ANIMATION
    // =====================
    private Animation<TextureRegion> runAnimation;
    private float stateTime = 0f;

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

        // Load animation frames
        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= 12; i++) {
            Texture tex = new Texture("enemy/run" + i + ".png");
            frames.add(new TextureRegion(tex));
        }

        runAnimation = new Animation<>(0.08f, frames, Animation.PlayMode.LOOP);
    }

    public void update(float delta) {
        stateTime += delta;

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

        TextureRegion frame = runAnimation.getKeyFrame(stateTime);

        float drawWidth  = frame.getRegionWidth()  * SCALE;
        float drawHeight = frame.getRegionHeight() * SCALE;

        float drawX = position.x - drawWidth / 2f;
        float drawY = position.y - drawHeight / 2f;

        // ===== DRAW NPC =====
        batch.draw(frame, drawX, drawY, drawWidth, drawHeight);

        // ===== HP BAR =====
        float barWidth = drawWidth * 0.8f;
        float barHeight = 16;
        float hpPercent = hp / maxHp;

        float barX = position.x - barWidth / 2f;
        float barY = drawY + drawHeight + 10;

        batch.setColor(1, 0, 0, 1);
        batch.draw(frame.getTexture(), barX, barY, barWidth, barHeight);

        batch.setColor(0, 1, 0, 1);
        batch.draw(frame.getTexture(), barX, barY, barWidth * hpPercent, barHeight);

        batch.setColor(1, 1, 1, 1);
    }

    public void dispose() {
        for (TextureRegion r : runAnimation.getKeyFrames()) {
            r.getTexture().dispose();
        }
    }
}
