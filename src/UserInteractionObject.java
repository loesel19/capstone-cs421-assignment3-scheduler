import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Scanner;

public class UserInteractionObject {
    /**
     * File Name :  UserInteractionObject
     * File Author : Andrew A. Loesel
     * Part of Project : CS 421 Assignment 3 - class scheduler
     * Organization : Saginaw Valley State University
     * Professor : Scott D. James
     * File Purpose : This Object is going to handle the user interaction for this program. It will ensure that
     *                the program flows as intended.
     */
    private DatabaseAccessObject databaseAccessObject; //the object that allows us to interact with our database
    private FileInteractionObject fileInteractionObject; //the object that allows us to read/write from files
    private SchedulerObject schedulerObject; //the object that handles schedule functionality
    //SUBPROGRAMS SUBPROGRAMS SUBPROGRAMS SUBPROGRAMS SUBPROGRAMS SUBPROGRAMS SUBPROGRAMS SUBPROGRAMS SUBPROGRAMS
    private void promptFilePath(){
        /**
         * @Name : promptFilePath
         * @Params : none
         * @Returns : a string containing the path to the file we want to read in.
         * @Purpose : the purpose of this method is to get the file path containing the courses we want to schedule.
         */
        System.out.println("Input the name of the file you would like to schedule");
    }
    public void startUp() throws SQLException, IOException, ClassNotFoundException {
        /**
         *
         */
        databaseAccessObject = new DatabaseAccessObject();
        fileInteractionObject = new FileInteractionObject();
        databaseAccessObject.startUp();
        promptFilePath();
        fileInteractionObject.instanciateBufferedReader(getInput());
        schedulerObject = new SchedulerObject(databaseAccessObject, fileInteractionObject);
        schedulerObject.scheduleAll(fileInteractionObject.readAllFileLine());
        middleFlow();
    }
    private void readInput(String strInput) throws IOException, SQLException, ClassNotFoundException {
        /**
         * @Name : readInput
         * @Params : strInput - the string that will determine the action the method takes.
         * @Returns : none
         * @Purpose :
         */
        switch (strInput){
            case "D":
                schedulerObject.printReportDayTime();
                break;
            case "P":
                schedulerObject.printReportProfessor();
                break;
            case "C":
                schedulerObject.printReportCourse();
                break;
            case "S":
                promptFilePath();
                fileInteractionObject.instanciateBufferedReader(getInput());
                schedulerObject.scheduleAll(fileInteractionObject.readAllFileLine());
                break;
            case "X":
                endSession();
                break;
            default:
                break;
        }
    }
    private void reportMenu(){
        /**
         * @Name : viewReporst
         * @Params : none
         * @Returns : none
         * @Purpose :
         */
        System.out.println("Press 'D' to see current schedule by Day/Time.");
        System.out.println("Press 'P' to see current schedule by Professor.");
        System.out.println("Press 'C' to see current schedule by Course Name.");
        System.out.println("Press 'S' to schedule another file.");
        promptQuit();
    }
    private void newFileMenu(){
        /**
         * @Name : newFileMenu
         * @Params : none
         * @Returns : none
         * @Purpose :
         */
        System.out.println("Input 'S' to schedule another file.");
    }
    private void promptQuit(){
        /**
         * @Name : promptQuit
         * @Params : none
         * @Returns : none
         * @Purpose :
         */
        System.out.println("Press 'X' to close the application.");
    }
    public void endSession() throws SQLException, ClassNotFoundException {
        /**
         * @Name : endSession
         * @Params :
         * @Returns :
         * @Purpose :
         */
        databaseAccessObject.endSession();
        schedulerObject = null;
        fileInteractionObject = null;
        databaseAccessObject = null;
    }
    private String getInput(){
        /**
         * @Name : getInput
         * @Params : none
         * @Returns : none
         * @Purpose : the purpose of this method is to get an input from the user and return it.
         */
        Scanner scanner = new Scanner(System.in);
        return scanner.next().toUpperCase(Locale.ROOT);
    }
    public void middleFlow() throws IOException, SQLException, ClassNotFoundException {
        /**
         * @Name : middleFlow
         * @Params : none
         * @Returns : none
         * @Purpose :
         */
        promptFilePath();
        fileInteractionObject.instanciateBufferedReader(getInput());
        schedulerObject.scheduleAll(fileInteractionObject.readAllFileLine());
        //now loop until user wishes to exit
        while(schedulerObject != null) {
            reportMenu();
            readInput(getInput());
        }
    }
}
