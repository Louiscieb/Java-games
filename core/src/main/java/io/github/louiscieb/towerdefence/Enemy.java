package io.github.louiscieb.towerdefence;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Enemy {

    Texture texture = new Texture("libgdx.png");

    Vector2 position;
    Array<Vector2> path;
    int targetIndex = 0;

    float speed = 80;
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
    }

    public boolean isDead() {
        return hp <= 0 || targetIndex >= path.size;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x - 16, position.y - 16);
    }

    public void dispose() {
        texture.dispose();
    }
}
