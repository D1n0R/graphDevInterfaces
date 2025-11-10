package com.homework;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class task1Test {

    @Test
    void searchInContactsTest1() {
        assertArrayEquals(new String[]{"John Smith", "Mark Johnson"}, task1.searchInContacts("Joh",
                new ArrayList<>(Arrays.asList("John Smith", "Mike Marley", "Hillary Cosplay", "Mark Johnson"))).toArray());
    }

    @Test
    void searchInContactsTest2() {
        assertArrayEquals(new String[]{"Mark Johnson"}, task1.searchInContacts("m John",
                new ArrayList<>(Arrays.asList("John Smith", "Mike Marley", "Hillary Cosplay", "Mark Johnson"))).toArray());
    }

    @Test
    void searchInContactsTest3() {
        assertArrayEquals(new String[]{"Kamil Englo", "Mjohn Kengsman", "Mjohn Keng"}, task1.searchInContacts("keng",
                new ArrayList<>(Arrays.asList("John Smith", "Mike Marley", "Hillary Cosplay",
                        "Mark Johnson", "Kamil Englo", "Mjohn Kengsman", "Mjohn Keng"))).toArray());
    }

}