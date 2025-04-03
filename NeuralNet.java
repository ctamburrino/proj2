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
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.lang.StringBuilder;

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
                    writer.write(row[j]);
                    if (j < row.length - 1) writer.write(" ");
                }
                writer.newLine();
            }

            System.out.println("Weights saved successfully to " + trainedWeightsFileName + "\n");
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void test(TestingSettings netTestingSettings){
    /*
    Tests neural net with dataset and trained weights.

    Parameters:
    -Testing SettingsnetTestingSettings: Data structure that holds testing information provided by user.
    */
        // Load trained weight matrices from file
        int [][] trainedWeightMatrix = netTestingSettings.trainedWeightMatrix;

        // Get dataset to test
        List<DataSample> dataset = netTestingSettings.dataset;

        // Create net architecture from first data sample in dataset
        DataSample firstSample = dataset.get(0);
        int numOutputNodes = firstSample.getOutputDimension();
        int numSamples = dataset.size();
        List<int> randomSample = new List<int>[numOutputNodes];
        for(int i = 0; i<numOutputNodes; i++){
            randomSample[i] = i;
        }
        Random random = new Random();

        // Deploy Neural Net
        int[][] netClassifications = new int[numSamples][numOutputNodes];
        for(int sampleNum = 0; sampleNum < dataset.size(); sampleNum++){
            int[] yIn = new int[numOutputNodes];
            DataSample sample = dataset.get(sampleNum);
            int[] inputSignals = sample.getPixelArray();
            int[] yOut = inputSignals;
            for (int outputNode = 0; outputNode < numOutputNodes; outputNode++) {        
                int randomIndex = randomSample.get(random.nextInt(randomSample.size));
                yIn[randomIndex] = calculateYIn(trainedWeightMatrix, inputSignals, randomIndex, yOut);
                yOut[outputNode] = applyActivationFunction(yIn[randomIndex], yOut[randomIndex]);
            }
            netClassifications[sampleNum] = yOut;
        }


        
        saveResultsToFile(netClassifications, netTestingSettings.testingResultsOutputFilePath);
    }

    public static int calculateYIn(int[][] weightMatrix, int[] inputSignals, int index, int[] yOut) {
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
            int computedYIn = inputSignals[index];
            for (int i = 0; i < inputSignals.length; i++) {
                computedYIn += yOut[i] * weightMatrix[i][index];
            }
            return computedYIn;
        }
    
    
        public static int applyActivationFunction(int yIn, int yOut) {
        /*
        Applies activation function to value
    
        Parameters:
        - int yIn: value to be apply activation function on
    
        Return:
        int representing output of function
        */
            if (yIn > 0) {
                return 1;
            } else if(yIn < 0){
                return -1;
            } else {
                return yOut;
            }
        }


        /*
        * a method that turns an array into a string without brackets or commas, with each element being space separated
        *
        * Parameters:
        * int[] array - the array to be transformed
        *
        * Returns:
        * a String of the transformed array
        *
        * */
        public static String arrayToString(int[] array){
            StringBuilder sb = new StringBuilder();
            for(int i=0; i<array.length; i++){
                sb.append(array[i]+" ");
            }
            return sb.toString();
        }

        /*Override the toString such that it prints out in the required format
        *
        * Parameters:
        * None
        *
        * Returns:
        * a String of the name followed by a new line and the array transformed by the arrayToString() method
        *
        * */
        @Override
        public String toString(){
            return this.name()+"\n"+Label.arrayToString(this.output);
        }
    }

    public static void saveResultsToFile(int[][] classifications, String testingResultsOutputFilePath){
    /*
    Saves classification results from testing to output file specified by user

    Parameters:
    - int[][] classifications: Matrix representing classification results
    - String testingResultsOutputFilePath: filepath of output file specified by user
    */
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testingResultsOutputFilePath))) {
            Label[] actualOutput = Label.values();
            int labelIncrement = 0;
            for (int[] row : classifications){
                //identifies the label it was trying to classify
                Label classifiedLabel = Label.getLabel(row);
                //prints out the expected result
                writer.write("Actual:\n"+actualOutput[labelIncrement].toString());
                writer.newLine();
                //prints out what it was classified as, and Undecided if the vector was not identified
                if(classifiedLabel==null){
                    writer.write("Classified:\nUndecided\n"+Label.arrayToString(row));
                }
                else{
                    writer.write("Classified:\n"+classifiedLabel.toString());
                }
                //increments the labels
                if(labelIncrement==6)
                    labelIncrement = 0;
                else
                    labelIncrement++;
                writer.newLine();
                writer.newLine();
            }
            writer.newLine();
            writer.newLine();
            writer.close();
            System.out.println("Results saved successfully to " + testingResultsOutputFilePath + "\n");
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}