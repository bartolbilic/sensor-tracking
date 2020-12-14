package com.rassus.utils;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VectorTimeComparatorTest {

    int[] a = {0, 0, 0, 1};
    int[] b = {0, 0, 0, 2};
    int[] c = {0, 2, 0, 5};
    int[] d = {0, 5, 0, 2};
    int[] e = {0, 0, 0, 3};
    int[] f = {0, 2, 0, 1};
    int[] g = {0, 3, 5, 3};

    private ArrayList<int[]> list = Lists.newArrayList(a, b, c, d, e, f, g);


    @Test
    public void testSorting1() {
        print(list);
        System.out.println("\n");
        list.sort(new VectorTimeComparator());
        print(list);
    }

    private void print(List<int[]> list) {
        for (int[] array : list) {
            System.out.println(Arrays.toString(array));
        }
    }


}