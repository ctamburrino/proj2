/*
 * This program implements the neural net portion of the program.  It has methods
 * to train the net and save weights to an output file, and test the neural net on
 * a dataset and save the results to an output file.
 * 
 * Authors:
 * - Cory Tamburrino
 * - David Kujawinski
 * - Dinh Troung
 * 
 * Date Last Modified: 4/3/2025
 */

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NeuralNet {
    public static boolean train(TrainingSettings netTrainingSettings){
    /*
    Creates neural net and performs hebb training rule based on information
    provided by user.

    Parameters:
    -Training Settings netTrainingSettings: Data structure that holds training information provided by user

    Return:
    - boolean representing training occured successfully.
    */
        // Get dataset
        List<DataSample> dataset = netTrainingSettings.dataset;

        // Create net architecture from first data sample in dataset
        DataSample firstSample = dataset.get(0);
        int numNodes = firstSample.getRowDimension() * firstSample.getColumnDimension();

        // Weight Matrices initialized with zero values
        int[][] weightMatrix = new int[numNodes][numNodes];

        for (DataSample sample : dataset){
            updateWeightMatrix(weightMatrix, sample.getPixelArray());
        }
        saveWeightsToFile(weightMatrix, netTrainingSettings.trainedWeightsFile);
        return true;
    }

    public static void updateWeightMatrix(int[][] weightMatrix, int[] sampleVector){
    /*
    Performs outer product of sampleVector with itself, and adds values to weight matrix.
    If on a diagonal entry of the matrix, skips update, and keeps value at 0.

    Parameters:
    - int[][] weightMatrix - current weight matrix
    - int[] sampleVector - current sample vector
    */
        int numElements = sampleVector.length;
        for(int i = 0; i < numElements; i++){
            for(int j = 0; j < numElements; j++){
                if (i != j){
                    weightMatrix[i][j] += sampleVector[i] * sampleVector[j];
                }
            }
        }
    }

    public static void saveWeightsToFile(int[][] weightMatrix, String trainedWeightsFileName){
    /*
    Saves trained weight values to output file

    Parameters:
    - int[][] weightMatrix: Matrix of current weight values
    - String trainedWeightsFileName: User specified output file name
    */
        // Save Node weights
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(trainedWeightsFileName))) {
            writer.write(weightMatrix.length + "\t\t// Number of nodes\n");

            for (int[] row : weightMatrix){
                for (int j = 0; j < row.length; j++){
                    writer.write(String.valueOf(row[j]));
                    if (j < row.length - 1) writer.write(" ");
                }
                writer.newLine();
            }

            System.out.println("Weights saved successfully to " + trainedWeightsFileName + "\n");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static int[][] test(TestingSettings netTestingSettings){
    /*
    Tests neural net with dataset and trained weights.

    Parameters:
    -Testing Settings netTestingSettings: Data structure that holds testing information provided by user.
    */
        // Load trained weight matrices from file
        int [][] trainedWeightMatrix = netTestingSettings.trainedWeightMatrix;
        int numNodes = netTestingSettings.numNodes;

        // Get dataset to test
        List<DataSample> dataset = netTestingSettings.dataset;
        int[][] netClassifications = new int[dataset.size()][numNodes];

        int sampleNum = 0;
        for (DataSample sample : dataset){
            sampleNum++;
            boolean converged = false;
            int[] xArray = sample.getPixelArray();
            int[] yArray = Arrays.copyOf(xArray, xArray.length);
            List<Integer> nodes = new ArrayList<>(numNodes);
            for (int i = 0; i < numNodes; i++){
                nodes.add(i);
            }
            Collections.shuffle(nodes);
            while(!converged){
                yArray = Arrays.copyOf(xArray, xArray.length);
                boolean activationChanged = false;
                for (int i = 0; i < numNodes; i++){
                    int yIn = calculateYIn(trainedWeightMatrix, xArray, i, yArray);
                    int yOut = applyActivationFunction(yIn, yArray[i]);
                        if (yArray[i] != yOut){
                            yArray[i] = yOut;
                            activationChanged = true;
                        }
                    }
                if (activationChanged == false){
                    converged = true;
                }else{
                    xArray = Arrays.copyOf(yArray, yArray.length);;
                }
            }
            netClassifications[sampleNum] = yArray;
        }

        // // Create net architecture from first data sample in dataset
        // DataSample firstSample = dataset.get(0);
        // int numOutputNodes = firstSample.getOutputDimension();
        // int numSamples = dataset.size();
        // List<int> randomSample = new List<int>[numOutputNodes];
        // for(int i = 0; i<numOutputNodes; i++){
        //     randomSample[i] = i;
        // }
        // Random random = new Random();

        // // Deploy Neural Net

        // for(int sampleNum = 0; sampleNum < dataset.size(); sampleNum++){
        //     int[] yIn = new int[numOutputNodes];
        //     DataSample sample = dataset.get(sampleNum);
        //     int[] inputSignals = sample.getPixelArray();
        //     int[] yOut = inputSignals;
        //     for (int outputNode = 0; outputNode < numOutputNodes; outputNode++) {        
        //         int randomIndex = randomSample.get(random.nextInt(randomSample.size));
        //         yIn[randomIndex] = calculateYIn(trainedWeightMatrix, inputSignals, randomIndex, yOut);
        //         yOut[outputNode] = applyActivationFunction(yIn[randomIndex], yOut[randomIndex]);
        //     }
        //     netClassifications[sampleNum] = yOut;
        // }

        return netClassifications;
        
        //saveResultsToFile(netClassifications, netTestingSettings.testingResultsOutputFilePath);
    }

    public static int calculateYIn(int[][] weightMatrix, int[] xArray, int neuronNum, int[] yArray) {
        /*
        This method calculates the y in value for the corresponding pattern.
    
        Parameters:
        - int[][] weightMatrix: Matrix of current weight values
        - int[] biasWeights: Array of current bias weight values
        - int[] inputSignals: pixels of the current sample
        - int outputNode: current outputNode being trained for
    
        Return:
        - int representing computed YIn
        */
            int computedYIn = xArray[neuronNum];
            for (int yElement = 0; yElement < yArray.length; yElement++) {
                computedYIn += yArray[yElement] * weightMatrix[yElement][neuronNum];
            }
            return computedYIn;
        }
    
    
        public static int applyActivationFunction(int yIn, int prevY) {
        /*
        Applies activation function to value.
    
        Parameters:
        - int yIn: value to be apply activation function on
    
        Return:
        int representing output of function
        */
            if (yIn > 0) {
                return 1;
            } else if(yIn < 0){
                return -1;
            } else{
                return prevY;
            }
        }
}