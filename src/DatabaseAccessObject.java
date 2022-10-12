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
    private static final String DATABASE_NAME = "ScheduleDatabase";
    private static final String SECTION_FILE_STRING = "Section_File.txt"; //the file name for our section file

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
         *           We simply want to check and see if  //
         * Notes :
         */
        boolean blnExists = false;
        Connection con = getConnection();
        Statement statement = con.createStatement();
        try{
            /* to see if the db exists we will try to get the connection, and execute a sql statement on one of the tables */

            statement.execute("SELECT * FROM " + PROFESSOR_TABLE_STRING + "");
            blnExists = true;

        }catch (Exception ex){
            blnExists = false;
        }
        statement.close();
        con.close();
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
    private void resetSections(){
        /**
         * @Name : resetSections
         * @Params : none
         * @Returns : none
         * @Purpose : the purpose of this method is to reset our sections tied to courses, and to accomplish this we
         *            barbarically delete the section file.
         */
        File file = new File(SECTION_FILE_STRING);
        if(file.exists()){
            file.delete();
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
            File dbFile = new File(dbFileName);
            if(dbFile.delete()){
                System.out.println("Old Database has been deleted.");
                resetSections();
            }


        }catch (Exception ex){
            ex.printStackTrace();
            System.out.println("Could not delete database");
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
        resetSections();
    }
    private void createDB() throws SQLException, ClassNotFoundException {
        /**
         * Name : createDB
         * Params : none.
         * Returns : none.
         * Purpose : The purpose of this method is to create our database.
         * Notes :
         */

        try {
            //create the tables for the database
            createTables();
            System.out.println("New database created.");
        } catch (Exception ex){
            ex.printStackTrace();
            System.out.println("An error has occurred.");
        }

    }
    public boolean startUp() throws SQLException, ClassNotFoundException, IOException {
        /**
         * Name : startUp
         * Params : none.
         * Returns : boolean - true -> prior existing db has been retained, false -> new db
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
                return true;
            }
            //reset the db and return from the function
            resetDB();
            return false;
        }
        /* if we make it our here we have no DB because it either never existed or it was destroyed.
            so let's create our database and initialize our 'static' tables
         */
        createDB();
        initializeCourseTable();
        initializeClassroomTable();
        initializeProfessorTable();
        return false;
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

    public boolean addSchedule(objFileData fileData, int intClassroomTUID, int intNewSection) throws SQLException, ClassNotFoundException {
        /**
         * @Name : addSchedule
         * @Params : fileData - an object that models the data read in from the schedule input file
         *           intClassroomTUID - the TUID for the classroom we wish to schedule the course in
         *           intNewSection - the section number for the course we will schedule.
         * @Returns : blnAdded - a boolean that represents if the schedule table entry was added or not.
         * @Purpose : the purpose of this method is to insert an entry into the schedule table. We grab a professor
         *            and course object corresponding to the prof and course name in fileData and try to execute the sql
         *            statement.
         */
        boolean blnAdded = false;
        //get connection and sql statement objects
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        //now we need to get the objects for our professor, classroom, and course to make an entry in our relation table SCHEDULE_tABLE
        objProfessor professor = getProfessor(fileData.getStrProfessorName());
        objCourse course = getCourse(fileData.getStrCourseName());
        String strSQL = "INSERT INTO " + SCHEDULE_TABLE_STRING + " (COURSE_TUID, COURSE_SECTION, CLASSROOM_TUID, PROFESSOR_TUID," +
                " START_TIME, END_TIME, DAYS) VALUES (" + course.getIntCourseTUID() + ", " +  intNewSection + ", " +
                intClassroomTUID + ", " + professor.getIntProfessorTUID() + ", '" + fileData.getStrStartTime() +
                "', '" + fileData.getStrEndTime() + "', '" + fileData.getStrDays() + "');";
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
    private String reportSQLString(String strWhereClause){
        /**
         * @Name : reportSQLString
         * @Params : strWhereClause - this is the where part of the sql statement we are going to execute
         * @Returns : a string containing the sql statement we want to execute against the database.
         * @Purpose : the purpose of this method is to act as a somewhat modular sql statement generator for the
         *            reports that we want to generate.
         */
        return "SELECT SCHEDULE_TABLE.TUID, PROFESSOR_NAME, CLASSROOM_NAME, CAPACITY, COURSE_ID, COURSE_SECTION, " +
                "SCHEDULE_TABLE.START_TIME, SCHEDULE_TABLE.END_TIME, SCHEDULE_TABLE.DAYS " +
                "FROM SCHEDULE_TABLE INNER JOIN CLASSROOM_TABLE ON SCHEDULE_TABLE.CLASSROOM_TUID = " +
                "CLASSROOM_TABLE.TUID INNER JOIN COURSES_TABLE ON SCHEDULE_TABLE.COURSE_TUID = COURSES_TABLE.TUID " +
                "INNER JOIN PROFESSOR_TABLE ON SCHEDULE_TABLE.PROFESSOR_TUID = PROFESSOR_TABLE.TUID " +
                 strWhereClause;
    }
    private objSchedulingTuple getPartialDayTimeList(Statement statement, ArrayList<Integer> lstAlreadySeenTUIDS, String strSQL){
        /**
         * @Name : getPartialDayTimeList
         * @Params : connection
         *           statement - an opened SQL statement
         *           lstAlreadySeenTUIDS - an arraylist containing tuids corresponding to schedule_table entries already accounted for
         *           strSQL - the SQL we execute to get our results
         * @Returns : objSchedulingTuple - a tuple containing a list of all the courses that we saw in this method,
         *            and the continually building list of all the schedule table tuids that we have seen.
         * @Purpose : The purpose of this method is to execute our sql statement, and add all of the results to a list
         *            representing a partial portion of our schedule report sorted by day/time.
         */
        ArrayList<objReport> lstPartial = new ArrayList<>();
        try{
            ResultSet resultSet = statement.executeQuery(strSQL);
            while(resultSet.next()){
                //go to next loop iteration if we have already seen this SCHEDULE table TUID
                if(lstAlreadySeenTUIDS.contains(resultSet.getInt("TUID")))
                    continue;
                //add a new object representing this data to lstScheduledCourses.
                lstPartial.add(new objReport(resultSet.getInt(1), resultSet.getString(5), resultSet.getInt(6),
                        resultSet.getString(2), resultSet.getString(3), resultSet.getInt(4),
                        resultSet.getString(9), resultSet.getString(7), resultSet.getString(8)));
                lstAlreadySeenTUIDS.add(resultSet.getInt(1));
            }
        } catch (Exception ex){
            //if an exception is thrown return null
            ex.printStackTrace();
            lstPartial = null;
        }
        return new objSchedulingTuple(lstPartial, lstAlreadySeenTUIDS);
    }
    private String changeDay(String strDay){
        /**
         * @Name : changeDay
         * @Params : strDay - the day that needs to be changed
         * @Returns : a string representing the changed day. or -1 if strDay was F
         * @Purpose : the purpose of this method is to take a day as a parameter and return the next weekday or -1 if
         *            F was passed in.
         */
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
        statement.close();
        connection.close();
        return mapSchedule;
    }
    public ArrayList<objReport> getScheduledCoursesByDayTime() throws SQLException, ClassNotFoundException {
        /**
         * @Name : getScheduledCoursesByDayTime
         * @Params : none
         * @Returns : lstScheduledCourses
         * @Purpose : The purpose of this method is to return an arraylist that represents our schedule sorted by days M-F
         *            and start time 8:30 - 3:00
         * @Notes : The data collection will be split into a few different sql queries. First we will get courses on
         *          M, then MW, then T, then W, then TR, then F. A second array list will be used to store Schedule_Table
         *          TUIDS for scheduled courses that have been added to our sorted arraylist so that a M only course is
         *          not also accounted for when we add MW courses.
         */
        ArrayList<objReport> lstScheduledCourses = new ArrayList<>(); //the list with we store our schedule objects in
        ArrayList<Integer> lstAlreadySeenTUIDS = new ArrayList<>(); //the list that will store courses that we have already seen
        Connection connection = getConnection();
        Statement statement = connection.createStatement();

        String strSQL = reportSQLString("WHERE (DAYS like'M%' OR DAYS) ORDER BY DAYS, START_TIME, COURSE_SECTION;");
        objSchedulingTuple tuple = getPartialDayTimeList(statement, lstAlreadySeenTUIDS, strSQL);
        lstScheduledCourses.addAll(tuple.getCourses());
        lstAlreadySeenTUIDS = tuple.getSeenTUIDS();
        //So M and MW are taken care of, now we want T, TR and W. We can take of this with a single Where clause
        strSQL = reportSQLString("WHERE (DAYS like'T%' OR DAYS like'W') ORDER BY DAYS, START_TIME, COURSE_SECTION;");
        tuple = getPartialDayTimeList(statement, lstAlreadySeenTUIDS, strSQL);
        lstScheduledCourses.addAll(tuple.getCourses());
        lstAlreadySeenTUIDS = tuple.getSeenTUIDS();
        //next we want to do R and F, the way we have constructed our methods means we need to do this in two sql queries
        strSQL = reportSQLString("WHERE DAYS = 'R' ORDER BY DAYS, START_TIME, COURSE_SECTION;");
        tuple = getPartialDayTimeList(statement, lstAlreadySeenTUIDS, strSQL);
        lstScheduledCourses.addAll(tuple.getCourses());
        lstAlreadySeenTUIDS = tuple.getSeenTUIDS();
        //now do F
        strSQL = reportSQLString("WHERE DAYS = 'F' ORDER BY DAYS, START_TIME, COURSE_SECTION;");
        tuple = getPartialDayTimeList(statement, lstAlreadySeenTUIDS, strSQL);
        lstScheduledCourses.addAll(tuple.getCourses());
        //close sql objects
        statement.close();
        connection.close();
        //return our list of scheduled courses.
        return lstScheduledCourses;
    }
    public ArrayList<objReport> getScheduledCoursesByProfessor() throws SQLException, ClassNotFoundException {
        /**
         * @Name : getScheduledCoursesByProfessor
         * @Params : none
         * @Returns : ArrayList<objReport> - an arraylist of report data model objects that are in order by professor
         * @Purpose : this method returns a list representation of all scheduled course section ordered by professor
         *            alphabetically.
         */

        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        //add where clause to our sql report string
        String strSQL = reportSQLString("ORDER BY Professor_Name,Course_ID, Course_Section, DAYS, start_time");
        objSchedulingTuple tuple = getPartialDayTimeList(statement, new ArrayList<Integer>(), strSQL);
        statement.close();
        connection.close();
        return tuple.getCourses();
    }
    public ArrayList<objReport> getScheduledCoursesByCourse() throws SQLException, ClassNotFoundException {
        /**
         * @Name : getScheduledCoursesByCourse
         * @Params : none
         * @Returns : ArrayList<objReport> - an arraylist of report data model objects that are in order by professor
         * @Purpose : This method will create a list of scheduled courses ordered by course names.
         */
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        //add the where clause to our sql report string
        String strSQL = reportSQLString("ORDER BY Course_ID, Course_Section, DAYS, start_time");
        objSchedulingTuple tuple = getPartialDayTimeList(statement, new ArrayList<Integer>(), strSQL);
        statement.close();
        connection.close();
        return tuple.getCourses();
    }
}
