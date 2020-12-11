package com.rassus.utils;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VectorTimeComparatorTest {

    int[] a = {0, 34, 54, 12};
    int[] b = {1, 34, 54, 5};
    int[] c = {0, 34, 32, 12};
    int[] d = {0, 34, 54, 12};

    private ArrayList<int[]> list = Lists.newArrayList(a, b, c, d);


    @Test
    public void testSorting1() {
        print(list);
        System.out.println("\n");
        list.sort(new VectorTimeComparator());
        print(list);
    }

    private void print(List<int[]> list) {
        for(int[] array : list) {
            System.out.println(Arrays.toString(array));
        }
    }


}