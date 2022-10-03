import com.sun.source.tree.WhileLoopTree;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
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
    private static final String SCHEDULE_TABLE_STRING = "Schedule_Table"; //the name of our schedule table in the database
    private static final LocalTime FIRST_SCHEDULE_TIME_MR = LocalTime.of(8, 30); //the first time a course can start at on Monday through Thursday
    private static final LocalTime LAST_SCHEDULE_TIME_MR = LocalTime.of(16, 30); //the last time a course can go until on Monday through Thursday
    private static final LocalTime FIRST_SCHEDULE_TIME_F = LocalTime.of(8, 30); //the first time a course can be scheduled on friday
    private static final LocalTime LAST_SCHEDULE_TIME_F = LocalTime.of(16,30); //the last time a course can be scheduled on friday



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
    private ArrayList<Integer> getScheduledCoursesInTime(String strDays, LocalTime timeStart, LocalTime timeEnd) throws SQLException, ClassNotFoundException {
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
            //check if lstScheduledClassesOnDay is null, if so just return a new arrayList
            if(lstScheduledClassesOnDay == null){
                return new ArrayList<>();
            }
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
    private String switchToAdjacentDays(String strDays){
        /**
         * Name : switchToAdjacentDays
         * Params : strDays - the days that need to be changed
         * Returns : strNewDays - the new days
         * Purpose : The purpose of this function is to run strDays through a switch case to determine what
         *           the proper days to switch to are, and return those new days
         */
        String strNewDays = strDays;
        switch (strDays){
            case "MW":
                strNewDays = "TR";
                break;
            case "TR":
                strNewDays = "MW";
                break;
            default:
                break;
        }
        return strNewDays;
    }
    private int getClassroomTUID(int intAlreadyScheduledCourses) throws SQLException, ClassNotFoundException {
        /**
         * Name : getClassroomTUID
         * Params : intAlreadyScheduledCourses - the number of courses already scheduled in this time slot.
         * Returns : intClassroomTUID - the TUID of the classroom we will schedule our course in next
         * Purpose : The purpose of this method is to get the next classroom to put a course in based on how
         *           many courses are already scheduled. We choose the next classroom based on the order of
         *           'A' first then 'B' then 'C'.
         */
        ArrayList<objClassroom> lstRooms = databaseAccessObject.getAllClassrooms();
        int intClassroomTUID = -1;
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


        for(objClassroom c : lstRooms){
            if(c.getStrClassroomName().equals(strClassroomNameNeeded)){
                intClassroomTUID = c.getIntClassroomTUID();
                break;
            }
        }
        return intClassroomTUID;
    }
    private void insertSchedule(objFileData fileData, int intAlreadyScheduledCourses, int intNewSection) throws SQLException, ClassNotFoundException {

        int intScheduleClassroomTUID = getClassroomTUID(intAlreadyScheduledCourses);
        try {
            databaseAccessObject.addSchedule(fileData, intScheduleClassroomTUID, intNewSection);
        }catch (Exception ex){
            ex.printStackTrace();
        }

    }
    private int getNewSection(objFileData fileData) throws SQLException, ClassNotFoundException {
        /**
         * Name : getNewSection
         * Params : fileData - an object containing data, including the course Name of the course we are trying to get the
         *                     next section for.
         * Returns : intNewSection - the section number of the course that we want to schedule.
         * Purpose : The purpose of this method is to get the next section number of the course that we are trying to schedule.
         *           we need to query the database to get the courseTUID, and then get the highest course section so that we
         *           can increment it and return it.
         */
        int intNewSection = 1; //the next available section.
        objCourse thisCourse = databaseAccessObject.getCourse(fileData.getStrCourseName());
        intNewSection = databaseAccessObject.getNewCourseSection(thisCourse.getIntCourseTUID());

        //if we could not read the next line of the result set that means there are no courses scheduled, and we should schedule this course as section 1.
        return intNewSection;
    }
    private boolean trySchedule(objFileData fileData, ArrayList<Integer> lstScheduledTUIDS, int intNewCourseSection) throws SQLException, ClassNotFoundException {
        /**
         * Name : trySchedule
         * Params : fileData - an objFileData object that contains data about the course we are trying to schedule. The
         *                     data has been read in from a file.
         *          lstScheduledTUIDS - an ArrayList of integers containing TUIDS for all scheduled course.
         *          intNewCourseSection - the section of the course we will try to schedule.
         * Returns : boolean - true -> we were able to schedule the course, false -> course not scheduled here.
         * Purpose : The purpose of this method is to try and schedule the course represented by objFileData. We first get
         *           all classrooms so that we can know how many there are. We then see if the list of scheduled courses
         *           has a size smaller than the list of all classrooms, if not we can not schedule a course and false
         *           will be returned. If so we schedule the course and return true.
         */
        ArrayList<objClassroom> lstAllClassrooms = databaseAccessObject.getAllClassrooms();
        if(lstScheduledTUIDS.size() < lstAllClassrooms.size()){
            insertSchedule(fileData, lstScheduledTUIDS.size(), intNewCourseSection);
            return true;
        }
        return false;
    }
    private void scheduleFourCredit(objFileData fileData) throws SQLException, ClassNotFoundException {
        /**
         * Name : scheduleFourCredit
         * Params : fileData - an object containing the data for the course we want to schedule from our input file
         * Returns : none
         * Purpose :
         */
        int intNewCourseSection = getNewSection(fileData);
        LocalTime timeOriginalStart = getTimeFromString(fileData.getStrStartTime());
        LocalTime timeOriginalEnd = getTimeFromString(fileData.getStrEndTime());
        String strOriginalDays = fileData.getStrDays();
        long lngBlockTimeHours = 2; //the hours we need for our 2 day class periods //TODO get this from intCreditHours
        //part one : try to schedule on desired days/ times

        ArrayList lstScheduledTUIDS = getScheduledCoursesInTime(strOriginalDays, timeOriginalStart, timeOriginalEnd);
        if(trySchedule(fileData, lstScheduledTUIDS, intNewCourseSection))
            return;

        //part two : switch days and try to schedule

        fileData.setStrDays(switchToAdjacentDays(strOriginalDays));
        lstScheduledTUIDS = getScheduledCoursesInTime(fileData.getStrDays(), timeOriginalStart, timeOriginalEnd);
        if(trySchedule(fileData, lstScheduledTUIDS, intNewCourseSection))
            return;

        //part three : switch to original days and try to schedule until we reach the end of available times.

        fileData.setStrDays(strOriginalDays);
        LocalTime newStartTime = timeOriginalStart.plus(lngBlockTimeHours, ChronoUnit.HOURS);
        LocalTime newEndTime = timeOriginalEnd.plus(lngBlockTimeHours, ChronoUnit.HOURS);
        /* loop through and change the start and end times and try to schedule until our new end time will be after our
           last available class time */
        while(newEndTime.isBefore(LAST_SCHEDULE_TIME_MR)){
            lstScheduledTUIDS = getScheduledCoursesInTime(fileData.getStrDays(), newStartTime, newEndTime);
            fileData.setStrStartTime(newStartTime.toString());
            fileData.setStrEndTime(newEndTime.toString());
            if(trySchedule(fileData, lstScheduledTUIDS, intNewCourseSection))
                return;
            newStartTime = newStartTime.plus(lngBlockTimeHours, ChronoUnit.HOURS);
            newEndTime = newEndTime.plus(lngBlockTimeHours, ChronoUnit.HOURS);
        }

        //part four : try all other times after and then before original times on adjacent days

        fileData.setStrDays(switchToAdjacentDays(strOriginalDays));
        newStartTime = timeOriginalStart.plus(lngBlockTimeHours, ChronoUnit.HOURS);
        newEndTime = timeOriginalEnd.plus(lngBlockTimeHours, ChronoUnit.HOURS);
        while(newEndTime.isBefore(LAST_SCHEDULE_TIME_MR)){
            lstScheduledTUIDS = getScheduledCoursesInTime(fileData.getStrDays(), newStartTime, newEndTime);
            fileData.setStrStartTime(newStartTime.toString());
            fileData.setStrEndTime(newEndTime.toString());
            if(trySchedule(fileData, lstScheduledTUIDS, intNewCourseSection))
                return;
            newStartTime = newStartTime.plus(lngBlockTimeHours, ChronoUnit.HOURS);
            newEndTime = newEndTime.plus(lngBlockTimeHours, ChronoUnit.HOURS);
        }
        //now try before
        newStartTime = FIRST_SCHEDULE_TIME_MR.plus(lngBlockTimeHours, ChronoUnit.HOURS);
        newEndTime = FIRST_SCHEDULE_TIME_MR.plus(lngBlockTimeHours, ChronoUnit.HOURS);
        while(newEndTime.isBefore(timeOriginalStart)){
            lstScheduledTUIDS = getScheduledCoursesInTime(fileData.getStrDays(), newStartTime, newEndTime);
            fileData.setStrStartTime(newStartTime.toString());
            fileData.setStrEndTime(newEndTime.toString());
            if(trySchedule(fileData, lstScheduledTUIDS, intNewCourseSection))
                return;
            newStartTime = newStartTime.plus(lngBlockTimeHours, ChronoUnit.HOURS);
            newEndTime = newEndTime.plus(lngBlockTimeHours, ChronoUnit.HOURS);
        }

        //part five : try all times before the original times on the original days
        fileData.setStrDays(strOriginalDays);
        newStartTime = FIRST_SCHEDULE_TIME_MR.plus(lngBlockTimeHours, ChronoUnit.HOURS);
        newEndTime = FIRST_SCHEDULE_TIME_MR.plus(lngBlockTimeHours, ChronoUnit.HOURS);
        while(newEndTime.isBefore(timeOriginalStart)){
            lstScheduledTUIDS = getScheduledCoursesInTime(fileData.getStrDays(), newStartTime, newEndTime);
            fileData.setStrStartTime(newStartTime.toString());
            fileData.setStrEndTime(newEndTime.toString());
            if(trySchedule(fileData, lstScheduledTUIDS, intNewCourseSection))
                return;
            newStartTime = newStartTime.plus(lngBlockTimeHours, ChronoUnit.HOURS);
            newEndTime = newEndTime.plus(lngBlockTimeHours, ChronoUnit.HOURS);
        }

        //part six : try fridays
        //since Friday classes are twice as long we have to watch how we increment
        fileData.setStrDays("F");
        newStartTime = FIRST_SCHEDULE_TIME_F;
        newEndTime = newStartTime.plus((2 * lngBlockTimeHours), ChronoUnit.HOURS);
        while(newEndTime.isBefore(LAST_SCHEDULE_TIME_F)){
            lstScheduledTUIDS = getScheduledCoursesInTime(fileData.getStrDays(), newStartTime, newEndTime);
            fileData.setStrStartTime(newStartTime.toString());
            fileData.setStrEndTime(newEndTime.toString());
            if(trySchedule(fileData, lstScheduledTUIDS, intNewCourseSection))
                return;
            newStartTime = newStartTime.plus((2 *lngBlockTimeHours), ChronoUnit.HOURS);
            newEndTime = newEndTime.plus((2 * lngBlockTimeHours), ChronoUnit.HOURS);
        }

        //we could not schedule the course, so we need to print out that it was not scheduled
        fileData.setStrDays(strOriginalDays);
        fileData.setStrStartTime(timeOriginalStart.toString());
        fileData.setStrEndTime(timeOriginalEnd.toString());
        System.out.println("Could not schedule section " + intNewCourseSection + " for " + fileData.toString());
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