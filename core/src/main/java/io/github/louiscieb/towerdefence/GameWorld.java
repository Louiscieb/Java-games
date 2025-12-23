package io.github.louiscieb.towerdefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

public class GameWorld {

    // =====================
    // GAME OBJECTS
    // =====================
    private Array<Enemy> enemies = new Array<>();
    private Array<Tower> towers = new Array<>();
    private Array<Projectile> projectiles = new Array<>();

    private Array<Vector2> path = new Array<>();
    private Array<Rectangle> buildZones = new Array<>();

    private OrthographicCamera camera;
    private Viewport viewport;

    private float spawnTimer = 0f;

    // =====================
    // ECONOMY
    // =====================
    private static final int TOWER_COST = 50;
    private int gold = 30000;

    // =====================
    // BASE SYSTEM
    // =====================
    private static final int BASE_MAX_HP = 20;
    private int baseHp = BASE_MAX_HP;
    private Vector2 basePosition; // 🔥 base location = end of path

    // =====================
    // ENEMY LEVEL SYSTEM
    // =====================
    private static final int MAX_ENEMY_LEVEL = 10;
    private static final float ENEMY_LEVEL_INTERVAL = 20f;

    private int enemyLevel = 1;
    private float enemyLevelTimer = 0f;

    // =====================
    // GAME STATE
    // =====================
    private boolean gameOver = false;
    private boolean gameWon = false;

    // =====================
    // UI / DRAWING
    // =====================
    private BitmapFont font;
    private Texture whitePixel;

    // =====================
    // CONSTRUCTOR
    // =====================
    public GameWorld(TiledMap map, OrthographicCamera camera, Viewport viewport) {
        this.camera = camera;
        this.viewport = viewport;

        // Font
        font = new BitmapFont();
        font.getData().setScale(3f);

        // 1x1 white pixel (for HP bars)
        Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pm.setColor(1, 1, 1, 1);
        pm.fill();
        whitePixel = new Texture(pm);
        pm.dispose();

        MapLayer entities = map.getLayers().get("entities");
        if (entities == null)
            throw new RuntimeException("Object layer 'entities' not found");

        // ===== PATH =====
        MapObject pathObj = entities.getObjects().get("Path");
        Polyline polyline = ((PolylineMapObject) pathObj).getPolyline();
        float[] vertices = polyline.getTransformedVertices();

        for (int i = 0; i < vertices.length; i += 2) {
            path.add(new Vector2(vertices[i], vertices[i + 1]));
        }

        // ===== BASE POSITION =====
        basePosition = path.get(path.size - 1).cpy();

        // ===== BUILD ZONES =====
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

        if (gameOver || gameWon) return;

        // ===== ENEMY LEVEL PROGRESSION =====
        enemyLevelTimer += delta;
        if (enemyLevelTimer >= ENEMY_LEVEL_INTERVAL) {
            enemyLevelTimer = 0f;
            if (enemyLevel < MAX_ENEMY_LEVEL) {
                enemyLevel++;
            }
        }

        // ===== SPAWN ENEMY =====
        spawnTimer += delta;
        if (spawnTimer > 2f) {
            enemies.add(new Enemy(path, enemyLevel));
            spawnTimer = 0f;
        }

        // ===== UPDATE OBJECTS =====
        for (Enemy e : enemies) e.update(delta);
        for (Tower t : towers) t.update(delta, enemies, projectiles);
        for (Projectile p : projectiles) p.update(delta);

        // ===== ENEMY HANDLING =====
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy e = enemies.get(i);

            // Enemy reached base
            if (e.targetIndex >= path.size) {
                baseHp--;
                enemies.removeIndex(i);

                if (baseHp <= 0) {
                    gameOver = true;
                }
                continue;
            }

            // Enemy killed
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

        // ===== WIN CONDITION =====
        if (enemyLevel == MAX_ENEMY_LEVEL && enemies.size == 0 && baseHp > 0) {
            gameWon = true;
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
            if (countTowersInZone(zone) >= 2) return;
            if (gold < TOWER_COST) return;

            gold -= TOWER_COST;
            towers.add(new Tower(mouse.x, mouse.y));
            return;
        }
    }

    private void handleUpgradeInput() {
        if (!Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) return;

        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mouse);

        for (Tower t : towers) {
            if (t.position.dst(mouse.x, mouse.y) < 80f) {
                if (t.canUpgrade(gold)) {
                    gold -= t.getUpgradeCost();
                    t.upgrade();
                }
                return;
            }
        }
    }

    private int countTowersInZone(Rectangle zone) {
        int count = 0;
        for (Tower t : towers) {
            if (zone.contains(t.position.x, t.position.y)) count++;
        }
        return count;
    }

    // =====================
    // DRAW
    // =====================
    public void draw(SpriteBatch batch) {

        // ===== WORLD =====
        for (Enemy e : enemies) e.draw(batch);
        for (Tower t : towers) t.draw(batch);
        for (Projectile p : projectiles) p.draw(batch);

        // ===== BASE HP BAR (NEAR BASE) =====
        float barWidth = 180;
        float barHeight = 16;
        float hpPercent = (float) baseHp / BASE_MAX_HP;

        float barX = basePosition.x - barWidth / 2f;
        float barY = basePosition.y + 40;

        batch.setColor(1, 0, 0, 1);
        batch.draw(whitePixel, barX, barY, barWidth, barHeight);

        batch.setColor(0, 1, 0, 1);
        batch.draw(whitePixel, barX, barY, barWidth * hpPercent, barHeight);

        batch.setColor(1, 1, 1, 1);

        font.draw(
            batch,
            "BASE",
            basePosition.x - 28,
            barY + 22
        );

        // ===== TOP-LEFT UI (GOLD + LEVEL) =====
        font.draw(
            batch,
            "Gold: " + gold + " | Enemy Lv: " + enemyLevel + "/" + MAX_ENEMY_LEVEL,
            camera.position.x - camera.viewportWidth / 2f + 20,
            camera.position.y + camera.viewportHeight / 2f - 20
        );

        // ===== GAME STATE =====
        if (gameOver) {
            font.draw(batch, "GAME OVER",
                camera.position.x - 120,
                camera.position.y);
        }

        if (gameWon) {
            font.draw(batch, "YOU WIN!",
                camera.position.x - 120,
                camera.position.y);
        }
    }

    // =====================
    // DISPOSE
    // =====================
    public void dispose() {
        for (Tower t : towers) t.dispose();
        Projectile.disposeShared();
        Enemy.disposeShared();
        whitePixel.dispose();
        font.dispose();
    }
}
