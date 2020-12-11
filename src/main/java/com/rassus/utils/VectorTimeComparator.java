package com.rassus.utils;

import java.util.Comparator;

public class VectorTimeComparator implements Comparator<int[]> {
    @Override
    public int compare(int[] t1, int[] t2) {
        if (t1.length != t2.length) {
            throw new RuntimeException("Vector time lengths don't match");
        }

        boolean isDetermined = false;
        boolean isBefore = false;

        for (int i = 0; i < t1.length; i++) {
            int time1 = t1[i];
            int time2 = t2[i];

            if (!isDetermined) {
                if (time1 < time2) {
                    isBefore = true;
                    isDetermined = true;
                }

                if (time1 > time2) {
                    isBefore = false;
                    isDetermined = true;
                }
            }

            if (isDetermined && isBefore && time1 > time2 ||
                    isDetermined && !isBefore && time1 < time2) {
                return 0;
            }
        }

        return isBefore ? -1 : 1;
    }
}