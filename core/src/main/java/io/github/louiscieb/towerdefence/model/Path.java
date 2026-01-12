package io.github.louiscieb.towerdefence.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * Représente un chemin suivi par les ennemis.
 * <p>
 * Le chemin est composé d’une liste ordonnée de points 2D
 * que les ennemis parcourent dans l’ordre.
 * </p>
 * <p>
 * Cette classe fait partie du modèle (MVC) et ne contient
 * aucune logique de rendu ou de déplacement.
 * </p>
 */
public class Path {

    /**
     * Liste ordonnée des points constituant le chemin.
     */
    private final Array<Vector2> points;

    /**
     * Crée un nouveau chemin à partir d’une liste de points.
     *
     * @param points points 2D formant le chemin
     */
    public Path(Array<Vector2> points) {
        this.points = points;
    }

    /**
     * Retourne le premier point du chemin.
     *
     * @return premier point
     */
    public Vector2 first() {
        return points.first();
    }

    /**
     * Retourne le dernier point du chemin.
     *
     * @return dernier point
     */
    public Vector2 last() {
        return points.peek();
    }

    /**
     * Retourne le nombre de points du chemin.
     *
     * @return taille du chemin
     */
    public int size() {
        return points.size;
    }

    /**
     * Retourne le point à l’index donné.
     *
     * @param index index du point demandé
     * @return point correspondant
     * @throws IndexOutOfBoundsException si l’index est invalide
     */
    public Vector2 get(int index) {
        return points.get(index);
    }
}
