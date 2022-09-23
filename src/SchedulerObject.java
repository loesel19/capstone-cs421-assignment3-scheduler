import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.*;

public class SchedulerObject {
    /**
     * File Name : SchedulerObject
     * File Author : Andrew A. Loesel
     * Part of Project : CS 421 Assignment 3 - class scheduler
     * Organization : Saginaw Valley State University
     * Professor : Scott D. James
     * File Purpose : This Object is going to handle the Scheduling for our program. We will build up our schedule algorithm
     * here.
     */
    //GLOBAL VARIABLES GLOBAL VARIABLES GLOBAL VARIABLES GLOBAL VARIABLES GLOBAL VARIABLES
    private DatabaseAccessObject databaseAccessObject; //the object we have created to deal with our database interactions.
    private FileInteractionObject fileInteractionObject;  //the object we have created to deal with our file interactions.
    private static final String One_Credit_Hour_Start_Time_Pattern = "([0-1]?[0-3]|9):00"; //the regex pattern used to schedule 1 credit hour classes.

    public SchedulerObject(DatabaseAccessObject databaseAccessObject, FileInteractionObject fileInteractionObject) {
        /**
         * Name : SchedulerObject
         * Params : databaseAccessObject - the database interaction object created in the caller Class. The caller class
         *                                 will ensure that this is not null.
         *          fileInteractionObject - the file interaction object created in the caller Class. The caller class will
         *                                  ensure that this is not null.
         * Purpose : This is the paramaterized constructor for SchedulerObject. This instanciates the SchedulerObjects
         *           database and file interaction objects.
         */
        this.databaseAccessObject = databaseAccessObject;
        this.fileInteractionObject = fileInteractionObject;
    }

    private String changeDays(String strDays){
        /**
         * Name : changeDays
         * Params : strDays - the current days we are trying to schedule on
         * Returns : strNewDays - the new days we will try to schedule on.
         * Purpose : The purpose of this function is to go through every possible day(s) and switch it to the next day(s)
         */
        String strNewDays = "";
        switch(strDays){
            case "M":
                strNewDays = "T";
                break;
            case "T":
                strNewDays = "W";
                break;
            case "W":
                strNewDays = "R";
                break;
            case "R":
            case "TR":
                strNewDays = "F";
                break;
            case "MR" :
                strNewDays = "TR";
                break;
            default:
                strNewDays = "-1";
                break;
        }
        return strNewDays;
    }
    private boolean scheduleFourCredit(objFileData fileData) {
        /**
         * Name : scheduleOne
         * Params : schedule - an objSchedule data model containing the data on the course we want to try to schedule.
         * Returns : none
         * Purpose : This method tries to
         */
    }

    private boolean scheduleThreeCredit(objFileData fileData) {

    }

    private boolean scheduleTwoCredit(objFileData fileData) {

    }

    private boolean scheduleOneCredit(objFileData fileData) {

    }
    public void Schedule(objFileData fileData) throws SQLException, ClassNotFoundException {

        boolean blnScheduled = false;
        objCourse course = databaseAccessObject.getCourse(fileData.getStrCourseName());


            switch (course.getIntCreditHours()){
                case 1:
                    blnScheduled = scheduleOneCredit(fileData);
                    break;
                case 2:
                    blnScheduled = scheduleTwoCredit(fileData);
                    break;
                case 3:
                    blnScheduled = scheduleThreeCredit(fileData);
                    break;
                case 4:
                    blnScheduled = scheduleFourCredit(fileData);
                    break;
                default:
                    System.out.println("Hit default in schedule method.");
                    return;
            }

    }

    public void scheduleAll(ArrayList<objFileData> lstFileData) throws SQLException, ClassNotFoundException {
        for(objFileData d : lstFileData){
            //try to schedule the course.
            Schedule(d);
        }
    }


    private objFileData getNextTime(objFileData fileData) throws SQLException, ClassNotFoundException {
        /**
         * Name : getNextTime
         * Params : fileData - an objFileData object representing the course section that we want to schedule.
         */
        objCourse course = databaseAccessObject.getCourse(fileData.getStrCourseName());
        Pattern pattern = Pattern.compile(One_Credit_Hour_Start_Time_Pattern);
        switch (course.getIntCreditHours()) {
            case 1:
                //get just the hour of our time by parsing the integer in our startTime string before the :
                int intNewTime = Integer.parseInt(fileData.getStrStartTime().split(":")[0]);
                intNewTime++; //add an hour
                String strNewTime = intNewTime + ":00";
                /* use the regex pattern we have to see if a 1 credit hour class can be scheduled */
                Matcher matcher = pattern.matcher(strNewTime);
                if(!matcher.find()){
                    //can not schedule on this day, try the next day.

                }
                fileData.setStrStartTime(strNewTime);
                break;
            case 2:

                break;
            case 3:

                break;
            case 4:

                break;
            default:

                break;

        }

        return fileData;
    }
}