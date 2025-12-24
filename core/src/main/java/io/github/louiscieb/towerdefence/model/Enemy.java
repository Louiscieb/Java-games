package io.github.louiscieb.towerdefence.model;

import com.badlogic.gdx.math.Vector2;

public class Enemy {

    // ===== GAMEPLAY =====
    private final int level;
    private final float maxHp;
    private float hp;
    private final float speed;
    private final int goldReward;

    // ===== MOVEMENT =====
    private final Path path;
    private final Vector2 position;
    private int targetIndex = 0;

    // ===== ANIMATION TIME (for View) =====
    private float animTime = 0f;

    public Enemy(Path path, int level) {
        this.path = path;
        this.position = path.first().cpy();
        this.level = level;

        this.maxHp = 120 + level * 60;
        this.hp = maxHp;
        this.speed = 70 + level * 6;
        this.goldReward = 15 + level * 5;
    }

    public void update(float delta) {
        animTime += delta;

        if (targetIndex >= path.size()) return;

        Vector2 target = path.get(targetIndex);
        Vector2 dir = target.cpy().sub(position);

        if (dir.len() < 2f) {
            targetIndex++;
            return;
        }

        dir.nor();
        position.mulAdd(dir, speed * delta);
    }

    // ===== DAMAGE =====
    public void takeDamage(float amount) {
        hp -= amount;
        if (hp < 0) hp = 0;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public boolean reachedBase() {
        return targetIndex >= path.size();
    }

    // ===== GETTERS (MVC → VIEW / CONTROLLER) =====
    public Vector2 getPosition() {
        return position;
    }

    public int getLevel() {
        return level;
    }

    public float getHp() {
        return hp;
    }

    public float getMaxHp() {
        return maxHp;
    }

    public int getGoldReward() {
        return goldReward;
    }

    public int getTargetIndex() {
        return targetIndex;
    }

    public float getAnimTime() {
        return animTime;
    }
}
