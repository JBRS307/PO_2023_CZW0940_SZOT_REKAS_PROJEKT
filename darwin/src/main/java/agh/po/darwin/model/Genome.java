package agh.po.darwin.model;

import java.util.Iterator;
import java.util.Random;

public class Genome {
    private String code;
    private int current;
    Random random = new Random();

    public Genome(int length) {
        //random genome
        this.code = generateRandomDigits(length);
        this.current = random.nextInt(0, 8);
    }

    public Genome(Animal dad, Animal mom, Simulation simulation) {
        var genesToMutate = random.nextInt(simulation.minMutations, simulation.maxMutations + 1);
        var dadPercentage = 100 * ((float) dad.getEnergy() / (dad.getEnergy() + mom.getEnergy()));
        var momPercentage = 100 * ((float) mom.getEnergy() / (dad.getEnergy() + mom.getEnergy()));

        var dadGenes = dad.getGenome().code;
        var momGenes = mom.getGenome().code;

        boolean startTranscriptionFromLeft = random.nextBoolean();
        int crossoverPoint = (int) (dadPercentage * simulation.genomeLength / 100);
        String childGenes;
        if (startTranscriptionFromLeft) {
            String leftGenes = dadGenes.substring(0, crossoverPoint);
            String rightGenes = momGenes.substring(crossoverPoint);
            childGenes = leftGenes + rightGenes;
        } else {
            String leftGenes = momGenes.substring(0, crossoverPoint);
            String rightGenes = dadGenes.substring(crossoverPoint);
            childGenes = leftGenes + rightGenes;
        }
        char[] childGenesArray = childGenes.toCharArray();
        for (int i = 0; i < genesToMutate; i++) {
            int geneIndexToMutate = random.nextInt(childGenesArray.length);
            char newGene = ("" + random.nextInt(0, 8)).charAt(0);
            childGenesArray[geneIndexToMutate] = newGene;
        }
        this.code = new String(childGenesArray);
        this.current = random.nextInt(0, 8);
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


//    public boolean hasNext() {
//        return true;
//    }

//    public MapDirection nextDirection() {
//        int index = Integer.parseInt(String.valueOf(code.charAt((current) % code.length())));
//        current += 1;
//        return MapDirection.fromInt(index);
//    }

    public int nextInt() {
        int index = Integer.parseInt(String.valueOf(code.charAt((current) % code.length())));
        current += 1;
        return index;
    }

    private String generateRandomDigits(int length) {
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(0, 8)); // Append a random digit (0-7)
        }
        return sb.toString();
    }
}
