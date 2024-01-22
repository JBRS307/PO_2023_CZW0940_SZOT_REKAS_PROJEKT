package agh.po.darwin.model;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static java.lang.Math.abs;

public class MapTile {
    private final Vector2d position;
    private final ConcurrentLinkedQueue<Animal> animals = new ConcurrentLinkedQueue<>();
    private boolean isThereGrass = false;
    private int growCount = 0;

    private boolean grassPreferred = false;

    public MapTile(Vector2d position, WorldMap map) {
        this.position = position;
    }

    public Vector2d getPosition() {
        return position;
    }

    public int getGrowCount() {
        return growCount;
    }
    public boolean getGrassPreferred() {
        return grassPreferred;
    }
    public void setGrassPreferred(boolean grassPreferred) {
        this.grassPreferred = grassPreferred;
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

    @Override
    public String toString() {
        if (!animals.isEmpty()) return "animal";
        return isThereGrass ? "grass" : "dirt";
    }

    public synchronized void grow() {
        this.setThereGrass(true);
        this.growCount++;
    }

    public void breed(AbstractMap map) {

        List<Animal> toBreed = animals.stream()
                .filter(animal -> animal.getEnergy() >= map.getSimulation().fedEnergy)
                .sorted(Comparator.reverseOrder())
                .toList();
        for (int i = 1; i < toBreed.size(); i += 2) {
            toBreed.get(i - 1).breed(toBreed.get(i), map);
        }
    }

    public void eat(AbstractMap map) {
        if (isThereGrass) animals.stream().max(Comparator.naturalOrder()).ifPresent(animal -> {
            animal.setEnergy(map.getSimulation().grassEatingEnergy + animal.getEnergy());
            animal.setGrassEaten(animal.getGrassEaten()+1);
            this.setThereGrass(false);
            map.getSimulation().grassCount--;
        });
    }

    public void move(AbstractMap map) {
        var iter = animals.iterator();
        while (iter.hasNext()) {
            var animal = iter.next();
            animal.move(map, map.getSimulation().hell);
        }
    }

//    public void deleteDead(AbstractMap map) {
//        var iter = animals.iterator();
//        while (iter.hasNext()) {
//            var animal = iter.next();
//            if (animal.getEnergy() <= 0) {
//                remove(animal);
//                map.getSimulation().animalsCount--;
//            }
//        }
//    }

    public void deleteDead(AbstractMap map) {
        var iter = animals.iterator();
        while (iter.hasNext()) {
            var animal = iter.next();
            if (animal.getEnergy() <= 0) {
                remove(animal);
                map.getSimulation().removeGenome(animal.getGenome().getCode());
                map.getSimulation().animalsCount--;
            }
        }
    }

    public void onClick(Simulation simulation) {
        var animal = animals.peek();
        if(animal != null){
            simulation.setTrackedAnimal(animal);
        }
        simulation.worldMap.mapChanged("update");
    }
}
