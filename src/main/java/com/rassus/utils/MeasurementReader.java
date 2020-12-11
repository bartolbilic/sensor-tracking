package com.rassus.utils;
import com.rassus.models.Measurement;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MeasurementReader {
    private static List<Measurement> measurements;

    public Measurement getMeasurement(String key) {
        return getMeasurements().stream()
                .filter(t -> t.getName().equals(key))
                .findFirst().orElse(null);
    }

    private List<Measurement> getMeasurements() {
        if (measurements == null || measurements.isEmpty()) {
            return parseLineToMeasurements();
        }

        return measurements;
    }

    private void generateNewMeasurements() {
        System.out.println("Starting measurement procedure...");
        measurements = parseLineToMeasurements();
    }

    private List<Measurement> parseLineToMeasurements() {
        List<Measurement> measurements = new ArrayList<>();

        String headerString = readHeaders();
        String valueString = readLine();

        String[] headers = headerString.split(",");
        String[] values = valueString.split(",");

        for (int i = 0; i < headers.length; i++) {
            Float value = 0.0f;
            try {
                value = Float.parseFloat(values[i]);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException ignored) {

            }
            measurements.add(new Measurement(headers[i], value));
        }

        return measurements;
    }

    private String readHeaders() {
        String line = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(new File("/Users/bartol/IdeaProjects/sensor-tracking/src/main/resources/data.csv")))) {
            line = reader.readLine();
        } catch (IOException ignored) {

        }

        return line;
    }

    private String readLine() {
        int lineNumber = generateLineNumber();
        System.out.println("Line number generated: " + lineNumber);
        String line = null;

        try (BufferedReader reader = new BufferedReader(new FileReader(new File("/Users/bartol/IdeaProjects/sensor-tracking/src/main/resources/data.csv")))) {
            for (int i = 0; i <= lineNumber; i++) {
                line = reader.readLine();
            }
        } catch (IOException ignored) {

        }

        return line;
    }


    private int generateLineNumber() {
        int timeElapsedSeconds = Stopwatch.getTimeElapsedSeconds();
        System.out.println("Time elapsed in seconds: " + timeElapsedSeconds);
        return timeElapsedSeconds % 100 + 2;
    }


}