package io.github.louiscieb.towerdefence;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Tower {

    Texture texture = new Texture("libgdx.png");

    Vector2 position;
    float range = 180;
    float cooldown = 0;

    public Tower(float x, float y) {
        position = new Vector2(x, y);
    }

    public void update(float delta, Array<Enemy> enemies, Array<Projectile> projectiles) {
        cooldown -= delta;

        if (cooldown <= 0) {
            Enemy target = findTarget(enemies);
            if (target != null) {
                projectiles.add(new Projectile(position, target));
                cooldown = 1f;
            }
        }
    }

    private Enemy findTarget(Array<Enemy> enemies) {
        Enemy best = null;
        float bestProgress = -1;

        for (Enemy e : enemies) {
            if (position.dst(e.position) <= range) {
                if (e.targetIndex > bestProgress) {
                    best = e;
                    bestProgress = e.targetIndex;
                }
            }
        }
        return best;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x - 16, position.y - 16);
    }

    public void dispose() {
        texture.dispose();
    }
}
