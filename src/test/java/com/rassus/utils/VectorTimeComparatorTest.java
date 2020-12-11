package com.rassus.utils;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VectorTimeComparatorTest {

    int[] a = {30, 30, 30, 30};
    int[] b = {0, 0, 2, 0};
    int[] c = {3, 0, 2, 0};
    int[] d = {0, 0, 2, 1};



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