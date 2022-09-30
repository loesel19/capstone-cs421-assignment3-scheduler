import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
    private static final String MONDAY_TO_THURSDAY_START_TIME_4_CREDIT = "8:30"; //the first time a class can start on monday or thursday
    private static final String MONDAY_TO_THURSDAY_END_TIME_4_CREDIT = "4:30";//the latest time a 4 credit course can go until on mon-thur
    private static final String MONDAY_TO_THURSDAY_START_TIME_3_CREDIT = "9:00";//the first time a 3 credit course may be scheduled on mon-thurs
    private static final String MONDAY_TO_THURSDAY_END_TIME_3_CREDIT = "4:30";//the last time a 3 credit course may be scheduled on mon-thurs


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

//    private String changeDays(String strDays){
//        /**
//         * Name : changeDays
//         * Params : strDays - the current days we are trying to schedule on
//         * Returns : strNewDays - the new days we will try to schedule on.
//         * Purpose : The purpose of this function is to go through every possible day(s) and switch it to the next day(s)
//         */
//        String strNewDays = "";
//        switch(strDays){
//            case "M":
//                strNewDays = "T";
//                break;
//            case "T":
//                strNewDays = "W";
//                break;
//            case "W":
//                strNewDays = "R";
//                break;
//            case "R":
//            case "TR":
//                strNewDays = "F";
//                break;
//            case "MR" :
//                strNewDays = "TR";
//                break;
//            default:
//                strNewDays = "-1";
//                break;
//        }
//        return strNewDays;
//    }
    private String[] getDaysSeparate(String strDays){
        /**
         * Name : getDaysSeparate
         * Prams : strDays - the days we are trying to separate
         * Returns : String[] - will contain either two separate days or just strDays as the only element of an array
         * Purpose : This method will enter a switch case for strDays, and if strDays is MW or TR will split this into
         *           an array with the days as two separate elements and return an array of string.
         */
        switch (strDays){
            case "MW":
                return new String[]{"M", "W"};
            case "TR":
                return new String[]{"T", "R"};
            default:
                return new String[]{strDays};
        }
    }
    private  LocalTime getTimeFromString(String strTime){
        /**
         * Name : getTimeFromString
         * Params : strTime - the time we want to get in string format, ex. "9:00" or "11:00"
         * Returns : time - a LocalTime object representation of our string
         * Purpose : The purpose of this method is to represent a string that supposed to be a time as a LocalTime object
         *           and return that object.
         */
        LocalTime time = LocalTime.of(Integer.parseInt(strTime.split(":")[0]), Integer.parseInt(strTime.split(":")[1]));
        return time;
    }
    private ArrayList<Integer> getScheduledCourseInTime(String strDays, String strStartTime, String strEndTime) throws SQLException, ClassNotFoundException {
        /**
         * Name : getScheduledCourseInTime
         * Params : strDays - the Days we want to check the schedule on.
         *          strStartTime - the start time we want to schedule a class for
         *          strEndTime - the end time we want to schedule a class for
         * Returns : lstAccountedForTUIDS - an arraylist containing the Scheudle_Table TUIDS for courses that are scheduled
         *                                  during the desired time slot.
         * Purpose : The purpose of this function is to get a list containing TUIDS for all courses whose time day combinitation
         *           is in conflict with the time slot of the class we want to schedule. we do this by first getting all courses
         *           scheduled on the days of our course. We then loop through each scheduled course and check if it conflicts with
         *           the course we want to schedule. While looping we maintain our arraylist of TUIDs as integers so that we can
         *           make sure we do not 'double dip' and add the same class twice. If we want to schedule a course on two day
         *           we then iterate through again to check for any courses on the other day that we did not account for already.
         *           Lastly we return the arrayList lstScheduledTUIDS.
         *           */
        ArrayList<Integer> lstAccountedForTUIDS = new ArrayList<>();
        HashMap<String, ArrayList<objSchedule>> mapScheduled = databaseAccessObject.getAllScheduled();
        String strDaysSeparate[] = getDaysSeparate(strDays);
        for(int i = 0; i < strDaysSeparate.length; i++){
            ArrayList<objSchedule> lstScheduledClassesOnDay = mapScheduled.get(strDaysSeparate[i]);
            LocalTime timeStart = getTimeFromString(strStartTime);
            LocalTime timeEnd = getTimeFromString(strEndTime);
            for(objSchedule s : lstScheduledClassesOnDay){
                LocalTime timeScheduledStart = getTimeFromString(s.getStrStartTime());
                LocalTime timeScheduledEnd = getTimeFromString(s.getStrEndTime());
                //first check if timeStart is after timeCurrentStart, and before timeCurrentEnd
                //TODO : ask if doing an || in the if statement is more readable here. (or shortens our method)
                if(timeStart.isAfter(timeScheduledStart) && timeStart.isBefore(timeScheduledEnd)){
                    //WE NEED TO COUNT THIS AS A CLASS IN OUR SLOT
                    if(!lstAccountedForTUIDS.contains(s.getIntTUID())){
                        lstAccountedForTUIDS.add(s.getIntTUID());
                    }
                    continue;
                }
                //next see if start time or end times are equal
                if(timeScheduledStart.equals(timeStart) || timeScheduledEnd.equals(timeEnd)){
                    //WE NEED TO COUNT THIS AS A CLASS IN OUR SLOT
                    if(!lstAccountedForTUIDS.contains(s.getIntTUID())){
                        lstAccountedForTUIDS.add(s.getIntTUID());
                    }
                    continue;
                }
                //next see if timeEnd is between start and end time
                if(timeEnd.isAfter(timeScheduledEnd) && timeEnd.isBefore(timeScheduledEnd)){
                    //NEED TO COUNT IN OUR SLOT
                    if(!lstAccountedForTUIDS.contains(s.getIntTUID())){
                        lstAccountedForTUIDS.add(s.getIntTUID());
                    }
                    continue;
                }
                //next case we need to account for is if the class we try to schedule starts before a scheduled course and ends after a scheduled course
                if(timeScheduledStart.isAfter(timeStart) && timeScheduledStart.isBefore(timeEnd)){
                    //the current class that we are looking at starts after the class we are looking to add, but its start time is within the new classes endtime
                    if(!lstAccountedForTUIDS.contains(s.getIntTUID())){
                        lstAccountedForTUIDS.add(s.getIntTUID());
                    }
                    continue;
                }
                //lastly we nned to check if a scheduled course starts before and ends after our desired times
                if(timeScheduledEnd.isAfter(timeStart) && timeScheduledEnd.isBefore(timeEnd)){
                    //the class we are trying to scheduled starts before an already scheduled class ends.
                    if(!lstAccountedForTUIDS.contains(s.getIntTUID())){
                        lstAccountedForTUIDS.add(s.getIntTUID());
                    }
                    continue;
                }

            }
        }
        return  lstAccountedForTUIDS;
    }


    private void insertSchedule(objFileData fileData, int intAlreadyScheduledCourses) throws SQLException, ClassNotFoundException {
        ArrayList<objClassroom> lstRooms = databaseAccessObject.getAllClassrooms();
        String strClassroomNameNeeded = "";
        if (intAlreadyScheduledCourses == 0){
            strClassroomNameNeeded = "A";
        }
        if(intAlreadyScheduledCourses == 1){
            strClassroomNameNeeded = "B";
        }
        if(intAlreadyScheduledCourses == 2){
            strClassroomNameNeeded = "C";
        }

    }
    private void scheduleFourCredit(objFileData fileData) throws SQLException, ClassNotFoundException {
        /**
         * Name : scheduleFourCredit
         * Params : fileData - an object containing the data for the course we want to schedule from our input file
         * Returns : none
         * Purpose :
         */
        ArrayList lstScheduledTUIDS = getScheduledCourseInTime(fileData.getStrDays(), fileData.getStrStartTime(), fileData.getStrEndTime());
        if(lstScheduledTUIDS.size() < 3){
            insertSchedule(fileData, lstScheduledTUIDS.size());
        }
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

        return false;
    }
    public void Schedule(objFileData fileData) throws SQLException, ClassNotFoundException {


        objCourse course = databaseAccessObject.getCourse(fileData.getStrCourseName());


            switch (course.getIntCreditHours()){
                case 1:
                    scheduleOneCredit(fileData);
                    break;
                case 2:
                    scheduleTwoCredit(fileData);
                    break;
                case 3:
                    scheduleThreeCredit(fileData);
                    break;
                case 4:
                    scheduleFourCredit(fileData);
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


}