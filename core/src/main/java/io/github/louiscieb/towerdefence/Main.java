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

public class Main extends ApplicationAdapter {

    private static final int TILE_SIZE = 32;
    private static final int MAP_WIDTH = 50;
    private static final int MAP_HEIGHT = 50;

    SpriteBatch batch;
    OrthographicCamera camera;
    Viewport viewport;

    TiledMap map;
    OrthogonalTiledMapRenderer mapRenderer;

    GameWorld world;

    @Override
    public void create() {
        batch = new SpriteBatch();

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

        map = new TmxMapLoader().load("map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        world = new GameWorld(map, camera, viewport);

    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();

        world.update(delta);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        world.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        batch.dispose();
        map.dispose();
        world.dispose();
    }
}
