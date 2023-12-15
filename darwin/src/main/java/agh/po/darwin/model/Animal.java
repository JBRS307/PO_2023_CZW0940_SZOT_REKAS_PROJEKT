package agh.po.darwin.model;

import java.util.Objects;
import java.util.UUID;

public class Animal implements WorldElement {
    private final UUID uuid;
    private final int genomeLength;
    private Vector2d position;
    private int energy;
    private Genome genome;
    private int lifeTime;
    private int amountOfChildren;

    private MapDirection direction = MapDirection.NORTH;

    public Animal(Vector2d position, int genomeLength) {
        this.uuid = UUID.randomUUID();
        this.position = position;
        this.genomeLength = genomeLength;
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

    public void update(AbstractMap map) {
        //new day
        int nextGene = genome.nextInt();
        int newDirectionIndex = (direction.toInt() + nextGene) % 8;
        this.direction = MapDirection.fromInt(newDirectionIndex);
        var newPos = this.position.add(this.direction.toUnitVector());

        if (map.canMoveTo(newPos)) {
            map.move(this.position, newPos);
            this.position = newPos;
        }

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
}
