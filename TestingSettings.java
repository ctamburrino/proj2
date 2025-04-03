/*
 * This program is a data structure to hold testing settings specified by user.
 * 
 * Authors:
 * - Cory Tamburrino
 * - David Kujawinski
 * - Dinh Troung
 * 
 * Date Last Modified: 3/7/2025
 */

import java.util.List;

public class TestingSettings {
    String trainedWeightsFilePath;
    String testingDataFilePath;
    String testingResultsOutputFilePath;
    double[][] trainedWeightMatrix;
    List<DataSample> dataset;
}
