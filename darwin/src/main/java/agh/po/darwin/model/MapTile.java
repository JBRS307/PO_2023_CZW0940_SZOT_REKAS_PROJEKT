package agh.po.darwin.model;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

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
    }

    @Override
    public String toString() {
        if (!animals.isEmpty()) return "animal";
        return isThereGrass ? "grass" : "dirt";
    }

    public synchronized void grow() {
        this.setThereGrass(true);
    }

    public void breed(AbstractMap map) {

        List<Animal> toBreed = animals.stream()
                .filter(animal -> animal.getEnergy() >= map.getSimulation().fedEnergy)
                .sorted(Comparator.reverseOrder())
                .toList();
        for (int i = 1; i < toBreed.size(); i += 2) {
            //TODO spawn more grass on equator
            toBreed.get(i - 1).breed(toBreed.get(i), map);
        }
    }

    public void eat(AbstractMap map) {
        if (isThereGrass) animals.stream().max(Comparator.naturalOrder()).ifPresent(animal -> {
            animal.setEnergy(map.getSimulation().fedEnergy + animal.getEnergy());
            this.setThereGrass(false);
            map.getSimulation().grassCount--;
        });
    }

    public void move(AbstractMap map) {
        var iter = animals.iterator();
        while (iter.hasNext()) {
            var animal = iter.next();
            animal.move(map);
        }
    }

    public void deleteDead(AbstractMap map) {
        var iter = animals.iterator();
        while (iter.hasNext()) {
            var animal = iter.next();
            if (animal.getEnergy() <= 0) {
                remove(animal);
                map.getSimulation().animalsCount--;
            }
        }
    }
}
