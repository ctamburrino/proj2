/*
 * This program implements the neural net portion of the program.  It has methods
 * to train the net, save weights to an output file, test the neural net on
 * a dataset and save those results to an output file.
 * 
 * Authors:
 * - Cory Tamburrino
 * - David Kujawinski
 * - Dinh Troung
 * 
 * Date Last Modified: 4/8/2025
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
    Creates neural net and and adjusts weights based on images provided
    by the training sample.

    Parameters:
    -Training Settings netTrainingSettings: Data structure that holds training information for the data samples.

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

        //Update the weight matrix for each sample
        for (DataSample sample : dataset){
            updateWeightMatrix(weightMatrix, sample.getPixelArray());
        }

        //Save weights to an ouput file
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

                //only change weights if not on the primary diagonal
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
    Tests neural net with dataset and trained weights using Hopfield Auto Net Algorithm.

    Parameters:
    -Testing Settings netTestingSettings: Data structure that holds testing information provided by user.

    Return:
    - int[][] of the classified samples.
    */
        // Load trained weight matrices from file
        int [][] trainedWeightMatrix = netTestingSettings.trainedWeightMatrix;
        int numNodes = netTestingSettings.numNodes;

        // Get dataset to test
        List<DataSample> dataset = netTestingSettings.dataset;
        int[][] netClassifications = new int[dataset.size()][numNodes];

        //Initialize sample number and go through each sample 
        int sampleNum = 0;
        for (DataSample sample : dataset){

            //Initialize converged, input array, and output array for each sample run. 
            boolean converged = false;
            int[] xArray = sample.getPixelArray();
            int[] yArray = Arrays.copyOf(xArray, xArray.length);

            //Create list of indicies that will be used to randomly select nodes
            List<Integer> nodes = new ArrayList<>(numNodes);
            for (int i = 0; i < numNodes; i++){
                nodes.add(i);
            }


            while(!converged){
                //Randomize the indicies
                Collections.shuffle(nodes);

                //Set ouput array equal to input array 
                yArray = Arrays.copyOf(xArray, xArray.length);
                boolean activationChanged = false;

                for (int i = 0; i < numNodes; i++){
                    //Get random index
                    int index = nodes.get(i);

                    //Calculate yIn and yOut
                    int yIn = calculateYIn(trainedWeightMatrix, xArray, index, yArray);
                    int yOut = applyActivationFunction(yIn, yArray[index]);
                    
                    //Check for change in output values
                    if (yArray[index] != yOut){
                        yArray[index] = yOut;
                        activationChanged = true;
                    }
                }

                //Check for convergence, if nothing set input array equal to 
                //output array and start over
                if (activationChanged == false){
                    converged = true;
                }else{
                    xArray = Arrays.copyOf(yArray, yArray.length);;
                }
            }
            //Retrieve classification and move to next sample
            netClassifications[sampleNum] = yArray;
            sampleNum++;
        }

        return netClassifications;
    }

    public static int calculateYIn(int[][] weightMatrix, int[] xArray, int neuronNum, int[] yArray) {
        /*
        This method calculates the y in value for the corresponding pattern.
    
        Parameters:
        - int[][] weightMatrix: Matrix of current weight values
        - int[] xArray: Array of current inpute values
        - int neuronNum: the index number of the current node being tested
        - int[] yArray: Array of current output values
    
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
        - int prevY: the current output value prior to activation
    
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