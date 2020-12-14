package com.rassus.utils;

import java.util.Comparator;

public class VectorTimeComparator implements Comparator<int[]> {
    @Override
    public int compare(int[] t1, int[] t2) {
        if (t1 == null || t2 == null) {
            throw new IllegalArgumentException("Null values aren't allowed");
        }

        if (t1.length != t2.length) {
            throw new RuntimeException("Vector time lengths don't match");
        }

        boolean isAGreater = true;
        for (int i = 0; i < t1.length; i++) {
            if (t1[i] < t2[i]) {
                isAGreater = false;
                break;
            }
        }

        if (isAGreater) {
            return 1;
        }

        boolean isBGreater = true;
        for (int i = 0; i < t1.length; i++) {
            if (t2[i] < t1[i]) {
                isBGreater = false;
                break;
            }
        }

        if (isBGreater) {
            return -1;
        }

        return 0;
    }
}