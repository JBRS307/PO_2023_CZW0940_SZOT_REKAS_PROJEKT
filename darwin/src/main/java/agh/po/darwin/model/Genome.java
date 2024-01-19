package agh.po.darwin.model;

import java.util.Iterator;
import java.util.Random;

public class Genome {
    private String code;
    private int current;

    private final boolean leftRight;
    private short direction;
    // Orientacja genu podczas ruchów lewo-prawo
    // 1 - w prawo, -1 - w lewo
    Random random = new Random();

    public Genome(int length, boolean leftRight) {
        //random genome
        this.code = generateRandomDigits(length);
        this.current = random.nextInt(this.code.length());
        this.leftRight = leftRight;

        if(this.leftRight) {
            boolean directionRandomizer = (random.nextInt(2) != 0);
            if (directionRandomizer) {
                this.direction = 1;
            } else {
                this.direction = -1;
            }
        }
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
            char newGene = ("" + random.nextInt(8)).charAt(0);
            childGenesArray[geneIndexToMutate] = newGene;
        }
        this.code = new String(childGenesArray);
        this.current = random.nextInt(this.code.length());
        this.leftRight = simulation.leftRight;

        if (this.leftRight) {
            boolean directionRandomizer = (random.nextInt(2) != 0);
            if (directionRandomizer) {
                this.direction = 1;
            } else {
                this.direction = -1;
            }
        }
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
        if (!this.leftRight) {
            return nextIntDefault();
        } else {
            return nextIntLeftRight();
        }
    }

    public int nextIntDefault() {
        int index = Integer.parseInt(String.valueOf(code.charAt(current)));
        current += 1;
        current %= this.code.length();
        return index;
    }

    public int nextIntLeftRight() {
        int index = Integer.parseInt(String.valueOf((code.charAt(current))));
        current += direction;
        if (current == this.code.length() || current == -1) {
            direction *= -1;
            current += direction;
        }
        return index;
    }

    private String generateRandomDigits(int length) {
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(8)); // Append a random digit (0-7)
        }
        return sb.toString();
    }
}
