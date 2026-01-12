package io.github.louiscieb.towerdefence.model;

import com.badlogic.gdx.math.Vector2;

/**
 * Représente un ennemi dans le jeu.
 * <p>
 * Un ennemi se déplace le long d’un {@link Path},
 * possède des points de vie, une vitesse et un niveau.
 * Ses statistiques évoluent en fonction de son niveau.

 * <p>
 * Cette classe appartient au modèle (MVC) et ne contient
 * aucune logique d’affichage.

 */
public class Enemy {

    // =====================
    // STATISTIQUES DE JEU
    // =====================

    /** Niveau de l’ennemi. */
    private final int level;

    /** Points de vie maximum. */
    private final float maxHp;

    /** Points de vie actuels. */
    private float hp;

    /** Vitesse de déplacement. */
    private final float speed;

    /** Or gagné par le joueur lorsque l’ennemi est tué. */
    private final int goldReward;

    // =====================
    // DEPLACEMENT
    // =====================

    /** Chemin suivi par l’ennemi. */
    private final Path path;

    /** Position actuelle de l’ennemi. */
    private final Vector2 position;

    /** Index du point cible actuel dans le chemin. */
    private int targetIndex = 0;

    // =====================
    // ANIMATION
    // =====================

    /**
     * Temps écoulé depuis la création de l’ennemi.
     * <p>
     * Utilisé par la vue pour gérer les animations
     * sans nécessiter une mise à jour séparée.
     </p>
     */
    private float animTime = 0f;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Crée un nouvel ennemi.
     *
     * @param path  chemin que l’ennemi doit suivre
     * @param level niveau de l’ennemi (influence ses statistiques)
     */
    public Enemy(Path path, int level) {
        this.path = path;
        this.position = path.first().cpy();
        this.level = level;

        this.maxHp = 120 + level * 60;
        this.hp = maxHp;
        this.speed = 70 + level * 6;
        this.goldReward = 10 + level * 5;
    }

    // =====================
    // MISE A JOUR
    // =====================

    /**
     * Met à jour l’état de l’ennemi.
     * <p>
     * Gère :
     * <ul>
     *     <li>Le déplacement le long du chemin</li>
     *     <li>L’avancement entre les points du chemin</li>
     *     <li>Le temps d’animation</li>
     * </ul>

     *
     * @param delta temps écoulé depuis la dernière frame
     */
    public void update(float delta) {
        animTime += delta;

        // L’ennemi a atteint la base
        if (targetIndex >= path.size()) return;

        Vector2 target = path.get(targetIndex);
        Vector2 dir = target.cpy().sub(position);

        // Si l’ennemi est suffisamment proche du point suivant, passer au suivant
        if (dir.len() < 2f) {
            targetIndex++;
            return;
        }

        dir.nor();
        position.mulAdd(dir, speed * delta);
    }

    // =====================
    // DEGATS ET ETAT
    // =====================

    /**
     * Inflige des dégâts à l’ennemi.
     *
     * @param amount quantité de dégâts infligés
     */
    public void takeDamage(float amount) {
        hp -= amount;
        if (hp < 0) hp = 0;
    }

    /**
     * Indique si l’ennemi est mort.
     *
     * @return {@code true} si les points de vie sont à zéro
     */
    public boolean isDead() {
        return hp <= 0;
    }

    /**
     * Indique si l’ennemi a atteint la base.
     *
     * @return {@code true} si l’ennemi est arrivé à la fin du chemin
     */
    public boolean reachedBase() {
        return targetIndex >= path.size();
    }

    // =====================
    // GETTERS (MODELE → VUE / CONTROLEUR)
    // =====================

    /**
     * @return position actuelle de l’ennemi
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * @return niveau de l’ennemi
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return points de vie actuels
     */
    public float getHp() {
        return hp;
    }

    /**
     * @return points de vie maximum
     */
    public float getMaxHp() {
        return maxHp;
    }

    /**
     * @return quantité d’or donnée lors de la mort
     */
    public int getGoldReward() {
        return goldReward;
    }

    /**
     * @return index du point cible actuel sur le chemin
     */
    public int getTargetIndex() {
        return targetIndex;
    }

    /**
     * @return temps écoulé pour les animations
     */
    public float getAnimTime() {
        return animTime;
    }
}
