package io.github.louiscieb.towerdefence.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Path {

    private final Array<Vector2> points;

    public Path(Array<Vector2> points) {
        this.points = points;
    }

    public Vector2 first() {
        return points.first();
    }

    public Vector2 last() {
        return points.peek();
    }

    public int size() {
        return points.size;
    }

    public Vector2 get(int index) {
        return points.get(index);
    }
}
