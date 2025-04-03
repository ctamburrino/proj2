/*
 * This program is the main driver of the discrete hopfield neural network.  The program
 * implements a UI to get user input, loads stored information from training 
 * data files, creates and trains a neural network by modifying and saving weight values.
 * 
 * It also tests new datasets using saved trained weight files to associate the new data
 * samples based on the training data it was fed.
 * 
 * Authors:
 * - Cory Tamburrino
 * - David Kujawinski
 * - Dinh Troung
 * 
 * Date Last Modified: 4/3/2025
 */

public class Main {
    public static void main(String[] args){
        UserIO.welcomeToHopfield();
    }
}
