package com.rassus.utils;

public class Stopwatch {
    private static long startTime;

    public static long getTimeElapsedMillis() {
        return System.currentTimeMillis() - startTime;
    }

    public static int getTimeElapsedSeconds() {
        return (int) (getTimeElapsedMillis() / 1000.0);
    }

    public static void setStartTime(long startTime) {
        Stopwatch.startTime = startTime;
    }
}