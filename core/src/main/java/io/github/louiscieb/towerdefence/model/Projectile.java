package io.github.louiscieb.towerdefence.model;

import com.badlogic.gdx.math.Vector2;

public class Projectile {

    private final Vector2 position; //position
    private final Enemy target; //cible
    private final float damage; //degats

    private static final float SPEED = 400f;

    private boolean done = false;
    private float animTime = 0f;

    public Projectile(Vector2 start, Enemy target, float damage) { //creation du projectile
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

        if (dir.len() < 8f) { //si dans la meme case
            target.takeDamage(damage);
            done = true;
            return;
        }

        dir.nor();
        position.mulAdd(dir, SPEED * delta);// Sinon mouvoir le projectile
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
