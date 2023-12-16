package agh.po.darwin.model;

import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class Animal implements WorldElement, Comparable<Animal> {
    private final UUID uuid;
    private final int genomeLength;
    private Vector2d position;
    private int energy;
    private Genome genome;
    private int lifeTime;
    private int amountOfChildren;
    private int animalsStartEnergy;
    private int age;
    private MapDirection direction = MapDirection.NORTH;

    public Animal(Vector2d position, int genomeLength, int animalsStartEnergy) {
        this.uuid = UUID.randomUUID();
        this.position = position;
        this.genomeLength = genomeLength;
        this.energy = animalsStartEnergy;
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

    public void setAmountOfChildren(int amountOfChildren) {
        this.amountOfChildren = amountOfChildren;
    }


    public void setAge(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public synchronized void move(AbstractMap map) {
        int nextGene = genome.nextInt();
        int newDirectionIndex = (direction.toInt() + nextGene) % 8;
        this.direction = MapDirection.fromInt(newDirectionIndex);
        var newPos = this.position.add(this.direction.toUnitVector());
        if (map.canMoveTo(newPos)) {
            map.move(this, newPos);
            this.position = newPos;
        }
        energy -= 1;
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
        } else if (this.age != other.age) {
            return Integer.compare(other.age, this.age);
        } else if (this.amountOfChildren != other.amountOfChildren) {
            return Integer.compare(other.amountOfChildren, this.amountOfChildren);
        } else {
            // Random selection among animals
            return new Random().nextInt(2) == 0 ? -1 : 1;
        }
    }


    public void breed(Animal other, AbstractMap map) {
        Animal child = new Animal(new Vector2d(this.position.getX(), this.position.getY()), genomeLength, map.getSimulation().breedEnergyCost*2);
        child.setGenome(new Genome(this, other, map.getSimulation()));

        this.amountOfChildren++;
        other.amountOfChildren++;
        this.energy -= map.getSimulation().breedEnergyCost;
        other.energy -= map.getSimulation().breedEnergyCost;
        map.place(child);

    }
}
