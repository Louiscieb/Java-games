package io.github.louiscieb.towerdefence;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.louiscieb.towerdefence.controller.GameWorld;
import io.github.louiscieb.towerdefence.model.Enemy;
import io.github.louiscieb.towerdefence.model.Projectile;
import io.github.louiscieb.towerdefence.model.Tower;
import io.github.louiscieb.towerdefence.view.EnemyRenderer;
import io.github.louiscieb.towerdefence.view.HudRenderer;
import io.github.louiscieb.towerdefence.view.ProjectileRenderer;
import io.github.louiscieb.towerdefence.view.TowerRenderer;
import io.github.louiscieb.towerdefence.view.Assets;

public class Main extends ApplicationAdapter {

    private static final int TILE_SIZE = 32;
    private static final int MAP_WIDTH = 50;
    private static final int MAP_HEIGHT = 50;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    private GameWorld world;

    // ===== VIEW / RENDERERS =====
    private EnemyRenderer enemyRenderer;
    private TowerRenderer towerRenderer;
    private ProjectileRenderer projectileRenderer;
    private HudRenderer hudRenderer;

    @Override
    public void create() {
        batch = new SpriteBatch();

        // ===== CAMERA (VIEW ONLY) =====
        camera = new OrthographicCamera();
        camera.setToOrtho(false,
            MAP_WIDTH * TILE_SIZE,
            MAP_HEIGHT * TILE_SIZE
        );
        camera.update();

        viewport = new FitViewport(
            MAP_WIDTH * TILE_SIZE,
            MAP_HEIGHT * TILE_SIZE,
            camera
        );

        // ===== MAP =====
        map = new TmxMapLoader().load("maps/map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        // ===== ASSETS (🔥 OBLIGATOIRE AVANT RENDERERS) =====
        Assets.load();

        // ===== CONTROLLER (MVC) =====
        world = new GameWorld(map, viewport);

        // ===== VIEW / RENDERERS =====
        enemyRenderer = new EnemyRenderer();
        towerRenderer = new TowerRenderer();
        projectileRenderer = new ProjectileRenderer();
        hudRenderer = new HudRenderer();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        // ===== MAP RENDER =====
        mapRenderer.setView(camera);
        mapRenderer.render();

        // ===== UPDATE GAME (CONTROLLER) =====
        world.update(delta);

        // ===== RENDER GAME (VIEW) =====
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (Enemy e : world.getEnemies()) {
            enemyRenderer.render(batch, e);
        }

        for (Tower t : world.getTowers()) {
            towerRenderer.render(batch, t);
        }

        for (Projectile p : world.getProjectiles()) {
            projectileRenderer.render(batch, p);
        }

        // ===== HUD =====
        float camLeft = camera.position.x - camera.viewportWidth / 2f;
        float camTop  = camera.position.y + camera.viewportHeight / 2f;

        hudRenderer.renderBaseHp(
            batch,
            world.getBasePosition(),
            world.getBaseHp(),
            world.getBaseMaxHp()
        );

        hudRenderer.renderTopLeft(
            batch,
            camLeft,
            camTop,
            world.getGold(),
            world.getEnemyLevel(),
            world.getMaxEnemyLevel()
        );

        hudRenderer.renderState(
            batch,
            camera.position.x,
            camera.position.y,
            world.getState()
        );

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {

        // ===== VIEW =====
        if (enemyRenderer != null) enemyRenderer.dispose();
        if (towerRenderer != null) towerRenderer.dispose();
        if (projectileRenderer != null) projectileRenderer.dispose();
        if (hudRenderer != null) hudRenderer.dispose();

        // ===== MAP / CORE =====
        if (mapRenderer != null) mapRenderer.dispose();
        if (map != null) map.dispose();
        if (batch != null) batch.dispose();

        Assets.dispose();
    }
}
