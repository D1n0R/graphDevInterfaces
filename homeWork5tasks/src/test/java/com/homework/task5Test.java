package com.homework;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class task5Test {
    @Test
    void findLongestMonotonicTest1(){
        assertEquals(task5.findLongestMonotonic(Arrays.asList(2, 7, 5, 4, 4, 3)), new task5.Pair(1, 3));
    }

    @Test
    void findLongestMonotonicTest2(){
        assertEquals(task5.findLongestMonotonic(Arrays.asList(1, 1)), new task5.Pair(0, 0));
    }

    @Test
    void findLongestMonotonicTest3(){
        assertEquals(task5.findLongestMonotonic(Arrays.asList(5, 4, 3, 2, 1, 2)), new task5.Pair(0, 4));
    }
}
