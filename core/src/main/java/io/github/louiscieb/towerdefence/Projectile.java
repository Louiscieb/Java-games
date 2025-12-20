package io.github.louiscieb.towerdefence;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Projectile {

    Texture texture = new Texture("libgdx.png");

    Vector2 position;
    Enemy target;
    float speed = 300;
    boolean done = false;

    public Projectile(Vector2 start, Enemy target) {
        this.position = start.cpy();
        this.target = target;
    }

    public void update(float delta) {
        if (target.isDead()) {
            done = true;
            return;
        }

        Vector2 dir = target.position.cpy().sub(position);
        if (dir.len() < 5f) {
            target.damage(25);
            done = true;
            return;
        }

        dir.nor();
        position.mulAdd(dir, speed * delta);
    }

    public boolean isDone() {
        return done;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x - 4, position.y - 4);
    }

    public void dispose() {
        texture.dispose();
    }
}
