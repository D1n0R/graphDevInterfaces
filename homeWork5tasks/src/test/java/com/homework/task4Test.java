package com.homework;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class task4Test {
    @Test
    void findMissingTest1(){
        assertEquals(task4.findMissing(Arrays.asList(1, 2, 3), Arrays.asList(3, 4)), Arrays.asList(1, 2));
    }

    @Test
    void findMissingTest2(){
        assertEquals(task4.findMissing(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3)), List.of());
    }

    @Test
    void findMissingTest3(){
        assertEquals(task4.findMissing(Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6)), Arrays.asList(1, 2, 3));
    }
}
