package com.homework;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        System.out.println(task1.searchInContacts("Joh",
                new ArrayList<>(Arrays.asList("John Smith", "Mike Marley", "Hillary Cosplay", "Mark Johnson"))));

        System.out.println(task1.searchInContacts("m John",
                new ArrayList<>(Arrays.asList("John Smith", "Mike Marley", "Hillary Cosplay", "Mark Johnson"))));

        System.out.println(task1.searchInContacts("keng",
                new ArrayList<>(Arrays.asList("John Smith", "Mike Marley", "Hillary Cosplay",
                        "Mark Johnson", "Kamil Englo", "Mjohn Kengsman", "Mjohn Keng"))));

        System.out.println(task2.calcChildWidths(200, Arrays.asList(-1, -1, -2))); // [50.0, 50.0, 100.0]
        System.out.println(task2.calcChildWidths(100, Arrays.asList(30, 40)));     // [30.0, 40.0]
        System.out.println(task2.calcChildWidths(50, Arrays.asList(60, -1)));      // !!!!!!!!!!

        System.out.println(task3.compareStrings("ab#c", "ad#c"));      // true
        System.out.println(task3.compareStrings("ab##", "c#d#"));      // true
        System.out.println(task3.compareStrings("a#c", "b"));          // false
        System.out.println(task3.compareStrings("######abc", "#abc")); // true

        System.out.println(task4.findMissing(Arrays.asList(1, 2, 3), Arrays.asList(3, 4)));      // [1, 2]
        System.out.println(task4.findMissing(Arrays.asList(1, 2, 3), Arrays.asList(1, 2, 3)));   // []
        System.out.println(task4.findMissing(Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6)));   // [1, 2, 3]

        System.out.println(task5.findLongestMonotonic(Arrays.asList(2, 7, 5, 4, 4, 3)));  // (1, 3)
        System.out.println(task5.findLongestMonotonic(Arrays.asList(1, 1)));              // (1, 1)
        System.out.println(task5.findLongestMonotonic(Arrays.asList(5, 4, 3, 2, 1, 2)));  // (0, 4)

    }
}