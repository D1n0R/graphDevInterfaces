package com.homework;

import java.util.*;

public class task5 {
    static class Pair {
        int start;
        int end;

        public Pair(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return "(" + start + ", " + end + ")";
        }
    }

    public static Pair findLongestMonotonic(List<Integer> arr) {
        int bestSt = 0, bestEnd = 0;
        int lenUp = 1, lenDown = 1;
        int stUp = 0, stDown = 0;

        for (int i = 1; i < arr.size(); i++) {
            if (arr.get(i) > arr.get(i - 1)) {
                lenUp++;
                lenDown = 1;
                stDown = i;

                if (lenUp > bestEnd - bestSt + 1) {
                    bestSt = stUp;
                    bestEnd = i;
                }

            } else if (arr.get(i) < arr.get(i - 1)) {
                lenDown++;
                lenUp = 1;
                stUp = i;
                if (lenDown > bestEnd - bestSt + 1) {
                    bestSt = stDown;
                    bestEnd = i;
                }

            } else {
                lenUp = 1;
                lenDown = 1;
                stUp = i;
                stDown = i;
            }
        }

        return new Pair(bestSt, bestEnd);
    }
}
