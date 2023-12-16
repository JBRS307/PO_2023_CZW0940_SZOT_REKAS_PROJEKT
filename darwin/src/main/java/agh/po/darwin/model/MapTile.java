package agh.po.darwin.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import static java.lang.Math.abs;

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

    public synchronized void update(DefaultMap map) {
        var equator = map.height / 2;
        var random = new Random();
        if (abs(equator - this.position.getY()) <= 2) {
            //5% chance for growth
            if (random.nextFloat() <= 0.05f) {
                this.setThereGrass(true);
            }
        } else {
            if (random.nextFloat() <= 0.005f) { //.5% chance for growth
                this.setThereGrass(true);
            }
        }
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
