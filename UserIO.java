/*
 * This program he program implements a UI to get user input for training and testing
 * settings.
 * 
 * Authors:
 * - Cory Tamburrino
 * - David Kujawinski
 * - Dinh Troung
 * 
 * Date Last Modified: 4/3/2025
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class UserIO {

    /*
    Runs loop to control user IO interface
    */
    public static void welcomeToHopfield() {
        int selection = 0;
        while (selection != 3) {
            selection = getUserIntSelection();
        }
    }

    // Create scanner object
    static Scanner scanner = new Scanner(System.in);

    /*
    Collects user input on main menu selection, and performs
    action specified by user.

    Return:
    - an int representing the user's choice
    */
    public static int getUserIntSelection(){
        TrainingSettings netTrainingSettings = new TrainingSettings();
        TestingSettings netTestingSettings = new TestingSettings();
        int choice;
        while(true) {
            System.out.println("Welcome to our neural network - A Discrete Hopfield Net!");
            System.out.println("1) Enter 1 to train the net on a data file");
            System.out.println("2) Enter 2 to test the net on a data file");
            System.out.println("3) Enter 3 to quit");
            if (scanner.hasNextInt()){
                choice = scanner.nextInt();
                scanner.nextLine();
                switch(choice){
                // User selects training
                case 1:
                    netTrainingSettings = getTrainingSettings(netTrainingSettings);
                    netTrainingSettings.dataset = FileParser.parseDataFile(netTrainingSettings.trainingDataFilePath);
                    if (NeuralNet.train(netTrainingSettings)){
                        System.out.println("Sucessfully trained Neural Net\n");
                    }else{
                        System.out.println("Failed to execute training algorithim.");
                    }
                    return 1;
                // User selecets testing
                case 2:
                    netTestingSettings = getTestingSettings(netTestingSettings);
                    netTestingSettings.dataset = FileParser.parseDataFile(netTestingSettings.testingDataFilePath);
                    FileParser.parseTrainedWeights(netTestingSettings);
                    int testingResults[][] = NeuralNet.test(netTestingSettings);
                    //saveResultsToFile(testingResults, netTestingSettings.testingResultsOutputFilePath);
                    return 2;
                // User quits program
                case 3:
                    scanner.close();
                    return 3;
                default:
                    System.out.println("Invalid selection, please try again!");
                }
            } else {
                System.out.println("\nPlease enter a valid number!\n");
                scanner.next();
            }
        }
    }


    /*
    Prompts user for questions, and collects response to be saved in
    a TrainingSettings data structure.

    Parameters:
    - netTrainingSettings - data structure to hold user's specified settings info

    Return:
    - netTrainingSettings - data structure filled with user's specified settings info
    */
    public static TrainingSettings getTrainingSettings(TrainingSettings netTrainingSettings){
        // Get training data file name
        String trainingFilePrompt = "\nEnter the training file name: ";
        String trainingFilePath = getValidFile(trainingFilePrompt);
        netTrainingSettings.trainingDataFilePath = trainingFilePath;

        // Get file name to save trained weights to
        String trainedWeightOutputPrompt = "\nEnter a file name to save the trained weight values:";
        String trainedWeightOutputFile = getValidFilename(trainedWeightOutputPrompt);
        netTrainingSettings.trainedWeightsFile = trainedWeightOutputFile;

        return netTrainingSettings;
    }

    /*
    Prompts user for question to and collects an response for a specified file

    Parameters:
    - prompt - prompt to display to user

    Return:
    - an String representing user's selection
    */
    private static String getValidFilename(String prompt){
        String filename;
        do {
            System.out.println(prompt);
            filename = scanner.nextLine().trim();
            if (!isValidFilename(filename)) {
                System.out.println("Invalid filename, Please try again!");
            }
        } while (!isValidFilename(filename));
        return filename + ".txt";
    }

    /*
    Checks if user's specified file name is a valid Linux filename

    Parameters:
    - filename - name of file specified by user

    Return:
    - boolean representing if filename is valid
    */
    private static boolean isValidFilename(String filename){
        if (filename == null || filename.isEmpty()) return false;
        if (!filename.matches("^[^/]*$")) return false;
        if (filename.equals(".") || filename.equals("..")) return false;
        if (filename.length() > 255) return false;
        return true;
    }

    /*
    Prompts user for questions, and collects response to be saved in
    a TestingSettings data structure.

    Parameters:
    - netTestingSettings - data structure to hold user's specified settings info

    Return:
    - netTestingSettings - data structure filled with user's specified settings info
    */
    private static TestingSettings getTestingSettings(TestingSettings netTestingSettings){
        // Get trained weights file name
        String trainedWeightsPrompt = "\nEnter the trained net weight file name:";
        String trainedWeightFilePath = getValidFile(trainedWeightsPrompt);
        netTestingSettings.trainedWeightsFilePath = trainedWeightFilePath;

        // Get testing data file name
        String testingFilePrompt = "\nEnter the testing file name: ";
        String testingFilePath = getValidFile(testingFilePrompt);
        netTestingSettings.testingDataFilePath = testingFilePath;
        
        // Get file name to save testing results to
        String testingResultsPrompt = "\nEnter a file name to save the testing results:";
        String testingResultsOutputFile = getValidFilename(testingResultsPrompt);
        netTestingSettings.testingResultsOutputFilePath = testingResultsOutputFile;

        return netTestingSettings;
    }

    /*
    Prompts user with question, and collects String response.

    Parameters:
    - prompt - prompt to display to user

    Return:
    - String representing filename specififed by user
    */
    private static String getValidFile(String prompt) {
        File file = new File("");
        String filePath;
        System.out.println(prompt);
        do{
            filePath = scanner.nextLine().trim() + ".txt";
            file = new File(filePath);

            if(!file.exists()){
                System.out.println("\nCould not find file, please try again!\n");
                System.out.println(prompt);
            }
        } while(file.exists() == false);
        return filePath;
    }

    // public static void saveResultsToFile(int[][] classifications, String testingResultsOutputFilePath){
    // /*
    // Saves classification results from testing to output file specified by user

    // Parameters:
    // - int[][] classifications: Matrix representing classification results
    // - String testingResultsOutputFilePath: filepath of output file specified by user
    // */
    //     try (BufferedWriter writer = new BufferedWriter(new FileWriter(testingResultsOutputFilePath))) {
    //         Label[] actualOutput = Label.values();
    //         int labelIncrement = 0;
    //         for (int[] row : classifications){
    //             //identifies the label it was trying to classify
    //             Label classifiedLabel = Label.getLabel(row);
    //             //prints out the expected result
    //             writer.write("Actual:\n"+actualOutput[labelIncrement].toString());
    //             writer.newLine();
    //             //prints out what it was classified as, and Undecided if the vector was not identified
    //             if(classifiedLabel==null){
    //                 writer.write("Classified:\nUndecided\n"+Label.arrayToString(row));
    //             }
    //             else{
    //                 writer.write("Classified:\n"+classifiedLabel.toString());
    //             }
    //             //increments the labels
    //             if(labelIncrement==6)
    //                 labelIncrement = 0;
    //             else
    //                 labelIncrement++;
    //             writer.newLine();
    //             writer.newLine();
    //         }
    //         writer.newLine();
    //         writer.newLine();
    //         writer.close();
    //         System.out.println("Results saved successfully to " + testingResultsOutputFilePath + "\n");
    //     } catch (IOException e){
    //         e.printStackTrace();
    //     }
    // }

        //     /*
        // * a method that turns an array into a string without brackets or commas, with each element being space separated
        // *
        // * Parameters:
        // * int[] array - the array to be transformed
        // *
        // * Returns:
        // * a String of the transformed array
        // *
        // * */
        // public static String arrayToString(int[] array){
        //     StringBuilder sb = new StringBuilder();
        //     for(int i=0; i<array.length; i++){
        //         sb.append(array[i]+" ");
        //     }
        //     return sb.toString();
        // }

        // /*Override the toString such that it prints out in the required format
        // *
        // * Parameters:
        // * None
        // *
        // * Returns:
        // * a String of the name followed by a new line and the array transformed by the arrayToString() method
        // *
        // * */
        // @Override
        // public String toString(){
        //     return this.name()+"\n"+Label.arrayToString(this.output);
        // }
}


