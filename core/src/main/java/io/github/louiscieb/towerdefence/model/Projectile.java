package io.github.louiscieb.towerdefence.model;

import com.badlogic.gdx.math.Vector2;

public class Projectile {

    private Vector2 position;
    private final Enemy target;
    private final float damage;

    private static final float SPEED = 450f;

    private boolean done = false;
    private float animTime = 0f;

    public Projectile(Vector2 start, Enemy target, float damage) {
        this.position = start.cpy();
        this.target = target;
        this.damage = damage;
    }

    public void update(float delta) {
        animTime += delta;

        if (done || target == null || target.isDead()) {
            done = true;
            return;
        }

        Vector2 dir = target.getPosition().cpy().sub(position);

        if (dir.len() < 8f) {
            target.takeDamage(damage);
            done = true;
            return;
        }

        dir.nor();
        position.mulAdd(dir, SPEED * delta);
    }

    // ===== GETTERS (MVC → VIEW) =====
    public Vector2 getPosition() {
        return position;
    }

    public boolean isDone() {
        return done;
    }

    public float getAnimTime() {
        return animTime;
    }
}
