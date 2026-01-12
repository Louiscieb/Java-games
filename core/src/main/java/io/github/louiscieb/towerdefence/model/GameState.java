package io.github.louiscieb.towerdefence.model;

/**
 * Représente les différents états possibles du jeu.
 * <p>
 * Cet enum permet de gérer le cycle de vie d’une partie
 * et de contrôler la logique du jeu selon son état courant.
 </p>
 */
public enum GameState {

    /**
     * Le jeu est en cours d’exécution.
     * <p>
     * Les ennemis apparaissent, les tours attaquent
     * et le joueur peut interagir normalement.

     */
    RUNNING,

    /**
     * La partie est terminée par une défaite.
     * <p>
     * La base du joueur a été détruite.

     */
    GAME_OVER,

    /**
     * La partie est terminée par une victoire.
     * <p>
     * Tous les ennemis ont été vaincus
     * et plus aucun spawn n’est actif.

     */
    WIN
}
