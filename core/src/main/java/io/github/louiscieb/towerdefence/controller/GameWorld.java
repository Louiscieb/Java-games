package io.github.louiscieb.towerdefence.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.louiscieb.towerdefence.audio.AudioManager;
import io.github.louiscieb.towerdefence.model.*;

/**
 * Représente le monde de jeu et la logique principale d’une partie.
 * <p>
 * Cette classe agit comme un contrôleur central :
 * <ul>
 *     <li>Gestion des ennemis, tours et projectiles</li>
 *     <li>Gestion de l’économie (or, coût des tours)</li>
 *     <li>Progression des niveaux ennemis</li>
 *     <li>Conditions de victoire et de défaite</li>
 *     <li>Gestion des entrées joueur (construction / amélioration)</li>
 * </ul>
 */
public class GameWorld {

    // =====================
    // COLLECTIONS DU MODELE
    // =====================

    /** Liste des ennemis actifs. */
    private final Array<Enemy> enemies = new Array<>();

    /** Liste des tours construites. */
    private final Array<Tower> towers = new Array<>();

    /** Liste des projectiles actifs. */
    private final Array<Projectile> projectiles = new Array<>();

    // =====================
    // ZONES DE CONSTRUCTION
    // =====================

    /** Zones autorisées pour la construction des tours. */
    private final Array<Rectangle> buildZones = new Array<>();

    // =====================
    // AFFICHAGE
    // =====================

    /** Viewport utilisé pour convertir les coordonnées écran ↔ monde. */
    private final Viewport viewport;

    // =====================
    // TIMERS
    // =====================

    /** Timer de génération des ennemis. */
    private float spawnTimer = 0f;

    /** Timer de progression du niveau des ennemis. */
    private float enemyLevelTimer = 0f;

    // =====================
    // ECONOMIE
    // =====================

    /** Coût de construction d’une tour. */
    private static final int TOWER_COST = 50;

    /** Quantité d’or du joueur. */
    private int gold = 300;

    // =====================
    // BASE
    // =====================

    /** Points de vie maximum de la base. */
    private static final int BASE_MAX_HP = 20;

    /** Points de vie actuels de la base. */
    private int baseHp = BASE_MAX_HP;

    /** Position de la base (fin du chemin). */
    private final Vector2 basePosition;

    // =====================
    // NIVEAUX ENNEMIS
    // =====================

    /** Niveau maximum des ennemis. */
    private static final int MAX_ENEMY_LEVEL = 10;

    /** Temps entre chaque augmentation de niveau ennemi. */
    private static final float ENEMY_LEVEL_INTERVAL = 20f;

    /** Niveau actuel des ennemis. */
    private int enemyLevel = 2;

    // =====================
    // CONTROLE DU SPAWN
    // =====================

    /** Indique si le spawn des ennemis est actif. */
    private boolean spawningEnabled = true;

    // =====================
    // ETAT DU JEU
    // =====================

    /** État actuel de la partie. */
    private GameState state = GameState.RUNNING;

    // =====================
    // CHEMIN DES ENNEMIS
    // =====================

    /** Chemin suivi par les ennemis. */
    private final Path path;

    // =====================
    // CONSTRUCTEUR
    // =====================

    /**
     * Crée un nouveau monde de jeu à partir d’une carte Tiled.
     *
     * @param map      carte Tiled contenant le chemin et les zones de construction
     * @param viewport viewport utilisé pour les conversions de coordonnées
     * @throws RuntimeException si la couche "entities" est absente
     */
    public GameWorld(TiledMap map, Viewport viewport) {
        this.viewport = viewport;

        MapLayer entities = map.getLayers().get("entities");
        if (entities == null)
            throw new RuntimeException("Object layer 'entities' not found");

        Polyline polyline =
            ((PolylineMapObject) entities.getObjects().get("Path")).getPolyline();

        float[] vertices = polyline.getTransformedVertices();
        Array<Vector2> points = new Array<>();

        for (int i = 0; i < vertices.length; i += 2) {
            points.add(new Vector2(vertices[i], vertices[i + 1]));
        }

        this.path = new Path(points);
        this.basePosition = path.last().cpy();

        for (MapObject obj : entities.getObjects()) {
            if ("build".equals(obj.getName())) {
                Rectangle r = ((RectangleMapObject) obj).getRectangle();
                buildZones.add(new Rectangle(r));
            }
        }
    }

