package agh.po.darwin.model;

public enum MapDirection {
    NORTH,
    NORTH_EAST,
    EAST,
    SOUTH_EAST,
    SOUTH,
    SOUTH_WEST,
    WEST,
    NORTH_WEST;

    @Override
    public String toString() {
        return switch (this){
            case EAST -> "Wschód";
            case WEST -> "Zachód";
            case NORTH -> "Północ";
            case SOUTH -> "Południe";
            case NORTH_EAST -> "Północny-Wschód";
            case SOUTH_EAST -> "Południowy-Wschód";
            case NORTH_WEST -> "Północny-Zachód";
            case SOUTH_WEST -> "Południowy-Zachód";
        };
    }

    public MapDirection next(){
        return switch (this){
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

    public MapDirection previous(){
        return switch (this){
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

    public Vector2d toUnitVector (){
        return switch (this) {
            case EAST -> new Vector2d(1,0);
            case WEST -> new Vector2d(-1,0);
            case NORTH -> new Vector2d(0,1);
            case SOUTH -> new Vector2d(0,-1);
            case NORTH_EAST -> new Vector2d(1,1);
            case SOUTH_EAST -> new Vector2d(1,-1);
            case NORTH_WEST -> new Vector2d(-1,1);
            case SOUTH_WEST -> new Vector2d(-1,-1);
        };
    }
}