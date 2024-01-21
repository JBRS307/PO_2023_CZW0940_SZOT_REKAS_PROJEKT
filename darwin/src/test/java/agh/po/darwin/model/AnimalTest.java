package agh.po.darwin.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AnimalTest {
    @Mock
    Genome genomeMock;
    @Mock
    DefaultMap defaultMapMock;
    @Mock
    Animal animalMock;
    @Mock
    Vector2d positionMock;
    @Mock
    Random randomMock = Mockito.mock(Random.class);

    Simulation sampleSimulation = new Simulation(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, false, false, false);

    @Test
    public void defaultMoveTest() {
        when(genomeMock.nextInt()).thenReturn(0); // zmockowany ruch, idzie tam gdzie patrzy
        when(defaultMapMock.getCurrentBounds()).thenReturn(new Boundary(new Vector2d(0, 0), new Vector2d(10, 10)));
        when(genomeMock.getCode()).thenReturn("00000");

        Animal animal1 = new Animal(new Vector2d(5, 5), genomeMock, 10, false);
        animal1.setDirection(MapDirection.NORTH);
        animal1.move(defaultMapMock, false);

        Animal animal2 = new Animal(new Vector2d(10, 10), genomeMock, 10, false);
        animal2.setDirection(MapDirection.EAST);
        animal2.move(defaultMapMock, false);

        Animal animal3 = new Animal(new Vector2d(10, 10), genomeMock, 10, false);
        animal3.setDirection(MapDirection.NORTH);
        animal3.move(defaultMapMock, false);

        assertEquals(new Vector2d(5, 6), animal1.getPosition());
        assertEquals(new Vector2d(0, 10), animal2.getPosition());
        assertEquals(new Vector2d(10, 9), animal3.getPosition());
    }

    @Test
    public void hellMoveTest() {
        when(genomeMock.nextInt()).thenReturn(0); // zmockowany ruch, idzie tam gdzie patrzy
        when(defaultMapMock.getCurrentBounds()).thenReturn(new Boundary(new Vector2d(0, 0), new Vector2d(10, 10)));
        when(genomeMock.getCode()).thenReturn("00000");
        when(randomMock.nextInt(Mockito.anyInt())).thenReturn(4);
        when(defaultMapMock.getSimulation()).thenReturn(sampleSimulation);

        Animal animal1 = new Animal(new Vector2d(5, 5), genomeMock, 10, false);
        animal1.setDirection(MapDirection.SOUTH_WEST);
        animal1.move(defaultMapMock, true);

        Animal animal2 = new Animal(new Vector2d(10, 10), genomeMock, 10, false);
        animal2.setDirection(MapDirection.NORTH);
        animal2.setRandomGenerator(randomMock);
        animal2.move(defaultMapMock, true);

        Animal animal3 = new Animal(new Vector2d(0, 0), genomeMock, 10, false);
        animal3.setDirection(MapDirection.WEST);
        animal3.setRandomGenerator(randomMock);
        animal3.move(defaultMapMock, true);

        assertEquals(new Vector2d(4, 4), animal1.getPosition());
        assertEquals(new Vector2d(4, 4), animal2.getPosition());
        assertEquals(new Vector2d(4, 4), animal3.getPosition());
    }

    @Test
    public void defaultMoveTestWithRotation() {
        when(defaultMapMock.getCurrentBounds()).thenReturn(new Boundary(new Vector2d(0, 0), new Vector2d(10, 10)));
        when(genomeMock.getCode()).thenReturn("0"); // bez znaczenia co tu jest
        when(genomeMock.nextInt()).thenReturn(1);

        Animal animal = new Animal(new Vector2d(5, 5), genomeMock, 10, false);
        animal.setDirection(MapDirection.NORTH);
        animal.move(defaultMapMock, false);

        assertEquals(new Vector2d(6, 6), animal.getPosition());

        when(genomeMock.nextInt()).thenReturn(7);

        animal.setDirection(MapDirection.NORTH);
        animal.setPosition(new Vector2d(5, 5));
        animal.move(defaultMapMock, false);

        assertEquals(new Vector2d(4, 6), animal.getPosition());



    }

}