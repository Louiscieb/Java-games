package io.github.louiscieb.towerdefence;

import com.badlogic.gdx.graphics.Pixmap;
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
    private static final float SCALE = 5f;

    // =====================
    // SHARED 1x1 WHITE TEXTURE (for HP bar)
    // =====================
    private static Texture WHITE_PIXEL;
    private static int WHITE_PIXEL_USERS = 0;

    private static void acquireWhitePixel() {
        if (WHITE_PIXEL == null) {
            Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pm.setColor(1, 1, 1, 1);
            pm.fill();
            WHITE_PIXEL = new Texture(pm);
            pm.dispose();
        }
        WHITE_PIXEL_USERS++;
    }

    private static void releaseWhitePixel() {
        WHITE_PIXEL_USERS--;
        if (WHITE_PIXEL_USERS <= 0) {
            WHITE_PIXEL_USERS = 0;
            if (WHITE_PIXEL != null) {
                WHITE_PIXEL.dispose();
                WHITE_PIXEL = null;
            }
        }
    }

    // =====================
    // ANIMATION
    // =====================
    private Animation<TextureRegion> runAnimation;
    private float stateTime = 0f;

    // Keep references to textures we created so we can dispose safely.
    private final Array<Texture> frameTextures = new Array<>();

    // =====================
    // MOVEMENT
    // =====================
    public Vector2 position;
    public Array<Vector2> path;
    public int targetIndex = 0;
    public float speed = 80f;

    // =====================
    // HEALTH
    // =====================
    public float maxHp = 100f;
    public float hp = 100f;

    public Enemy(Array<Vector2> path) {
        this.path = path;
        this.position = path.first().cpy();

        acquireWhitePixel();

        // Load animation frames
        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= 12; i++) {
            Texture tex = new Texture("enemy/run" + i + ".png");
            frameTextures.add(tex);
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
        float barHeight = 10f;
        float hpPercent = maxHp <= 0 ? 0f : (hp / maxHp);
        if (hpPercent < 0f) hpPercent = 0f;
        if (hpPercent > 1f) hpPercent = 1f;

        float barX = position.x - barWidth / 2f;
        float barY = drawY + drawHeight + 8f;

        // Background (red)
        batch.setColor(1, 0, 0, 1);
        batch.draw(WHITE_PIXEL, barX, barY, barWidth, barHeight);

        // Foreground (green)
        batch.setColor(0, 1, 0, 1);
        batch.draw(WHITE_PIXEL, barX, barY, barWidth * hpPercent, barHeight);

        // Reset tint
        batch.setColor(1, 1, 1, 1);
    }

    public void dispose() {
        // Dispose textures we created (safe, no casting)
        for (Texture t : frameTextures) {
            t.dispose();
        }
        frameTextures.clear();

        releaseWhitePixel();
    }
}
