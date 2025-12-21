package io.github.louiscieb.towerdefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
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

public class GameWorld {

    Array<Enemy> enemies = new Array<>();
    Array<Tower> towers = new Array<>();
    Array<Projectile> projectiles = new Array<>();

    Array<Vector2> path = new Array<>();
    Array<Rectangle> buildZones = new Array<>();

    OrthographicCamera camera;
    float spawnTimer = 0f;

    public GameWorld(TiledMap map, OrthographicCamera camera) {
        this.camera = camera;

        // ===== DEBUG: list layers =====
        System.out.println("TMX layers:");
        for (MapLayer l : map.getLayers()) {
            System.out.println("- " + l.getName());
        }

        // ===== OBJECT LAYER =====
        MapLayer entities = map.getLayers().get("entities");
        if (entities == null) {
            throw new RuntimeException("Object layer 'entities' not found");
        }

        // ===== PATH (POLYLINE) =====
        MapObject pathObj = entities.getObjects().get("Path");
        if (pathObj == null) {
            throw new RuntimeException("Path object not found (expected 'Path')");
        }

        if (!(pathObj instanceof PolylineMapObject)) {
            throw new RuntimeException("Path must be a PolylineObject");
        }

        Polyline polyline = ((PolylineMapObject) pathObj).getPolyline();
        float[] vertices = polyline.getTransformedVertices();

        for (int i = 0; i < vertices.length; i += 2) {
            path.add(new Vector2(vertices[i], vertices[i + 1]));
        }

        if (path.size < 2) {
            throw new RuntimeException("Path must contain at least 2 points");
        }

        // ===== BUILD ZONES =====
        for (MapObject obj : entities.getObjects()) {
            if ("build".equals(obj.getName())) {
                if (!(obj instanceof RectangleMapObject)) {
                    throw new RuntimeException("Build zones must be RectangleObjects");
                }
                buildZones.add(((RectangleMapObject) obj).getRectangle());
            }
        }

        System.out.println("Loaded path points: " + path.size);
        System.out.println("Loaded build zones: " + buildZones.size);
    }

    public void update(float delta) {

        // ===== SPAWN ENEMIES =====
        spawnTimer += delta;
        if (spawnTimer > 2f) {
            enemies.add(new Enemy(path));
            spawnTimer = 0f;
        }

        // ===== UPDATE =====
        for (Enemy e : enemies) e.update(delta);
        for (Tower t : towers) t.update(delta, enemies, projectiles);
        for (Projectile p : projectiles) p.update(delta);

        // ===== CLEANUP =====
        for (int i = enemies.size - 1; i >= 0; i--) {
            if (enemies.get(i).isDead()) enemies.removeIndex(i);
        }

        for (int i = projectiles.size - 1; i >= 0; i--) {
            if (projectiles.get(i).isDone()) projectiles.removeIndex(i);
        }

        // ===== BUILD ON CLICK =====
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 mouse = camera.unproject(
                new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)
            );

            for (Rectangle r : buildZones) {
                if (r.contains(mouse.x, mouse.y)) {
                    towers.add(new Tower(
                        r.x + r.width / 2,
                        r.y + r.height / 2
                    ));
                    break;
                }
            }
        }
    }

    public void draw(SpriteBatch batch) {
        for (Enemy e : enemies) e.draw(batch);
        for (Tower t : towers) t.draw(batch);
        for (Projectile p : projectiles) p.draw(batch);
    }

    public void dispose() {
        for (Enemy e : enemies) e.dispose();
        for (Tower t : towers) t.dispose();
        for (Projectile p : projectiles) p.dispose();
    }
}
