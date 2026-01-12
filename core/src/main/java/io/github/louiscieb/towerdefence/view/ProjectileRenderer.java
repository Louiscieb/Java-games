package io.github.louiscieb.towerdefence.view;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.github.louiscieb.towerdefence.model.Projectile;

/**
 * Gère le rendu des projectiles du jeu.
 * <p>
 * Cette classe utilise l'animation du projectile définie dans {@link Assets}.
 * Elle dessine le projectile à l'écran en fonction de sa position et de son temps d'animation.
 */
public class ProjectileRenderer {

    /** Animation du projectile. */
    private final Animation<TextureRegion> animation;

    /**
     * Initialise le renderer du projectile.
     * <p>
     * Vérifie que les assets sont chargés avant de créer le renderer.
     *
     * @throws IllegalStateException si {@link Assets#projectileAnim} n'a pas été chargé
     */
    public ProjectileRenderer() {
        if (Assets.projectileAnim == null) {
            throw new IllegalStateException(
                "Assets not loaded: call Assets.load() before creating renderers"
            );
        }
        animation = Assets.projectileAnim;
    }

    /**
     * Rend un projectile à l'écran.
     *
     * @param batch SpriteBatch utilisé pour le rendu
     * @param p     Projectile à dessiner
     */
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

    /**
     * Libère les ressources créées par ce renderer.
     * <p>
     * Les assets sont disposés centralement via {@link Assets#dispose()},
     * donc rien n'est nécessaire ici.
     */
    public void dispose() {
        // Assets disposed centrally
    }
}
