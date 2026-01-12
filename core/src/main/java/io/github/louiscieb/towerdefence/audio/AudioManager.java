package io.github.louiscieb.towerdefence.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Gestionnaire centralisé des sons et musiques du jeu.
 * <p>
 * Cette classe implémente le pattern Singleton afin de garantir
 * une seule instance de gestion audio dans toute l'application.
 * Elle permet de jouer :
 * <ul>
 *     <li>Les effets sonores (projectiles, ennemis, victoire, défaite)</li>
 *     <li>La musique de fond en boucle</li>
 * </ul>
 */
public class AudioManager {

    /**
     * Instance unique du AudioManager (Singleton).
     */
    private static AudioManager instance;

    /** Son joué lors du tir d’un projectile. */
    private final Sound projectileSound;

    /** Musique de fond du jeu. */
    private final Music backgroundMusic;

    /** Son joué lorsqu’un squelette meurt. */
    private final Sound squelletonnomore;

    /** Son joué lors d’une défaite. */
    private final Sound defeat;

    /** Son joué lors d’une victoire. */
    private final Sound victory;

    /**
     * Constructeur privé.
     * <p>
     * Charge tous les fichiers audio nécessaires au jeu
     * et configure la musique de fond (boucle et volume).

     */
    private AudioManager() {
        projectileSound = Gdx.audio.newSound(
            Gdx.files.internal("audio/Fireball.mp3")
        );

        squelletonnomore = Gdx.audio.newSound(
            Gdx.files.internal("audio/Bones.mp3")
        );

        victory = Gdx.audio.newSound(
            Gdx.files.internal("audio/Victory.mp3")
        );

        defeat = Gdx.audio.newSound(
            Gdx.files.internal("audio/Defeat.mp3")
        );

        backgroundMusic = Gdx.audio.newMusic(
            Gdx.files.internal("audio/Music.mp3")
        );
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.4f);
    }

    /**
     * Récupère l’instance unique de l’AudioManager.
     *
     * @return l’instance du {@link AudioManager}
     */
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    /**
     * Joue le son du projectile.
     */
    public void playProjectile() {
        projectileSound.play(0.2f);
    }

    /**
     * Joue le son de mort d’un ennemi.
     */
    public void playDying() {
        squelletonnomore.play(0.4f);
    }

    /**
     * Joue le son de victoire.
     */
    public void playVictory() {
        victory.play(0.3f);
    }

    /**
     * Joue le son de défaite.
     */
    public void playDefeat() {
        defeat.play(0.5f);
    }

    /**
     * Lance la musique de fond si elle n’est pas déjà en cours de lecture.
     */
    public void playMusic() {
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
    }

    /**
     * Arrête la musique de fond si elle est en cours de lecture.
     */
    public void stopMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
    }

    /**
     * Libère les ressources audio.
     * <p>
     * Cette méthode doit être appelée à la fermeture du jeu
     * afin d’éviter les fuites mémoire.
     */
    public void dispose() {
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
        }
        if (projectileSound != null) {
            projectileSound.dispose();
        }
        if (squelletonnomore != null) {
            squelletonnomore.dispose();
        }
        if (victory != null) {
            victory.dispose();
        }
        if (defeat != null) {
            defeat.dispose();
        }
    }
}