    // =====================
    // MISE A JOUR
    // =====================

    /**
     * Met à jour l’état du monde de jeu.
     *
     * @param delta temps écoulé depuis la dernière frame
     */
    public void update(float delta) {
        if (state != GameState.RUNNING) return;

        // Progression du niveau des ennemis
        enemyLevelTimer += delta;
        if (enemyLevelTimer >= ENEMY_LEVEL_INTERVAL) {
            enemyLevelTimer = 0f;

            if (enemyLevel < MAX_ENEMY_LEVEL) {
                enemyLevel++;
            } else {
                spawningEnabled = false;
            }
        }

        // Génération des ennemis
        if (spawningEnabled) {
            spawnTimer += delta;
            if (spawnTimer > 2f) {
                enemies.add(new Enemy(path, enemyLevel));
                spawnTimer = 0f;
            }
        }

        // Mise à jour des entités
        for (Enemy e : enemies) e.update(delta);
        for (Tower t : towers) t.update(delta, enemies, projectiles);
        for (Projectile p : projectiles) {
            if (p.consumeJustCreated()) {
                AudioManager.getInstance().playProjectile();
            }
            p.update(delta);
        }

        // Gestion des ennemis
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy e = enemies.get(i);

            if (e.reachedBase()) {
                baseHp--;
                enemies.removeIndex(i);

                if (baseHp <= 0) {
                    AudioManager.getInstance().playDefeat();
                    state = GameState.GAME_OVER;
                }
                continue;
            }

            if (e.isDead()) {
                gold += e.getGoldReward();
                enemies.removeIndex(i);
                AudioManager.getInstance().playDying();
            }
        }

        // Nettoyage des projectiles
        for (int i = projectiles.size - 1; i >= 0; i--) {
            if (projectiles.get(i).isDone()) {
                projectiles.removeIndex(i);
            }
        }

        // Condition de victoire
        if (!spawningEnabled && enemies.isEmpty() && baseHp > 0) {
            AudioManager.getInstance().playVictory();
            state = GameState.WIN;
        }

        handleBuildInput();
        handleUpgradeInput();
    }

    // =====================
    // GESTION DES ENTREES
    // =====================

    /** Gère l’entrée de construction des tours. */
    private void handleBuildInput() {
        if (!Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) return;

        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mouse);

        for (Rectangle zone : buildZones) {
            if (!zone.contains(mouse.x, mouse.y)) continue;

            if (countTowersInZone(zone) >= 1) return;
            if (gold < TOWER_COST) return;

            gold -= TOWER_COST;

            float cx = zone.x + zone.width / 2f;
            float cy = zone.y + zone.height / 2f;

            towers.add(new Tower(cx, cy));
            return;
        }
    }

    /** Gère l’amélioration des tours existantes. */
    private void handleUpgradeInput() {
        if (!Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) return;

        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mouse);

        for (Tower t : towers) {
            if (t.getPosition().dst(mouse.x, mouse.y) < 80f) {
                if (t.canUpgrade(gold)) {
                    gold -= t.getUpgradeCost();
                    t.upgrade();
                }
                return;
            }
        }
    }

    /**
     * Compte le nombre de tours présentes dans une zone donnée.
     *
     * @param zone zone de construction
     * @return nombre de tours dans la zone
     */
    private int countTowersInZone(Rectangle zone) {
        for (Tower t : towers) {
            if (zone.contains(t.getPosition().x, t.getPosition().y))
                return 1;
        }
        return 0;
    }

    // =====================
    // GETTERS
    // =====================

    public Array<Enemy> getEnemies() { return enemies; }
    public Array<Tower> getTowers() { return towers; }
    public Array<Projectile> getProjectiles() { return projectiles; }

    public int getGold() { return gold; }
    public int getEnemyLevel() { return enemyLevel; }
    public int getMaxEnemyLevel() { return MAX_ENEMY_LEVEL; }

    public int getBaseHp() { return baseHp; }
    public int getBaseMaxHp() { return BASE_MAX_HP; }
    public Vector2 getBasePosition() { return basePosition; }

    public GameState getState() { return state; }
}
