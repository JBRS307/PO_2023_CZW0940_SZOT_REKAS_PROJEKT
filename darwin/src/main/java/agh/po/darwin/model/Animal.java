package agh.po.darwin.model;

import java.util.*;

public class Animal implements Comparable<Animal> {
    private final UUID uuid;
    private final int genomeLength;
    private Vector2d position;
    private int energy;
    private Genome genome;
    private int lifeTime;
    private int grassEaten;
    private int amountOfChildren;
    private String activeGen;
    private MapDirection direction = MapDirection.NORTH;

    private final List<Animal> children = new ArrayList<>();

    public Animal(Vector2d position, int genomeLength, int animalsStartEnergy) {
        this.uuid = UUID.randomUUID();
        this.position = position;
        this.genomeLength = genomeLength;
        this.energy = animalsStartEnergy;
        this.lifeTime = 0;
        this.grassEaten = 0;
        genome = new Genome(genomeLength);
    }

    /**
     * Auto Generated Getters and Setters
     **/

    public UUID getUuid() {
        return uuid;
    }

    public Vector2d getPosition() {
        return position;
    }

    public void setPosition(Vector2d position) {
        this.position = position;
    }

    public int getEnergy() {
        return energy;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public Genome getGenome() {
        return genome;
    }

    public void setGenome(Genome genome) {
        this.genome = genome;
    }

    public int getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(int lifeTime) {
        this.lifeTime = lifeTime;
    }

    public int getAmountOfChildren() {
        return amountOfChildren;
    }
    public int getAmountOfDecadence(){
        Queue<Animal> toVisit = new ArrayDeque<>();
        Map<Animal, Boolean> visited = new HashMap<>();
        int decadence = 0;
        children.forEach(child->{
            toVisit.add(child);
        });
        while (!toVisit.isEmpty()){
            var animal = toVisit.poll();
            if(visited.containsKey(animal)){
                continue;
            }
            animal.children.forEach(child->{
                toVisit.add(child);
            });
            visited.put(animal,true);
            decadence++;
        }
        return decadence;
    }

    public void setAmountOfChildren(int amountOfChildren) {
        this.amountOfChildren = amountOfChildren;
    }


    public void move(AbstractMap map) {
        int nextGene = genome.nextInt();
        activeGen = String.valueOf(nextGene);
        int newDirectionIndex = (direction.toInt() + nextGene) % 8;
        this.direction = MapDirection.fromInt(newDirectionIndex);

        Vector2d newPos = this.position.add(this.direction.toUnitVector());

        // Handling boundary conditions
        if (isOutOfBoundsVertical(newPos, map)) {
            this.direction = reflectVertical(this.direction);
            newPos = this.position.add(this.direction.toUnitVector());
        }
        newPos = adjustHorizontalBoundary(newPos, map);

        // Move if the new position is valid
        if (map.canMoveTo(newPos)) {
            map.move(this, newPos);
            this.position = newPos;
        }
        energy -= 1;
        lifeTime+=1;
    }

    private boolean isOutOfBoundsVertical(Vector2d pos, AbstractMap map) {
        int upperYBoundary = map.getCurrentBounds().rightUpperBoundary().getY();
        return pos.getY() < 0 || pos.getY() > upperYBoundary;
    }

    private MapDirection reflectVertical(MapDirection direction) {
        return MapDirection.fromVector(new Vector2d(direction.toUnitVector().getX(), -1 * direction.toUnitVector().getY()));
    }

    private Vector2d adjustHorizontalBoundary(Vector2d pos, AbstractMap map) {
        int upperXBoundary = map.getCurrentBounds().rightUpperBoundary().getX();
        if (pos.getX() < 0) {
            return new Vector2d(upperXBoundary, pos.getY());
        } else if (pos.getX() > upperXBoundary) {
            return new Vector2d(0, pos.getY());
        }
        return pos;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Animal animal = (Animal) o;
        return energy == animal.energy && lifeTime == animal.lifeTime && amountOfChildren == animal.amountOfChildren && Objects.equals(uuid, animal.uuid) && Objects.equals(position, animal.position) && Objects.equals(genome, animal.genome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, position, energy, genome, lifeTime, amountOfChildren);
    }


    @Override
    public int compareTo(Animal other) {
        if (this.energy != other.energy) {
            return Integer.compare(other.energy, this.energy);
        } else if (this.lifeTime != other.lifeTime) {
            return Integer.compare(other.lifeTime, this.lifeTime);
        } else if (this.amountOfChildren != other.amountOfChildren) {
            return Integer.compare(other.amountOfChildren, this.amountOfChildren);
        } else {
            // Random selection among animals
            return new Random().nextInt(2) == 0 ? -1 : 1;
        }
    }


    public void breed(Animal other, AbstractMap map) {
        Animal child = new Animal(new Vector2d(this.position.getX(), this.position.getY()), genomeLength, map.getSimulation().breedEnergyCost * 2);
        child.setGenome(new Genome(this, other, map.getSimulation()));

        this.amountOfChildren++;
        other.amountOfChildren++;
        this.energy -= map.getSimulation().breedEnergyCost;
        other.energy -= map.getSimulation().breedEnergyCost;
        map.place(child);

        //Update Statistics
        map.getSimulation().animalsCount++;
        map.getSimulation().addGenome(child.genome.getCode());
        map.getSimulation().animals.add(child);

        children.add(child);
        other.children.add(child);

    }

    public int getGrassEaten() {
        return grassEaten;
    }

    public void setGrassEaten(int grassEaten) {
        this.grassEaten = grassEaten;
    }

    public String getActiveGen() {
        return activeGen;
    }
}
