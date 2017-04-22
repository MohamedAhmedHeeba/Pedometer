package com.example.mohamed.accelerometer;

import java.util.List;


public class StatisticsUtil {

    public double findMean(List<Double> list) {
        double total = 0;
        for (int i = 0; i < list.size(); i++) {
            total += list.get(i);
        }
        return total / list.size();
    }

    public double standardDeviation(List<Double> list, double mean) {
        double sum = 0;
        for (int i = 0; i < list.size(); i++) {
            list.set(i, Math.pow(list.get(i) - mean, 2));
            sum += list.get(i);
        }
        return Math.sqrt(sum / list.size());
    }

    public int finAllPeaks(List<Double> list, double minPeak) {
        int counter = 0;
        for (int i = 0; i < list.size(); i++) {
            if (i + 2 < list.size()) {
                double one = list.get(i), two = list.get(i + 1), three = list.get(i + 2);
                if (one < two && two > three && two > minPeak) {
                    counter++;
                }
            }
        }
        return counter;
    }

}