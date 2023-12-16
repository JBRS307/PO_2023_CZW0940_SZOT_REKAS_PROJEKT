package agh.po.darwin.model;

public class DefaultMap extends AbstractMap {

    final int width;
    final int height;

    public DefaultMap(int width, int height) {
        this.width = width;
        this.height = height;

        //create grid
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                var pos = new Vector2d(row, col);
                tiles.put(pos, new MapTile(pos, this));
            }
        }
    }

//    The simulation of each day consists of the following sequence of steps:

//    1. Removal of dead animals from the map.
//    2. Turning and movement of each animal.
//    3. Consumption of plants by animals occupying their fields.
//    4. Reproduction of well-fed animals located in the same field.
//    5. Growth of new plants in selected map fields.


    @Override
    public void update() {
        performActionOnAllTiles(this::deleteDead);
        performActionOnAllTiles(this::move);
        performActionOnAllTiles(this::eat);
        performActionOnAllTiles(this::breed);
        performActionOnAllTiles(this::grow);
        mapChanged("next day");
    }

    private void grow(MapTile mapTile, AbstractMap map) {
        mapTile.grow(map);
    }

    private void breed(MapTile mapTile, AbstractMap map) {
        mapTile.breed(map);
    }

    private void eat(MapTile mapTile, AbstractMap map) {
        mapTile.eat(map);
    }

    private void move(MapTile mapTile, AbstractMap map) {
        mapTile.update((DefaultMap) map);
    }

    private void deleteDead(MapTile mapTile, AbstractMap map) {
        mapTile.deleteDead(map);
    }

    private void performActionOnAllTiles(TileAction action) {
        var iter = tiles.entrySet().iterator();
        while (iter.hasNext()) {
            var tile = iter.next().getValue();
            action.perform(tile, this);
        }
    }

    private interface TileAction {
        void perform(MapTile tile, AbstractMap abstractMap);
    }

    @Override
    public Boundary getCurrentBounds() {
        return new Boundary(new Vector2d(0, 0), new Vector2d(width - 1, height - 1));
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return position.getX() >= getCurrentBounds().leftDownBoundary().getX() &&
                position.getX() <= getCurrentBounds().rightUpperBoundary().getX() &&
                position.getY() >= getCurrentBounds().leftDownBoundary().getY() &&
                position.getY() <= getCurrentBounds().rightUpperBoundary().getY();

    }


}
