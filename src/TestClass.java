import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestClass {
    private static DatabaseAccessObject DAO = new DatabaseAccessObject();
    private static final String testFilePath = "testFile1.txt";
    public static void main(String args[]) throws SQLException, ClassNotFoundException, IOException {

        runDBTests();
        runFileTests();
        runScheduleTests();
    }
    public static boolean DBTest1() throws SQLException, ClassNotFoundException {
        /**
         * Name : Test1
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : Here we check to see if our database startup will create a new database if none exists
         * Notes :
         */
        //this test is to see if our DatabaseAccessObject.checkDBExists returns false when there is no database
        System.out.println("Running DBTest1 ...");
        try{
            DAO.startUp();
        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;

    }

    public static boolean DBTest3() throws SQLException, ClassNotFoundException {
        /**
         * Name : Test3
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : the purpose of this method is to see if we can search for objects in our database given good input.
         *           This test also lets us know if our professor, course and classroom tables are being initialized properly
         * Notes : This test does not include anything from the schedule table
         */
        System.out.println("Running DBTest3 ...");
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
    public static boolean DBTest4() throws SQLException, ClassNotFoundException {
        /**
         * Name : Test4
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : the purpose of this method is to test that our method for getting a list with all scheduled courses
         *           works properly.
         */
        System.out.println("Running DBTest4 ...");
        //first schedule a few more courses
        DAO.addSchedule(1, "02", 1, 1, "10:30", "12:30", "MW");
        DAO.addSchedule(1, "03", 1, 1, "12:30", "02:30", "MW");
        DAO.addSchedule(1, "04", 1, 1, "02:30", "04:30", "MW");

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
    public static boolean DBTest5() throws SQLException, ClassNotFoundException {
        /**
         * Name : Test5
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : The purpose of this test is to see that our end procedure of asking the user if they want to delete
         *           the database or not executes properly when the Program finishes running.
         */
        System.out.println("Running DBTest5 ...");
        try {
            DAO.endSession();
            return true;
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
    public static void runDBTests() throws SQLException, ClassNotFoundException {
        /**
         * Name : runDBTests
         * Returns : none.
         * Purpose : This function is meant to run the database tests that have been constructed and output their results
         */
        System.out.println("BEGIN DATABASE TESTS .....");
        System.out.println("DBTest1 : " + DBTest1());
        //dbtest2 deleted
        System.out.println("DBTest3 : " + DBTest3());
        System.out.println("DBTest4 : " + DBTest4() + "Disregard false if 1 input on DBTest1.");
        System.out.println("DBTest5 : " + DBTest5());
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
        FileInteractionObject fileInteractionObject = new FileInteractionObject(testFilePath);
        if(!fileInteractionObject.instanciateBufferedReader()){
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
        FileInteractionObject fileInteractionObject = new FileInteractionObject(testFilePath);
        if(!fileInteractionObject.instanciateBufferedReader()){
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
        FileInteractionObject fileInteractionObject = new FileInteractionObject(strPath);
        if(!fileInteractionObject.instanciateBufferedReader()){
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
    public static boolean ScheduleTest2(){
        /**
         * Name : ScheduleTest2
         * Returns : boolean - true -> test passed, false -> test failed.
         * Purpose : The purpose of this test is to see if our method to not schedule two classes within the same
         *           time slot works.
         */

        return true;
    }
    public static void runScheduleTests(){
        /**
         * Name : runScheduleTests
         * Params : None
         * Purpose : The purpose of this method is to run our scheduling tests.
         */
        System.out.println("STARTING SCHEDULE TESTS .....");
        System.out.println("SCHEDULE TEST 1 : " + ScheduleTest1());
        System.out.println("END SCHEDULE TESTS .....");
    }
}
