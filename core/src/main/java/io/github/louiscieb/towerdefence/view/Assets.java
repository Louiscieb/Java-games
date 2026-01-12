package io.github.louiscieb.towerdefence.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

/**
 * Gère le chargement et la libération des assets du jeu.
 * <p>
 * Cette classe centralise les textures et animations pour
 * faciliter l’accès depuis les renderers.
 * </p>
 * <p>
 * Actuellement, elle charge l’animation du projectile.
 * </p>
 */
public class Assets {

    // ===== TEXTURES =====

    /** Sprite sheet du projectile. */
    private static Texture projectileSheet;

    // ===== ANIMATIONS =====

    /** Animation du projectile (boucle). */
    public static Animation<TextureRegion> projectileAnim;

    /** Indique si les assets ont déjà été chargés. */
    private static boolean loaded = false;

    // =====================
    // CHARGEMENT
    // =====================

    /**
     * Charge tous les assets nécessaires.
     * <p>
     * Cette méthode est idempotente : les assets ne sont
     * chargés qu’une seule fois.
     * </p>
     */
    public static void load() {
        if (loaded) return;

        // ===== PROJECTILE =====
        projectileSheet = new Texture("projectiles/fireball_spritesheet.png");

        int frames = 6;
        int frameWidth = projectileSheet.getWidth() / frames;
        int frameHeight = projectileSheet.getHeight();

        TextureRegion[][] split =
            TextureRegion.split(projectileSheet, frameWidth, frameHeight);

        Array<TextureRegion> regions = new Array<>();
        for (int i = 0; i < frames; i++) {
            regions.add(split[0][i]);
        }

        projectileAnim = new Animation<>(
            0.06f,
            regions,
            Animation.PlayMode.LOOP
        );

        loaded = true;
    }

    // =====================
    // NETTOYAGE
    // =====================

    /**
     * Libère tous les assets chargés.
     * <p>
     * Cette méthode doit être appelée lors de la fermeture
     * du jeu pour éviter les fuites mémoire.
     * </p>
     */
    public static void dispose() {
        if (!loaded) return;

        projectileSheet.dispose();
        loaded = false;
    }
}
