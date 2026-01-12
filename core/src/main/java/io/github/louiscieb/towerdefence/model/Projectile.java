package io.github.louiscieb.towerdefence.model;

import com.badlogic.gdx.math.Vector2;

/**
 * Représente un projectile tiré par une tour vers un ennemi.
 * <p>
 * Le projectile se déplace en ligne droite vers sa cible
 * et inflige des dégâts lorsqu’il l’atteint.

 * <p>
 * Cette classe appartient au modèle (MVC) et ne contient
 * aucune logique d’affichage.

 */
public class Projectile {

    // =====================
    // PROPRIETES
    // =====================

    /** Position actuelle du projectile. */
    private final Vector2 position;

    /** Cible du projectile. */
    private final Enemy target;

    /** Dégâts infligés à la cible. */
    private final float damage;

    /** Indique si le projectile vient d’être créé (utile pour le son). */
    private boolean justCreated = true;

    // =====================
    // CONSTANTES
    // =====================

    /** Vitesse de déplacement du projectile. */
    private static final float SPEED = 400f;

    // =====================
    // ETAT
    // =====================

    /** Indique si le projectile a terminé son cycle de vie. */
    private boolean done = false;

    /**
     * Temps écoulé depuis la création du projectile.
     * Utilisé pour l’animation côté vue.
     */
    private float animTime = 0f;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Crée un nouveau projectile.
     *
     * @param start  position de départ du projectile
     * @param target ennemi ciblé
     * @param damage dégâts infligés à l’impact
     */
    public Projectile(Vector2 start, Enemy target, float damage) {
        this.position = start.cpy();
        this.target = target;
        this.damage = damage;
    }

    // =====================
    // MISE A JOUR
    // =====================

    /**
     * Met à jour l’état du projectile.
     * <p>
     * Gère :
     * <ul>
     *     <li>Le déplacement vers la cible</li>
     *     <li>La détection de l’impact</li>
     *     <li>L’application des dégâts</li>
     * </ul>

     *
     * @param delta temps écoulé depuis la dernière frame
     */
    public void update(float delta) {
        animTime += delta;

        if (done || target == null || target.isDead()) {
            done = true;
            return;
        }

        Vector2 dir = target.getPosition().cpy().sub(position);

        // Impact : le projectile est suffisamment proche de la cible
        if (dir.len() < 8f) {
            target.takeDamage(damage);
            done = true;
            return;
        }

        dir.nor();
        position.mulAdd(dir, SPEED * delta);
    }

    // =====================
    // GETTERS (MODELE → VUE)
    // =====================

    /**
     * Indique si le projectile vient d’être créé.
     * <p>
     * Cette méthode ne retourne {@code true} qu’une seule fois,
     * puis se désactive automatiquement.
     *
     * @return {@code true} lors du premier appel uniquement
     */
    public boolean consumeJustCreated() {
        if (justCreated) {
            justCreated = false;
            return true;
        }
        return false;
    }

    /**
     * @return position actuelle du projectile
     */
    public Vector2 getPosition() {
        return position;
    }

    /**
     * @return {@code true} si le projectile doit être supprimé
     */
    public boolean isDone() {
        return done;
    }

    /**
     * @return temps écoulé pour l’animation
     */
    public float getAnimTime() {
        return animTime;
    }
}
