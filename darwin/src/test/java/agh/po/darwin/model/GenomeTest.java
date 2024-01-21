package agh.po.darwin.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GenomeTest {

    @Test
    public void nextIntDefaultTest() {
        Genome genome = new Genome(5, false);
        genome.setCode("01234");

        genome.setCurrent(1);
        assertEquals(1, genome.nextInt());
        assertEquals(2, genome.nextInt());

        genome.setCurrent(4);
        assertEquals(4, genome.nextInt());
        assertEquals(0, genome.nextInt());
    }

    @Test
    public void nextIndLeftRightTest() {
        Genome genome = new Genome(5, true);
        genome.setCode("01234");

        genome.setDirection((short) 1);
        genome.setCurrent(1);
        assertEquals(1, genome.nextInt());
        assertEquals(2, genome.nextInt());

        genome.setDirection((short) -1);
        genome.setCurrent(3);
        assertEquals(3, genome.nextInt());
        assertEquals(2, genome.nextInt());

        genome.setDirection((short) 1);
        genome.setCurrent(4);
        assertEquals(4, genome.nextInt());
        assertEquals(-1, genome.getDirection()); //kierunek
        assertEquals(4, genome.nextInt());
        assertEquals(3, genome.nextInt());

        genome.setDirection((short) -1);
        genome.setCurrent(0);
        assertEquals(0, genome.nextInt());
        assertEquals(1, genome.getDirection()); //kierunek
        assertEquals(0, genome.nextInt());
        assertEquals(1, genome.nextInt());
    }

}