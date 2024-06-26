package agh.po.darwin.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
    public final boolean hell;
    public final boolean leftRight;
    public final boolean csvExport;


    //Statistics
    protected int animalsCount = 0;
    protected int grassCount = 0;
    protected final Map<String, Integer> genomeCount = new HashMap<>();
    protected long day = 0L;


    protected final List<Animal> animals = new LinkedList<Animal>();
    protected float speed = 2;
    protected Animal trackedAnimal;


    protected boolean shouldClose = false;
    protected AbstractMap worldMap;
    private boolean pause;


    public Simulation(int width, int height, int startingGrassAmount, int grassEatingEnergy, int grassGrowthPerDay,
                      int startingAnimalsAmount, int animalsStartEnergy, int fedEnergy, int breedEnergyCost, int minMutations,
                      int maxMutations, int genomeLength, boolean hell, boolean leftRight, boolean csvExport) {
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
        this.hell = hell;
        this.leftRight = leftRight;
        this.csvExport = csvExport;
        initializeSimulation();

    }

    public int getAnimalsCount() {
        return animalsCount;
    }

    public int getGrassCount() {
        return grassCount;
    }

    private void initializeSimulation() {
        worldMap = new DefaultMap(width, height, this);
        var random = new Random();

        //first lets spawn grass
        for (int i = 0; i < startingGrassAmount; i++) {
            var randomPos = new Vector2d(random.nextInt(width), random.nextInt(height));
            if (worldMap.tiles.get(randomPos).isThereGrass()) {
                i--;
                continue;
            }
            worldMap.tiles.get(randomPos).setThereGrass(true);
            grassCount++;
        }
        //then spawn animals
        for (int i = 0; i < startingAnimalsAmount; i++) {
            var randomPos = new Vector2d(random.nextInt(width), random.nextInt(height));
            if (worldMap.isOccupied(randomPos)) {
                i--;
                continue;
            }
            var animal = new Animal(randomPos, genomeLength, animalsStartEnergy, leftRight);
            worldMap.place(animal);
            addGenome(animal.getGenome().getCode());
            animals.add(animal);
            animalsCount++;
        }
    }

    public void run() {
        while (!shouldClose) {
            if (!pause) update();
            //base speed is to update once per second
            try {
                Thread.sleep((long) ((1 / speed) * 1000));
            } catch (InterruptedException e) {
                //when InterruptedException we cannot do anything ;(, i will just pass it upper then.
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized void update() {
        if(csvExport) writeToCsv();
        worldMap.update();
        day += 1;
    }

    public void stop() {
        shouldClose = true;
    }

    public AbstractMap getWorldMap() {
        return worldMap;
    }

    public void setGrassPreferredTiles() {
        List<MapTile> tiles = new ArrayList<>(this.worldMap.getTiles().values());
        int tilesLength = tiles.size();

        tiles.sort((t1, t2) -> t2.getGrowCount() - t1.getGrowCount());
        int preferredTiles = (int) (tilesLength * 0.2);

        for (int i = 0; i < tilesLength; i++) {
            if (i <= preferredTiles) {
                tiles.get(i).setGrassPreferred(true);
            } else {
                tiles.get(i).setGrassPreferred(false);
            }
        }
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setPause(boolean b) {
        this.pause = b;
    }

    public boolean getPause() {
        return this.pause;
    }

    public long getDay() {
        return day;
    }

    public Map<String, Integer> getGenomeCount() {
        return genomeCount;
    }

    public void addGenome(String genome) {
        genomeCount.put(genome, genomeCount.getOrDefault(genome, 0)+1);
    }

    public void removeGenome(String genome) {
        genomeCount.put(genome, genomeCount.getOrDefault(genome, 1)-1);
    }

    public Animal getTrackedAnimal() {
        return trackedAnimal;
    }

    public void setTrackedAnimal(Animal trackedAnimal) {
        this.trackedAnimal = trackedAnimal;
    }

    public synchronized double getAverageLifeSpan() {
        return  animals.stream().filter(animal->animal.getEnergy()<=0).mapToInt(animal->animal.getLifeTime()).average().orElse(0);
    }

    public synchronized double getAverageEnergy() {
        return  animals.stream().filter(animal->animal.getEnergy()>0).mapToInt(animal->animal.getEnergy()).average().orElse(0);
    }

    public synchronized double getAverageCountOfChildren() {
        return animals.stream().filter(animal->animal.getEnergy()>0).mapToInt(animal->animal.getAmountOfChildren()).average().orElse(0);
    }

    public void setTopGenomes() {
        int topCounter = Collections.max(genomeCount.values());

        List<String> topGenomes = genomeCount.entrySet().stream()
                .filter(elem -> elem.getValue() == topCounter)
                .map(Map.Entry::getKey)
                .toList();

        var iter = animals.iterator();
        while(iter.hasNext()) {
            var animal = iter.next();
            if (topGenomes.contains(animal.getGenome().getCode())) {
                animal.getGenome().setDominantGenome(true);
            } else {
                animal.getGenome().setDominantGenome(false);
            }
        }
    }

    private void writeToCsv() {
        try {
            Files.createDirectories(Paths.get("./csv"));
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
        File fp = new File("./csv/" + uuid + ".csv");
        StringBuilder line = new StringBuilder();

        try {
            if(!fp.exists()) fp.createNewFile();
            FileWriter fpWriter = new FileWriter(fp, true);
            BufferedWriter bpWriter = new BufferedWriter(fpWriter);
            if(day == 0) {
                line.append("dzień, liczba zwierzaków, liczba roślin, średnia energia, średnia długość życia, średnia liczba dzieci\n");
            }

            line.append(day).append(", ")
                .append(animalsCount).append(", ")
                .append(grassCount).append(", ")
                .append(getAverageEnergy()).append(", ")
                .append(getAverageLifeSpan()).append(", ")
                .append(getAverageCountOfChildren()).append("\n");

            bpWriter.write(line.toString());
            bpWriter.close();
            fpWriter.close();
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }
}
