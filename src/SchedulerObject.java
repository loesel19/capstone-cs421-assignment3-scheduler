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
    private static final LocalTime FIRST_SCHEDULE_TIME_MR_4Cred = LocalTime.of(8, 30); //the first time a 4 credit course can start at on Monday through Thursday
    private static final LocalTime FIRST_SCHEDULE_TIME_MR_3Cred = LocalTime.of(9,0); //the first time a 3 credit course can start monday through thursday
    private static final LocalTime FIRST_SCHEDULE_TIME_MR_2And1Cred = LocalTime.of(9,0); //the first time 1 or 2 credit course can start monday through thursday
    private static final LocalTime LAST_SCHEDULE_TIME_MR_4And3Cred = LocalTime.of(16, 30); //the last time a course can go until on Monday through Thursday
    private static final LocalTime LAST_SCHEDULE_TIME_F = LocalTime.of(16,0); //the last time a 3 cred class can go until Monday through Thursday
    private static final LocalTime LAST_SCHEDULE_TIME_MR_2Cred = LocalTime.of(14,0); //the last time a 2 hour course can be scheduled on monday through thursday
    private static final LocalTime LAST_SCHEDULE_TIME_MR_1Cred = LocalTime.of(15, 0); //the last time a 1 hour course can be scheduled on monday through thursday



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
        LocalTime time;
        if(Integer.parseInt(strTime.split(":")[0]) < 8){
            time = LocalTime.of(12 + Integer.parseInt(strTime.split(":")[0]), Integer.parseInt(strTime.split(":")[1]));
        }else {
            time = LocalTime.of(Integer.parseInt(strTime.split(":")[0]), Integer.parseInt(strTime.split(":")[1]));
        }
        return time;
    }
    private ArrayList<Integer> getScheduledCoursesInTime(String strDays, LocalTime timeStart, LocalTime timeEnd) throws SQLException, ClassNotFoundException {
        /**
         * Name : getScheduledCourseInTime
         * Params : strDays - the Days we want to check the schedule on.
         *          strStartTime - the start time we want to schedule a class for
         *          strEndTime - the end time we want to schedule a class for
         * Returns : lstAccountedForTUIDS - an arraylist containing the Schedule_Table TUIDS for courses that are scheduled
         *                                  during the desired time slot.
         * Purpose : The purpose of this function is to get a list containing TUIDS for all courses whose time day combination
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
        ArrayList<Integer> lstClassroomTUIDS = new ArrayList<>();
        for(int i = 0; i < strDaysSeparate.length; i++){
            ArrayList<objSchedule> lstScheduledClassesOnDay = mapScheduled.get(strDaysSeparate[i]);
            //check if lstScheduledClassesOnDay is null, if so just return a new arrayList
            if(lstScheduledClassesOnDay == null){
                return new ArrayList<>();
            }
            for(objSchedule s : lstScheduledClassesOnDay){
                LocalTime timeScheduledStart = getTimeFromString(s.getStrStartTime());
                LocalTime timeScheduledEnd = getTimeFromString(s.getStrEndTime());
                /* here we need to consider a few conflict conditions. 1: if the start time or end times of a course are
                   equal we know that the scheduled course and the course we are scheduling overlap. 2: If the course we are
                   trying to schedule starts after the scheduled course start time and before the scheduled course end time
                   there is an overlap. 3: If the new course ends after the scheduled course start time and before its end
                   time there is an overlap. 4: If the scheduled course starts after the new courses start time and before
                   its end time there is an overlap. 5: If the scheduled course ends after the new courses start time and before
                   its end time there is an overlap. */
                if(timeStart.equals(timeScheduledStart) || timeEnd.equals(timeScheduledEnd) || (timeStart.isAfter(timeScheduledStart) &&
                        timeStart.isBefore(timeScheduledEnd)) || (timeEnd.isAfter(timeScheduledStart) && timeEnd.isBefore(timeScheduledEnd))
                || (timeScheduledStart.isAfter(timeStart) && timeScheduledStart.isBefore(timeEnd)) || (timeScheduledEnd.isAfter(timeStart)
                        && timeScheduledEnd.isBefore(timeEnd))){
                    //Before we count this as an overlap we need to make sure it has not already been accounted for.
                    if(!lstAccountedForTUIDS.contains(s.getIntTUID()) && !lstClassroomTUIDS.contains(s.getIntClassroomTUID())){
                        lstAccountedForTUIDS.add(s.getIntTUID());
                        lstClassroomTUIDS.add(s.getIntClassroomTUID());
                    }
                    continue;
                }


            }
        }
        return  lstClassroomTUIDS;
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
    private void insertSchedule(objFileData fileData, int intClassroomTUID, int intNewSection) throws SQLException, ClassNotFoundException {
        /**
         * Name : insertSchedule
         * Params : fileData - the object containing all the data of the course we are trying to schedule
         *          intAlreadyScheduledCourses - the amount of courses scheduled in this time slot
         *          intNewSection - the section of the course we are going to schedule
         * Returns : none
         * Purpose : The purpose of this method is to schedule a course. We do this by using our database access object
         *           and invoking its addSchedule method passing in the fileData, the classroomTUID, which is derived
         *           from the already scheduled courses, and the new section. In a perfect world this method would
         *           never throw an exception since we know we are getting good data, but catch any exception in case.
         */

        try {
            /* let's make sure that the time format passed to the addSchedule method is standard and not military time
                to do this we can get an int from the hours portion of the time string and if it is greater than 12 we
                will subtract 12.
             */
            int intCurrHours = Integer.parseInt(fileData.getStrStartTime().split(":")[0]);
            //if time is !> 12:59 it will be left alone.
            if(intCurrHours > 12)
                fileData.setStrStartTime((intCurrHours - 12) + ":" + fileData.getStrStartTime().split(":")[1]);
            //now do the same thing for end time
            intCurrHours = Integer.parseInt(fileData.getStrEndTime().split(":")[0]);
            if(intCurrHours > 12)
                fileData.setStrEndTime((intCurrHours - 12) + ":" + fileData.getStrEndTime().split(":")[1]);
            databaseAccessObject.addSchedule(fileData, intClassroomTUID, intNewSection);
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
    private boolean trySchedule(objFileData fileData, ArrayList<Integer> lstClassroomTUIDS, int intNewCourseSection) throws SQLException, ClassNotFoundException {
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
        if(lstClassroomTUIDS.size() < lstAllClassrooms.size()){
            int intLargestAvailableRoomTUID = 0;
            boolean blnA = true;
            boolean blnB = true;
            boolean blnC = true;
            for(int tuid : lstClassroomTUIDS){
                if(tuid == 1)
                    blnA = false;
                if(tuid == 2)
                    blnB = false;
                if(tuid == 3)
                    blnC = false;
            }
            if (blnC)
                intLargestAvailableRoomTUID = 3;
            if(blnB)
                intLargestAvailableRoomTUID = 2;
            if(blnA)
                intLargestAvailableRoomTUID = 1;
            insertSchedule(fileData, intLargestAvailableRoomTUID, intNewCourseSection);
            return true;
        }
        return false;
    }
    private boolean loopTrySchedule(LocalTime newStartTime, LocalTime newEndTime, LocalTime firstTimeWhile, LocalTime secondTimeWhile,
                                    int intNewCourseSection, long lngBlockTimeHours, long lngMinutes, objFileData fileData)
            throws SQLException, ClassNotFoundException {
        /**
         * Name : loopTrySchedule
         * Params : newStartTime - the LocalTime start time of the course we try to schedule
         *          newEndTime - the LocalTime end time of the course we try to schedule
         *          firstTimeWhile - The LocalTime we check that our end time is before in our while loop
         *          secondTimeWhile - The LocalTime we check that our end time is equal to in our while loop.
         *          intNewCourseSection -  the new course section for the course we are tyring to schedule
         *          lngBlockTimeHours - this will be the hours that our times are incremented by
         *          lngMinutes - this is the minutes we increment our times by
         *          fileData - contains data about the course we are trying to schedule
         * Returns : boolean - true -> course was scheduled, false -> course not scheduled.
         * Purpose : This method loops through each time slot designated by our newStart and newEnd Times until the newEndTime
         *           is after firstTimeWhile. In each iteration we set the fileData fields for times accordingly, try to
         *           schedule this course, and return true if scheduled. If course not scheduled we increment start time and
         *           end time by lngHours and Minutes.
         */
        while(newEndTime.isBefore(firstTimeWhile) || newEndTime.equals(secondTimeWhile)){
            ArrayList<Integer> lstScheduledClassroomTUIDS = getScheduledCoursesInTime(fileData.getStrDays(), newStartTime, newEndTime);
            fileData.setStrStartTime(newStartTime.toString());
            fileData.setStrEndTime(newEndTime.toString());
            if(trySchedule(fileData, lstScheduledClassroomTUIDS, intNewCourseSection))
                return true;
            newStartTime = newStartTime.plus((lngBlockTimeHours / 2), ChronoUnit.HOURS);
            newEndTime = newEndTime.plus((lngBlockTimeHours / 2), ChronoUnit.HOURS);
            //add minutes in
          //  newStartTime = newStartTime.plus(lngMinutes, ChronoUnit.MINUTES);
          //  newEndTime = newEndTime.plus(lngMinutes, ChronoUnit.MINUTES);
        }

        return false;
    }
    private boolean loopTryScheduleFriday(LocalTime newStartTime, LocalTime newEndTime, LocalTime lastTime, int intNewCourseSection,
                                          objFileData fileData) throws SQLException, ClassNotFoundException {
        /**
         * Name : loopTrySchedule
         * Params : newStartTime - the LocalTime start time of the course we try to schedule
         *          newEndTime - the LocalTime end time of the course we try to schedule
         *          lastTime - The LocalTime we check that our end time is before in our while loop
         *          intNewCourseSection -  the new course section for the course we are tyring to schedule
         *          lngBlockTimeHours - this will be the hours that our times are incremented by
         *          lngMinutes - this is the minutes we increment our times by
         *          fileData - contains data about the course we are trying to schedule
         * Returns : boolean - true -> course was scheduled, false -> course not scheduled
         * Purpose : This method handles checking for every time slot in hour increments on friday. We loop through trying to
         *           schedule the course that fileData represents until the newEndTime is after the lastTime(which will be last
         *           end time on friday.).
         */
        while(newEndTime.isBefore(lastTime) || newEndTime.equals(lastTime)){
            ArrayList<Integer> lstScheduledClassroomTUIDS = getScheduledCoursesInTime(fileData.getStrDays(), newStartTime, newEndTime);
            fileData.setStrStartTime(newStartTime.toString());
            fileData.setStrEndTime(newEndTime.toString());
            if(trySchedule(fileData, lstScheduledClassroomTUIDS, intNewCourseSection))
                return true;
            newStartTime = newStartTime.plus(1, ChronoUnit.HOURS);
            newEndTime = newEndTime.plus(1, ChronoUnit.HOURS);
            //we do not add the minutes here, because we are only incrementing by an hour on fridays
        }
        return false;
    }
    private boolean loopTrySchedule2Or1Credit(LocalTime courseStartTime, LocalTime courseEndTime, LocalTime lastTime,
                                              int intNewCourseSection, objFileData fileData) throws SQLException, ClassNotFoundException {
        /**
         * Name : loopTrySchedule2Or1Credit
         * Params : courseStartTime - the startTime of the course we are trying to schedule
         *          courseEndTime - the end time of the course we are trying to schedule
         *          lastTime - this is the time that our class must be scheduled before
         *          intNewCourseSection - the new section for this course.
         *          fileData - an object that has fields corresponding to detail about the course we are scheduling.
         * Returns : boolean - true -> course was scheduled, false -> course was not scheduled
         * Purpose : This method will loop through the courseEndtime until it is after the lastTime. In each loop iteration
         *           we first try to schedule this course by calling the try schedule function. If we cannot schedule
         *           we will increment the time by an hour.
         */
        while(!courseEndTime.isAfter(lastTime)){
            ArrayList<Integer> lstScheduledClassroomTUIDS = getScheduledCoursesInTime(fileData.getStrDays(), courseStartTime, courseEndTime);
            fileData.setStrStartTime(courseStartTime.toString());
            fileData.setStrEndTime(courseEndTime.toString());
            if(trySchedule(fileData, lstScheduledClassroomTUIDS, intNewCourseSection))
                return true;
            courseStartTime = courseStartTime.plus(1, ChronoUnit.HOURS);
            courseEndTime = courseEndTime.plus(1, ChronoUnit.HOURS);
        }
        return false;
    }
    private String nextDayNoFriday(String strDay){
        /**
         * Name : nextDayNoFriday
         * Params : strDay - the day that we want to get the next day for
         * Returns : string representation of the next day skipping friday
         * Purpose : The purpose of this method is to increment the input day by 1 day essentially, so Monday will go to
         *           Tuesday (M to T), When we have Thursday we will loop around to Monday again.
         */
        switch (strDay){
            case "M":
                return "T";
            case "T":
                return "W";
            case "W":
                return "R";
            case "R":
                return "M";
            default:
                return "-1";
        }
    }

    private void scheduleFourOrThreeCredit(objFileData fileData) throws SQLException, ClassNotFoundException {
        /**
         * Name : scheduleFourCredit
         * Params : fileData - an object containing the data for the course we want to schedule from our input file
         * Returns : none
         * Purpose : This method is where we do the logical processing to figure out where the new course can be scheduled
         * Notes : Each time we increment a time by the credit hours / 2 we also need to add the remaining minutes, which
         *         we get with a little typecasting equation work at the start of this method.
         */
        int intNewCourseSection = getNewSection(fileData); //the new course section
        int intStartHour = Integer.parseInt(fileData.getStrStartTime().split(":")[0]);
        int intEndHour = Integer.parseInt(fileData.getStrEndTime().split(":")[0]);
        if(intStartHour < 8)
            intStartHour += 12;
        if(intEndHour < 8)
            intEndHour += 12;
        LocalTime timeOriginalStart = LocalTime.of(intStartHour, Integer.parseInt(fileData.getStrStartTime().split(":")[1])); //the original start time
        LocalTime timeOriginalEnd = LocalTime.of(intEndHour, Integer.parseInt(fileData.getStrEndTime().split(":")[1])); //the original end time
        String strOriginalDays = fileData.getStrDays(); //the original days
        objCourse course = databaseAccessObject.getCourse(fileData.getStrCourseName()); //the course we are trying to schedule
        long lngBlockTimeHours = course.getIntCreditHours(); //the hours we need for our 2 day class periods
        long lngMinutes = (long) ((((double) course.getIntCreditHours() / 2.0) * 60) % 60);
         /* this equation is used to put
         any minutes we have remaining after a course time is cut in half for split classes into a variable to add to
         a LocalTime when we loop through slots. It will come in handy for 3 credit hour courses. */

        //part one : try to schedule on desired days/ times
        /* lstScheduledTUIDS will frequently be assigned an arrayList of course tuids that are scheduled in the current
           time block on the current days that we are trying to schedule the new course for.
         */
        fileData.setStrStartTime(timeOriginalStart.toString());
        fileData.setStrEndTime(timeOriginalEnd.toString());
        ArrayList lstScheduledClassroomTUIDS = getScheduledCoursesInTime(strOriginalDays, timeOriginalStart, timeOriginalEnd);
        if (trySchedule(fileData, lstScheduledClassroomTUIDS, intNewCourseSection))
            return;

        //part two : switch days and try to schedule

        fileData.setStrDays(switchToAdjacentDays(strOriginalDays));
        lstScheduledClassroomTUIDS = getScheduledCoursesInTime(fileData.getStrDays(), timeOriginalStart, timeOriginalEnd);
        if (trySchedule(fileData, lstScheduledClassroomTUIDS, intNewCourseSection))
            return;

        //part three : switch to original days and try to schedule until we reach the end of the day.

        fileData.setStrDays(strOriginalDays);
        /* we will need to get new start and end times and set them to hours/2 + remaining minutes to schedule in the immediate
           next time slot on the original days */
        LocalTime newStartTime = timeOriginalStart.plus((lngBlockTimeHours / 2), ChronoUnit.HOURS);
        LocalTime newEndTime = timeOriginalEnd.plus((lngBlockTimeHours / 2), ChronoUnit.HOURS);
        //we also need to add minutes in.
       // newStartTime = newStartTime.plus(lngMinutes, ChronoUnit.MINUTES);
       // newEndTime = newEndTime.plus(lngMinutes, ChronoUnit.MINUTES);
        //get last time for 3 or 4 credit class depending on how many credit hours this course is
        LocalTime lastTime = LAST_SCHEDULE_TIME_MR_4And3Cred;

        if (loopTrySchedule(newStartTime, newEndTime, lastTime, lastTime, intNewCourseSection, lngBlockTimeHours, lngMinutes, fileData))
            return;

        //part four : try the rest of the slots starting from the first one on the adjacent days, but we eed to skip the original time slot
        //try before original time slot
        if (course.getIntCreditHours() == 3)
            newStartTime = FIRST_SCHEDULE_TIME_MR_3Cred;
        else
            newStartTime = FIRST_SCHEDULE_TIME_MR_4Cred;

        newEndTime = newStartTime.plus((lngBlockTimeHours / 2), ChronoUnit.HOURS);
      //  newEndTime = newEndTime.plus(lngMinutes, ChronoUnit.MINUTES);
        if (loopTrySchedule(newStartTime, newEndTime, timeOriginalStart, timeOriginalStart, intNewCourseSection, lngBlockTimeHours, lngMinutes, fileData))
            return;

        //now try all time slots after original start time.
        fileData.setStrDays(switchToAdjacentDays(strOriginalDays));
        newStartTime = timeOriginalStart.plus((lngBlockTimeHours / 2), ChronoUnit.HOURS);
        newEndTime = timeOriginalEnd.plus((lngBlockTimeHours / 2), ChronoUnit.HOURS);
        //don't forget to add the minutes in
     //   newStartTime = newStartTime.plus(lngMinutes, ChronoUnit.MINUTES);
      //  newEndTime = newEndTime.plus(lngMinutes, ChronoUnit.MINUTES);
        if (loopTrySchedule(newStartTime, newEndTime, lastTime, lastTime, intNewCourseSection, lngBlockTimeHours, lngMinutes, fileData))
            return;

        //part five : try all times before the original times on the original days
        fileData.setStrDays(strOriginalDays);
        if (course.getIntCreditHours() == 3)
            newStartTime = FIRST_SCHEDULE_TIME_MR_3Cred;
        else
            newStartTime = FIRST_SCHEDULE_TIME_MR_4Cred;

        newEndTime = newStartTime.plus((lngBlockTimeHours / 2), ChronoUnit.HOURS);
       // newEndTime = newEndTime.plus(lngMinutes, ChronoUnit.MINUTES);
        if (loopTrySchedule(newStartTime, newEndTime, timeOriginalStart, timeOriginalEnd, intNewCourseSection, lngBlockTimeHours, lngMinutes, fileData))
            return;


        //part six : try fridays
        //since Friday classes are twice as long we have to watch how we increment
        fileData.setStrDays("F");
        if (course.getIntCreditHours() == 3) {
            newStartTime = FIRST_SCHEDULE_TIME_MR_3Cred;
            lastTime = LAST_SCHEDULE_TIME_F;
        } else {
            newStartTime = FIRST_SCHEDULE_TIME_MR_4Cred;
            lastTime = LAST_SCHEDULE_TIME_MR_4And3Cred;
        }
        newEndTime = newStartTime.plus(lngBlockTimeHours, ChronoUnit.HOURS);
      //  newEndTime = newEndTime.plus(lngMinutes, ChronoUnit.MINUTES);
        if (loopTryScheduleFriday(newStartTime, newEndTime, lastTime, intNewCourseSection, fileData))
            return;

        //we could not schedule the course, so we need to print out that it was not scheduled
        fileData.setStrDays(strOriginalDays);
        fileData.setStrStartTime(timeOriginalStart.toString());
        fileData.setStrEndTime(timeOriginalEnd.toString());
        System.out.println("Could not schedule section " + intNewCourseSection + " for " + fileData.toString());
    }

    private void scheduleOneOrTwoCredit(objFileData fileData) throws SQLException, ClassNotFoundException {
        /**
         * Name : scheduleOneOrTwoCredit
         * Params : fileData - a data model object that represents the class that we want to schedule
         * Returns : None
         * Purpose : This method will take care of the algorithm and logic for scheduling a 1 or 2 credit course.
         *           In both cases the algorithm starts on the original day, looks at first time - last time of that day
         *           then before the start time on that day. Then we loop through and try to schedule in each time slot on
         *           the rest of the non-friday days. Finally try friday.
         * Notes :
         */
        objCourse course = databaseAccessObject.getCourse(fileData.getStrCourseName()); //extra data about the course we are scheduling.
        if(course.getIntCourseTUID() == 5){
            System.out.print("");
        }
        int intNewCourseSection = getNewSection(fileData);
        int intStartHour = Integer.parseInt(fileData.getStrStartTime().split(":")[0]);
        int intEndHour = Integer.parseInt(fileData.getStrEndTime().split(":")[0]);
        if(intStartHour < 8)
            intStartHour += 12;
        if(intEndHour < 8)
            intEndHour += 12;
        LocalTime startTime = LocalTime.of(intStartHour, Integer.parseInt(fileData.getStrStartTime().split(":")[1]));
        LocalTime endTime = LocalTime.of(intEndHour, Integer.parseInt(fileData.getStrEndTime().split(":")[1]));
        LocalTime originalStartTime = startTime;
        LocalTime originalEndTime = endTime;
        String strOriginalDay = fileData.getStrDays();
        //try to schedule on the original day from the given times to end of day
        if(loopTrySchedule2Or1Credit(startTime, endTime, LAST_SCHEDULE_TIME_F, intNewCourseSection, fileData))
            return;
        //now change the day
        fileData.setStrDays(nextDayNoFriday(strOriginalDay));
        startTime = FIRST_SCHEDULE_TIME_MR_2And1Cred;
        endTime = FIRST_SCHEDULE_TIME_MR_2And1Cred.plus(course.getIntCreditHours(), ChronoUnit.HOURS);
        //loop until we get back to our original day.
        while(!fileData.getStrDays().equals(strOriginalDay)){
            if(loopTrySchedule2Or1Credit(startTime, endTime, LAST_SCHEDULE_TIME_F, intNewCourseSection, fileData))
                return;
            //if not scheduled we want to increment our day
            fileData.setStrDays(nextDayNoFriday(fileData.getStrDays()));
        }
        //now we have to try the times before our original time on our original day
        endTime = FIRST_SCHEDULE_TIME_MR_2And1Cred.plus(course.getIntCreditHours(), ChronoUnit.HOURS);
        if(loopTrySchedule2Or1Credit(FIRST_SCHEDULE_TIME_MR_2And1Cred, endTime, originalStartTime, intNewCourseSection, fileData))
            return;
        //now we have to try friday
        fileData.setStrDays("F");
        endTime = FIRST_SCHEDULE_TIME_MR_2And1Cred.plus(course.getIntCreditHours(), ChronoUnit.HOURS);
        if(loopTrySchedule2Or1Credit(FIRST_SCHEDULE_TIME_MR_2And1Cred, endTime, LAST_SCHEDULE_TIME_F, intNewCourseSection, fileData))
            return;
        //could not schedule course
        fileData.setStrDays(strOriginalDay);
        fileData.setStrStartTime(originalStartTime.toString());
        fileData.setStrEndTime(originalEndTime.toString());
        System.out.println("Could not schedule "+ intNewCourseSection + " for " + fileData.toString());

    }
    public void Schedule(objFileData fileData) throws SQLException, ClassNotFoundException {


        objCourse course = databaseAccessObject.getCourse(fileData.getStrCourseName());


            switch (course.getIntCreditHours()){
                case 1:
                case 2:
                    scheduleOneOrTwoCredit(fileData);
                    break;
                case 3:
                case 4:
                    scheduleFourOrThreeCredit(fileData);
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
    public void printReportDayTime() throws SQLException, ClassNotFoundException {
        /**
         * @Name : printReportDayTime
         * @Params : none
         * @Returns : none
         * @Purpose :
         */
        ArrayList<objReport> lstReport = databaseAccessObject.getScheduledCoursesByDayTime();
        System.out.println("Printing out report by day and time");
        for (objReport r : lstReport){
            System.out.println(r.toString());
        }
    }
    public void printReportProfessor() throws SQLException, ClassNotFoundException {
        /**
         * @Name : printReportProfessor
         * @Params : none
         * @Returns : none
         * @Purpose
         */
        ArrayList<objReport> lstReport = databaseAccessObject.getScheduledCoursesByProfessor();
        System.out.println("Printing out report by professor");
        for (objReport r : lstReport){
            System.out.println(r.toString());
        }
    }
    public void printReportCourse() throws SQLException, ClassNotFoundException {
        /**
         * @Name : printReportCourse
         * @Params : none
         * @Returns : none
         * @Purpose
         */
        ArrayList<objReport> lstReport = databaseAccessObject.getScheduledCoursesByCourse();
        System.out.println("Printing out report by course name");
        for (objReport r : lstReport){
            System.out.println(r.toString());
        }
    }

}