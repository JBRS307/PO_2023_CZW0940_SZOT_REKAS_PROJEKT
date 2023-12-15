package agh.po.darwin.model;

import java.util.UUID;

public class Animal {
    private final UUID uuid;
    private Vector2d position;
    private int energy;
    private String genome;
    private int lifeTime;
    private int amountOfChildren;

    public Animal() {
        this.uuid = UUID.randomUUID();
    }

}
