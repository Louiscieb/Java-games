package io.github.louiscieb.towerdefence.view;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.louiscieb.towerdefence.model.Projectile;

public class ProjectileRenderer {

    private final Animation<TextureRegion> animation;

    public ProjectileRenderer() {
        if (Assets.projectileAnim == null) {
            throw new IllegalStateException(
                "Assets not loaded: call Assets.load() before creating renderers"
            );
        }
        animation = Assets.projectileAnim;
    }

    public void render(SpriteBatch batch, Projectile p) {
        TextureRegion frame = animation.getKeyFrame(p.getAnimTime());
        float size = 20f;

        batch.draw(
            frame,
            p.getPosition().x - size / 2f,
            p.getPosition().y - size / 2f,
            size,
            size
        );
    }

    public void dispose() {
        // Assets disposed centrally
    }
}
