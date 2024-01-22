package agh.po.darwin.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MapTileTest {
    Simulation sampleSimulation = new Simulation(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, false, false, false);

    @Mock
    DefaultMap mapMock;

    @Mock
    Genome genomeMock;

    @Test
    public void eatTest() {
        when(genomeMock.getCode()).thenReturn("00000");
        when(mapMock.getSimulation()).thenReturn(sampleSimulation);

        MapTile tile = new MapTile(new Vector2d(5, 5), mapMock);
        Animal animal = new Animal(new Vector2d(5, 5), genomeMock, 10, false);

        tile.put(animal);
        tile.eat(mapMock);

        assertEquals(10, animal.getEnergy());

        tile.setThereGrass(true);
        tile.eat(mapMock);

        assertEquals(11, animal.getEnergy());

    }

    @Test
    public void moveTest() {
        when(genomeMock.getCode()).thenReturn("00000");
        when(mapMock.getCurrentBounds()).thenReturn(new Boundary(new Vector2d(0, 0), new Vector2d(10, 10)));
        when(mapMock.getSimulation()).thenReturn(sampleSimulation);

        MapTile tile = new MapTile(new Vector2d(5, 5), mapMock);
        Animal animal1 = new Animal(new Vector2d(5, 5), genomeMock, 10, false);
        animal1.setDirection(MapDirection.SOUTH_EAST);

        Animal animal2 = new Animal(new Vector2d(0, 0), genomeMock, 10, false);
        animal2.setDirection(MapDirection.SOUTH_WEST);

        tile.put(animal1);
        tile.put(animal2);
        tile.move(mapMock);

        assertEquals(new Vector2d(6, 4), animal1.getPosition());
        assertEquals(new Vector2d(10, 0), animal2.getPosition());

    }

}