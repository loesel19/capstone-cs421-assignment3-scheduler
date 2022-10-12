import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
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
    private HashMap<String, Integer> mapSections; //the map that will hold course sections
    //SUBPROGRAMS SUBPROGRAMS SUBPROGRAMS SUBPROGRAMS SUBPROGRAMS SUBPROGRAMS SUBPROGRAMS SUBPROGRAMS SUBPROGRAMS
    private void promptFilePath(){
        /**
         * @Name : promptFilePath
         * @Params : none
         * @Returns : a string containing the path to the file we want to read in.
         * @Purpose : the purpose of this method is to get the file path containing the courses we want to schedule.
         */
        System.out.println("Input the path to the file you would like to schedule");
    }
    public void startUp() throws SQLException, IOException, ClassNotFoundException {
        /**
         * @Name : startUp
         * @Params : none
         * @Returns : none
         * @Purpose : this method will handle the flow of user interaction for the program. We first make our
         *            databaseAccessObject, fileInteractionObject and schedulerObject. we then startUp our
         *            databaseAccessObject, and catch what that startup method returns. If dao.startUp returns false
         *            we have an empty schedule and need to prompt for a file path. We then instantiate mapSections,
         *            and progress to the middleFlow of the program.
         */
        databaseAccessObject = new DatabaseAccessObject();
        fileInteractionObject = new FileInteractionObject();
        schedulerObject = new SchedulerObject(databaseAccessObject, fileInteractionObject);
        if(!databaseAccessObject.startUp()){
            /* being here means that we have a fresh database now, and need to ask the user for a file path to load */
            this.mapSections = fileInteractionObject.getSectionMap();
            //after loading sections lets prompt for a file path until we get a good file name
            promptFilePath();
            while (!fileInteractionObject.instanciateBufferedReader(getInput())){
                System.out.println("File path given did not yield a readable file.");
                promptFilePath();
            }
            this.mapSections = schedulerObject.scheduleAll(fileInteractionObject.readAllFileLine(), this.mapSections);
        }
        if(this.mapSections == null)
            this.mapSections = fileInteractionObject.getSectionMap();
        middleFlow();
    }
    private void readInput(String strInput) throws IOException, SQLException, ClassNotFoundException {
        /**
         * @Name : readInput
         * @Params : strInput - the string that will determine the action the method takes.
         * @Returns : none
         * @Purpose : The purpose of this method is to see if strInput matches one our menu inputs we take the appropriate
         *            action, if not we just break
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
                //we want to read a new file in
                promptFilePath();
                if(fileInteractionObject.instanciateBufferedReader(getInput()))
                    this.mapSections = schedulerObject.scheduleAll(fileInteractionObject.readAllFileLine(), this.mapSections);
                else
                    System.out.println("Could not find the specified file.");
                break;
            case "X":
                //we want to close the program. First write out our section file then call endSession.
                fileInteractionObject.writeOutSectionFile(this.mapSections);
                endSession();
                break;
            default:
                break;
        }
    }
    private void reportMenu(){
        /**
         * @Name : reportMenu
         * @Params : none
         * @Returns : none
         * @Purpose : The purpose of this method is to print out a report menu for the user.
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
         * @Purpose : The purpose of this method is to print the input prompt for a new schedule file
         */
        System.out.println("Input 'S' to schedule another file.");
    }
    private void promptQuit(){
        /**
         * @Name : promptQuit
         * @Params : none
         * @Returns : none
         * @Purpose : the purpose of this method is to prompt the input command to close the application
         */
        System.out.println("Press 'X' to close the application.");
    }
    public void endSession() throws SQLException, ClassNotFoundException, IOException {
        /**
         * @Name : endSession
         * @Params :
         * @Returns :
         * @Purpose : the purpose of this method is to control how the program will run when the session is ending.
         *            We close out the interaction objects and point our objects to null for cleanup up. We call the
         *            dao.endSession method to handle how the database will shut down.
         */
        schedulerObject = null;
        fileInteractionObject.closeBufferedReader();
        fileInteractionObject = null;
        databaseAccessObject.endSession();
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
         * @Purpose : the purpose of this method is to control how the program runs in the interim. We continually
         *            print the menu to the user until they wish to exit and schedulerObject is pointed to null.
         */
        //now loop until user wishes to exit
        while(schedulerObject != null) {
            reportMenu();
            readInput(getInput());
        }
    }
}
