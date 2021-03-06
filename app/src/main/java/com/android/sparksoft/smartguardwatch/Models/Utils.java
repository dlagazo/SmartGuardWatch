package com.android.sparksoft.smartguardwatch.Models;

import java.util.ArrayList;

public class Utils {
    public static long getCurrentTimeStampInMillis() {
        return System.currentTimeMillis() / 1000;
    }

    public static boolean isAccelerometerArrayExceedingTimeLimit(ArrayList<AccelerometerData> a, long timeLimit) {
        return (a.size() > 1) && (a.get(a.size() - 1).getTimestamp() - a.get(0).getTimestamp() > timeLimit);
    }

    //TODO: Probably better to just insert the data as normalized instead of normalizing here
    public static int getNumberOfPeaksThatExceedThreshold(ArrayList<AccelerometerData> a, double FALL_THRESHOLD) {
        int numberOfPeaksThatExceedThreshold = 0;
        for (int i = 1; i < a.size() - 1; ++i) {
            double prev = a.get(i - 1).getNormalizedAcceleration();
            double curr = a.get(i).getNormalizedAcceleration();
            double next = a.get(i + 1).getNormalizedAcceleration();

            if (curr > prev && curr > next) {
                if (a.get(i).getNormalizedAcceleration() > FALL_THRESHOLD) {
                    ++numberOfPeaksThatExceedThreshold;
                }
            }
        }
        return numberOfPeaksThatExceedThreshold;
    }

    public static double getAverageNormalizedAcceleration(ArrayList<AccelerometerData> a) {
        double average = 0.0;
        for (AccelerometerData data : a) {
            average += data.getNormalizedAcceleration();
        }
        return average / a.size();
    }

    public ArrayList<AccelerometerData> runMedianFilter(ArrayList<AccelerometerData> a) {

        return a;
    }
}
