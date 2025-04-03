/*
 * This program he program implements a UI to get user input for training and testing
 * settings.
 * 
 * Authors:
 * - Cory Tamburrino
 * - David Kujawinski
 * - Dinh Troung
 * 
 * Date Last Modified: 3/7/2025
 */

import java.io.File;
import java.util.Scanner;

public class UserIO {

    /*
    Runs loop to control user IO interface
    */
    public static void welcomeToPerceptron() {
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
            System.out.println("Welcome to our first neural network - A Perceptron Net!");
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
                    int numEpochs = NeuralNet.train(netTrainingSettings);
                    if (numEpochs > 0){
                        System.out.println("Training convereged after " + numEpochs + " epochs.\n");
                    }else{
                        System.out.println("Failed to execute training algorithim.");
                    }
                    return 1;
                // User selecets testing
                case 2:
                    netTestingSettings = getTestingSettings(netTestingSettings);
                    netTestingSettings.dataset = FileParser.parseDataFile(netTestingSettings.testingDataFilePath);
                    FileParser.parseTrainedWeights(netTestingSettings);
                    NeuralNet.test(netTestingSettings);
                    //System.out.println(testingResults);
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

        // Get weight initialization selection
        String weightPrompt = "\nEnter 0 to initialize weights to 0, enter 1 to initialize weights to random values between -0.5 and 0.5:";
        int weightChoice = getIntInput(weightPrompt, 0, 1);
        netTrainingSettings.setWeightsToZero = (weightChoice == 0);

        // Get maximum epochs
        String epochPrompt = "\nEnter the maximum number of training epochs:";
        int epochChoice = getIntInput(epochPrompt, 1, 10000);
        netTrainingSettings.maxEpochs = epochChoice;

        // Get file name to save trained weights to
        String trainedWeightOutputPrompt = "\nEnter a file name to save the trained weight values:";
        String trainedWeightOutputFile = getValidFilename(trainedWeightOutputPrompt);
        netTrainingSettings.trainedWeightsFile = trainedWeightOutputFile;

        // Get learning rate (alpha)
        String alphaPrompt = "\nEnter the learning rate alpha from 0 to 1 but not including 0:";
        double alphaChoice = getDoubleInput(alphaPrompt, 0.1, 1.0);
        netTrainingSettings.learningRate = alphaChoice;

        // Get threshold (theta)
        String thetaPrompt = "\nEnter the threshold theta:";
        double thetaChoice = getDoubleInput(thetaPrompt, -10000.0, 10000.0);
        netTrainingSettings.thetaThreshold = thetaChoice;

        // Get weight change threshold
        String weightThresholdPrompt = "\nEnter the threshold to be used for measuring weight changes:";
        double weightThresholdChoice = getDoubleInput(weightThresholdPrompt, 0.0000001, 10000000.0);
        netTrainingSettings.weightChangeThreshold = weightThresholdChoice;

        return netTrainingSettings;
    }

    /*
    Prompts user for question to and collects an int response

    Parameters:
    - prompt - prompt to display to user
    - min - minimum int selection
    - max - maximum int selection

    Return:
    - an int representing user's selection
    */
    private static int getIntInput(String prompt, int min, int max) {
        int input;
        while (true) {
            System.out.println(prompt);
            if (scanner.hasNextInt()) {
                input = scanner.nextInt();
                scanner.nextLine();  // Consume the newline
                if (input >= min && input <= max) {
                    return input;  // Return the valid input
                } else {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                }
            } else {
                System.out.println("Invalid input, please enter a valid integer.");
                scanner.next();  // Consume the invalid input
            }
        }
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
    Prompts user for question to and collects an double response

    Parameters:
    - prompt - prompt to display to user
    - min - minimum double selection
    - max - maximum double selection

    Return:
    - a double representing user's selection
    */
    private static double getDoubleInput(String prompt, double min, double max) {
        double input;
        while (true){
            System.out.println(prompt);
            if(scanner.hasNextFloat()){
                input = scanner.nextFloat();
                scanner.nextLine(); // Consume new line
                if(input > min && input <= max){
                    return input;
                } else{
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                }
            } else {
                System.out.println("Invalid input, please enter a valid input.");
                scanner.next();  // Consume the invalid input
            }
        }
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

        // Get training data file name
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
}
