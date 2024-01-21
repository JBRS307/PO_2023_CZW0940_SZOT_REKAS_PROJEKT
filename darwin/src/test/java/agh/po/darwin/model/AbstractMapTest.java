package agh.po.darwin.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AbstractMapTest {
    DefaultMap worldMap;

    @Mock
    Simulation simulationMock;
    @Mock
    Genome genomeMock;
    @BeforeEach
    public void createMap() {
        worldMap = new DefaultMap(10, 10, simulationMock);
    }

    @Test
    public void placeAndAtTest() {
        when(genomeMock.getCode()).thenReturn("00000");

        Animal animal1 = new Animal(new Vector2d(5, 5), genomeMock, 10, false);
        Animal animal2 = new Animal(new Vector2d(3, 3), genomeMock, 10, false);

        worldMap.place(animal1);
        worldMap.place(animal2);

        assertEquals("animal", worldMap.at(new Vector2d(5, 5)).toString());
        assertEquals("animal", worldMap.at(new Vector2d(3, 3)).toString());
        assertEquals("dirt", worldMap.at(new Vector2d(7, 7)).toString());
    }

    @Test
    public void moveToNewTileTest() {
        when(genomeMock.getCode()).thenReturn("00000");

        Animal animal = new Animal(new Vector2d(5, 5), genomeMock, 10, false);

        worldMap.place(animal);

        assertEquals("animal", worldMap.at(new Vector2d(5, 5)).toString());
        assertEquals("dirt", worldMap.at(new Vector2d(7, 7)).toString());

        worldMap.moveToNewTile(animal, new Vector2d(7, 7));

        assertEquals("dirt", worldMap.at(new Vector2d(5, 5)).toString());
        assertEquals("animal", worldMap.at(new Vector2d(7, 7)).toString());
    }
}