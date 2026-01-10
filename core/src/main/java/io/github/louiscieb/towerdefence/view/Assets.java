package io.github.louiscieb.towerdefence.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Assets {

    // ===== TEXTURES =====
    private static Texture projectileSheet;

    // ===== ANIMATIONS =====
    public static Animation<TextureRegion> projectileAnim;

    private static boolean loaded = false;

    // =====================
    // CHARGEMENT
    // =====================
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
    // DISPOSE
    // =====================
    public static void dispose() {
        if (!loaded) return;

        projectileSheet.dispose();
        loaded = false;
    }
}
