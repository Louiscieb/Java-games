package io.github.louiscieb.towerdefence.view;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import io.github.louiscieb.towerdefence.model.Enemy;

/**
 * Gère le rendu graphique des ennemis.
 * <p>
 * Cette classe affiche :
 * <ul>
 *     <li>L’animation de course de l’ennemi</li>
 *     <li>La barre de vie au-dessus de l’ennemi</li>
 *     <li>Le niveau de l’ennemi (texte)</li>
 * </ul>
 * <p>
 * Elle s’occupe également de la création de textures auxiliaires
 * comme un pixel blanc pour les barres de vie.
 */
public class EnemyRenderer {

    /** Facteur d’échelle pour agrandir les sprites. */
    private static final float SCALE = 4f;

    /** Animation de course de l’ennemi. */
    private final Animation<TextureRegion> runAnimation;

    /** Pixel blanc 1x1 utilisé pour dessiner les barres de vie. */
    private final Texture whitePixel;

    /** Police pour afficher le niveau de l’ennemi. */
    private final BitmapFont font;

    /**
     * Initialise le renderer de l’ennemi.
     * <p>
     * Charge toutes les textures de l’animation de course,
     * crée un pixel blanc pour les barres de vie et initialise la police.
     */
    public EnemyRenderer() {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 1; i <= 12; i++) {
            Texture tex = new Texture("enemy/Run" + i + ".png");
            frames.add(new TextureRegion(tex));
        }
        runAnimation = new Animation<>(0.08f, frames, Animation.PlayMode.LOOP);

        // Pixel blanc 1x1 pour barre de vie
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1, 1, 1, 1);
        pm.fill();
        whitePixel = new Texture(pm);
        pm.dispose();

        font = new BitmapFont();
        font.getData().setScale(1.5f);
    }

    /**
     * Rend un ennemi à l’écran.
     *
     * @param batch SpriteBatch utilisé pour le rendu
     * @param e     Ennemi à dessiner
     */
    public void render(SpriteBatch batch, Enemy e) {
        TextureRegion frame = runAnimation.getKeyFrame(e.getAnimTime());

        float w = frame.getRegionWidth() * SCALE;
        float h = frame.getRegionHeight() * SCALE;

        float x = e.getPosition().x;
        float y = e.getPosition().y;

        batch.draw(frame, x - w / 2f, y - h / 2f, w, h);

        // Barre de vie
        float barWidth = w * 0.85f;
        float barHeight = 7f;
        float hpPercent = e.getHp() / e.getMaxHp();
        float barX = x - barWidth / 2f;
        float barY = y + h / 2f + 20f;

        batch.setColor(1, 0, 0, 1);
        batch.draw(whitePixel, barX, barY, barWidth, barHeight);

        batch.setColor(0, 1, 0, 1);
        batch.draw(whitePixel, barX, barY, barWidth * hpPercent, barHeight);

        batch.setColor(1, 1, 1, 1);

        font.draw(batch, "Lv " + e.getLevel(), x - 22f, barY + barHeight + 16f);
    }

    /**
     * Libère les ressources créées par ce renderer.
     * <p>
     * Important : les textures utilisées dans les {@link TextureRegion}
     * ne sont pas disposées ici. Si besoin, garder des références
     * pour un dispose complet.
     */
    public void dispose() {
        whitePixel.dispose();
        font.dispose();
    }
}
