package org.example;

import java.util.concurrent.TimeUnit;

public class LaufzeitMessung {

    private long startTime;

    public void startMeasurement() {
        this.startTime = System.nanoTime();
    }

    public void stopMeasurement(String reason) {
        long endTime = System.nanoTime();
        long duration = endTime - this.startTime;

        long milliseconds = TimeUnit.NANOSECONDS.toMillis(duration);

        //System.out.println(reason + " hat " + duration + " Nanosekunden gedauert!");
        System.out.println(reason + " hat " + milliseconds + " Millisekunden gedauert!");
    }
}
