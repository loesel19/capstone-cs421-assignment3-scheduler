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
    private HashMap<String, Integer> getNewSection(objFileData fileData, HashMap<String, Integer> mapSections) throws SQLException, ClassNotFoundException {
        /**
         * Name : getNewSection
         * Params : fileData - an object containing data, including the course Name of the course we are trying to get the
         *                     next section for.
         *          mapSections - a Hashmap with course names as keys and course sections as values.
         * Returns : mapSections - a Hashmap with course names as keys and course sections as values.
         * Purpose : The purpose of this method is to increment the entry of the course that fileData represents by 1
         *           and return the hashmap
         */
        int intNewSection = 1; //the next available section.
        objCourse thisCourse = databaseAccessObject.getCourse(fileData.getStrCourseName());
        //replace this courses entry in the map with an incremented entry
        mapSections.replace(thisCourse.getStrCourseID(), mapSections.get(thisCourse.getStrCourseID()), mapSections.get(thisCourse.getStrCourseID()) + 1);
        return mapSections;
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
            /* we want to loop through the list of taken classrooms at this time slot and we flip the boolean to false if it
            is part of the list */
            for(int tuid : lstClassroomTUIDS){
                if(tuid == 1)
                    blnA = false;
                if(tuid == 2)
                    blnB = false;
                if(tuid == 3)
                    blnC = false;
            }
            //now we first check c, then b, then a booleans. We do it in this order because the last one checked is the largest
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
        //loop through until the courses endtime is after the specified lasttime, try to schedule the course on each iteration
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
        //loop through until the courses endtime is after the specified lasttime, try to schedule the course on each iteration
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
        //loop through until the courses endtime is after the specified lasttime, try to schedule the course on each iteration
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
    private boolean ThreeOrFourCredAlgorithm(objFileData fileData, objCourse course, LocalTime timeOriginalStart, LocalTime timeOriginalEnd,
                                             String strOriginalDays, int intNewCourseSection, long lngBlockTimeHours, long lngMinutes) throws SQLException, ClassNotFoundException {
        /**
         * @Name : ThreeOrFourCredAlgorithm
         * @Params : fileData - a file data model object that has data needed to schedule the course
         *           course - the course that we are scheduling a section for
         *           timeOriginalStart - the original start time for this section
         *           timeOriginalEnd - the original end time for this section
         *           strOriginalDays - the original days for this section
         *           intNewCourseSection - the number for this new section
         *           lngBlockTimeHours - the hours of the 2 day split class
         *           lngMinutes - the minutes for the 2 day split class
         * @Returns : boolean - true -> scheduled this section, false -> couldn't schedule section
         * @Purpose : the purpose of this method is to execute the algorithmic approach to scheduling a course. The parts
         *            of the algorithm are labeled in inline code.
         */
        //part one : try to schedule on desired days/ times
        /* lstScheduleClassroomTUIDS will frequently be assigned an arrayList of course tuids that are scheduled in the current
           time block on the current days that we are trying to schedule the new course for.
         */
        fileData.setStrStartTime(timeOriginalStart.toString());
        fileData.setStrEndTime(timeOriginalEnd.toString());
        ArrayList lstScheduledClassroomTUIDS = getScheduledCoursesInTime(strOriginalDays, timeOriginalStart, timeOriginalEnd);
        if (trySchedule(fileData, lstScheduledClassroomTUIDS, intNewCourseSection))
            return true;

        //part two : switch days and try to schedule
        fileData.setStrDays(switchToAdjacentDays(strOriginalDays));
        lstScheduledClassroomTUIDS = getScheduledCoursesInTime(fileData.getStrDays(), timeOriginalStart, timeOriginalEnd);
        if (trySchedule(fileData, lstScheduledClassroomTUIDS, intNewCourseSection))
            return true;

        //part three : switch to original days and try to schedule until we reach the end of the day.
        fileData.setStrDays(strOriginalDays);
        /* we will need to get new start and end times and set them to hours/2 + remaining minutes to schedule in the immediate
           next time slot on the original days */
        LocalTime newStartTime = timeOriginalStart.plus((lngBlockTimeHours / 2), ChronoUnit.HOURS);
        LocalTime newEndTime = timeOriginalEnd.plus((lngBlockTimeHours / 2), ChronoUnit.HOURS);
        //get last time for 3 or 4 credit class depending on how many credit hours this course is
        LocalTime lastTime = LAST_SCHEDULE_TIME_MR_4And3Cred;
        if (loopTrySchedule(newStartTime, newEndTime, lastTime, lastTime, intNewCourseSection, lngBlockTimeHours, lngMinutes, fileData))
            return true;
        //part four : try the rest of the slots starting from the first one on the adjacent days, but we eed to skip the original time slot
        //try before original time slot
        if (course.getIntCreditHours() == 3)
            newStartTime = FIRST_SCHEDULE_TIME_MR_3Cred;
        else
            newStartTime = FIRST_SCHEDULE_TIME_MR_4Cred;

        newEndTime = newStartTime.plus((lngBlockTimeHours / 2), ChronoUnit.HOURS);
        if (loopTrySchedule(newStartTime, newEndTime, timeOriginalStart, timeOriginalStart, intNewCourseSection, lngBlockTimeHours, lngMinutes, fileData))
            return true;

        //now try all time slots after original start time.
        fileData.setStrDays(switchToAdjacentDays(strOriginalDays));
        newStartTime = timeOriginalStart.plus((lngBlockTimeHours / 2), ChronoUnit.HOURS);
        newEndTime = timeOriginalEnd.plus((lngBlockTimeHours / 2), ChronoUnit.HOURS);
        if (loopTrySchedule(newStartTime, newEndTime, lastTime, lastTime, intNewCourseSection, lngBlockTimeHours, lngMinutes, fileData))
            return true;

        //part five : try all times before the original times on the original days
        fileData.setStrDays(strOriginalDays);
        if (course.getIntCreditHours() == 3)
            newStartTime = FIRST_SCHEDULE_TIME_MR_3Cred;
        else
            newStartTime = FIRST_SCHEDULE_TIME_MR_4Cred;
        newEndTime = newStartTime.plus((lngBlockTimeHours / 2), ChronoUnit.HOURS);
        if (loopTrySchedule(newStartTime, newEndTime, timeOriginalStart, timeOriginalEnd, intNewCourseSection, lngBlockTimeHours, lngMinutes, fileData))
            return true;
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
        if (loopTryScheduleFriday(newStartTime, newEndTime, lastTime, intNewCourseSection, fileData))
            return true;
        return false;
    }
    private HashMap<String, Integer> scheduleFourOrThreeCredit(objFileData fileData, HashMap<String, Integer> mapSections)
            throws SQLException, ClassNotFoundException {
        /**
         * Name : scheduleFourCredit
         * Params : fileData - an object containing the data for the course we want to schedule from our input file
         *          mapSections - a Hashmap with course names as keys and course sections as values.
         * Returns : mapSections - a Hashmap with course names as keys and course sections as values.
         * Purpose : This method is where we do the logical processing to figure out where the new course can be scheduled
         * Notes : Each time we increment a time by the credit hours / 2 we also need to add the remaining minutes, which
         *         we get with a little typecasting equation work at the start of this method.
         *         mapSections will always reflect the new section that is trying to be scheduled in this method when
         *         it is returned
         */
        mapSections = getNewSection(fileData, mapSections); //get our map with the incremented section for this course
        //get start times and end times
        int intStartHour = Integer.parseInt(fileData.getStrStartTime().split(":")[0]);
        int intEndHour = Integer.parseInt(fileData.getStrEndTime().split(":")[0]);
        if(intStartHour < 8)
            intStartHour += 12;
        if(intEndHour < 8)
            intEndHour += 12;
        //save our start and end times for future use
        LocalTime timeOriginalStart = LocalTime.of(intStartHour, Integer.parseInt(fileData.getStrStartTime().split(":")[1])); //the original start time
        LocalTime timeOriginalEnd = LocalTime.of(intEndHour, Integer.parseInt(fileData.getStrEndTime().split(":")[1])); //the original end time
        String strOriginalDays = fileData.getStrDays(); //the original days
        objCourse course = databaseAccessObject.getCourse(fileData.getStrCourseName()); //the course we are trying to schedule
        int intNewCourseSection = mapSections.get(course.getStrCourseID()); //get the incremented section from the map
        long lngBlockTimeHours = course.getIntCreditHours(); //the hours we need for our 2 day class periods
        /* this equation is used to put
         any minutes we have remaining after a course time is cut in half for split classes into a variable to add to
         a LocalTime when we loop through slots. It will come in handy for 3 credit hour courses. */
        long lngMinutes = (long) ((((double) course.getIntCreditHours() / 2.0) * 60) % 60);
        //call our method to handle the scheduling algorithm
        if(ThreeOrFourCredAlgorithm(fileData, course, timeOriginalStart, timeOriginalEnd, strOriginalDays, intNewCourseSection, lngBlockTimeHours, lngMinutes))
            return mapSections;
        //we could not schedule the course, so we need to print out that it was not scheduled
        fileData.setStrDays(strOriginalDays);
        fileData.setStrStartTime(timeOriginalStart.toString());
        fileData.setStrEndTime(timeOriginalEnd.toString());
        System.out.println("Could not schedule section " + intNewCourseSection + " for " + fileData.toString());
        return mapSections;
    }

    private HashMap<String, Integer> scheduleOneOrTwoCredit(objFileData fileData, HashMap<String, Integer> mapSections) throws SQLException, ClassNotFoundException {
        /**
         * Name : scheduleOneOrTwoCredit
         * Params : fileData - a data model object that represents the class that we want to schedule
         *          mapSections - a Hashmap with course names as keys and course sections as values.
         * Returns : mapSections - a Hashmap with course names as keys and course sections as values.
         * Purpose : This method will take care of the algorithm and logic for scheduling a 1 or 2 credit course.
         *           In both cases the algorithm starts on the original day, looks at first time - last time of that day
         *           then before the start time on that day. Then we loop through and try to schedule in each time slot on
         *           the rest of the non-friday days. Finally try friday.
         * Notes : mapSections will always reflect the new section that is trying to be scheduled in this method when
         *         it is returned
         */
        objCourse course = databaseAccessObject.getCourse(fileData.getStrCourseName()); //extra data about the course we are scheduling.
        if(course.getIntCourseTUID() == 5){
            System.out.print("");
        }
        //increment the section for this course
        mapSections = getNewSection(fileData, mapSections);
        int intNewCourseSection = mapSections.get(course.getStrCourseID()); //the new section for this course
        //the start and end hours
        int intStartHour = Integer.parseInt(fileData.getStrStartTime().split(":")[0]);
        int intEndHour = Integer.parseInt(fileData.getStrEndTime().split(":")[0]);
        if(intStartHour < 8)
            intStartHour += 12;
        if(intEndHour < 8)
            intEndHour += 12;
        //get the times of this course we try to schedule and save them for later use
        LocalTime startTime = LocalTime.of(intStartHour, Integer.parseInt(fileData.getStrStartTime().split(":")[1]));
        LocalTime endTime = LocalTime.of(intEndHour, Integer.parseInt(fileData.getStrEndTime().split(":")[1]));
        LocalTime originalStartTime = startTime;
        LocalTime originalEndTime = endTime;
        String strOriginalDay = fileData.getStrDays();
        //try to schedule on the original day from the given times to end of day
        if(loopTrySchedule2Or1Credit(startTime, endTime, LAST_SCHEDULE_TIME_F, intNewCourseSection, fileData))
            return mapSections;
        //now change the day
        fileData.setStrDays(nextDayNoFriday(strOriginalDay));
        startTime = FIRST_SCHEDULE_TIME_MR_2And1Cred;
        endTime = FIRST_SCHEDULE_TIME_MR_2And1Cred.plus(course.getIntCreditHours(), ChronoUnit.HOURS);
        //loop until we get back to our original day.
        while(!fileData.getStrDays().equals(strOriginalDay)){
            if(loopTrySchedule2Or1Credit(startTime, endTime, LAST_SCHEDULE_TIME_F, intNewCourseSection, fileData))
                return mapSections;
            //if not scheduled we want to increment our day
            fileData.setStrDays(nextDayNoFriday(fileData.getStrDays()));
        }
        //now we have to try the times before our original time on our original day
        endTime = FIRST_SCHEDULE_TIME_MR_2And1Cred.plus(course.getIntCreditHours(), ChronoUnit.HOURS);
        if(loopTrySchedule2Or1Credit(FIRST_SCHEDULE_TIME_MR_2And1Cred, endTime, originalStartTime, intNewCourseSection, fileData))
            return mapSections;
        //now we have to try friday
        fileData.setStrDays("F");
        endTime = FIRST_SCHEDULE_TIME_MR_2And1Cred.plus(course.getIntCreditHours(), ChronoUnit.HOURS);
        if(loopTrySchedule2Or1Credit(FIRST_SCHEDULE_TIME_MR_2And1Cred, endTime, LAST_SCHEDULE_TIME_F, intNewCourseSection, fileData))
            return mapSections;
        //could not schedule course
        fileData.setStrDays(strOriginalDay);
        fileData.setStrStartTime(originalStartTime.toString());
        fileData.setStrEndTime(originalEndTime.toString());
        System.out.println("Could not schedule "+ intNewCourseSection + " for " + fileData.toString());
        return mapSections;
    }
    public HashMap<String, Integer> Schedule(objFileData fileData, HashMap<String, Integer> mapSections) throws SQLException, ClassNotFoundException {
        /**
         * @Name : Schedule
         * @Params : fileData - the file data object model for the course we want to schedule
         *           mapSections - a hashmap that has key value pairs of course, current section number.
         * @Returns : mapSections - a hashmap that has key value pairs of course, current section number.
         * @Purpose : The purpose of this method is to send the filedata that we are trying to schedule to the proper
         *            scheduling method based off of credit hours
         */
        objCourse course = databaseAccessObject.getCourse(fileData.getStrCourseName());
            switch (course.getIntCreditHours()){
                case 1:
                case 2:
                    return scheduleOneOrTwoCredit(fileData, mapSections);
                case 3:
                case 4:
                    return scheduleFourOrThreeCredit(fileData, mapSections);
                default:
                    System.out.println("Hit default in schedule method. Somehow");
                    return null;
            }

    }

    public HashMap<String, Integer> scheduleAll(ArrayList<objFileData> lstFileData, HashMap<String, Integer> mapSections) throws SQLException, ClassNotFoundException {
        /**
         * @Name : scheduleAll
         * @Params : lstFileData - a list of file data object models representing all course sections we want to schedule
         *           mapSections - a hashmap with key value pairs of course name, current section number.
         * @Returns : mapSections - a hashmap with key value pairs of course name, current section number.
         * @Purpose : the purpose of this method is to try to schedule every single fileData object in lstFileData and
         *            then return mapSections so that this objects controlling class.
         */
        for(objFileData d : lstFileData){
            //try to schedule the course.
            mapSections = Schedule(d, mapSections);
        }
        return mapSections;
    }
    private void printHeaders(){
        /**
         * @Name : printHeaders
         * @Params : none
         * @Returns : none
         * @Purpose : the purpose of this method is to print headers for our reports.
         */
        System.out.println("--------------------------------------------------------------------------------------------------------");
        System.out.println("Course ID     Section     Professor Name     Classroom     Capacity     Days     Start Time     End Time");
        System.out.println("--------------------------------------------------------------------------------------------------------");
    }
    public void printReportDayTime() throws SQLException, ClassNotFoundException {
        /**
         * @Name : printReportDayTime
         * @Params : none
         * @Returns : none
         * @Purpose : The purpose of this method is to print out a report of every single scheduled section of a course
         *            by day and time. We make sure the output is formatted nicely.
         */
        //get a list that has the proper order of objects from our database access object.
        ArrayList<objReport> lstReport = databaseAccessObject.getScheduledCoursesByDayTime();
        //print headers
        System.out.println("Printing out report by day and time");
        printHeaders();
        //loop through each list entry and print it out with the formatted print statement.
        for (objReport r : lstReport){
            System.out.printf("%-13s %-11d %-18s %-13s %-12d %-8s %-14s %-8s\n", r.getStrCourseName(), r.getIntCourseSection(),
                    r.getStrProfessorName(), r.getStrClassroomName(), r.getIntClassroomCapacity(), r.getStrDays(),
                    r.getStrStartTime(), r.getStrEndTime());
        }
    }
    public void printReportProfessor() throws SQLException, ClassNotFoundException {
        /**
         * @Name : printReportProfessor
         * @Params : none
         * @Returns : none
         * @Purpose : the purpose of this method is to print out a report of all scheduled courses ordered by professor
         *            name in alphabetical order
         */
        //get a list that has the proper order of objects from our database access object.
        ArrayList<objReport> lstReport = databaseAccessObject.getScheduledCoursesByProfessor();
        //print headers
        System.out.println("Printing out report by professor");
        printHeaders();
        //loop through each list entry and use the formatted print method to print the information out nicely
        for (objReport r : lstReport){
            System.out.printf("%-13s %-11d %-18s %-13s %-12d %-8s %-14s %-8s\n", r.getStrCourseName(), r.getIntCourseSection(),
                    r.getStrProfessorName(), r.getStrClassroomName(), r.getIntClassroomCapacity(), r.getStrDays(),
                    r.getStrStartTime(), r.getStrEndTime());
        }
    }
    public void printReportCourse() throws SQLException, ClassNotFoundException {
        /**
         * @Name : printReportCourse
         * @Params : none
         * @Returns : none
         * @Purpose : The purpose of this method is to print a report of every course ordered by course ID, in ascending order.
         */
        //get a list that has the proper order of objects from our database access object.
        ArrayList<objReport> lstReport = databaseAccessObject.getScheduledCoursesByCourse();
        //print headers
        System.out.println("Printing out report by course name");
        printHeaders();
        //loop through each list entry and use the formatted print statement to print the object nicely.
        for (objReport r : lstReport){
            System.out.printf("%-13s %-11d %-18s %-13s %-12d %-8s %-14s %-8s\n", r.getStrCourseName(), r.getIntCourseSection(),
                    r.getStrProfessorName(), r.getStrClassroomName(), r.getIntClassroomCapacity(), r.getStrDays(),
                    r.getStrStartTime(), r.getStrEndTime());
        }
    }

}