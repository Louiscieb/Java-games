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

import io.github.louiscieb.towerdefence.audio.AudioManager;
import io.github.louiscieb.towerdefence.controller.GameWorld;
import io.github.louiscieb.towerdefence.model.Enemy;
import io.github.louiscieb.towerdefence.model.GameState;
import io.github.louiscieb.towerdefence.model.Projectile;
import io.github.louiscieb.towerdefence.model.Tower;
import io.github.louiscieb.towerdefence.view.*;

/**
 * Point d’entrée principal du jeu Tower Defence.
 * <p>
 * Cette classe gère :
 * <ul>
 *     <li>L’initialisation du moteur LibGDX</li>
 *     <li>La caméra et le viewport</li>
 *     <li>Le chargement de la carte</li>
 *     <li>La boucle principale du jeu (update + render)</li>
 * </ul>
 * <p>
 * Elle joue le rôle de lien entre le contrôleur
 * ({@link io.github.louiscieb.towerdefence.controller.GameWorld})
 * et les différentes vues (renderers).
 * </p>
 */

public class Main extends ApplicationAdapter {

    // =====================
    // CONSTANTES
    // =====================

    /** Taille d’une tuile en pixels. */
    private static final int TILE_SIZE = 32;

    /** Largeur de la carte (en tuiles). */
    private static final int MAP_WIDTH = 50;

    /** Hauteur de la carte (en tuiles). */
    private static final int MAP_HEIGHT = 50;

    // =====================
    // CORE LIBGDX
    // =====================

    /** SpriteBatch principal pour le rendu. */
    private SpriteBatch batch;

    /** Caméra orthographique du jeu. */
    private OrthographicCamera camera;

    /** Viewport pour gérer le redimensionnement. */
    private Viewport viewport;

    // =====================
    // MAP
    // =====================

    /** Carte Tiled du jeu. */
    private TiledMap map;

    /** Renderer de la carte Tiled. */
    private OrthogonalTiledMapRenderer mapRenderer;

    // =====================
    // CONTROLLER
    // =====================

    /** Monde du jeu (logique principale). */
    private GameWorld world;

    // =====================
    // VIEW / RENDERERS
    // =====================

    private EnemyRenderer enemyRenderer;
    private TowerRenderer towerRenderer;
    private ProjectileRenderer projectileRenderer;
    private HudRenderer hudRenderer;


    // =====================
    // INITIALISATION
    // =====================

    /**
     * Méthode appelée au lancement du jeu.
     * <p>
     * Initialise :
     * <ul>
     *     <li>La caméra et le viewport</li>
     *     <li>La carte et son renderer</li>
     *     <li>Les assets (textures, sons)</li>
     *     <li>Le monde du jeu (MVC)</li>
     *     <li>Les renderers</li>
     * </ul>
     */
    @Override
    public void create() {
        batch = new SpriteBatch();

        // ===== CAMERA =====
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

        // ===== ASSETS & AUDIO =====
        Assets.load();
        AudioManager.getInstance();

        // ===== CONTROLLER =====
        world = new GameWorld(map, viewport);

        // ===== VIEW =====
        enemyRenderer = new EnemyRenderer();
        towerRenderer = new TowerRenderer();
        projectileRenderer = new ProjectileRenderer();
        hudRenderer = new HudRenderer();
    }

    // =====================
    // BOUCLE PRINCIPALE
    // =====================

    /**
     * Boucle principale du jeu.
     * <p>
     * Appelée à chaque frame, elle gère :
     * <ul>
     *     <li>La mise à jour de la logique du jeu</li>
     *     <li>Le rendu de la carte</li>
     *     <li>Le rendu des entités</li>
     *     <li>Le HUD</li>
     *     <li>La gestion du son</li>
     * </ul>
     *
     */
    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        // ===== CLEAR =====
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        // ===== MAP RENDER =====
        mapRenderer.setView(camera);
        mapRenderer.render();

        // ===== UPDATE GAME =====
        world.update(delta);

        // ===== AUDIO =====
        if (world.getState() == GameState.RUNNING) {
            AudioManager.getInstance().playMusic();
        } else {
            AudioManager.getInstance().stopMusic();
        }

        // ===== RENDER GAME =====
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

    // =====================
    // RESIZE
    // =====================

    /**
     * Appelée lors d’un redimensionnement de la fenêtre.
     *
     * @param width  nouvelle largeur
     * @param height nouvelle hauteur
     */
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    // =====================
    // NETTOYAGE
    // =====================

    /**
     * Libère toutes les ressources utilisées par le jeu.
     */
    @Override
    public void dispose() {

        // ===== VIEW =====
        if (enemyRenderer != null) enemyRenderer.dispose();
        if (towerRenderer != null) towerRenderer.dispose();
        if (projectileRenderer != null) projectileRenderer.dispose();
        if (hudRenderer != null) hudRenderer.dispose();

        // ===== CORE =====
        if (mapRenderer != null) mapRenderer.dispose();
        if (map != null) map.dispose();
        if (batch != null) batch.dispose();

        AudioManager.getInstance().dispose();
        Assets.dispose();
    }
}
