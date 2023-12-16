package agh.po.darwin.model;

import java.util.Iterator;
import java.util.Map;

public class DefaultMap extends AbstractMap {

    final int width;
    final int height;

    public DefaultMap(int width, int height) {
        this.width = width;
        this.height = height;
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                var pos = new Vector2d(row, col);
                tiles.put(pos, new MapTile(pos, this));
            }
        }
    }
//    Symulacja każdego dnia składa się z poniższej sekwencji kroków:
//
//    1. Usunięcie martwych zwierzaków z mapy.
//    2. Skręt i przemieszczenie każdego zwierzaka.
//    3. Konsumpcja roślin, na których pola weszły zwierzaki.
//    4. Rozmnażanie się najedzonych zwierzaków znajdujących się na tym samym polu.
//    5. Wzrastanie nowych roślin na wybranych polach mapy.

    @Override
    public void update() {
        Iterator<Map.Entry<Vector2d, MapTile>> it = tiles.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Vector2d, MapTile> entry = it.next();
            var tile = entry.getValue();
            tile.update(this);
        }

        mapChanged("next day");
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
