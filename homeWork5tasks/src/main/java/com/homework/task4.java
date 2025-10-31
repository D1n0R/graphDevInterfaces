package com.homework;

import java.util.*;

public class task4 {

    private static boolean binSearch(List<Integer> list, int target) {
        int l = 0;
        int r = list.size() - 1;

        while (l <= r) {
            int m = l + (r - l) / 2;

            if (list.get(m) == target) {
                return true;
            } else if (list.get(m) < target) {
                l = m + 1;
            } else {
                r = m - 1;
            }
        }

        return false;
    }

    public static List<Integer> findMissing(List<Integer> list1, List<Integer> list2) {
        List<Integer> result = new ArrayList<>();

        for (int num : list1)
            if (!binSearch(list2, num)) result.add(num);

        return result;
    }
}
