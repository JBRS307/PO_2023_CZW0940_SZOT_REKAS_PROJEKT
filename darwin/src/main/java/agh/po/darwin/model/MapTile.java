package agh.po.darwin.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MapTile {
    private final Vector2d position;
    private final ConcurrentLinkedQueue<Animal> animals = new ConcurrentLinkedQueue<>();
    private boolean isThereGrass = false;

    public MapTile(Vector2d position, WorldMap map) {
        this.position = position;
    }

    public Vector2d getPosition() {
        return position;
    }


    public boolean isThereGrass() {
        return isThereGrass;
    }

    public void setThereGrass(boolean thereGrass) {
        isThereGrass = thereGrass;
    }

    public ConcurrentLinkedQueue<Animal> getAnimals() {
        return animals;
    }

    public boolean isOccupied() {
        return !animals.isEmpty();
    }

    public void put(Animal animal) {
        animals.add(animal);
    }

    public void remove(Animal animal) {
        animals.remove(animal);
    }

    public synchronized void update(AbstractMap map) {
        var iter = animals.iterator();
        while (iter.hasNext()) {
            var animal = iter.next();
            animal.update(map);
        }
    }

    @Override
    public String toString() {
        if (!animals.isEmpty()) return "animal";
        return isThereGrass ? "grass" : "dirt";
    }
}
