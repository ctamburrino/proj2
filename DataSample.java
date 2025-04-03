/*
 * This program is a data structure to hold sample data.
 * 
 * Authors:
 * - Cory Tamburrino
 * - David Kujawinski
 * - Dinh Troung
 * 
 * Date Last Modified: 3/7/2025
 */

public class DataSample {
    // Instance Variables
    private int rowDimension;
    private int columnDimension;
    private int outputDimension;
    private char label;
    private int[] pixelArray;
    private int[] outputVector;

    // Constructor
    public DataSample(int rows, int columns, int outputDimension){
        this.rowDimension = rows;
        this.columnDimension = columns;
        this.outputDimension = outputDimension;
    }

    // Getters
    public char getLabel(){
        return label;
    }

    public int[] getPixelArray(){
        return pixelArray;
    }

    public int getRowDimension(){
        return rowDimension;
    }

    public int getColumnDimension(){
        return columnDimension;
    }

    public int getOutputDimension(){
        return outputDimension;
    }

    public int[] getOutputVector(){
        return outputVector;
    }

    // Setters
    public void setPixelArray(int[] pixelArray){
        this.pixelArray = pixelArray;
    }
    
    public void setOutputVector(int[] outputVector){
        this.outputVector = outputVector;
    }

    public void setLabel(char letter){
        this.label = letter;
    }
}
