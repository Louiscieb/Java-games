package io.github.louiscieb.towerdefence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
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

    float spawnTimer = 0;

    public GameWorld(TiledMap map, OrthographicCamera camera) {
        this.camera = camera;

        MapLayer objects = map.getLayers().get("Objects");

        // PATH WAYPOINTS
        for (int i = 0; ; i++) {
            MapObject obj = objects.getObjects().get("path_" + i);
            if (obj == null) break;
            Rectangle r = ((RectangleMapObject) obj).getRectangle();
            path.add(new Vector2(r.x, r.y));
        }

        // BUILD ZONES
        for (MapObject obj : objects.getObjects()) {
            if ("build".equals(obj.getName())) {
                buildZones.add(((RectangleMapObject) obj).getRectangle());
            }
        }
    }

    public void update(float delta) {

        // SPAWN ENEMIES
        spawnTimer += delta;
        if (spawnTimer > 2f) {
            enemies.add(new Enemy(path));
            spawnTimer = 0;
        }

        // UPDATE ENEMIES
        for (Enemy e : enemies) {
            e.update(delta);
        }

        // UPDATE TOWERS
        for (Tower t : towers) {
            t.update(delta, enemies, projectiles);
        }

        // UPDATE PROJECTILES
        for (Projectile p : projectiles) {
            p.update(delta);
        }

        // CLEANUP
        for (int i = enemies.size - 1; i >= 0; i--) {
            if (enemies.get(i).isDead()) {
                enemies.removeIndex(i);
            }
        }

        for (int i = projectiles.size - 1; i >= 0; i--) {
            if (projectiles.get(i).isDone()) {
                projectiles.removeIndex(i);
            }
        }


        // BUILD ON CLICK
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            Vector3 mouse = camera.unproject(
                new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0)
            );

            for (Rectangle r : buildZones) {
                if (r.contains(mouse.x, mouse.y)) {
                    towers.add(new Tower(r.x + r.width / 2, r.y + r.height / 2));
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
