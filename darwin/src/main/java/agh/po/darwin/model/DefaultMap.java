package agh.po.darwin.model;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.abs;
import static java.lang.Math.random;

public class DefaultMap extends AbstractMap {

    final int width;
    final int height;

    public DefaultMap(int width, int height, Simulation simulation) {
        super(simulation);
        this.width = width;
        this.height = height;

        //create grid
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                var pos = new Vector2d(row, col);
                tiles.put(pos, new MapTile(pos, this));
            }
        }
        System.out.println(width*height + " " + tiles.size());
    }

//    The simulation of each day consists of the following sequence of steps:

//    1. Removal of dead animals from the map.
//    2. Turning and movement of each animal.
//    3. Consumption of plants by animals occupying their fields.
//    4. Reproduction of well-fed animals located in the same field.
//    5. Growth of new plants in selected map fields.


    @Override
    public void update() {
        performActionOnAllTiles(this::prepareToMove);
        performActionOnAllTiles(this::deleteDead);
        performActionOnAllTiles(this::move);
        performActionOnAllTiles(this::eat);
        performActionOnAllTiles(this::breed);
        growGrass();
        mapChanged("next day");
    }

    private void growGrass() {
        var equatorLineY = getSimulation().height / 2;
        var random = new Random();
        List<MapTile> growable = new ArrayList<>(tiles.values().stream().filter(tile -> !tile.isThereGrass()).toList());

        List<MapTile> growableOnEquator = new ArrayList<>(growable.stream().filter(tile -> abs(tile.getPosition().getY() - equatorLineY) <= 2).toList());
        List<MapTile> growableNotOnEquator = new ArrayList<>(growable.stream().filter(tile -> abs(tile.getPosition().getY() - equatorLineY) > 2).toList());

        Collections.shuffle(growableOnEquator);
        Collections.shuffle(growableNotOnEquator);
        Collections.shuffle(growable);

        int growOnEquaoriterator = 0;
        int growNotOnEquatorIterator = 0;
        int growIterator = 0;

        for (int i = 0; i < getSimulation().grassGrowthPerDay; i++) {
            try {
                if (!growableOnEquator.isEmpty() && !growableNotOnEquator.isEmpty()) {
                    if (random.nextFloat() <= .8f) {// 80-20 rule
                        growableOnEquator.get(growOnEquaoriterator).grow();
                        growOnEquaoriterator++;
                    } else {
                        growableNotOnEquator.get(growNotOnEquatorIterator).grow();
                        growNotOnEquatorIterator++;
                    }
                } else {
                    growable.get(growIterator).grow();
                    growIterator++;
                }
                getSimulation().grassCount++;
            } catch (Exception e) {
                //xD
                break;
            }
        }
    }

    private void breed(MapTile mapTile, AbstractMap map) {
        mapTile.breed(map);
    }

    private void eat(MapTile mapTile, AbstractMap map) {
        mapTile.eat(map);
    }

    private void move(MapTile mapTile, AbstractMap map) {
        mapTile.move(map);
    }

    private void deleteDead(MapTile mapTile, AbstractMap map) {
        mapTile.deleteDead(map);
    }

    private void prepareToMove(MapTile mapTile, AbstractMap map) {
        mapTile.prepareToMove(map);
    }


    private void performActionOnAllTiles(TileAction action) {
        tiles.forEach((pos, tile) -> {
            action.perform(tile, this);
        });
    }

    private interface TileAction {
        void perform(MapTile tile, AbstractMap abstractMap);
    }

    @Override
    public Boundary getCurrentBounds() {
        return new Boundary(new Vector2d(0, 0), new Vector2d(width - 1, height - 1));
    }
}
