import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.Buffer;

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
}
