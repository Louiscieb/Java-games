package io.github.louiscieb.towerdefence.view;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import io.github.louiscieb.towerdefence.model.Tower;

/**
 * Gère le rendu des tours dans le jeu.
 * <p>
 * Affiche la tour avec son animation idle et son niveau.
 * Chaque tour a sa position et son animation déterminée par son temps d'animation.
 */
public class TowerRenderer {

    /** Sprite sheet de la tour. */
    private final Texture spriteSheet;

    /** Animation de la tour. */
    private final Animation<TextureRegion> animation;

    /** Police pour afficher le niveau de la tour. */
    private final BitmapFont font;

    /**
     * Initialise le renderer de la tour.
     * <p>
     * Charge la texture de la tour, découpe les frames pour l'animation et initialise la police.
     */
    public TowerRenderer() {
        spriteSheet = new Texture("towers/red_moon_idle.png");

        int frames = 11;
        int fw = spriteSheet.getWidth() / frames;
        int fh = spriteSheet.getHeight();

        TextureRegion[][] split = TextureRegion.split(spriteSheet, fw, fh);
        Array<TextureRegion> arr = new Array<>();
        for (int i = 0; i < frames; i++) arr.add(split[0][i]);

        animation = new Animation<>(0.1f, arr, Animation.PlayMode.LOOP);

        font = new BitmapFont();
        font.getData().setScale(2.5f);
    }

    /**
     * Rend une tour à l'écran.
     *
     * @param batch SpriteBatch utilisé pour le rendu
     * @param t     Tour à dessiner
     */
    public void render(SpriteBatch batch, Tower t) {
        TextureRegion frame = animation.getKeyFrame(t.getAnimTime());

        float scale = 1f;
        float w = frame.getRegionWidth() * scale;
        float h = frame.getRegionHeight() * scale;

        float x = t.getPosition().x;
        float y = t.getPosition().y;

        batch.draw(frame, x - w / 2f, y - h / 2f, w, h);

        font.draw(batch, "Lv " + t.getLevel(), x - 10, y + h / 2 + 20);
    }

    /**
     * Libère les ressources créées par ce renderer.
     */
    public void dispose() {
        spriteSheet.dispose();
        font.dispose();
    }
}
