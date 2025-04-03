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
    private int[] pixelArray;

    // Constructor
    public DataSample(int rows, int columns){
        this.rowDimension = rows;
        this.columnDimension = columns;
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

    // Setters
    public void setPixelArray(int[] pixelArray){
        this.pixelArray = pixelArray;
    }
}
