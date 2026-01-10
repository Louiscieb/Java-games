package io.github.louiscieb.towerdefence.model;

import com.badlogic.gdx.math.Vector2;

public class Enemy {

    // ===== GAMEPLAY =====
    private final int level;
    private final float maxHp;
    private float hp;
    private final float speed;
    private final int goldReward;

    // ===== MOVEMENT =====
    private final Path path;
    private final Vector2 position;
    private int targetIndex = 0;

    // ===== Temps d'animation (Pour View) =====
    private float animTime = 0f; //on le met ici pour ne pas a faire deux update (un update render et un update model)(car le delta est du coté de Gameworld)

    public Enemy(Path path, int level) {//constructeur
        this.path = path;
        this.position = path.first().cpy();
        this.level = level;

        this.maxHp = 120 + level * 60;
        this.hp = maxHp;
        this.speed = 70 + level * 6;
        this.goldReward = 15 + level * 5;
    }

    public void update(float delta) {
        animTime += delta;

        if (targetIndex >= path.size()) return;//ennemi a la base

        Vector2 target = path.get(targetIndex);
        Vector2 dir = target.cpy().sub(position);//a modifier si le jeu ramme
        // Si l’ennemi est suffisamment proche du point suivant → passer au suivant
        if (dir.len() < 2f) {
            targetIndex++;
            return;
        }

        dir.nor();
        position.mulAdd(dir, speed * delta);//fait avancer
    }

    // ===== DAMAGE =====targetIndex
    public void takeDamage(float amount) {
        hp -= amount;
        if (hp < 0) hp = 0;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public boolean reachedBase() {
        return targetIndex >= path.size();
    }

    // ===== GETTERS (MVC → VIEW / CONTROLLER) =====
    public Vector2 getPosition() {
        return position;
    }

    public int getLevel() {
        return level;
    }

    public float getHp() {
        return hp;
    }

    public float getMaxHp() {
        return maxHp;
    }

    public int getGoldReward() {
        return goldReward;
    }

    public int getTargetIndex() {
        return targetIndex;
    }

    public float getAnimTime() {
        return animTime;
    }
}
