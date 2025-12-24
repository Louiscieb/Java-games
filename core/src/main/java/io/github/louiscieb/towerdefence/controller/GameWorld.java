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

import io.github.louiscieb.towerdefence.model.*;

public class GameWorld {

    // ===== MODEL COLLECTIONS =====
    private final Array<Enemy> enemies = new Array<>();
    private final Array<Tower> towers = new Array<>();
    private final Array<Projectile> projectiles = new Array<>();

    // ===== BUILD ZONES =====
    private final Array<Rectangle> buildZones = new Array<>();

    // ===== VIEWPORT =====
    private final Viewport viewport;

    // ===== TIMERS =====
    private float spawnTimer = 0f;
    private float enemyLevelTimer = 0f;

    // ===== ECONOMY =====
    private static final int TOWER_COST = 50;
    private int gold = 300;

    // ===== BASE =====
    private static final int BASE_MAX_HP = 20;
    private int baseHp = BASE_MAX_HP;
    private final Vector2 basePosition;

    // ===== ENEMY LEVELS =====
    private static final int MAX_ENEMY_LEVEL = 10;
    private static final float ENEMY_LEVEL_INTERVAL = 20f;
    private int enemyLevel = 1;

    // ===== SPAWN CONTROL (🔥 FIX) =====
    private boolean spawningEnabled = true;

    // ===== GAME STATE =====
    private GameState state = GameState.RUNNING;

    // ===== PATH =====
    private final Path path;

    // =====================
    // CONSTRUCTOR
    // =====================
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
    // UPDATE
    // =====================
    public void update(float delta) {
        if (state != GameState.RUNNING) return;

        // ===== LEVEL PROGRESSION =====
        enemyLevelTimer += delta;
        if (enemyLevelTimer >= ENEMY_LEVEL_INTERVAL) {
            enemyLevelTimer = 0f;

            if (enemyLevel < MAX_ENEMY_LEVEL) {
                enemyLevel++;
            } else {
                // 🔥 stop spawning forever at level 10
                spawningEnabled = false;
            }
        }

        // ===== SPAWNING =====
        if (spawningEnabled) {
            spawnTimer += delta;
            if (spawnTimer > 2f) {
                enemies.add(new Enemy(path, enemyLevel));
                spawnTimer = 0f;
            }
        }

        // ===== UPDATE ENTITIES =====
        for (Enemy e : enemies) e.update(delta);
        for (Tower t : towers) t.update(delta, enemies, projectiles);
        for (Projectile p : projectiles) p.update(delta);

        // ===== ENEMY RESOLUTION =====
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy e = enemies.get(i);

            if (e.reachedBase()) {
                baseHp--;
                enemies.removeIndex(i);

                if (baseHp <= 0) {
                    state = GameState.GAME_OVER;
                }
                continue;
            }

            if (e.isDead()) {
                gold += e.getGoldReward();
                enemies.removeIndex(i);
            }
        }

        // ===== PROJECTILE CLEANUP =====
        for (int i = projectiles.size - 1; i >= 0; i--) {
            if (projectiles.get(i).isDone()) {
                projectiles.removeIndex(i);
            }
        }

        // ===== WIN CONDITION (🔥 NOW WORKS) =====
        if (!spawningEnabled && enemies.isEmpty() && baseHp > 0) {
            state = GameState.WIN;
        }

        handleBuildInput();
        handleUpgradeInput();
    }

    // =====================
    // INPUT
    // =====================
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
