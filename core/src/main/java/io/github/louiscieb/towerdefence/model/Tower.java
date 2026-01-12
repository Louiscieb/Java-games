package io.github.louiscieb.towerdefence.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Représente une tour de défense.
 * <p>
 * Une tour attaque automatiquement les ennemis à portée,
 * tire des projectiles et peut être améliorée afin
 * d’augmenter son efficacité.
 * </p>
 * <p>
 * Cette classe fait partie du modèle (MVC) et ne contient
 * aucune logique d’affichage.
 * </p>
 */
public class Tower {

    // =====================
    // PROPRIETES
    // =====================

    /** Position de la tour dans le monde. */
    private final Vector2 position;

    /** Temps restant avant le prochain tir. */
    private float cooldown = 0f;

    /**
     * Temps écoulé pour les animations.
     * Utilisé côté vue.
     */
    private float animTime = 0f;

    // =====================
    // SYSTEME D’AMELIORATION
    // =====================

    /** Niveau actuel de la tour. */
    private int level = 1;

    /** Coût de la prochaine amélioration. */
    private int upgradeCost = 50;

    /** Dégâts infligés par projectile. */
    private float damage = 25f;

    /** Portée d’attaque de la tour. */
    private float range = 180f;

    /** Temps entre deux tirs (en secondes). */
    private float fireRate = 1f;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Crée une nouvelle tour à la position donnée.
     *
     * @param x position X
     * @param y position Y
     */
    public Tower(float x, float y) {
        this.position = new Vector2(x, y);
    }

    // =====================
    // MISE A JOUR
    // =====================

    /**
     * Met à jour l’état de la tour.
     * <p>
     * Gère :
     * <ul>
     *     <li>Le cooldown de tir</li>
     *     <li>La recherche de cible</li>
     *     <li>Le tir de projectiles</li>
     * </ul>
     *
     *
     * @param delta       temps écoulé depuis la dernière frame
     * @param enemies     liste des ennemis actifs
     * @param projectiles liste des projectiles à alimenter
     */
    public void update(float delta, Array<Enemy> enemies, Array<Projectile> projectiles) {
        animTime += delta;
        cooldown -= delta;

        if (cooldown <= 0f) {
            Enemy target = findTarget(enemies);
            if (target != null) {
                projectiles.add(new Projectile(position, target, damage));
                cooldown = fireRate;
            }
        }
    }

    // =====================
    // CIBLAGE
    // =====================

    /**
     * Recherche la meilleure cible parmi les ennemis à portée.
     * <p>
     * La priorité est donnée à l’ennemi le plus avancé
     * sur le chemin vers la base.
     *
     *
     * @param enemies liste des ennemis
     * @return ennemi ciblé ou {@code null} si aucun n’est à portée
     */
    private Enemy findTarget(Array<Enemy> enemies) {
        Enemy best = null;
        float bestProgress = -1;

        for (Enemy e : enemies) {
            if (e.isDead()) continue;

            if (position.dst(e.getPosition()) <= range) {
                if (e.getTargetIndex() > bestProgress) {
                    best = e;
                    bestProgress = e.getTargetIndex();
                }
            }
        }
        return best;
    }

    // =====================
    // AMELIORATION
    // =====================

    /**
     * Indique si la tour peut être améliorée.
     *
     * @param gold or disponible du joueur
     * @return {@code true} si l’amélioration est possible
     */
    public boolean canUpgrade(int gold) {
        return gold >= upgradeCost;
    }

    /**
     * Améliore la tour.
     * <p>
     * Augmente :
     * <ul>
     *     <li>Les dégâts</li>
     *     <li>La portée</li>
     *     <li>La vitesse de tir</li>
     * </ul>
     * Et augmente le coût de la prochaine amélioration.
     *
     */
    public void upgrade() {
        level++;
        damage += 10;
        range += 20;
        fireRate *= 0.8f;
        upgradeCost = (int) (upgradeCost * 1.8f);
    }

    // =====================
    // GETTERS (MODELE → VUE / CONTROLEUR)
    // =====================

    /**
     * @return position de la tour
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * @return niveau actuel de la tour
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return coût de la prochaine amélioration
     */
    public int getUpgradeCost() {
        return upgradeCost;
    }

    /**
     * @return temps écoulé pour l’animation
     */
    public float getAnimTime() {
        return animTime;
    }
}
