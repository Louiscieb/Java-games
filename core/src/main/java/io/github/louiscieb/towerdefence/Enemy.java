package io.github.louiscieb.towerdefence;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
    // LEVEL SYSTEM
    // =====================
    private final int level;
    private final float maxHp;
    private float hp;
    private final float speed;
    private final int goldReward;

    // =====================
    // MOVEMENT
    // =====================
    public Vector2 position;
    public Array<Vector2> path;
    public int targetIndex = 0;

    // =====================
    // ANIMATION (SHARED)
    // =====================
    private static Animation<TextureRegion> runAnimation;
    private static Array<Texture> animationTextures; // 🔥 store textures safely
    private float stateTime = 0f;

    // =====================
    // UI (SHARED)
    // =====================
    private static Texture whitePixel;
    private static BitmapFont font;

    // =====================
    // CONSTRUCTOR
    // =====================
    public Enemy(Array<Vector2> path, int level) {
        this.path = path;
        this.position = path.first().cpy();
        this.level = level;

        this.maxHp = 120 + level * 60;
        this.hp = maxHp;
        this.speed = 70 + level * 6;
        this.goldReward = 15 + level * 5;

        loadAnimationOnce();
        loadUiAssetsOnce();
    }

    // =====================
    // LOAD ASSETS ONCE
    // =====================
    private void loadAnimationOnce() {
        if (runAnimation != null) return;

        animationTextures = new Array<>();
        Array<TextureRegion> frames = new Array<>();

        for (int i = 1; i <= 12; i++) {
            Texture tex = new Texture("enemy/run" + i + ".png");
            animationTextures.add(tex);
            frames.add(new TextureRegion(tex));
        }

        runAnimation = new Animation<>(
            0.08f,
            frames,
            Animation.PlayMode.LOOP
        );
    }

    private void loadUiAssetsOnce() {
        if (whitePixel == null) {
            Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pm.setColor(1, 1, 1, 1);
            pm.fill();
            whitePixel = new Texture(pm);
            pm.dispose();
        }

        if (font == null) {
            font = new BitmapFont();
            font.getData().setScale(1.5f);
        }
    }

    // =====================
    // UPDATE
    // =====================
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

    // =====================
    // DAMAGE
    // =====================
    public void damage(float dmg) {
        hp -= dmg;
        if (hp < 0) hp = 0;
    }

    public boolean isDead() {
        return hp <= 0 || targetIndex >= path.size;
    }

    public int getGoldReward() {
        return goldReward;
    }

    // =====================
    // DRAW
    // =====================
    public void draw(SpriteBatch batch) {
        TextureRegion frame = runAnimation.getKeyFrame(stateTime);

        float w = frame.getRegionWidth() * SCALE;
        float h = frame.getRegionHeight() * SCALE;

        batch.draw(
            frame,
            position.x - w / 2f,
            position.y - h / 2f,
            w,
            h
        );

        // HP BAR
        float barWidth = w * 0.85f;
        float barHeight = 7f;
        float hpPercent = hp / maxHp;

        float barX = position.x - barWidth / 2f;
        float barY = position.y + h / 2f + 20f;

        batch.setColor(1, 0, 0, 1);
        batch.draw(whitePixel, barX, barY, barWidth, barHeight);

        batch.setColor(0, 1, 0, 1);
        batch.draw(whitePixel, barX, barY, barWidth * hpPercent, barHeight);

        batch.setColor(1, 1, 1, 1);

        font.draw(
            batch,
            "Lv " + level,
            position.x - 22f,
            barY + barHeight + 16f
        );
    }

    // =====================
    // DISPOSE (SAFE)
    // =====================
    public static void disposeShared() {
        if (whitePixel != null) whitePixel.dispose();
        if (font != null) font.dispose();

        if (animationTextures != null) {
            for (Texture t : animationTextures) {
                t.dispose();
            }
            animationTextures.clear();
        }
    }
}
