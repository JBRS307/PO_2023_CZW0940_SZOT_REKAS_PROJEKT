package agh.po.darwin.model;

public class DefaultMap extends AbstractMap {

    final int width;
    final int height;

    public DefaultMap(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void update() {

    }

    @Override
    public Boundary getCurrentBounds() {
        return new Boundary(new Vector2d(0, 0), new Vector2d(width - 1, height - 1));
    }


}
