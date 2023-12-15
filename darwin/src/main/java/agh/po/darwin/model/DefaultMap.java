package agh.po.darwin.model;

import java.util.Iterator;
import java.util.Map;

public class DefaultMap extends AbstractMap {

    final int width;
    final int height;

    public DefaultMap(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void update() {
        Iterator<Map.Entry<Vector2d, Animal>> it = animals.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Vector2d, Animal> entry = it.next();
            var animal = entry.getValue();
            animal.update(this);
        }

        mapChanged("");
    }

    @Override
    public Boundary getCurrentBounds() {
        return new Boundary(new Vector2d(0, 0), new Vector2d(width - 1, height-1));
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return position.getX() >= getCurrentBounds().leftDownBoundary().getX() &&
                position.getX() <= getCurrentBounds().rightUpperBoundary().getX() &&
                position.getY() >= getCurrentBounds().leftDownBoundary().getY() &&
                position.getY() <= getCurrentBounds().rightUpperBoundary().getY();

    }


}
