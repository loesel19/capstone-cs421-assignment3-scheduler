import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestClass {
    private static DatabaseAccessObject DAO = new DatabaseAccessObject();
    private static final String testFilePath = "testFile1.txt";
    private static final String SCHEDULE_FILE_PATH = "Schedule_Slot_Catalog.txt";
    public static void main(String args[]) throws SQLException, ClassNotFoundException, IOException {

        runDBTests();
        runFileTests();
        runScheduleTests();
    }
    public static boolean DBTest1() throws SQLException, ClassNotFoundException {
        /**
         * Name : DBTest 1
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : Here we check to see if our database startup will create a new database if none exists
         * Notes :
         */
        //this test is to see if our DatabaseAccessObject.checkDBExists returns false when there is no database
        System.out.println("Running DBTest 1 ...");
        try{
            DAO.startUp();
        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;

    }

    public static boolean DBTest2() throws SQLException, ClassNotFoundException {
        /**
         * Name : DBTest 2
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : the purpose of this method is to see if we can search for objects in our database given good input.
         *           This test also lets us know if our professor, course and classroom tables are being initialized properly
         * Notes : This test does not include anything from the schedule table
         */
        System.out.println("Running DBTest 2 ...");
        objCourse course = DAO.getCourse("CSC 105");
        if(course == null){
            System.out.println("Failed to get Course ");
            return false;
        }
        System.out.println(course.getIntCourseTUID() + " : " + course.getStrCourseID() + " : " +
                course.getStrCourseTitle() + " : " + course.getIntCreditHours());
        //now we do professor
        objProfessor professor = DAO.getProfessor("James");
        if(professor == null){
            System.out.println("Failed to get Professor");
            return false;
        }
        System.out.println(professor.getIntProfessorTUID() + " : " + professor.getStrProfessorName());

        //now do classroom
        objClassroom classroom = DAO.getClassroom("A");
        if(classroom == null){
            System.out.println("Failed to get Classroom");
            return false;
        }
        System.out.println(classroom.getIntClassroomTUID() + " : " + classroom.getStrClassroomName() + " : " +
                classroom.getIntCapacity());

        return true;
    }
    public static boolean DBTest3() throws SQLException, ClassNotFoundException {
        /**
         * Name : DBTest 3
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : the purpose of this method is to test that our method for getting a list with all scheduled courses
         *           works properly.
         */
        System.out.println("Running DBTest 3 ...");
        //first schedule a few more courses
        DAO.addSchedule(1, "02", 1, 1, "10:30", "12:30", "MW");
        DAO.addSchedule(1, "03", 1, 1, "12:30", "2:30", "MW");
        DAO.addSchedule(1, "04", 1, 1, "12:00", "3:00", "W");

        ArrayList<objSchedule> schedule = DAO.readAllScheduled();
        //check that the object is not null and contains 4 objSchedule
        if(schedule == null || schedule.size() != 3){

            return false;
        }
        for(objSchedule s : schedule){
            System.out.println(s.intTUID + " : " + s.getIntCourseTUID() + " : " + s.getStrCourseSection() + " : " + s.getIntClassroomTUID() + " : " +
                    s.getIntProfessorTUID() + " : " + s.getStrStartTime() + " : " + s.getStrEndTime() + " : " + s.getStrDays());
        }

        return true;
    }
    public static boolean DBTest4() throws SQLException, ClassNotFoundException {
        /**
         * Name : DB Test 4
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : The purpose of this test is to see that our end procedure of asking the user if they want to delete
         *           the database or not executes properly when the Program finishes running.
         */
        System.out.println("Running DBTest 4 ...");
        try {
            DAO.endSession();
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
    public static boolean DBTest5() throws SQLException, ClassNotFoundException {
        /**
         * Name : DBTest5
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : This test checks to see if our database access object method for finding an available slot
         *           will work when there are no classes scheduled in that spot
         */
        System.out.println("STARTING DBTest 5 ...");
        objClassroom classroom = DAO.getFreeClassroom("10:00", "11:00", "M");
        if(classroom != null){
            System.out.println("getFreeClassroom method returned " + classroom.getStrClassroomName());
            return true;
        }
        return false;
    }
    public static boolean DBTest6() throws SQLException, ClassNotFoundException {
        /**
         * Name : DBTest 6
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : This test makes sure we can add to our schedule table
         */
        DAO.addSchedule(1,"05", 1, 1, "8:30", "10:30","TR");
        DAO.addSchedule(1, "06",2, 3, "8:30", "12:30", "TR");
        DAO.addSchedule(1,"07", 3, 2, "10:00", "2:00", "T");
        return true;
    }
    public static boolean DBTest7() throws SQLException, ClassNotFoundException, IOException {
        /**
         * Name : DBTest7
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : The purpose of this class is to see that we can get all scheduled classes
         */
        System.out.println("STARTING DBTEST 7 ...");
        HashMap<String, ArrayList<objSchedule>> mapSchedule;
        mapSchedule = DAO.getAllScheduled();
        for(Map.Entry<String, ArrayList<objSchedule>> e : mapSchedule.entrySet()){
            System.out.println("All classes on " + e.getKey() + " ---");
            for(objSchedule s : e.getValue()){
                System.out.println(s.getIntTUID() + " " + s.getStrDays() + " " + s.getStrStartTime() + " " + s.getStrEndTime());
            }
        }
        return true;
    }
    public static boolean DBTest8() throws SQLException, ClassNotFoundException, IOException {
        /**
         * Name : DBTest8
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose :
         */
        System.out.println("Starting DB Test 8");
        String strDaysCheck = "TR";
        String strStartTime = "10:00";
        String strEndTime = "12:00";
        FileInteractionObject fileInteractionObject = new FileInteractionObject();
        fileInteractionObject.instanciateBufferedReader(SCHEDULE_FILE_PATH);
        HashMap<String, ArrayList<String>> mapTimes = fileInteractionObject.getTimes();
        HashMap<String, ArrayList<objSchedule>> mapSchedule = DAO.getAllScheduled();
        String strSeperateDays[] = getDaysSeperate(strDaysCheck);
        ArrayList<Integer> lstScheduledTUIDS = new ArrayList<>();
        int intStartIndex = 0;
        int intEndIndex = 0;

        for(int i = 0; i < strSeperateDays.length; i++){
            System.out.println("Looking at " + strSeperateDays[i]);
            ArrayList<String> lstTemp = mapTimes.get(strSeperateDays[i]);
            //lstTemp is every possible time slot on the given day
            //loop through until s = our start time, then from our start time to end time check mapSchedule on the days
            //to see how many classes are in the time period
            for (String s : lstTemp){
                if(s.equals(strStartTime)){
                    intStartIndex = lstTemp.indexOf(s);
                }
                if(s.equals(strEndTime)){
                    intEndIndex = lstTemp.indexOf(s);
                    break;
                }
            }
            //now that we have the index of the start and end times lets loop through and try to find scheduled classes
            ArrayList<objSchedule> lstSchedule = mapSchedule.get(strSeperateDays[i]);
            ArrayList<String> lstTimes = mapTimes.get(strSeperateDays[i]);
            for(int j = intStartIndex; j <= intEndIndex; j++){
                String strCurrTime = lstTimes.get(j);


                for(objSchedule schedule : lstSchedule){
                    LocalTime time = LocalTime.parse("9:00");
                    if(schedule.getStrStartTime().equals(strCurrTime)){
                        if(!lstScheduledTUIDS.contains(schedule.getIntTUID())) {
                            System.out.println("Added " + schedule.getIntTUID() + " on " + strSeperateDays[i]);

                            lstScheduledTUIDS.add(schedule.getIntTUID());
                        }else{
                            System.out.println("** caught extra");
                        }
                    }
                    if(schedule.getStrEndTime().equals(strCurrTime)){
                        if(!lstScheduledTUIDS.contains(schedule.getIntTUID())) {
                            System.out.println("Added " + schedule.getIntTUID() + " on " + strSeperateDays[i]);

                            lstScheduledTUIDS.add(schedule.getIntTUID());
                        }else{
                            System.out.println("** caught extra");
                        }
                    }
                }

            }
        }
        System.out.println("On "+ strDaysCheck + " " + strStartTime + "-" + strEndTime + " There are " + lstScheduledTUIDS.size() + " already scheduled");

        return true;
    }
    HashMap<String, ArrayList<String>> getAllTimesForClass(String strStartTime, String strEndTime, String[] strDays) throws IOException {
        FileInteractionObject fileInteractionObject = new FileInteractionObject();
        fileInteractionObject.instanciateBufferedReader("Schedule_Slot_Catalog.txt");
        HashMap<String, ArrayList<String>> mapTimes = fileInteractionObject.getTimes();
        HashMap<String, ArrayList<String>> mapCurrentClassTimes = new HashMap<>();
        for (int i = 0; i < strDays.length; i++){
            String strKey = strDays[i];
            ArrayList<String> lstAllTimesOnDay = mapTimes.get(strKey);
            ArrayList<String> lstTimeFiller = new ArrayList<>();
            boolean blnAdd = false;
            for(String s : lstAllTimesOnDay){

                if(blnAdd){
                    lstTimeFiller.add(s);
                }
                if(s.equals(strStartTime)){
                    lstTimeFiller.add(s);
                    blnAdd = true;
                }
                if(s.equals(strEndTime)){
                    break;
                }
            }
            mapCurrentClassTimes.put(strKey, lstTimeFiller);
        }
        return mapCurrentClassTimes;

    }
    public static String[] getDaysSeperate(String strDays){
        switch (strDays){
            case "MW":
                return new String[]{"M", "W"};
            case "TR":
                return new String[]{"T", "R"};
            default:
                return new String[]{strDays};
        }
    }
    public static void runDBTests() throws SQLException, ClassNotFoundException, IOException {
        /**
         * Name : runDBTests
         * Returns : none.
         * Purpose : This function is meant to run the database tests that have been constructed and output their results
         */
        System.out.println("BEGIN DATABASE TESTS .....");
        System.out.println("DBTest1 : " + DBTest1());
        System.out.println("DBTest2 : " + DBTest2());
        System.out.println("DBTest3 : " + DBTest3());
        System.out.println("DBTest4 : " + DBTest4() + " Disregard false if 1 input on DBTest1.");
        System.out.println("DBTest5 : " + DBTest5());
        System.out.println("DBTest6 : " + DBTest6());
        System.out.println("DBTest7 : " + DBTest7());
        //System.out.println("DBTest8 : " + DBTest8());

        System.out.println("END DATABASE TESTS .....");
    }
    public static boolean FileTest1() throws IOException {
        /**
         * Name : FileTest1
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : The purpose of this Test is to see if we can make an instance of our fileInteractionObject, and
         *           read a single line of it.
         */
        System.out.println("STARTING FILE TEST 1 ...");
        FileInteractionObject fileInteractionObject = new FileInteractionObject();
        if(!fileInteractionObject.instanciateBufferedReader(testFilePath)){
            return false;
        }
        try {
            objFileData objFileData = fileInteractionObject.readFileLine();
            System.out.println(objFileData.getStrCourseName() + " : " + objFileData.getStrProfessorName() + " : " +
                    objFileData.getStrDays() + " : " + objFileData.getStrStartTime() + " : " + objFileData.getStrEndTime());
            return true;
        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
    public static boolean FileTest2() throws IOException {
        /**
         * Name : FileTest2
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : The purpose of this method is to try and read all lines of our file into a list.
         */
        System.out.println("STARTING FILE TEST 2 ...");
        FileInteractionObject fileInteractionObject = new FileInteractionObject();
        if(!fileInteractionObject.instanciateBufferedReader(testFilePath)){
            return false;
        }
        ArrayList<objFileData> lstFileData = fileInteractionObject.readAllFileLine();
        for(objFileData d : lstFileData){
            System.out.println(d.toString());
        }
        return true;

    }
    public static boolean FileTest3() throws IOException {
        /**
         * Name : FileTest3
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : The purpose of this test is to take a known good file path from the user and check that we can open
         *           that file and then read its data out
         */
        System.out.println("STARTING FILE TEST 3 ...");
        Scanner sc = new Scanner(System.in);
        String strPath = "";
        System.out.println("Please enter the path to the file you would like to Schedule.");
        strPath = sc.next();
        FileInteractionObject fileInteractionObject = new FileInteractionObject();
        if(!fileInteractionObject.instanciateBufferedReader(strPath)){
            System.out.println("Bad path");
            return false;
        }
        //now get the list of data models and read them out
        ArrayList<objFileData> lstFileData = fileInteractionObject.readAllFileLine();
        for(objFileData d : lstFileData){
            System.out.println(d.toString());
        }
        return true;
    }
    public static boolean FileTest4() throws FileNotFoundException {
        /**
         * Name : FileTest4
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : The purpose of this test is to see if we can read the cataloged course slots into a hashmap.
         *           we also print them out if output wanted by test.
         */
        FileInteractionObject fileInteractionObject = new FileInteractionObject();
        fileInteractionObject.instanciateBufferedReader("Schedule_Slot_Catalog.txt");
        HashMap<String, ArrayList<String>> map = new HashMap<>();
        try{
            map = fileInteractionObject.getTimes();
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
        Scanner sc= new Scanner(System.in);
        System.out.println("Would you like to see all possible time slots? [y/n]");
        String strInput = "";
        while(!strInput.equals("y") && !strInput.equals("n"))
            strInput = sc.next().toLowerCase(Locale.ROOT);
        if(strInput.equals("y")){
            for(Map.Entry<String, ArrayList<String>> e : map.entrySet()){
                System.out.print("--" + e.getKey() + "--  ");
                for (String s : e.getValue()){
                    System.out.print(s + " ");
                }
                System.out.println("");
            }
        }
        return true;
    }
    public static void runFileTests() throws IOException {
        /**
         * Name : runFileTests
         * Params : none.
         * Returns : none.
         * Purpose : This function is meant to run the File tests that have been constructed and output their results
         */
        System.out.println("BEGIN FILE INTERACTION TESTS .....");
        System.out.println("FileTest1 : " + FileTest1());
        System.out.println("FileTest2 : " + FileTest2());
        System.out.println("FileTest3 : " + FileTest3());
        System.out.println("FileTest4 : " + FileTest4());
        System.out.println("END FILE INTERACTION TESTS .....");
    }
    public static boolean ScheduleTest1(){
        /**
         * Name : ScheduleTest1
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : the purpose of this test is to check that our regex patterns work for the desired time slots
         */
        System.out.println("STARTING SCHEDULE TEST 1 ...");
        //create a pattern that will match a time between 9:00AM and 3:00PM
        Pattern pattern = Pattern.compile("([0-1]?[0-3]|9):00");
        //now we want to try every time to see that it matches correctly
        Matcher matcher = pattern.matcher("9:00");
        if(!matcher.find()){
            System.out.println("Failed on 9:00");
            return false;
        }
        matcher = pattern.matcher("10:00");
        if(!matcher.find()){
            System.out.println("Failed on 10:00");
            return false;
        }
        matcher = pattern.matcher("11:00");
        if(!matcher.find()){
            System.out.println("Failed on 11:00");
            return false;
        }
        matcher = pattern.matcher("12:00");
        if(!matcher.find()){
            System.out.println("Failed on 12:00");
            return false;
        }
        matcher = pattern.matcher("1:00");
        if(!matcher.find()){
            System.out.println("Failed on 1:00");
            return false;
        }
        matcher = pattern.matcher("2:00");
        if(!matcher.find()){
            System.out.println("Failed on 2:00");
            return false;
        }
        matcher = pattern.matcher("3:00");
        if(!matcher.find()){
            System.out.println("Failed on 3:00");
            return false;
        }
        //now lets give it one it should fail
        matcher = pattern.matcher("5:00");
        if(matcher.find()){
            System.out.println("Failed on 5:00");
            return false;
        }
        return true;
    }
    public static boolean ScheduleTest2() throws SQLException, ClassNotFoundException {
        /**
         * Name : ScheduleTest2
         * Returns : boolean - true -> test passed, false -> test failed.
         * Purpose : The purpose of this method is to see if using the LocalTime class to compare times and see how classes are
         *           scheduled in a given time slot works.
         */

        String strTestDays = "TR";
        String strTestStartTime = "10:00";
        String strTestEndTime = "11:30";
        String strTestDaysSeperate[] = getDaysSeperate(strTestDays);
        ArrayList<Integer> lstAccountedForTUIDS = new ArrayList<>();
        HashMap<String, ArrayList<objSchedule>> mapScheduled = DAO.getAllScheduled();
        for(int i = 0; i < strTestDaysSeperate.length; i++){
            ArrayList<objSchedule> lstScheduledClassesOnDay = mapScheduled.get(strTestDaysSeperate[i]);
            LocalTime timeTestStart = LocalTime.of(Integer.parseInt(strTestStartTime.split(":")[0]),
                    Integer.parseInt(strTestStartTime.split(":")[1]));
            LocalTime timeTestEnd = LocalTime.of(Integer.parseInt(strTestEndTime.split(":")[0]),
                    Integer.parseInt(strTestEndTime.split(":")[1]));
            for(objSchedule s : lstScheduledClassesOnDay){
                LocalTime timeCurrentStart = getTimeFromString(s.getStrStartTime());
                LocalTime timeCurrentEnd = getTimeFromString(s.getStrEndTime());
                //first check if timeStart is after timeCurrentStart, and before timeCurrentEnd
                if(timeTestStart.isAfter(timeCurrentStart) && timeTestStart.isBefore(timeCurrentEnd)){
                    //WE NEED TO COUNT THIS AS A CLASS IN OUR SLOT
                    if(!lstAccountedForTUIDS.contains(s.getIntTUID())){
                        lstAccountedForTUIDS.add(s.getIntTUID());
                    }
                    continue;
                }
                //next see if start time or end times are equal
                if(timeCurrentStart.equals(timeTestStart) || timeCurrentEnd.equals(timeTestEnd)){
                    //WE NEED TO COUNT THIS AS A CLASS IN OUR SLOT
                    if(!lstAccountedForTUIDS.contains(s.getIntTUID())){
                        lstAccountedForTUIDS.add(s.getIntTUID());
                    }
                    continue;
                }
                //next see if timeEnd is between start and end time
                if(timeTestEnd.isAfter(timeCurrentEnd) && timeTestEnd.isBefore(timeCurrentEnd)){
                    //NEED TO COUNT IN OUR SLOT
                    if(!lstAccountedForTUIDS.contains(s.getIntTUID())){
                        lstAccountedForTUIDS.add(s.getIntTUID());
                    }
                    continue;
                }
                //next case we need to account for is if the class we try to schedule starts before a scheduled course and ends after a scheduled course
                if(timeCurrentStart.isAfter(timeTestStart) && timeCurrentStart.isBefore(timeTestEnd)){
                    //the current class that we are looking at starts after the class we are looking to add, but its start time is within the new classes endtime
                    if(!lstAccountedForTUIDS.contains(s.getIntTUID())){
                        lstAccountedForTUIDS.add(s.getIntTUID());
                    }
                    continue;
                }

            }
        }
        for(int tuid : lstAccountedForTUIDS){
            System.out.println(tuid + " is in the timeslot");
        }
        System.out.println("There are " + lstAccountedForTUIDS.size()+ " scheduled in slot.");
        return true;
    }
    public static LocalTime getTimeFromString(String strTime){
        LocalTime time = LocalTime.of(Integer.parseInt(strTime.split(":")[0]), Integer.parseInt(strTime.split(":")[1]));
        return time;
    }
    public static boolean ScheduleTest3() throws SQLException, ClassNotFoundException, IOException {
        /**
         * Name : ScheduleTest3
         * Returns : boolean, true -> test passed, false -> test failed
         * Purpose : The Purpose of this test is to see if we can take in a file that will max out all time slots for 4
         *           credit courses, and try to schedule them all.
         * Notes : this test has been deprecated since moving sections to a map.
         */
        System.out.println("STARTING SCHEDULE TEST 3 ...");
        DAO.endSession();
        DAO.startUp();

        FileInteractionObject fileInteractionObject = new FileInteractionObject();
        fileInteractionObject.instanciateBufferedReader("Test_Fill_Schedule_4_Cred.txt");
        SchedulerObject scheduler = new SchedulerObject(DAO, fileInteractionObject);
        scheduler.scheduleAll(fileInteractionObject.readAllFileLine(), fileInteractionObject.getSectionMap());
        HashMap<String, ArrayList<objSchedule>> mapAllScheduled = DAO.getAllScheduled();
        for(Map.Entry<String, ArrayList<objSchedule>> e : mapAllScheduled.entrySet()){
            for(objSchedule s : e.getValue()){
                System.out.println(s.toString());
            }
        }
        return true;
    }
    public static boolean ScheduleTest4() throws SQLException, ClassNotFoundException, IOException {
        /**
         * Name : ScheduleTest4
         * Returns : boolean - true -> test passed, false -> test failed.
         * Purpose : The purpose of this test is to see if we can schedule both 4 and 3 credit courses at the same time.
         * Notes : this test is deprecated since moving sections to a map
         */
        System.out.println("STARTING SCHEDULE TEST 4 ...");
        DAO.endSession();
        DAO.startUp();
        try {
            FileInteractionObject fileInteractionObject = new FileInteractionObject();
            fileInteractionObject.instanciateBufferedReader("Test_Fill_Three_And_Four_Cred.txt");
            SchedulerObject schedulerObject = new SchedulerObject(DAO, fileInteractionObject);
            schedulerObject.scheduleAll(fileInteractionObject.readAllFileLine(), fileInteractionObject.getSectionMap());
        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        HashMap<String, ArrayList<objSchedule>> mapAllScheduled = DAO.getAllScheduled();
        for(Map.Entry<String, ArrayList<objSchedule>> e : mapAllScheduled.entrySet()){
            for(objSchedule s : e.getValue()){
                System.out.println(s.toString());
            }
        }
        return true;
    }
    public static boolean ScheduleTest5() throws IOException, SQLException, ClassNotFoundException {
        /**
         * Name : ScheduleTest5
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : The purpose of this class is to test scheduling 1 and 2 credit courses, we just read them in
         *           from a file and then try to schedule them all.
         * Note : this test is deprecated since moving sections to a map
         */
        FileInteractionObject fileInteractionObject = new FileInteractionObject();
        fileInteractionObject.instanciateBufferedReader("Test_Schedule_1_2_Cred.txt");
        SchedulerObject schedulerObject = new SchedulerObject(DAO, fileInteractionObject);
        schedulerObject.scheduleAll(fileInteractionObject.readAllFileLine(), fileInteractionObject.getSectionMap());

        return true;
    }
    public static void runScheduleTests() throws SQLException, ClassNotFoundException, IOException {
        /**
         * Name : runScheduleTests
         * Params : None
         * Purpose : The purpose of this method is to run our scheduling tests.
         */
        System.out.println("STARTING SCHEDULE TESTS .....");
        System.out.println("SCHEDULE TEST 1 : " + ScheduleTest1());
        //System.out.println("SCHEDULE TEST 2 : " + ScheduleTest2());
//        System.out.println("SCHEDULE TEST 3 : " + ScheduleTest3());
        //System.out.println("SCHEDULE TEST 4 : " + ScheduleTest4());
        System.out.println("END SCHEDULE TESTS .....");
    }
}
