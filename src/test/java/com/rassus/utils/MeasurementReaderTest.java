package com.rassus.utils;

import com.rassus.models.Measurement;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@Slf4j
class MeasurementReaderTest {
    private static MeasurementReader measurementReader;

    @BeforeAll
    public static void setup() {
        measurementReader = new MeasurementReader();
    }

    @Test
    public void bruteForceTest() {
        for(int i = 2; i <= 101; i++) {
            Measurement measurement = measurementReader.getMeasurement("CO");

            System.out.println(measurement.getName());
            System.out.println(measurement.getValue());
        }
    }

}