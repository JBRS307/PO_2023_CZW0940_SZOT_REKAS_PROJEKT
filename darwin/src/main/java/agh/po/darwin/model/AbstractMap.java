package agh.po.darwin.model;


import agh.po.darwin.exception.PositionAlreadyOccupiedException;
import javafx.application.Platform;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractMap implements WorldMap {
    protected Map<Vector2d, Animal> animals = new ConcurrentHashMap<>();
    protected Map<Vector2d, Grass> grassFields = new ConcurrentHashMap<>();
    protected LinkedList<MapChangeListener> subscribers = new LinkedList<>();

    public void registerSubscriber(MapChangeListener mapChangeListener) {
        subscribers.add(mapChangeListener);
    }

    public void unregisterSubscriber(MapChangeListener mapChangeListener) {
        subscribers.remove(mapChangeListener);
    }

    protected void mapChanged(String msg) {
        var map = this;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (var subscriber : subscribers) {
                    subscriber.mapChanged(map, msg);
                }
            }
        });

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


    public synchronized void move(Vector2d position, Vector2d newPos) {
        this.animals.put(newPos, this.animals.get(position));
        this.animals.remove(position);
    }

    public void killAnimal(Vector2d position) {
        this.animals.remove(position);
    }
}
