package agh.po.darwin.model;

import java.util.*;

public class Animal implements Comparable<Animal> {
    private final UUID uuid;
    private final int genomeLength;
    private final boolean leftRight;
    private Vector2d position;
    private int energy;
    private Genome genome;
    private int lifeTime;
    private int grassEaten;
    private int moveCount = 0;
    private int amountOfChildren;
    private String activeGen;
    private MapDirection direction = MapDirection.NORTH;
    private boolean moved = false;

    private Random randomGenerator = new Random();

    private final List<Animal> children = new ArrayList<>();
    public Animal(Vector2d position, Genome genome, int animalsStartEnergy, boolean leftRight) {
        this.uuid = UUID.randomUUID();
        this.position = position;
        this.genomeLength = genome.getCode().length();
        this.energy = animalsStartEnergy;
        this.lifeTime = 0;
        this.grassEaten = 0;
        this.genome = genome;
        this.leftRight = leftRight;
    }

    public Animal(Vector2d position, int genomeLength, int animalsStartEnergy, boolean leftRight) {
        this(position, new Genome(genomeLength, leftRight), animalsStartEnergy, leftRight);
    }


    /**
     * Auto Generated Getters and Setters
     **/

    public UUID getUuid() {
        return uuid;
    }
    public MapDirection getDirection() {
        return direction;
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
    public int getGenomeLength() {
        return genomeLength;
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

    public void move(AbstractMap map, boolean hell) {
        if(!moved) {
            if (hell) {
                hellMove(map);
            } else {
                defaultMove(map);
            }
            moveCount++;
            moved = true;
        }
    }

    private void hellMove(AbstractMap map) {
        int nextGene = genome.nextInt();
        activeGen = String.valueOf(nextGene);
        int newDirectionIndex = (direction.toInt() + nextGene) % 8;
        this.direction  = MapDirection.fromInt(newDirectionIndex);

        Vector2d newPos = this.position.add(this.direction.toUnitVector());

        //Hellish boundary conditions
        if (isOutOfBoundsHorizontal(newPos, map) ||
            isOutOfBoundsVertical(newPos, map)) {
            int upperXBound = map.getCurrentBounds().rightUpperBoundary().getX();
            int upperYBound = map.getCurrentBounds().rightUpperBoundary().getY();

            newPos = new Vector2d(randomGenerator.nextInt(upperXBound+1),
                                  randomGenerator.nextInt(upperYBound+1));
            energy -= map.getSimulation().breedEnergyCost;
        } else {
            energy -= 1;
        }
        map.moveToNewTile(this, newPos);
        this.position = newPos;
        lifeTime += 1;
    }

    private void defaultMove(AbstractMap map) {
        int nextGene = genome.nextInt();
        activeGen = String.valueOf(nextGene);
        int newDirectionIndex = (direction.toInt() + nextGene) % 8;
        this.direction = MapDirection.fromInt(newDirectionIndex);

        Vector2d newPos = this.position.add(this.direction.toUnitVector());

        // Handling boundary conditions
        if (isOutOfBoundsVertical(newPos, map)) {
            this.direction = reflectVertical(this.direction);
            newPos = adjustVerticalBoundary(newPos, map);
        }
        newPos = adjustHorizontalBoundary(newPos, map);

        // There are no situations where move would be illegal
        // so usage of MapValidator is needless
        map.moveToNewTile(this, newPos);
        this.position = newPos;
        energy -= 1;
        lifeTime+=1;
    }

    private boolean isOutOfBoundsVertical(Vector2d pos, AbstractMap map) {
        int upperYBoundary = map.getCurrentBounds().rightUpperBoundary().getY();
        return pos.getY() < 0 || pos.getY() > upperYBoundary;
    }

    private boolean isOutOfBoundsHorizontal(Vector2d pos, AbstractMap map) {
        int upperX = map.getCurrentBounds().rightUpperBoundary().getX();
        return pos.getX() < 0 || pos.getX() > upperX;
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

    private Vector2d adjustVerticalBoundary(Vector2d pos, AbstractMap map) {
        int upperYBoundary = map.getCurrentBounds().rightUpperBoundary().getY();
        if (pos.getY() > upperYBoundary) {
            return new Vector2d(pos.getX(), upperYBoundary);
        } else if (pos.getY() < 0) {
            return new Vector2d(pos.getY(), 0);
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
            return randomGenerator.nextInt(2) == 0 ? -1 : 1;
        }
    }


    public void breed(Animal other, AbstractMap map) {
        Animal child = new Animal(new Vector2d(this.position.getX(), this.position.getY()), new Genome(this, other, map.getSimulation()), map.getSimulation().breedEnergyCost * 2, this.leftRight);
//        child.setGenome(new Genome(this, other, map.getSimulation()));

        this.amountOfChildren++;
        other.amountOfChildren++;
        this.energy -= map.getSimulation().breedEnergyCost;
        other.energy -= map.getSimulation().breedEnergyCost;
        map.place(child);

        //Update Statistics
        map.getSimulation().animalsCount++;
        map.getSimulation().addGenome(child.genome.getCode());
        map.getSimulation().animals.add(child);

        this.children.add(child);
        other.children.add(child);

    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }
    public int getMoveCount() {
        return this.moveCount;
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

    public void setDirection(MapDirection direction) { //do testów
        this.direction = direction;
    }

    public void setRandomGenerator(Random randomGenerator) {
        this.randomGenerator = randomGenerator;
    }
}
