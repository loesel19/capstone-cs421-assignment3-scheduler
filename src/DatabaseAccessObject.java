import javax.swing.plaf.nimbus.State;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.constant.Constable;
import java.sql.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseAccessObject {
    /**
     * File Name : DatabaseAccessObject
     * File Author : Andrew A. Loesel
     * Part of Project : CS 421 Assignment 3 - class scheduler
     * Organization : Saginaw Valley State University
     * Professor : Scott D. James
     * File Purpose : The purpose of this file is to handle all of our database CRUD (Create Read Update Delete)
     *                operations for the project. We will outline methods to each of these operations for our tables.
     */
    //GLOBAL VARIABLES GLOBAL VARIABLES GLOBAL VARIABLES GLOBAL VARIABLES
    private static final String dbFileName = "ScheduleDatabase.db"; //what we will name our database
    private static final String connString = "jdbc:sqlite:" + dbFileName; //the connection string for our database
    private static final String SCHEDULE_TABLE_STRING = "SCHEDULE_TABLE"; //the name of the table we schedule in
    private static final String COURSE_TABLE_STRING = "COURSES_TABLE"; //the name of our course table
    private static final String PROFESSOR_TABLE_STRING = "PROFESSOR_TABLE"; //the name of our professor table
    private static final String CLASSROOM_TABLE_STRING = "CLASSROOM_TABLE";
    private static final String COURSE_CATALOG_PATH_STRING = "Course_Catalog.txt";//the path of the course catalog we want to use
    private static final String CLASSROOM_CATALOG_PATH_STRING = "Classroom_Catalog.txt"; //the path of the classroom catalog we want to use
    private static final String PROFESSOR_CATALOG_PATH_STRING = "Professor_Catalog.txt"; //the path of the professor catalog we want to use

    //SUBPROGRAMS SUBPROGRAMS SUBPROGRAMS SUBPROGRAMS SUBPROGRAMS
    private static Connection getConnection() throws ClassNotFoundException, SQLException{
        /**
         * Name : getConnection
         * Params : none.
         * Returns : con - the sqlConnection for our database.
         * Purpose : the purpose of this method is to return the sqlConnection object for our database.
         * Notes :
         */
        Connection con;
        con = DriverManager.getConnection(connString);
        return con;
    }
    private boolean checkDBExists() throws ClassNotFoundException, SQLException{
        /**
         * Name : checkDBExists
         * Params : none.
         * Returns : boolean | true -> exists, false -> does not exist.
         * Purpose : this method will be to check if the database exists when the application is started up.
         *           We simply want to check and see if  //TODO//
         * Notes :
         */
        boolean blnExists = false;
        try{
            /* to see if the db exists we will try to get the connection, and execute a sql statement on one of the tables */
            Connection con = getConnection();
            Statement statement = con.createStatement();
            statement.execute("SELECT * FROM " + PROFESSOR_TABLE_STRING + "");
            blnExists = true;
            con.close();
        }catch (Exception ex){
            blnExists = false;
        }
        return blnExists;
    }
    private boolean createProfessorTable() throws SQLException, ClassNotFoundException {
        /**
         * Name : createProfessorTable
         * Params : None.
         * Returns : bool - true -> table created, false -> table not created.
         * Purpose : The purpose of this method is to try and create our professor table in our database.
         * Notes :
         */
        //get connection and create a statement
        boolean blnCreated = false;
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        String strSQL = "CREATE TABLE " + PROFESSOR_TABLE_STRING + " (TUID INTEGER PRIMARY KEY AUTOINCREMENT, PROFESSOR_NAME CHAR(50))";
        try{
            statement.execute(strSQL);
            blnCreated = true;
        }catch (Exception ex){

            ex.printStackTrace();
            blnCreated = false;
        }
        statement.close();
        connection.close();
        return blnCreated;
    }
    private boolean createCourseTable() throws SQLException, ClassNotFoundException {
        /**
         * Name : createCourseTable
         * Params : None
         * Returns : blnCreated - a boolean where true -> table created, false -> table not created.
         * Purpose : The purpose of this method is to try and create our course table in our database.
         * Notes :
         */
        boolean blnCreated = false;
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        String strSQL = "CREATE TABLE " + COURSE_TABLE_STRING + " (TUID INTEGER PRIMARY KEY AUTOINCREMENT, COURSE_ID CHAR(10), COURSE_TITLE CHAR(100)," +
                " CREDITS INTEGER)";
        try{
            statement.execute(strSQL);
            blnCreated = true;
        }catch (Exception ex){

            ex.printStackTrace();
            blnCreated = false;
        }
        statement.close();
        connection.close();

        return blnCreated;
    }
    private boolean createClassroomTable() throws SQLException, ClassNotFoundException {
        /**
         * Name : createClassroomTable
         * Params : None
         * Returns : blnCreated - a boolean where true -> table created, false -> table not created.
         * Purpose : The purpose of this method is to try and create our classroom table in our database.
         * Notes :
         */
        boolean blnCreated = false;
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        String strSQL = "CREATE TABLE " + CLASSROOM_TABLE_STRING + " (TUID INTEGER PRIMARY KEY AUTOINCREMENT, CLASSROOM_NAME CHAR(20), CAPACITY INTEGER)";
        try{
            statement.execute(strSQL);
            blnCreated = true;
        }catch (Exception ex){

            ex.printStackTrace();
            blnCreated = false;
        }
        statement.close();
        connection.close();

        return blnCreated;
    }
    private boolean createScheduleTable() throws SQLException, ClassNotFoundException {
        /**
         * Name : createScheduleTable
         * Params : None
         * Returns : blnCreated - a boolean where true -> table created, false -> table not created.
         * Purpose : The purpose of this method is to try and create our Schedule table in our database.
         * Notes :
         */
        boolean blnCreated = false;
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        String strSQL = "CREATE TABLE " + SCHEDULE_TABLE_STRING + " (TUID INTEGER PRIMARY KEY AUTOINCREMENT, COURSE_TUID INTEGER, COURSE_SECTION INTEGER," +
                " CLASSROOM_TUID INTEGER, PROFESSOR_TUID INTEGER, START_TIME CHAR(5), END_TIME CHAR(5), DAYS CHAR(2))";
        try{
            statement.execute(strSQL);
            blnCreated = true;
        }catch (Exception ex){

            ex.printStackTrace();
            blnCreated = false;
        }
        statement.close();
        connection.close();

        return blnCreated;
    }
    private void createTables() throws  ClassNotFoundException, SQLException{
        /**
         * Name : createTables
         * Params : none.
         * Returns : none.
         * Purpose : When called this function will create the 4 tables we need in our database. Those being : tbl_class,
         *           tbl_proffessor, tbl_classroom, and tbl_schedule
         * Notes :
         */
        if(!createProfessorTable()){
            System.out.println("Error creating professor table");
        }
        if(!createClassroomTable()){
            System.out.println("Error creating classroom table");
        }
        if(!createCourseTable()){
            System.out.println("Error creating course table");
        }
        if(!createScheduleTable()){
            System.out.println("Error creating schedule table");
        }

    }
    private void destroyDB() throws ClassNotFoundException, SQLException{
        /**
         * Name : destroyDB
         * Params : none.
         * Returns : none.
         * Purpose : The purpose of this method is to destroy our entire database when called.
         * Notes :
         */
        try {
            File dbFile = new File("ScheduleDatabase.db");
            if(dbFile.delete()){
                System.out.println("Old Database has been deleted.");
                return;
            }
            System.out.println("Could not delete database");

        }catch (Exception ex){
            ex.printStackTrace();

        }
    }
    private void initializeProfessorTable() throws IOException {
        /**
         * Name : initializeProfessorTable
         * Params : none
         * Returns : none
         * Purpose : The purpose of this method is to initialize our professor table, we first get all professors from
         *           our classroom catalog by getting a list of classroom data models from the file interaction object.
         *           we then try to add the professors to our professor table.
         */
        FileInteractionObject fileInteractionObject = new FileInteractionObject();
        fileInteractionObject.instanciateBufferedReader(PROFESSOR_CATALOG_PATH_STRING);
        ArrayList<objProfessor> professors = fileInteractionObject.readAllCatalogedProfessors();
        for(objProfessor p : professors){
            try{
                addProfessor(p.getStrProfessorName());
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
    private void initializeClassroomTable() throws IOException {
        /**
         * Name : initializeClassroomTable
         * Params : none
         * Returns : none
         * Purpose : The purpose of this method is to initialize the classroom table. We first get all of the classrooms
         *           from our classroom catalog using our fileInteraction object. We then loop through all of these classroom
         *           data object models and insert them into our classroom table.
         */
        FileInteractionObject fileInteractionObject = new FileInteractionObject();
        fileInteractionObject.instanciateBufferedReader(CLASSROOM_CATALOG_PATH_STRING);
        ArrayList<objClassroom> classrooms = fileInteractionObject.readAllCatalogedClassrooms();
        for(objClassroom c : classrooms){
            try{
                addClassroom(c.getStrClassroomName(), c.getIntCapacity());
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
    }
    private void initializeCourseTable() throws IOException, SQLException, ClassNotFoundException {
        /**
         * Name : initializeCourseTable
         * Params : None
         * Returns : None
         * Purpose : The purpose of this method is to initialize the Course table. We first read in all of the courses
         *           from our course catalog file and receive them as a list of course data models from our fileInteractionObject.
         *           We then loop through all of the objects and add them to the courses table
         */
        FileInteractionObject fileInteractionObject = new FileInteractionObject();
        fileInteractionObject.instanciateBufferedReader(COURSE_CATALOG_PATH_STRING);
        ArrayList<objCourse> courses = fileInteractionObject.readAllCatalogedCourses();
        for(objCourse c : courses){
            try {
                addCourse(c.getStrCourseID(), c.getStrCourseTitle(), c.getIntCreditHours());
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

    }
    private void wipeScheduleTable() throws SQLException, ClassNotFoundException {
        /**
         * Name : wipeScheduleTable
         * Params : none.
         * Return : none.
         * Purpose : the purpose of this function is to delete all data from the schedule table
         * Notes :
         */
        //get connection object and create a statement
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        String strSQL = "DELETE FROM " + SCHEDULE_TABLE_STRING + "";
        try{
            statement.execute(strSQL);
            strSQL = "DELETE FROM SQLITE_SEQUENCE WHERE name='" + SCHEDULE_TABLE_STRING + "'";
            statement.execute(strSQL);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        statement.close();
        connection.close();

    }
    public void resetDB() throws SQLException, ClassNotFoundException {
        /**
         * Name : resetDB
         * Params : none.
         * Returns : none.
         * Purpose : The purpose of this method is to reset the database to its default state. We first
         *           call our method to wipe the schedule table. We then make sure that our static tables (PROFESSOR, COURSES, CLASSROOM)
         *           have the default data in them.
         * Notes :
         */
        wipeScheduleTable();
    }
    private void createDB(){
        /**
         * Name : createDB
         * Params : none.
         * Returns : none.
         * Purpose : The purpose of this method is to create our database.
         * Notes :
         */
        try {
            DatabaseMetaData metaData = getConnection().getMetaData();
            //create the tables for the database
            createTables();
            System.out.println("New database created.");
        } catch (Exception ex){
            ex.printStackTrace();
            System.out.println("An error has occurred.");
        }

    }
    public void startUp() throws SQLException, ClassNotFoundException, IOException {
        /**
         * Name : startUp
         * Params : none.
         * Returns : none.
         * Purpose : This method will run when the application is started. We want to check if the database exists,
         *           and if it does ask the user if they would like to delete the database and start fresh.
         * Notes :
         */
        Scanner sc = new Scanner(System.in); //scanner for user inputs
        if(checkDBExists()){
            String strInput = ""; //string for user input. string instead of a char so we do not throw an exception if user inputs more than 1 character
            /* database exists already ask user if they would like to destroy it */
            System.out.println("Database exists already, would you like to reset it?");

            while (!strInput.equals("0") && !strInput.equals("1")) {
                System.out.println("Enter 0 to reset, 1 to keep");
                strInput = sc.next();
            }
            if(strInput.equals("1")){
                /* keep  database, so do nothing here and return */
                return;
            }
            //reset the db and return from the function
            resetDB();
            return;
        }
        /* if we make it our here we have no DB because it either never existed or it was destroyed.
            so let's create our database and initialize our 'static' tables
         */
        createDB();
        initializeCourseTable();
        initializeClassroomTable();
        initializeProfessorTable();
        return;
    }
    public void endSession() throws SQLException, ClassNotFoundException {
        /**
         * Name : endSession
         * Params : none.
         * Returns : none.
         * Purpose : The purpose of this function is to check if the user would like to destroy or keep the database
         *           when the program execution comes to an end.
         * Notes :
         */
        Scanner sc = new Scanner(System.in);
        String strInput = "";

        while (!strInput.equals("0") && !strInput.equals("1")) {
            System.out.println("Enter 0 to destroy database, 1 to keep.");
            strInput = sc.next();
        }
        if(strInput.equals("1")){
            return;
        }
        destroyDB();
    }
    public boolean addCourse(String strCourseID, String strCourseTitle, int intCourseHours) throws SQLException, ClassNotFoundException {
        /**
         * Name : addClass
         * Params : strCourseID - the course number, ex. CSC 105
         *          strCourseTitle - the course title, ex. Computers and Programming
         *          intCourseHours - the credit hours of the course, ex. 4
         * Returns : blnAdded - true -> sucess, false -> failure
         * Purpose : this method takes 3 inputs as parameters and tries to add them as an entry in the class table
         *           of our database.
         * Notes :
         */
        boolean blnAdded = false;
        //get connection and sql statement objects
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        //now lets make a string with our sql insert statement
        String strSQL = "INSERT INTO " + COURSE_TABLE_STRING + " (COURSE_ID, COURSE_TITLE, CREDITS)" +
                " values ('" + strCourseID + "', '" + strCourseTitle + "', " + intCourseHours + ");";
        //try to run the statement
        try{
            statement.execute(strSQL);
            blnAdded = true;
        } catch (Exception ex){
            ex.printStackTrace();
        }
        statement.close();
        connection.close();
        return blnAdded;
    }
    public boolean addProfessor(String strProfessorName) throws SQLException, ClassNotFoundException {
        /**
         * Name : addProfessor
         * Params : strProfessorName - the name of the professor we are adding, ex. Will Williams
         * Returns : blnAdded - true -> sucess, false -> failure
         * Purpose : In this method we try to insert a new professor entry in our database, and return our boolean
         *           variable indicating sucess or failure in this avenue.
         * Notes :
         */
        boolean blnAdded = false; //the boolean indicating a sucessful entry input.

        //get connection and sql statement objects
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        //now lets make a string with our sql insert statement
        String strSQL = "INSERT INTO " + PROFESSOR_TABLE_STRING + " (PROFESSOR_NAME)" +
                " values ('" + strProfessorName + "');";
        //try to run the statement
        try{
            statement.execute(strSQL);
            blnAdded = true;
        } catch (Exception ex){
            ex.printStackTrace();
        }
        statement.close();
        connection.close();
        return blnAdded;
    }
    public boolean addClassroom(String strClassroomName, int intCapacity) throws SQLException, ClassNotFoundException {
        /**
         * Name : addClassroom
         * Params : strClassroomName - the name we want to give the classroom, ex. A
         *          intCapacity - the capacity of the classroom, ex. 30
         * Returns : blnAdded - true -> sucess, false -> failure
         * Purpose : The purpose of this method is to try to insert an entry into our classroom table. We return blnAdded
         *           which indicates the insertions sucess or failure.
         * Notes :
         */
        boolean blnAdded = false; //the boolean indicating a sucessful entry input.

        //get connection and sql statement objects
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        //now lets make a string with our sql insert statement
        String strSQL = "INSERT INTO " + CLASSROOM_TABLE_STRING + " (CLASSROOM_NAME, CAPACITY)" +
                " values ('" + strClassroomName + "', " + intCapacity + ");";
        //try to run the statement
        try{
            statement.execute(strSQL);
            blnAdded = true;
        } catch (Exception ex){
            ex.printStackTrace();
        }
        //we need to close our db interaction objects
        statement.close();
        connection.close();
        return blnAdded;
    }
    private int getCourseSection(String strCourseName) throws SQLException, ClassNotFoundException {
        /**
         * Name : getCourseSection
         * Params : strCourseName - the name of the course that we want to get the section for
         * Returns : intNextSection - the next available section for the course.
         * Purpose : the purpose of this method is to get the next available section for the given course. We will
         *           first execute a sql query on the schedule table to find the max course section, increment it by
         *           1 and return it.
         */
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        String strSQL = "SELECT MAX COURSE_SECTION FROM " + SCHEDULE_TABLE_STRING + " WHERE COURSE_NAME = '" +
                strCourseName + "';";
        int intNextSection = 1;
        //now we want to try and execute our query
        try{
            ResultSet resultSet = statement.executeQuery(strSQL);
            if(resultSet.next()){
               intNextSection = resultSet.getInt("COURSE_SECTION") + 1;
            }
        }catch (Exception ex){
            ex.printStackTrace();
            intNextSection = -1;
        }

        statement.close();
        connection.close();
        return intNextSection;
    }
    public objClassroom getFreeClassroom(String strStartTime, String strEndTime, String strDays) throws SQLException, ClassNotFoundException {
        /**
         * Name : getFreeClassroom
         * Params : strStartTime - the startTime of the desired time slot
         *          strEndTime - the end time of the desired slot
         *          strDays - the days of the desired slot
         * Returns : strClassroom - a string containing the classroom name of the free classroom if one is found. If
         *                          none is found this is returned as "-1"
         * Purpose : The purpose of this function is to search for a free classroom in the desired timeslot on the desired
         *           days. A SQL query will be used, and if no results are found meeting the start time, end time and
         *           days requirements we will return "-1"
         */
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        String strDaysStatement = getStrDaysStatement(strDays);
        ArrayList<Integer> lstTakenTUIDS = new ArrayList<>(); //a list to hold the classroom tuids that are unavailable during this time.
        String strSQL = "SELECT * FROM " + SCHEDULE_TABLE_STRING + " WHERE (Start_Time BETWEEN '" + strStartTime +
                "' AND '" + strEndTime + "') OR (End_Time BETWEEN '" + strStartTime + "' AND '" + strEndTime + "') AND " +
                strDaysStatement + " ORDER BY Classroom_TUID ASC";
        try{
            ResultSet resultSet = statement.executeQuery(strSQL);

            while(resultSet.next()){
                System.out.println(resultSet.getInt("Course_TUID"));
                lstTakenTUIDS.add(resultSet.getInt("Classroom_TUID"));
            }
        } catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
        //get all classrooms in a list
        ArrayList<objClassroom> lstClassrooms = getAllClassrooms();
        //see if the list of all classrooms is the same size as our list of taken classrooms for this time
        if(lstClassrooms.size() == lstTakenTUIDS.size()){
            return null;
        }
        //there is an open classroom, lets iterate through the tuids, which are sorted low to high, and find the classroom
        //TODO : Ask Dr. James if the nested looping is less efficient than a single loop with a sql query here
        objClassroom highestCapacityAvailableClassroom = null;
        for(objClassroom c : lstClassrooms){
            boolean blnTaken = false;
            for(int i : lstTakenTUIDS){
                if(i == c.getIntClassroomTUID()){
                    blnTaken = true;
                }
            }
            if(!blnTaken){
                highestCapacityAvailableClassroom = c;
                break;
            }
        }
        statement.close();
        connection.close();
        return highestCapacityAvailableClassroom;
    }
    public boolean addSchedule(objFileData fileData, int intClassroomTUID, int intNewSection) throws SQLException, ClassNotFoundException {
        //TODO REWORK THIS
        boolean blnAdded = false;
        //get connection and sql statement objects
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        //now we need to get the objects for our professor, classroom, and course to make an entry in our relation table SCHEDULE_tABLE
        objProfessor professor = getProfessor(fileData.getStrProfessorName());
        objCourse course = getCourse(fileData.getStrCourseName());
        String strSQL = "INSERT INTO TABLE " + SCHEDULE_TABLE_STRING + " (COURSE_TUID, COURSE_SECTION, CLASSROOM_TUID, PROFESSOR_TUID," +
                " START_TIME, END_TIME, DAYS) VALUES (" + course.getIntCourseTUID() + ", '" +  intNewSection + "', " +
                intClassroomTUID + ", " + professor.getIntProfessorTUID() + ", '" + fileData.getStrStartTime() +
                "', '" + fileData.getStrEndTime() + "', '" + fileData.getStrDays() + "';";
        try{
            blnAdded = statement.execute(strSQL);
        }catch (Exception ex){
            ex.printStackTrace();
            blnAdded = false;
        }
        //make sure to close db interaction objects
        statement.close();
        connection.close();
        return blnAdded;
    }
    public boolean addSchedule(int intCourseTUID, String strCourseSection, int intClassroomTUID, int intProfessorTUID,
                               String strStartTime, String strEndTime, String strDays) throws SQLException, ClassNotFoundException {
        /**
         * Name : addSchedule
         * Params : intCourseTUID - the Table Unique IDentifier(TUID) of the course, ex. 1
         *          strCourseSection - the section of the course, ex. 01
         *          intClassroomTUID - the TUID for the classroom we are scheduling this class in, ex. 1
         *          intProfessorTUID - the TUID for the professor teaching this class, ex. 2
         *          strStartTIme - represents the start time of the course, ex. 8:30
         *          strEndTIme - represents the end time of the course, ex. 10:30
         *          strDays - the day(s) of the week on which the course will be taught, ex. MW
         * Returns : blnAdded - true -> sucess, false -> failure
         * Purpose : In this method we try to fill a slot in our schedule by inserting an entry in our schedule table.
         *           We then return blnAdded signifying either sucess or failure to do so.
         * Notes :
         */
        boolean blnAdded = false; //the boolean indicating a successful entry input.

        //get connection and sql statement objects
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        //now lets make a string with our sql insert statement
        String strSQL = "INSERT INTO " + SCHEDULE_TABLE_STRING + " (COURSE_TUID, COURSE_SECTION, CLASSROOM_TUID, PROFESSOR_TUID," +
                " START_TIME, END_TIME, DAYS) values (" + intCourseTUID + ", '" + strCourseSection + "', " +
                intClassroomTUID + ", " + intProfessorTUID + ", '" + strStartTime + "', '" + strEndTime + "', '" + strDays
                + "');";
        //try to run the statement
        try{
            statement.execute(strSQL);
            blnAdded = true;
        } catch (Exception ex){
            ex.printStackTrace();
        }
        statement.close();
        connection.close();
        return blnAdded;
    }
    private String getStrDaysStatement(String strDays){
        /**
         * Name : getStrDaysStatement
         * Params : strDays - the days we want to make a statement for
         * Returns : strDaysStatement - the statement for the given days.
         * Purpose : the purpose of this method is to make sure that we have a statement that would cover
         *           just M,W,T,R as DAYS column data in our SCHEDULE_TABLE when we want to search for scheduled courses.
         * Notes :
         */
        String strDaysStatement = "";
        switch (strDays){
            case "M":
                strDaysStatement = "DAYS = 'M' OR 'MW'";
                break;
            case "T":
                strDaysStatement = "DAYS = 'T' OR 'TR'";
                break;
            case "W":
                strDaysStatement = "DAYS = 'W' OR 'MW'";
                break;
            case "R":
                strDaysStatement = "DAYS = 'R' OR 'TR'";
                break;
            case "MW":
                strDaysStatement = "DAYS = 'M' OR 'W' OR 'MW'";
                break;
            case "TR":
                strDaysStatement = "DAYS = 'T' OR 'R' OR 'TR'";
                break;
            default:
                strDaysStatement = "DAYS = '" + strDays + "'";
                break;
        }
        return strDaysStatement;
    }
    public ArrayList<objSchedule> getScheduleDays(String strDays) throws SQLException, ClassNotFoundException {
        /**
         * Name : getSchedule
         * Params : strDays - the days we will try to find a scheduled course on.
         * Returns : lstSchedule - a list of objSchedule objects that will contain all scheduled courses on the given
         *                         strDays.
         * Purpose : The purpose of this method is to try to find scheduled courses on the given days and return an
         *           ArrayList with all of these courses. If we do not find any an empty list is returned.
         * Notes :
         */
        Connection connection = getConnection(); //our sql connection object
        Statement statement = connection.createStatement(); //a sql statement object
        String strSQL = "SELECT * FROM "+ SCHEDULE_TABLE_STRING + " WHERE " + getStrDaysStatement(strDays);
        ArrayList<objSchedule> lstSchedule = new ArrayList<objSchedule>();
        try{
            ResultSet resultSet = statement.executeQuery(strSQL);
            while(resultSet.next()){
                objSchedule schedule = new objSchedule(resultSet.getInt("TUID"), resultSet.getInt("COURSE_TUID"),
                        resultSet.getInt("COURSE_SECTION"), resultSet.getInt("CLASSROOM_TUID"),
                        resultSet.getInt("PROFESSOR_TUID"), resultSet.getString("START_TIME"), resultSet.getString("END_TIME"),
                        resultSet.getString("DAYS"));
                lstSchedule.add(schedule);
            }
        } catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
        //close db interaction objects
        statement.close();
        connection.close();
        return lstSchedule;
    }
    public ArrayList<objClassroom> getAllClassrooms() throws SQLException, ClassNotFoundException {
        /**
         * Name : getAllClassrooms
         * Params : none
         * Returns : lstClassrooms - an arraylist containing data object models of all the classrooms in our Classrooms table
         * Purpose : the purpose of this method is to get an arraylist with all of our classrooms in it.
         * Notes :
         */
        ArrayList<objClassroom> lstClassrooms = new ArrayList<>();
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        String strSQL = "SELECT * FROM " + CLASSROOM_TABLE_STRING + ";";
        try{
            ResultSet resultSet = statement.executeQuery(strSQL);
            while(resultSet.next()){
                lstClassrooms.add(new objClassroom(resultSet.getInt("TUID"), resultSet.getString("CLASSROOM_NAME"), resultSet.getInt("CAPACITY")));
            }
        }catch (Exception ex){
            ex.printStackTrace();
            lstClassrooms = null;
        }
        statement.close();
        connection.close();
        return lstClassrooms;
    }
    public objSchedule getOneScheduledCourse(String strStartTime, String strEndTime, String strDays) throws SQLException, ClassNotFoundException {
        /**
         * Name : getOneScheduledCourse
         * Params : strStartTime - the startTime of the period that we want to search for a course in.
         *          strEndTime - the end time of the period that we want to search for a course in.
         *          strDays - the days that we want to search for a scheduled course during.
         * Returns : schedule - an objSchedule object with data for a SCHEDULE_TABLE entry, we will return null
         *                      if we do not find an entry in our time period.
         * Purpose : the purpose of this method is to try to get a single scheduled course during our given time
         *           slot on the given days.
         * Notes :
         */
        ArrayList<objSchedule> lstSchedule = getScheduleDays(strDays); //an arraylist with all scheduled courses on the given days
        //first return null if we do not have a course on these day(s)
        if(lstSchedule == null || lstSchedule.size() == 0){
            return null;
        }
        //loop through each objSchedule in lstSchedule
        for(objSchedule s : lstSchedule){
            LocalTime localTime = LocalTime.parse(strStartTime);
            if(localTime.isAfter(LocalTime.parse(s.getStrStartTime())) && localTime.isBefore(LocalTime.parse(s.getStrEndTime()))){
                return s;
            }
            localTime = LocalTime.parse(strEndTime);
            if(localTime.isAfter(LocalTime.parse(s.getStrStartTime())) && localTime.isBefore(LocalTime.parse(s.getStrEndTime()))){
                return s;
            }
        }
        return null;
    }
    public ArrayList<objSchedule> readAllScheduled() throws SQLException, ClassNotFoundException {
        /**
         * Name : readAllScheduled
         * Parmas : None.
         * Returns : lstScheduled - a list containing every scheduled class.
         * Purpose :  the purpose of this method is to get every single class we have scheduled and return them as entries
         *            in a list of objSchedule objects.
         * Notes :
         */
        ArrayList<objSchedule> lstScheduled = new ArrayList<objSchedule>();

        //get connection and sql statement objects
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        String strSQL = "SELECT * FROM " + SCHEDULE_TABLE_STRING + "";
        try{
            //execute statement and read first
            ResultSet resultSet = statement.executeQuery(strSQL);
            //read all the data returned in the result set

            while(resultSet.next()){
                objSchedule temp = new objSchedule(resultSet.getInt("TUID"), resultSet.getInt("COURSE_TUID"),
                        resultSet.getInt("COURSE_SECTION"), resultSet.getInt("CLASSROOM_TUID"),
                        resultSet.getInt("PROFESSOR_TUID"), resultSet.getString("START_TIME"), resultSet.getString("END_TIME"),
                        resultSet.getString("DAYS"));
                lstScheduled.add(temp);
            }
        } catch (Exception ex){
            //when we get an exception we should return null so that a half assed data doesn't get returned.
            ex.printStackTrace();
            return null;
        }
        //close db interaction objects
        statement.close();
        connection.close();
        return lstScheduled;
    }
    public objCourse getCourse(String strCourseID) throws SQLException, ClassNotFoundException {
        /**
         * Name : getCourse
         * Params : strCourseID - The CourseID of the the course we want to grab from the courses table, ex. CSC 105
         * Returns : course - the Course whose data we try to get from the database, if data extraction is a failure
         *                    this object will be null.
         * Purpose : In this method we try to get data from a course by querying our database courses table for
         *           entries with this courseID. We Then return either a model of this data as an objCourse, or null.
         * Notes :
         */

        objCourse course = null; //the course that we are trying to model data for
        //create connection and statement objects
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        //and a string to try and find a course with this id
        String strSQL = "SELECT * FROM " + COURSE_TABLE_STRING + " WHERE COURSE_ID = '" + strCourseID + "';";
        //now try and execute that statement
        try{
            ResultSet resultSet = statement.executeQuery(strSQL);
            course = new objCourse(resultSet.getInt("TUID"), resultSet.getString("COURSE_ID"),
                    resultSet.getString("COURSE_TITLE"), resultSet.getInt("CREDITS"));
            resultSet.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        statement.close();
        connection.close();

        return course;
    }
    public objCourse getCourse(int intCourseTUID) throws SQLException, ClassNotFoundException {
        /**
         * Name : getCourse
         * Params : intCourseTUID - The TUID of the the course we want to grab from the courses table, ex. 1
         * Returns : course - the Course whose data we try to get from the database, if data extraction is a failure
         *                    this object will be null.
         * Purpose : In this method we try to get data from a course by querying our database courses table for
         *           entries with this TUID. We Then return either a model of this data as an objCourse, or null.
         * Notes : this is just a copy past of getCourse(String strCourseName) where the SQL string is changed to search on tuid.
         */

        objCourse course = null; //the course that we are trying to model data for
        //create connection and statement objects
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        //and a string to try and find a course with this id
        String strSQL = "SELECT * FROM " + COURSE_TABLE_STRING + " WHERE TUID = '" + intCourseTUID + "';";
        //now try and execute that statement
        try{
            ResultSet resultSet = statement.executeQuery(strSQL);
            course = new objCourse(resultSet.getInt("TUID"), resultSet.getString("COURSE_ID"),
                    resultSet.getString("COURSE_TITLE"), resultSet.getInt("CREDITS"));
            resultSet.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        statement.close();
        connection.close();

        return course;
    }
    public objClassroom getClassroom(String strClassroomName) throws SQLException, ClassNotFoundException {
        /**
         * Name : getClassroom
         * Params : strClassroomName
         * Returns : classroom - the classroom which we are trying to model data for. If we can not find this classroom
         *                       in our databases classroom table we will just return null.
         * Purpose : the purpose of this method is to attempt to find a classroom with the corresponding classroom name
         *           in our classrooms table, and then populate an objClassroom with that data and return it. If we can
         *           not find a classroom we return null.
         * Notes :
         */

        objClassroom classroom = null; //the classroom we are trying to model

        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        //and a string to try and find a course with this id
        String strSQL = "SELECT * FROM " + CLASSROOM_TABLE_STRING + " WHERE CLASSROOM_NAME = '" + strClassroomName + "';";
        //now try and execute that statement
        try{
            ResultSet resultSet = statement.executeQuery(strSQL);
            classroom =  new objClassroom(resultSet.getInt("TUID"), resultSet.getString("CLASSROOM_NAME"),
                    resultSet.getInt("CAPACITY"));
            resultSet.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        statement.close();
        connection.close();

        return classroom;
    }
    public objProfessor getProfessor(String strProfessorName) throws SQLException, ClassNotFoundException {
        /**
         * Name : getProfessor
         * Params : strProfessorName - the name of the professor we want data about
         * Returns : professor - The objProfessor we try to model, will return as null if we can not find this professor
         * Purpose : The purpose of this method is to search our database for a professor with this name, and model their
         *           data into an objProfessor, return null if professor can not be found.
         * Notes :
         */
        objProfessor professor = null; //the professor we are trying to model

        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        //and a string to try and find a course with this id
        String strSQL = "SELECT * FROM " + PROFESSOR_TABLE_STRING + " WHERE PROFESSOR_NAME = '" + strProfessorName + "';";
        //now try and execute that statement
        try{
            ResultSet resultSet = statement.executeQuery(strSQL);
            professor =  new objProfessor(resultSet.getInt("TUID"), resultSet.getString("PROFESSOR_NAME"));
            resultSet.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
        statement.close();
        connection.close();
        return professor;
    }
    public HashMap<String, ArrayList<objSchedule>> getAllScheduled() throws SQLException, ClassNotFoundException {
        /**
         * Name : getAllScheduled
         * Params : strDays
         * Returns : mapSchedule - a hashMap where keys are days and values are an arraylist of all scheduled courses on that day.
         * Purpose : The purpose of this method is to get a hashmap with all scheduled classes on each day
         */
        HashMap<String, ArrayList<objSchedule>> mapSchedule = new HashMap<>(); // our container
        //get connection and statement objects
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = null; //the resultset of our sql queries
        //first lets get all classes on monday
        String strDay = "M";
        String strSQL;
        ArrayList<objSchedule> lstSchedule;
        try{
            while(!strDay.equals("-1")){
                strSQL = "SELECT * FROM " + SCHEDULE_TABLE_STRING + " WHERE Days LIKE \"%"+ strDay + "%\";";
                lstSchedule = new ArrayList<>();
                resultSet = statement.executeQuery(strSQL);
                while(resultSet.next()){
                    objSchedule schedule = new objSchedule(resultSet.getInt("TUID"), resultSet.getInt("Course_TUID"),
                            resultSet.getInt("Course_Section"), resultSet.getInt("Classroom_TUID"),
                            resultSet.getInt("Professor_TUID"), resultSet.getString("Start_Time"),
                            resultSet.getString("End_Time"), strDay);
                    lstSchedule.add(schedule);
                }
                //now we add an entry to our hashmap
                mapSchedule.put(strDay, lstSchedule);
                strDay = changeDay(strDay);
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return mapSchedule;
    }
    private String changeDay(String strDay){
        switch (strDay){
            case "M":
                return "T";
            case "T":
                return "W";
            case "W":
                return "R";
            case "R":
                return "F";
            default:
                return "-1";
        }
    }
    public ResultSet queryDB(String strSQL) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        ResultSet resultSet = null;
        try {
            resultSet = statement.executeQuery(strSQL);
        } catch (Exception ex){
            ex.printStackTrace();
        }
        statement.close();
        connection.close();
        return resultSet;

    }
}
