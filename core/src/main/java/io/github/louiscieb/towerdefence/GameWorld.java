package io.github.louiscieb.towerdefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

    private Array<Enemy> enemies = new Array<>();
    private Array<Tower> towers = new Array<>();
    private Array<Projectile> projectiles = new Array<>();

    private Array<Vector2> path = new Array<>();
    private Array<Rectangle> buildZones = new Array<>();

    private OrthographicCamera camera;
    private Viewport viewport;

    private float spawnTimer = 0f;

    // ===== ECONOMY =====
    private static final int TOWER_COST = 50;
    private int gold = 300;
    private int goldPerKill = 20;

    // ===== UI =====
    private BitmapFont font;

    public GameWorld(TiledMap map, OrthographicCamera camera, Viewport viewport) {
        this.camera = camera;
        this.viewport = viewport;

        font = new BitmapFont();
        font.getData().setScale(3f); // BIG text

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

        spawnTimer += delta;
        if (spawnTimer > 2f) {
            enemies.add(new Enemy(path));
            spawnTimer = 0f;
        }

        for (Enemy e : enemies) e.update(delta);
        for (Tower t : towers) t.update(delta, enemies, projectiles);
        for (Projectile p : projectiles) p.update(delta);

        // ===== ENEMY DEATH =====
        for (int i = enemies.size - 1; i >= 0; i--) {
            Enemy e = enemies.get(i);
            if (e.isDead()) {
                gold += goldPerKill;
                e.dispose();
                enemies.removeIndex(i);
            }
        }

        // ===== PROJECTILE CLEANUP =====
        for (int i = projectiles.size - 1; i >= 0; i--) {
            if (projectiles.get(i).isDone()) {
                projectiles.removeIndex(i);
            }
        }

        handleBuildInput();
        handleUpgradeInput();
    }

    // =====================
    // BUILD TOWER (LEFT CLICK)
    // =====================
    private void handleBuildInput() {
        if (!Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) return;

        Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        viewport.unproject(mouse);

        for (Rectangle zone : buildZones) {
            if (!zone.contains(mouse.x, mouse.y)) continue;
            if (countTowersInZone(zone) >= 2) return;

            if (gold < TOWER_COST) {
                System.out.println("Not enough gold to build tower");
                return;
            }

            gold -= TOWER_COST;
            towers.add(new Tower(mouse.x, mouse.y));
            System.out.println("Tower built. Gold left: " + gold);
            return;
        }
    }

    // =====================
    // UPGRADE TOWER (RIGHT CLICK)
    // =====================
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
    // DRAW (NO begin/end HERE)
    // =====================
    public void draw(SpriteBatch batch) {

        for (Enemy e : enemies) e.draw(batch);
        for (Tower t : towers) t.draw(batch);
        for (Projectile p : projectiles) p.draw(batch);

        // ===== GOLD (TOP-LEFT RELATIVE TO CAMERA) =====
        font.draw(
            batch,
            "Gold: " + gold,
            camera.position.x - camera.viewportWidth / 2f + 20,
            camera.position.y + camera.viewportHeight / 2f - 20
        );
    }

    public void dispose() {
        for (Enemy e : enemies) e.dispose();
        for (Tower t : towers) t.dispose();
        Projectile.disposeShared();
        font.dispose();
    }
}
