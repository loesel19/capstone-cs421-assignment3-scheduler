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
         * Name : scheduleFourCredit
         * Params : fileData - the object containing the data about the class we want to try and schedule.
         * Returns : blnScheduled - true -> we scheduled the class, false -> class not scheduled
         * Purpose : This method tries to schedule a four credit hour class. We will Check our scheduled table to see
         *           if the appropriate time slot is clear based on our scheduling criteria. If no spot available we
         *           will change the time or day depending on scheduling criteria and try again.
         * Notes :
         */
        boolean blnScheduled = false;

        return blnScheduled;

    }

    private boolean scheduleThreeCredit(objFileData fileData) {

        boolean blnScheduled = false;

        return blnScheduled;
    }

    private boolean scheduleTwoCredit(objFileData fileData) {

        boolean blnScheduled = false;

        return blnScheduled;
    }

    private boolean scheduleOneCredit(objFileData fileData) throws SQLException, ClassNotFoundException {
        /**
         * Name : scheduleOneCredit
         * Params : fileData - a data model object that represents the class that we want to schedule
         * Returns : blnScheduled - true -> we were able to schedule the class, false -> class not scheduled.
         * Purpose : The purpose of this method is to try and schedule a one credit hour course based on our
         *           scheduling criteria. If the first slot is not open we change the time and days according to the
         *           scheduling criteria. Then we return a boolean that represents if we could schedule the course at all
         *           or not.
         * Notes :
         */
        //the first thing we want to do is see if we can schedule this course during the originally designated time slot
        DatabaseAccessObject databaseAccessObject = new DatabaseAccessObject();
        objSchedule schedule = databaseAccessObject.getOneScheduledCourse(fileData.getStrStartTime(), fileData.getStrEndTime(),
                fileData.getStrDays());
        ArrayList<objClassroom> lstClassrooms = databaseAccessObject.getAllClassrooms();
        if(schedule == null){
                if (databaseAccessObject.addSchedule(fileData)) {
                     return true;
                }
        }else{
            while(!fileData.getStrDays().equals("-1")){
                while(!fileData.getStrStartTime().equals("-1")){
                    fileData = getNextTime(fileData);
                    schedule = databaseAccessObject.getOneScheduledCourse(fileData.getStrStartTime(), fileData.getStrEndTime(),
                            fileData.getStrDays());
                    if(schedule == null) {
                        databaseAccessObject.addSchedule(fileData);
                        return true;
                    }
                }
                fileData.setStrDays(changeDays(fileData.getStrDays()));
            }
        }

        return false;
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
                    System.out.println("Hit default in schedule method. Somehow");
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
                int intNewStartTime = Integer.parseInt(fileData.getStrStartTime().split(":")[0]);
                intNewStartTime++; //add an hour
                String strNewStartTime = intNewStartTime + ":00";
                //now get new end time
                int intNewEndTime = Integer.parseInt(fileData.getStrEndTime().split(":")[0]);
                intNewEndTime++;
                String strNewEndTime = intNewEndTime + ":00";
                /* use the regex pattern we have to see if a 1 credit hour class can be scheduled */
                Matcher matcher = pattern.matcher(strNewStartTime);
                if(!matcher.find()){
                    //can not schedule on this day, try the next day.
                    fileData.setStrEndTime("-1");
                    fileData.setStrStartTime("-1");
                    break;
                }
                fileData.setStrStartTime(strNewStartTime);
                fileData.setStrEndTime(strNewEndTime);
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