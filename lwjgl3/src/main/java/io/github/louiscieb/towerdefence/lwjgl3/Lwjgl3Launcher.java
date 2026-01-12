package io.github.louiscieb.towerdefence.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import io.github.louiscieb.towerdefence.Main;

/**
 * Classe de lancement de l'application desktop utilisant LWJGL3.
 * <p>
 * Cette classe est le point d'entrée principal de l'application sur ordinateur.
 * Elle configure la fenêtre, les options graphiques (VSync, FPS, OpenGL),
 * puis démarre le jeu LibGDX.
 * </p>
 */
public class Lwjgl3Launcher {

    /**
     * Point d'entrée principal de l'application.
     * <p>
     * Vérifie si la JVM doit être relancée (notamment sur macOS pour le thread principal),
     * puis crée et lance l'application LibGDX.
     * </p>
     *
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        if (StartupHelper.startNewJvmIfRequired()) return;
        createApplication();
    }

    /**
     * Crée et démarre l'application LWJGL3.
     *
     * @return une instance de {@link Lwjgl3Application}
     */
    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    /**
     * Définit la configuration par défaut de l'application.
     * <p>
     * Inclut :
     * <ul>
     *     <li>Le titre de la fenêtre</li>
     *     <li>La synchronisation verticale (VSync)</li>
     *     <li>La limite de FPS</li>
     *     <li>Le mode fenêtré</li>
     *     <li>Les icônes de l'application</li>
     *     <li>L'émulation OpenGL via ANGLE</li>
     * </ul>
     * </p>
     *
     * @return la configuration LWJGL3 par défaut
     */
    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();

        configuration.setTitle("TowerDefence");

        /**
         * <p>
         * Active la VSync pour synchroniser le rendu avec la fréquence de l’écran
         * afin d’éviter le tearing et d’améliorer la stabilité visuelle.
         * </p>
         */
        configuration.useVsync(true);

        /**
         * <p>
         * Limite le nombre de FPS en fonction de la fréquence de rafraîchissement
         * de l’écran (+1 pour garantir l’activation de la VSync).
         * </p>
         */
        configuration.setForegroundFPS(
            Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1
        );

        /**
         * <p>
         * Définit la taille de la fenêtre en mode fenêtré.
         * </p>
         */
        configuration.setWindowedMode(640, 480);

        /**
         * <p>
         * Définit les icônes de l'application pour les différents formats.
         * </p>
         */
        configuration.setWindowIcon(
            "libgdx128.png",
            "libgdx64.png",
            "libgdx32.png",
            "libgdx16.png"
        );

        /**
         * <p>
         * Active l’émulation OpenGL via ANGLE afin d’améliorer la compatibilité
         * sur différentes plateformes et configurations matérielles.
         * </p>
         */
        configuration.setOpenGLEmulation(
            Lwjgl3ApplicationConfiguration.GLEmulation.ANGLE_GLES20,
            0,
            0
        );

        return configuration;
    }
}
