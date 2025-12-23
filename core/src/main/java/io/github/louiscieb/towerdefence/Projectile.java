package io.github.louiscieb.towerdefence;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Projectile {

    private static Texture sheet;
    private static Animation<TextureRegion> animation;

    private Vector2 position;
    private Enemy target;
    private float damage;
    private float speed = 450f;
    private boolean done = false;
    private float stateTime = 0f;

    public Projectile(Vector2 start, Enemy target, float damage) {
        this.position = start.cpy();
        this.target = target;
        this.damage = damage;

        if (sheet == null) {
            sheet = new Texture("projectiles/fireball_spritesheet.png");

            int frames = 6;
            int fw = sheet.getWidth() / frames;
            int fh = sheet.getHeight();

            TextureRegion[][] split = TextureRegion.split(sheet, fw, fh);
            Array<TextureRegion> arr = new Array<>();
            for (int i = 0; i < frames; i++) arr.add(split[0][i]);

            animation = new Animation<>(0.06f, arr, Animation.PlayMode.LOOP);
        }
    }

    public void update(float delta) {
        stateTime += delta;

        if (target == null || target.isDead()) {
            done = true;
            return;
        }

        Vector2 dir = target.position.cpy().sub(position);
        if (dir.len() < 8f) {
            target.damage(damage);
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
        TextureRegion frame = animation.getKeyFrame(stateTime);
        float size = 20f;
        batch.draw(frame,
            position.x - size / 2,
            position.y - size / 2,
            size, size);
    }

    public static void disposeShared() {
        if (sheet != null) sheet.dispose();
    }
}
