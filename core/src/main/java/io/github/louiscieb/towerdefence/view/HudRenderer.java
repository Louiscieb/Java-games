package io.github.louiscieb.towerdefence.view;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import io.github.louiscieb.towerdefence.model.GameState;

/**
 * Gère l'affichage de l'interface utilisateur (HUD) du jeu.
 * <p>
 * Affiche :
 * <ul>
 *     <li>Les informations en haut à gauche (or et niveau des ennemis)</li>
 *     <li>La barre de vie de la base</li>
 *     <li>L'état de fin de partie (victoire ou défaite)</li>
 * </ul>
 */
public class HudRenderer {

    /** Police utilisée pour afficher les textes du HUD. */
    private final BitmapFont font;

    /** Pixel blanc 1x1 utilisé pour dessiner les barres de vie. */
    private final Texture whitePixel;

    /**
     * Initialise le renderer du HUD.
     * <p>
     * Crée la police et le pixel blanc nécessaire pour dessiner les barres.
     */
    public HudRenderer() {
        font = new BitmapFont();
        font.getData().setScale(3f);

        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1, 1, 1, 1);
        pm.fill();
        whitePixel = new Texture(pm);
        pm.dispose();
    }

    /**
     * Affiche les informations en haut à gauche de l'écran.
     *
     * @param batch         SpriteBatch utilisé pour le rendu
     * @param camLeft       Coordonnée X gauche de la caméra
     * @param camTop        Coordonnée Y haut de la caméra
     * @param gold          Or du joueur
     * @param enemyLevel    Niveau actuel des ennemis
     * @param maxEnemyLevel Niveau maximal des ennemis
     */
    public void renderTopLeft(SpriteBatch batch, float camLeft, float camTop, int gold, int enemyLevel, int maxEnemyLevel) {
        font.draw(batch,
            "Gold: " + gold + " | Enemy Lv: " + enemyLevel + "/" + maxEnemyLevel,
            camLeft + 20,
            camTop - 20
        );
    }

    /**
     * Affiche la barre de vie de la base.
     *
     * @param batch        SpriteBatch utilisé pour le rendu
     * @param basePosition Position de la base
     * @param hp           Points de vie actuels de la base
     * @param maxHp        Points de vie maximum de la base
     */
    public void renderBaseHp(SpriteBatch batch, Vector2 basePosition, int hp, int maxHp) {
        float hpPercent = (float) hp / maxHp;

        float barWidth = 180;
        float barHeight = 16;

        float x = basePosition.x - barWidth / 2f;
        float y = basePosition.y + 40;

        batch.setColor(1, 0, 0, 1);
        batch.draw(whitePixel, x, y, barWidth, barHeight);

        batch.setColor(0, 1, 0, 1);
        batch.draw(whitePixel, x, y, barWidth * hpPercent, barHeight);

        batch.setColor(1, 1, 1, 1);
        font.draw(batch, "BASE", basePosition.x - 28, y + 22);
    }

    /**
     * Affiche l'état de fin de partie (victoire ou défaite) au centre de l'écran.
     *
     * @param batch SpriteBatch utilisé pour le rendu
     * @param camX  Coordonnée X du centre de la caméra
     * @param camY  Coordonnée Y du centre de la caméra
     * @param state État actuel du jeu
     */
    public void renderState(SpriteBatch batch, float camX, float camY, GameState state) {
        if (state == GameState.GAME_OVER) {
            font.draw(batch, "GAME OVER", camX - 120, camY);
        } else if (state == GameState.WIN) {
            font.draw(batch, "YOU WIN!", camX - 120, camY);
        }
    }

    /**
     * Libère les ressources créées par ce renderer.
     */
    public void dispose() {
        whitePixel.dispose();
        font.dispose();
    }
}
