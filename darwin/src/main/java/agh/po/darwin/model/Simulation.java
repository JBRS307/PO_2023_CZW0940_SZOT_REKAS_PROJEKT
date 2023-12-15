package agh.po.darwin.model;

import java.util.UUID;

public class Simulation {
    public final UUID uuid;
    public final int width;
    public final int height;
    public final int startingGrassAmount;
    public final int grassGrowthPerDay;
    public final int grassEatingEnergy;
    public final int animalsStartAmount;
    public final int animalsStartEnergy;
    public final int fedEnergy;
    public final int breedEnergyCost;
    public final int minMutations;
    public final int maxMutations;
    public final int genomeLength;


    public Simulation(int width, int height, int startingGrassAmount, int grassEatingEnergy, int grassGrowthPerDay, int animalsStartAmount, int animalsStartEnergy, int fedEnergy, int breedEnergyCost, int minMutations, int maxMutations, int genomeLength) {
        this.uuid = UUID.randomUUID();
        this.width = width;
        this.height = height;
        this.startingGrassAmount = startingGrassAmount;
        this.grassEatingEnergy = grassEatingEnergy;
        this.grassGrowthPerDay = grassGrowthPerDay;
        this.animalsStartAmount = animalsStartAmount;
        this.animalsStartEnergy = animalsStartEnergy;
        this.fedEnergy = fedEnergy;
        this.breedEnergyCost = breedEnergyCost;
        this.minMutations = minMutations;
        this.maxMutations = maxMutations;
        this.genomeLength = genomeLength;
        initializeSimulation();
    }

    private void initializeSimulation() {

    }

    public void run() {
    }
}
