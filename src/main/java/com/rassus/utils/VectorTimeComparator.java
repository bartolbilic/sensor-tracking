package com.rassus.utils;

import java.util.Comparator;

public class VectorTimeComparator implements Comparator<int[]> {
    @Override
    public int compare(int[] t1, int[] t2) {
        if (t1.length != t2.length) {
            throw new RuntimeException("Vector time lengths don't match");
        }

        int[] results = new int[4];

        for (int i = 0; i < t1.length; i++) {

            if (t1[i] == t2[i]) {
                results[i] = 0;
            }

            if (t1[i] < t2[i]) {
                results[i] = -1;
            }

            if (t1[i] > t2[i]) {
                results[i] = 1;
            }
        }

        boolean hasNegative = false;
        boolean hasPositive = false;

        for (int i = 0; i < 4; i++) {
            if (results[i] < 0) {
                hasNegative = true;
            }

            if (results[i] > 0) {
                hasPositive = true;
            }
        }

        if (hasNegative && hasPositive) {
            return 0;
        }

        if (hasNegative) {
            return -1;
        }

        if (hasPositive) {
            return 1;
        }

        return 0;
    }
}