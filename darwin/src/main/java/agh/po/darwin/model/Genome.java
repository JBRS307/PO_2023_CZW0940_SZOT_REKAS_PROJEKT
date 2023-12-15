package agh.po.darwin.model;

import java.util.Iterator;
import java.util.Random;

public class Genome {
    private String code;
    private int current = 0;

    public Genome(int lenght) {
        //random genome
        this.code = generateRandomDigits(lenght);
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public boolean hasNext() {
        return true;
    }

    public MapDirection nextDirection() {
        int index = Integer.parseInt(String.valueOf(code.charAt((current) % code.length())));
        current += 1;
        return MapDirection.fromInt(index);
    }

    public int nextInt() {
        int index = Integer.parseInt(String.valueOf(code.charAt((current) % code.length())));
        current += 1;
        return index;
    }

    private String generateRandomDigits(int length) {
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(0, 8)); // Append a random digit (0-7)
        }
        return sb.toString();
    }
}
