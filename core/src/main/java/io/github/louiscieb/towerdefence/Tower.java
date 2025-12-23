package io.github.louiscieb.towerdefence;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Tower {

    private static Texture spriteSheet;
    private static Animation<TextureRegion> animation;
    private static BitmapFont font = new BitmapFont();
    static {
        font.getData().setScale(2.5f); // 🔥 bigger Lv text
    }

    public Vector2 position;

    private float cooldown = 0f;
    private float stateTime = 0f;

    // ===== LEVEL SYSTEM =====
    private int level = 1;
    private int upgradeCost = 50;

    private float damage = 25f;
    private float range = 180f;
    private float fireRate = 1f;

    public Tower(float x, float y) {
        position = new Vector2(x, y);

        if (spriteSheet == null) {
            spriteSheet = new Texture("towers/red_moon_idle.png");

            int frames = 11;
            int fw = spriteSheet.getWidth() / frames;
            int fh = spriteSheet.getHeight();

            TextureRegion[][] split = TextureRegion.split(spriteSheet, fw, fh);
            Array<TextureRegion> arr = new Array<>();
            for (int i = 0; i < frames; i++) arr.add(split[0][i]);

            animation = new Animation<>(0.1f, arr, Animation.PlayMode.LOOP);
        }
    }

    public void update(float delta, Array<Enemy> enemies, Array<Projectile> projectiles) {
        stateTime += delta;
        cooldown -= delta;

        if (cooldown <= 0f) {
            Enemy target = findTarget(enemies);
            if (target != null) {
                projectiles.add(new Projectile(position, target, damage));
                cooldown = fireRate;
            }
        }
    }

    private Enemy findTarget(Array<Enemy> enemies) {
        Enemy best = null;
        float progress = -1;
        for (Enemy e : enemies) {
            if (position.dst(e.position) <= range && e.targetIndex > progress) {
                best = e;
                progress = e.targetIndex;
            }
        }
        return best;
    }

    // ===== UPGRADE =====
    public boolean canUpgrade(int gold) {
        return gold >= upgradeCost;
    }

    public void upgrade() {
        level++;
        damage += 15;
        range += 20;
        fireRate *= 0.9f;
        upgradeCost = (int)(upgradeCost * 1.6f);
    }

    public int getUpgradeCost() {
        return upgradeCost;
    }

    public int getLevel() {
        return level;
    }

    public void draw(SpriteBatch batch) {
        TextureRegion frame = animation.getKeyFrame(stateTime);

        float scale = 1.5f;
        float w = frame.getRegionWidth() * scale;
        float h = frame.getRegionHeight() * scale;

        batch.draw(frame,
            position.x - w / 2,
            position.y - h / 2,
            w, h);

        // ===== LEVEL TEXT =====
        font.draw(batch, "Lv " + level,
            position.x - 10,
            position.y + h / 2 + 20);
    }

    public void dispose() { }
}
