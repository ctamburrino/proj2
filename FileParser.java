/*
 * This program implements file parsing into data structures to be used by the neural net.
 * 
 * Authors:
 * - Cory Tamburrino
 * - David Kujawinski
 * - Dinh Troung
 * 
 * Date Last Modified: 3/7/2025
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileParser {
    static int inputRows;
    static int inputColumns;
    static int outputDimensions;
    static int numSamples;

    public static List<DataSample> parseDataFile(String dataFileName){
    /*
    Parses supplied data file into a individual data samples and saves them in a list

    Parameters:
    - String dataFileName: File name of data file to be parsed

    Return:
    List of DataSamples representing the dataset of sample data
    */
    List<DataSample> dataset = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(dataFileName))){
            String line;
            // Parse header lines
            for (int i = 0;i < 4; i++){
                line = reader.readLine();
                String[] parts = line.trim().split("\\s+");
                int value = Integer.parseInt(parts[0]);

                switch(i) {
                    case 0: inputRows = value;
                    case 1: inputColumns = value;
                    case 2: outputDimensions = value;
                    case 3: numSamples = value;
                }
            }

            // Parse Samples
            for (int i = 0; i < numSamples; i++){
                int[] pixelArray = new int[inputRows * inputColumns];
                
                // Consume blank line
                reader.readLine();
                
                // Parse pixel array
                int pixelArrayIndex = 0;
                for (int j = 0; j < inputRows; j++){
                    line = reader.readLine();
                    String[] parts = line.trim().split("\\s+");
                    for (int k = 0; k < inputColumns; k ++){
                        pixelArray[pixelArrayIndex] = (Integer.parseInt(parts[k]));
                        pixelArrayIndex++;
                    }
                }
                // Consume blank line
                reader.readLine();

                // Parse output vector
                String[] parts = reader.readLine().trim().split("\\s+");
                int[] outputVector = new int[outputDimensions];
                int outputVectorIndex = 0;
                for (int k = 0; k < outputDimensions; k++){
                    outputVector[outputVectorIndex] = Integer.parseInt(parts[k]);
                    outputVectorIndex++;
                }

                // Parse label
                char label = reader.readLine().charAt(0);
                
                // Create data sample and store in dataset
                DataSample newDataSample = createDataSample(inputRows, inputColumns, outputDimensions);
                newDataSample.setPixelArray(pixelArray);
                newDataSample.setOutputVector(outputVector);
                newDataSample.setLabel(label);
                dataset.add(newDataSample);
            }
            return dataset;
        }catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return null;
        } 
    }

    public static DataSample createDataSample(int rows, int columns, int outputDimension){
    /*
    Creates data sample object

    Parameters:
    - int rows: number of rows in input grid
    - int columns: number of columns in input grid
    - int output Dimension: number of output types

    Return:
    DataSample representing one sample of data
    */
        DataSample newDataSample = new DataSample(rows, columns, outputDimension);
        return newDataSample;
    }

    public static void parseTrainedWeights(TestingSettings netTestingSettings){
    /*
    Parses file of trained weights

    Parameters:
    - TestingSettings netTestingSettings: 
    */
        String trainedWeightsFileName = netTestingSettings.trainedWeightsFilePath;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(trainedWeightsFileName))){
            // Parse number of input nodes
            String line;
            line = reader.readLine();
            String[] parts = line.trim().split("\\s+");
            int numInputNodes = Integer.parseInt(parts[0]);

            // Parse number of output nodes
            line = reader.readLine();
            parts = line.trim().split("\\s+");
            int numOutputNodes = Integer.parseInt(parts[0]);

            // Parse theta threshold value
            line = reader.readLine();
            parts = line.trim().split("\\s+");
            netTestingSettings.thetaThreshold = Double.parseDouble(parts[0]);

            // Consume blank line
            reader.readLine();

            // Create weight data structures
            double[][] weightMatrix = new double[numInputNodes][numOutputNodes];
            double[] biasWeights = new double[numOutputNodes];

            // Parse node weights
            for (int rowNum = 0; rowNum < numInputNodes; rowNum++){
                line = reader.readLine();
                parts = line.trim().split("\\s+");
                for (int columnNum = 0; columnNum < numOutputNodes; columnNum++){
                    weightMatrix[rowNum][columnNum] = Double.parseDouble(parts[columnNum]);
                }
            }
            reader.readLine();
            netTestingSettings.trainedWeightMatrix = weightMatrix;

            // Consume blank line
            line = reader.readLine();

            // Parse bias weights
            parts = line.trim().split("\\s+");
            for (int columnNum = 0; columnNum < numOutputNodes; columnNum++){
                biasWeights[columnNum] = Double.parseDouble(parts[columnNum]);
            }
            netTestingSettings.trainedBiasWeights = biasWeights;
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } 
    }
}