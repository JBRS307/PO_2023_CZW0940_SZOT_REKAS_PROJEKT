package agh.po.darwin.model;


import javafx.application.Platform;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractMap implements WorldMap {
    protected Map<Vector2d, MapTile> tiles = new ConcurrentHashMap<>();
    protected LinkedList<MapChangeListener> subscribers = new LinkedList<>();

    protected AbstractMap(Simulation simulation) {
        this.simulation = simulation;
    }

    public void registerSubscriber(MapChangeListener mapChangeListener) {
        subscribers.add(mapChangeListener);
    }

    public void unregisterSubscriber(MapChangeListener mapChangeListener) {
        subscribers.remove(mapChangeListener);
    }

    private final Simulation simulation;

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
        return tiles.get(position).isOccupied();
    }

    public MapTile at(Vector2d position) {
        return (tiles.get(position));
    }


    public abstract void update();

    public void place(Animal animal) {
        this.tiles.get(animal.getPosition()).put(animal);
    }


    public synchronized void move(Animal animal, Vector2d newPos) {
        tiles.get(newPos).put(animal);
        tiles.get(animal.getPosition()).remove(animal);
    }

    public void killAnimal(Animal animal) {
        this.tiles.get(animal.getPosition()).remove(animal);
    }

    public Simulation getSimulation() {
        return simulation;
    }
}
