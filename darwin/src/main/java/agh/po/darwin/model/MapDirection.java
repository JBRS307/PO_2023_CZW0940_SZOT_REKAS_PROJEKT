package agh.po.darwin.model;

import java.util.HashMap;

public enum MapDirection {
    NORTH,
    NORTH_EAST,
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST;

    private static final HashMap<Vector2d, MapDirection> directionMap = new HashMap<>();

    static {
        directionMap.put(new Vector2d(1, 0), EAST);
        directionMap.put(new Vector2d(-1, 0), WEST);
        directionMap.put(new Vector2d(0, 1), NORTH);
        directionMap.put(new Vector2d(0, -1), SOUTH);
        directionMap.put(new Vector2d(1, 1), NORTH_EAST);
        directionMap.put(new Vector2d(1, -1), SOUTH_EAST);
        directionMap.put(new Vector2d(-1, 1), NORTH_WEST);
        directionMap.put(new Vector2d(-1, -1), SOUTH_WEST);
    }


    @Override
    public String toString() {
        return switch (this) {
            case EAST -> "Wschod";
            case WEST -> "Zachod";
            case NORTH -> "Polnoc";
            case SOUTH -> "Poludnie";
            case NORTH_EAST -> "Polnocny-Wschod";
            case SOUTH_EAST -> "Poludniowy-Wschod";
            case NORTH_WEST -> "Polnocny-Zachod";
            case SOUTH_WEST -> "Poludniowy-Zachod";
        };
    }
    public static MapDirection fromVector(Vector2d vector) {
        return directionMap.get(vector);
    }

    public MapDirection next() {
        return switch (this) {
            case EAST -> SOUTH_EAST;
            case WEST -> NORTH_WEST;
            case NORTH -> NORTH_EAST;
            case SOUTH -> SOUTH_WEST;
            case NORTH_EAST -> EAST;
            case SOUTH_EAST -> SOUTH;
            case NORTH_WEST -> NORTH;
            case SOUTH_WEST -> WEST;
        };
    }

    public MapDirection previous() {
        return switch (this) {
            case EAST -> NORTH_EAST;
            case WEST -> SOUTH_WEST;
            case NORTH -> NORTH_WEST;
            case SOUTH -> SOUTH_EAST;
            case NORTH_EAST -> NORTH;
            case SOUTH_EAST -> EAST;
            case NORTH_WEST -> WEST;
            case SOUTH_WEST -> SOUTH;
        };
    }

    public Vector2d toUnitVector() {
        return switch (this) {
            case EAST -> new Vector2d(1, 0);
            case WEST -> new Vector2d(-1, 0);
            case NORTH -> new Vector2d(0, 1);
            case SOUTH -> new Vector2d(0, -1);
            case NORTH_EAST -> new Vector2d(1, 1);
            case SOUTH_EAST -> new Vector2d(1, -1);
            case NORTH_WEST -> new Vector2d(-1, 1);
            case SOUTH_WEST -> new Vector2d(-1, -1);
        };
    }

//    public int toInt() {
//        return switch (this) {
//            case EAST -> 1;
//            case WEST -> 2;
//            case NORTH -> 3;
//            case SOUTH -> 4;
//            case NORTH_EAST -> 5;
//            case SOUTH_EAST -> 6;
//            case NORTH_WEST -> 7;
//            case SOUTH_WEST -> 8;
//        };
//    }

    public int toInt() {
        return switch (this) {
            case NORTH -> 0;
            case NORTH_EAST -> 1;
            case EAST -> 2;
            case SOUTH_EAST -> 3;
            case SOUTH -> 4;
            case SOUTH_WEST -> 5;
            case WEST -> 6;
            case NORTH_WEST -> 7;
        };
    }

    public static MapDirection fromInt(int i) {
        return switch (i) {
            case 1 -> NORTH_EAST;
            case 5 -> SOUTH_WEST;
            case 7 -> NORTH_WEST;
            case 3 -> SOUTH_EAST;
            case 0 -> NORTH;
            case 2 -> EAST;
            case 6 -> WEST;
            case 4 -> SOUTH;
            default -> throw new IllegalStateException("Unexpected value: " + i);
        };
    }

    public static int toInt(MapDirection direction) {
        return switch (direction) {
            case NORTH_EAST -> 1;
            case SOUTH_WEST -> 5;
            case NORTH_WEST -> 7;
            case SOUTH_EAST -> 3;
            case NORTH -> 0;
            case EAST -> 2;
            case WEST -> 6;
            case SOUTH -> 4;
        };
    }
}