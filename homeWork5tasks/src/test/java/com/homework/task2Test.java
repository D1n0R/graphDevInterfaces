package com.homework;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class task2Test {
    @Test
    void calcChildWidthsTest1(){
        assertEquals(task2.calcChildWidths(200, Arrays.asList(-1, -1, -2)), Arrays.asList(50, 50, 100));
    }

    @Test
    void calcChildWidthsTest2(){
        assertEquals(task2.calcChildWidths(100, Arrays.asList(30, 40)), Arrays.asList(30, 40));
    }

    @Test
    void calcChildWidthsTest3(){
        assertEquals(task2.calcChildWidths(50, Arrays.asList(60, -1)), null);
    }
}
