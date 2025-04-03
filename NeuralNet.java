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
 * Date Last Modified: 3/7/2025
 */


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.lang.StringBuilder;

public class NeuralNet {
    public static int train(TrainingSettings netTrainingSettings){
    /*
    Creates neural net and performs perceptron learning rule based on information
    provided by user.

    Parameters:
    -Training Settings netTrainingSettings: Data structure that holds training information provided by user

    Return:
    - int representing number of epochs of training occured.
    */
        // Get dataset
        List<DataSample> dataset = netTrainingSettings.dataset;

        // Create net architecture from first data sample in dataset
        DataSample firstSample = dataset.get(0);
        int numInputNodes = firstSample.getRowDimension() * firstSample.getColumnDimension();
        int numOutputNodes = firstSample.getOutputDimension();

        // Weight Matrices initialized with zero values by default
        double[][] weightMatrix = new double[numInputNodes][numOutputNodes];
        double[] biasWeights = new double[numOutputNodes];

        // Create training variables
        double learningRate = netTrainingSettings.learningRate;
        double thetaThreshold = netTrainingSettings.thetaThreshold;

        // Set weights to random values if selected by user
        if (!netTrainingSettings.setWeightsToZero){
            // Initialize bias weights
            initializeWeightsRandomValues(biasWeights);

            // Initialize node weights
            for (int i = 0; i < numInputNodes; i++){
                initializeWeightsRandomValues(weightMatrix[i]);
            }
        }

        // Perform training algorithm
        double[] yIn = new double[numOutputNodes];
        int[] yOut = new int[numOutputNodes];
        boolean converged = false;
        int epochNum = 0;
        while (!converged && epochNum < netTrainingSettings.maxEpochs){
            epochNum++;
            boolean weightChanged = false;
            for (DataSample sample : dataset){
                int[] inputSignals = sample.getPixelArray();
                int[] targetOutputs = sample.getOutputVector();
                for (int outputNode = 0; outputNode < numOutputNodes; outputNode++) {
                    yIn[outputNode] = calculateYIn(weightMatrix, biasWeights, inputSignals, outputNode);
                    yOut[outputNode] = applyActivationFunction(yIn[outputNode], thetaThreshold);

                    if (yOut[outputNode] != targetOutputs[outputNode]){
                        weightChanged = updateWeights(weightMatrix, biasWeights, inputSignals, targetOutputs, learningRate, outputNode, netTrainingSettings.weightChangeThreshold);
                        //weightChanged = true;
                    }
                }
            }
            if (weightChanged == false){
                converged = true;
            }
        }
        // If epochNum stopped while loop
        if (!converged){
            System.out.println("Training reached max epochs: " + netTrainingSettings.maxEpochs + "  before converging");
        }
        saveWeightsToFile(weightMatrix, biasWeights, netTrainingSettings.trainedWeightsFile, netTrainingSettings.thetaThreshold);
        return epochNum;
    }

    public static void initializeWeightsRandomValues(double[] weights) {
    /*
    Fills an array with random values in range -0.5 to 0.5 to initialize weights.

    Parameters:
    -weights: array of weights to be filled.
    */
        for (int i = 0; i < weights.length; i++) {
            weights[i] = (double) (Math.random() - 0.5);
        }
    }

    public static double calculateYIn(double[][] weightMatrix, double[] biasWeights, int[] inputSignals, int outputNode) {
    /*
    This method calculates the y in value for the corresponding pattern.

    Parameters:
    - double[][] weightMatrix: Matrix of current weight values
    - double[] biasWeights: Array of current bias weight values
    - int[] inputSignals: pixels of the current sample
    - int outputNode: current outputNode being trained for

    Return:
    - double representing computed YIn
    */
        double computedYIn = biasWeights[outputNode];
        for (int i = 0; i < inputSignals.length; i++) {
            computedYIn += inputSignals[i] * weightMatrix[i][outputNode];
        }
        return computedYIn;
    }


    public static int applyActivationFunction(double yIn, double thetaThreshold) {
    /*
    Applies activation function to value

    Parameters:
    - double yIn: value to be apply activation function on
    - double thetaThreshold: user specificed threshold value for activation function

    Return:
    int representing output of function
    */
        if (yIn > thetaThreshold) {
            return 1;
        } else if(yIn < thetaThreshold){
            return -1;
        } else {
            return 0;
        }
    }


