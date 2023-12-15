package agh.po.darwin.model;

import java.util.Random;
import java.util.UUID;

public class Simulation {
    public final UUID uuid;
    public final int width;
    public final int height;
    public final int startingGrassAmount;
    public final int grassGrowthPerDay;
    public final int grassEatingEnergy;
    public final int startingAnimalsAmount;
    public final int animalsStartEnergy;
    public final int fedEnergy;
    public final int breedEnergyCost;
    public final int minMutations;
    public final int maxMutations;
    public final int genomeLength;

    protected long day = 0L;
    protected float speed = 1;

    protected boolean shouldClose = false;
    protected AbstractMap worldMap;


    public Simulation(int width, int height, int startingGrassAmount, int grassEatingEnergy, int grassGrowthPerDay, int startingAnimalsAmount, int animalsStartEnergy, int fedEnergy, int breedEnergyCost, int minMutations, int maxMutations, int genomeLength) {
        this.uuid = UUID.randomUUID();
        this.width = width;
        this.height = height;
        this.startingGrassAmount = startingGrassAmount;
        this.grassEatingEnergy = grassEatingEnergy;
        this.grassGrowthPerDay = grassGrowthPerDay;
        this.startingAnimalsAmount = startingAnimalsAmount;
        this.animalsStartEnergy = animalsStartEnergy;
        this.fedEnergy = fedEnergy;
        this.breedEnergyCost = breedEnergyCost;
        this.minMutations = minMutations;
        this.maxMutations = maxMutations;
        this.genomeLength = genomeLength;
        initializeSimulation();

    }

    private void initializeSimulation() {
        worldMap = new DefaultMap(width, height);
        var random = new Random();

        //first lets spawn grass
        for (int i = 0; i < startingGrassAmount; i++) {

            var randomPos = new Vector2d(random.nextInt(0, width), random.nextInt(0, height));
            if (worldMap.grassFields.get(randomPos) != null) {
                i--;
                continue;
            }
            worldMap.grassFields.put(randomPos, new Grass());

        }
        //then spawn animals
        for (int i = 0; i < startingAnimalsAmount; i++) {
            var randomPos = new Vector2d(random.nextInt(0, width), random.nextInt(0, height));
            if (worldMap.isOccupied(randomPos)) {
                i--;
                continue;
            }
            worldMap.place(new Animal(randomPos, genomeLength));
        }
    }

    public void run() {
        while (!shouldClose) {
            update();
            //base speed is to update once per second
            try {
                Thread.sleep((long) (speed * 1000));
            } catch (InterruptedException e) {
                //when InterruptedException we cannot do anything ;(, i will just pass it upper then.
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized void update() {
        worldMap.update();
        day += 1;
    }

    public void stop() {
        shouldClose = true;
    }

    public AbstractMap getWorldMap() {
        return worldMap;
    }

}
