package agh.po.darwin.model;


import agh.po.darwin.exception.PositionAlreadyOccupiedException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class AbstractMap implements WorldMap {
    protected Map<Vector2d, Animal> animals = new HashMap<>();
    protected Map<Vector2d, Grass> grassFields = new HashMap<>();
    protected LinkedList<MapChangeListener> subscribers = new LinkedList<>();

    public void registerSubscriber(MapChangeListener mapChangeListener) {
        subscribers.add(mapChangeListener);
    }

    public void unregisterSubscriber(MapChangeListener mapChangeListener) {
        subscribers.remove(mapChangeListener);
    }

    protected void mapChanged(String msg) {
        for (var subscriber : subscribers) {
            subscriber.mapChanged(this, msg);
        }
    }

    public boolean isOccupied(Vector2d position) {
        return animals.get(position) != null;
    }

    public WorldElement objectAt(Vector2d position) {
        return (WorldElement) (animals.get(position) != null ? animals.get(position) : grassFields.get(position));
    }


    public abstract void update();

    public void place(Animal animal) {
        this.animals.put(animal.getPosition(), animal);
    }


}
