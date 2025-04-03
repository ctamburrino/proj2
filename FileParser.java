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
            for (int i = 0; i < 3; i++){
                line = reader.readLine();
                String[] parts = line.trim().split("\\s+");
                int value = Integer.parseInt(parts[0]);

                switch(i) {
                    case 0: inputRows = value;
                    case 1: inputColumns = value;
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
                
                // Create data sample and store in dataset
                DataSample newDataSample = createDataSample(inputRows, inputColumns);
                newDataSample.setPixelArray(pixelArray);
                dataset.add(newDataSample);
            }
            return dataset;
        }catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return null;
        } 
    }

    public static DataSample createDataSample(int rows, int columns){
    /*
    Creates data sample object

    Parameters:
    - int rows: number of rows in input grid
    - int columns: number of columns in input grid
    - int output Dimension: number of output types

    Return:
    DataSample representing one sample of data
    */
        DataSample newDataSample = new DataSample(rows, columns);
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

            // Consume blank line
            reader.readLine();

            // Create weight data structures
            int[][] weightMatrix = new int[numInputNodes][numInputNodes];

            // Parse node weights
            for (int rowNum = 0; rowNum < numInputNodes; rowNum++){
                line = reader.readLine();
                parts = line.trim().split("\\s+");
                for (int columnNum = 0; columnNum < numInputNodes; columnNum++){
                    weightMatrix[rowNum][columnNum] = Integer.parseInt(parts[columnNum]);
                }
            }
            reader.readLine();
            netTestingSettings.trainedWeightMatrix = weightMatrix;
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        } 
    }
}