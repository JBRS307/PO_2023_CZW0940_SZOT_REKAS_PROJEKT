package agh.po.darwin.model;

import agh.po.darwin.exception.PositionAlreadyOccupiedException;

public class DefaultMap implements WorldMap {

    @Override
    public void place(Animal animal) throws PositionAlreadyOccupiedException {

    }

    @Override
    public Boundary getCurrentBounds() {
        return null;
    }

    @Override
    public void move(Animal animal, MapDirection direction) {

    }

    @Override
    public boolean isOccupied(Vector2d position) {
        return false;
    }

    @Override
    public WorldElement objectAt(Vector2d position) {
        return null;
    }
}
