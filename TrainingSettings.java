/*
 * This program is a data structure to hold training settings specified by user.
 * 
 * Authors:
 * - Cory Tamburrino
 * - David Kujawinski
 * - Dinh Troung
 * 
 * Date Last Modified: 3/7/2025
 */

import java.util.List;

public class TrainingSettings {
    String trainingDataFilePath;
    boolean setWeightsToZero;
    int maxEpochs;
    String trainedWeightsFile;
    double learningRate;
    double thetaThreshold;
    double weightChangeThreshold;
    List<DataSample> dataset;
}
