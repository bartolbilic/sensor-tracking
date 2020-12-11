package com.rassus.utils;
import java.util.Random;

public class EmulatedSystemClock {

    private final long startTime;
    private final double jitter;

    public EmulatedSystemClock() {
        startTime = System.currentTimeMillis();
        Random r = new Random();
        jitter = (r.nextInt(400) - 200) / 1000d;
    }

    public long currentTimeMillis() {
        long current = System.currentTimeMillis();
        long diff = current - startTime;
        double coef = diff / 1000;
        return startTime + Math.round(diff * Math.pow((1 + jitter), coef));
    }
}