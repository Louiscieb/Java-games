package io.github.louiscieb.towerdefence.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Tower {

    private final Vector2 position;

    private float cooldown = 0f;
    private float animTime = 0f;

    // upgrade system
    private int level = 1;
    private int upgradeCost = 50;

    private float damage = 25f;
    private float range = 180f;
    private float fireRate = 1f;

    public Tower(float x, float y) {
        this.position = new Vector2(x, y);
    }

    public void update(float delta, Array<Enemy> enemies, Array<Projectile> projectiles) {
        animTime += delta;
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
        float bestProgress = -1;

        for (Enemy e : enemies) {
            if (e.isDead()) continue;

            if (position.dst(e.getPosition()) <= range) {
                if (e.getTargetIndex() > bestProgress) {
                    best = e;
                    bestProgress = e.getTargetIndex();
                }
            }
        }
        return best;
    }

    // upgrade
    public boolean canUpgrade(int gold) { return gold >= upgradeCost; }

    public void upgrade() {
        level++;
        damage += 15;
        range += 20;
        fireRate *= 0.9f;
        upgradeCost = (int)(upgradeCost * 1.6f);
    }

    // getters
    public Vector2 getPosition() { return position; }
    public int getLevel() { return level; }
    public int getUpgradeCost() { return upgradeCost; }
    public float getAnimTime() { return animTime; }
}
