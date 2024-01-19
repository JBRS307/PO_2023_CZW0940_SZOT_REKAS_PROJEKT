package agh.po.darwin.model;

import agh.po.darwin.exception.PositionAlreadyOccupiedException;

public interface WorldMap {
    void place(Animal animal) throws PositionAlreadyOccupiedException;

    Boundary getCurrentBounds();

    /**
     * Return true if given position on the map is occupied. Should not be
     * confused with canMove since there might be empty positions where the animal
     * cannot move.
     *
     * @param position Position to check.
     * @return True if the position is occupied.
     */
    boolean isOccupied(Vector2d position);

    /**
     * Return an animal at a given position.
     *
     * @param position The position of the animal.
     * @return animal or null if the position is not occupied.
     */
    MapTile at(Vector2d position);
}
