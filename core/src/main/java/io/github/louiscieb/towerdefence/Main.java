package io.github.louiscieb.towerdefence;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class Main extends ApplicationAdapter {

    SpriteBatch batch;
    OrthographicCamera camera;

    TiledMap map;
    OrthogonalTiledMapRenderer mapRenderer;

    GameWorld world;

    @Override
    public void create() {
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        map = new TmxMapLoader().load("map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);

        world = new GameWorld(map, camera);
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();

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
    public void dispose() {
        batch.dispose();
        map.dispose();
        world.dispose();
    }
}
