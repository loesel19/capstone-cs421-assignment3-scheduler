import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;

public class FileInteractionObject {
    /**
     * File Name : FileInteractionObject
     * File Author : Andrew A. Loesel
     * Part of Project : CS 421 Assignment 3 - class scheduler
     * Organization : Saginaw Valley State University
     * Professor : Scott D. James
     * File Purpose : The purpose of this file is to handle our file interactions in the application. We will mostly need
     *                to read in data from a file in a specified format. We will use an object to get this data.
     */

    private String strFilePath;
    BufferedReader bufferedReader;

    public FileInteractionObject(String strFilePath){
        /**
         * Name : FileInteractionObject
         * Params : strFilePath - the path to the file that we will be reading.
         * Purpose : This is the paramaterized constructor for the FileInteractionObject. We
         *           simply take an input file path and set this objects strFilePath field to that file path.
         */
        this.strFilePath = strFilePath;
    }
    public boolean instanciateBufferedReader() throws FileNotFoundException {
        /**
         * Name : instanciateBufferedReader
         * Params : none.
         * Returns : boolean - true -> successfully instanced bufferedReader, false -> failure
         */
        try {
            bufferedReader = new BufferedReader(new FileReader(strFilePath));
            return true;
        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
    public objFileData readFileLine() throws IOException {
        /**
         * Name : readFileLine
         * Params : none.
         * Returns : fileData - an object containing the data items that we will try to extract from this line of the file.
         * Purpose : The purpose of this function is to try reading 1 line of the file into an objFileData object which models
         *           the data from that line, and returning that object.
         * Notes :
         */
        objFileData fileData = null;

        String line = bufferedReader.readLine();
        String[] strValues = line.split("\t");
        try{
            fileData = new objFileData(strValues[0], strValues[1], strValues[2], strValues[3], strValues[4]);
        } catch (Exception ex){
            return null;
        }

        return fileData;
    }
    public ArrayList<objFileData> readAllFileLine() throws IOException {
        /**
         * Name : readAllFileLine
         * Params : none.
         * Returns : lstAllFileLine - an arraylist containing objFileData object data models of each line of code.
         * Purpose : The purpose of this method is to return the data objects models of each line of the file in a single
         *           arrayList.
         * Notes :
         */
        ArrayList<objFileData> lstFileData = new ArrayList<>();
        String line; //a line of data from our file
        //we want to read a line and check that it is not null. if line is null we are at the end of the file and will move on.
        while((line = bufferedReader.readLine()) != null){
            String[] strValues = line.split("\t");
            try{
                //add a new objFileData to the list with this data.
                lstFileData.add(new objFileData(strValues[0], strValues[1], strValues[2], strValues[3], strValues[4]));
            } catch (Exception ex){
                //if a non null line fails to read return null
                return null;
            }
        }

        return lstFileData;
    }
}
