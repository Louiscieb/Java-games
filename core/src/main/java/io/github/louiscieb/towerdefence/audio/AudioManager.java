package io.github.louiscieb.towerdefence.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {

    private static AudioManager instance;

    private  final  Sound projectileSound;

    private  final Music backgroundMusic;

    private final  Sound squelletonnomore;

    private  final Sound defeat;

    private final  Sound victory;

    private AudioManager() {
        projectileSound = Gdx.audio.newSound(
            Gdx.files.internal("audio/Fireball.mp3")
        );
        squelletonnomore = Gdx.audio.newSound(
            Gdx.files.internal("audio/Bones.mp3")
        );
         victory= Gdx.audio.newSound(
            Gdx.files.internal("audio/Victory.mp3")
        );
        defeat = Gdx.audio.newSound(
            Gdx.files.internal("audio/Defeat.mp3")
        );
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/Music.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.4f);
    }

    // Méthode pour récupérer l’instance
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    public void playProjectile() {
        projectileSound.play(0.2f);
    }

    public void playDying() {
        squelletonnomore.play(0.4f);
    }

    public void playVictory() {
        victory.play(0.3f);
    }

    public void playDefeat() {
        defeat.play(0.5f);
    }

    // Méthodes d’action
    public void playMusic() {
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.play();
        }
    }

    public void stopMusic() {
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
    }

    public void dispose() {
        if (backgroundMusic != null) {
            backgroundMusic.dispose();
            projectileSound.dispose();
        }
    }
}
