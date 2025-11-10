package com.homework;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class task3Test {
    @Test
    void compareStringsTest1(){
        assertTrue(task3.compareStrings("ab#c", "ad#c"));
    }

    @Test
    void compareStringsTest2(){
        assertTrue(task3.compareStrings("ab##", "c#d#"));
    }

    @Test
    void compareStringsTest3(){
        assertFalse(task3.compareStrings("a#c", "b"));
    }

    @Test
    void compareStringsTest4(){
        assertTrue(task3.compareStrings("######abc", "#abc"));
    }
}