    public static boolean updateWeights(double[][] weightMatrix, double[] biasWeights, int[] inputSignals, int[] targetOutputs, double learningRate, int outputNode, double weightChangeThreshold) {
    /*
    Updates weight according to weight change formula, if calculated weight delta 
    is greater than the user specificed weight change threshold value.

    Parameters:
    - double[][] weightMatrix: Matrix of current weight values
    - double[] biasWeights: Array of current bias weight values
    - int[] inputSignals: pixels of the current sample
    - int[] targetOutputs: target values of the current sample
    - double learningRate: alpha learning rate specified by user
    - int outputNode: used to update correct column of weights
    - double weightChangeThreshold: threshold to stabilize weight change
    */
        boolean greaterThanChangeThreshold = false;
        // Update node weights
        for (int i = 0; i < inputSignals.length; i++) {
            double weightDelta = learningRate * targetOutputs[outputNode] * inputSignals[i];
            if (weightDelta > weightChangeThreshold){
                weightMatrix[i][outputNode] += weightDelta;
                greaterThanChangeThreshold = true;
            }
        }
        // Update bias weight
        double biasWeightDelta = learningRate * targetOutputs[outputNode];
        if(biasWeightDelta > weightChangeThreshold){
            biasWeights[outputNode] += biasWeightDelta;
            greaterThanChangeThreshold = true;
        }
        return greaterThanChangeThreshold;
    }

    public static void saveWeightsToFile(double[][] weightMatrix, double[]biasWeights, String trainedWeightsFileName, double thetaThreshold){
    /*
    Saves trained weight values to output file

    Parameters:
    - double[][] weightMatrix: Matrix of current weight values
    - double[] biasWeights: Array of current bias weight values
    - String trainedWeightsFileName: User specified output file name
    - double thetaThreshold: theta value used in training to be carried over to testing
    */
        // Save Node weights
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(trainedWeightsFileName))) {
            writer.write(weightMatrix.length + "\t\t// Number of input nodes\n");
            writer.write(weightMatrix[0].length + "\t\t//Number of output nodes\n");
            writer.write(thetaThreshold + "\t\t// Theta Threshold used for training\n\n");

            for (double[] row : weightMatrix){
                for (int j = 0; j < row.length; j++){
                    writer.write(String.format("%.6f", row[j]));
                    if (j < row.length - 1) writer.write(" ");
                }
                writer.newLine();
            }
            writer.newLine();

            // Save bias weights
            for (int j = 0; j< biasWeights.length; j++){
                writer.write(String.format("%.6f", biasWeights[j]));
                if (j < biasWeights.length - 1) writer.write(" ");
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
        double [][] trainedWeightMatrix = netTestingSettings.trainedWeightMatrix;
        double [] trainedBiasWeights = netTestingSettings.trainedBiasWeights;

        // Get dataset to test
        List<DataSample> dataset = netTestingSettings.dataset;
        double thetaThreshold = netTestingSettings.thetaThreshold;

        // Create net architecture from first data sample in dataset
        DataSample firstSample = dataset.get(0);
        int numOutputNodes = firstSample.getOutputDimension();
        int numSamples = dataset.size();

        // Deploy Neural Net
        int[][] netClassifications = new int[numSamples][numOutputNodes];
        for(int sampleNum = 0; sampleNum < dataset.size(); sampleNum++){
            double[] yIn = new double[numOutputNodes];
            int[] yOut = new int[numOutputNodes];
            DataSample sample = dataset.get(sampleNum);
            int[] inputSignals = sample.getPixelArray();

            for (int outputNode = 0; outputNode < numOutputNodes; outputNode++) {
                yIn[outputNode] = calculateYIn(trainedWeightMatrix, trainedBiasWeights, inputSignals, outputNode);
                yOut[outputNode] = applyActivationFunction(yIn[outputNode], thetaThreshold);
            }
            netClassifications[sampleNum] = yOut;
        }
        saveResultsToFile(netClassifications, netTestingSettings.testingResultsOutputFilePath);
    }

    //enumerated type to help identify the label based on the output vector
    public enum Label{
        //creating teh Lables based on
        A(new int[]{1, -1, -1, -1, -1, -1, -1}),
        B(new int[]{-1, 1, -1, -1, -1, -1, -1}),
        C(new int[]{-1, -1, 1, -1, -1, -1, -1}),
        D(new int[]{-1, -1, -1, 1, -1, -1, -1}),
        E(new int[]{-1, -1, -1, -1, 1, -1, -1}),
        J(new int[]{-1, -1, -1, -1, -1, 1, -1}),
        K(new int[]{-1, -1, -1, -1, -1, -1, 1});
        //instance variable
        private final int[] output;
        /*initialization function
        * Parameters:
        * int[] output - the output vector associated with the label
        */
        Label(int[] output){
            this.output = output;
        }

        /*
        * Method that returns the corresponding label based on the given output array.
        *
        * Parameters:
        * int[] givenOutput - an individual output array from testing
        *
        * Return:
        * Label object - the corresponding label to the output array
        * null - if the output vector does not correspond with any of the labels
        *
        * */
        public static Label getLabel(int[] givenOutput){
            for(Label label : Label.values()){
                if(Arrays.equals(givenOutput, label.output)){
                    return label;
                }
            }
            return null;
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