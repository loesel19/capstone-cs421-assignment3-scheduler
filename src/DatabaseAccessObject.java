import javax.swing.plaf.nimbus.State;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
            statement.execute("SELECT * FROM PROFESSOR_TABLE");
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
        String strSQL = "CREATE TABLE PROFESSOR_TABLE (TUID INTEGER PRIMARY KEY AUTOINCREMENT, PROFESSOR_NAME CHAR(50))";
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
        String strSQL = "CREATE TABLE COURSES_TABLE (TUID INTEGER PRIMARY KEY AUTOINCREMENT, COURSE_ID CHAR(10), COURSE_TITLE CHAR(100)," +
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
        String strSQL = "CREATE TABLE CLASSROOM_TABLE (TUID INTEGER PRIMARY KEY AUTOINCREMENT, CLASSROOM_NAME CHAR(20), CAPACITY INTEGER)";
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
        String strSQL = "CREATE TABLE SCHEDULE_TABLE (TUID INTEGER PRIMARY KEY AUTOINCREMENT, COURSE_TUID INTEGER, COURSE_SECTION CHAR(5)," +
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
    public void startUp() throws SQLException, ClassNotFoundException {
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
            System.out.println("Database exists already, would you like to destroy it?");

            while (!strInput.equals("0") && !strInput.equals("1")) {
                System.out.println("Enter 0 to destroy, 1 to keep");
                strInput = sc.next();
            }
            if(strInput.equals("1")){
                /* keep  database, so do nothing here and return */
                return;
            }
            //destroy db
            destroyDB();
        }
        /* if we make it our here we have no DB because it either never existed or it was destroyed.
            so let's create our database
         */
        createDB();
        return;
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
        String strSQL = "INSERT INTO COURSES_TABLE (COURSE_ID, COURSE_TITLE, CREDITS)" +
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
        String strSQL = "INSERT INTO PROFESSOR_TABLE (PROFESSOR_NAME)" +
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
        String strSQL = "INSERT INTO CLASSROOM_TABLE (CLASSROOM_NAME, CAPACITY)" +
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
        boolean blnAdded = false; //the boolean indicating a sucessful entry input.

        //get connection and sql statement objects
        Connection connection = getConnection();
        Statement statement = connection.createStatement();
        //now lets make a string with our sql insert statement
        String strSQL = "INSERT INTO SCHEDULE_TABLE (COURSE_TUID, COURSE_SECTION, CLASSROOM_TUID, PROFESSOR_TUID," +
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
    public ArrayList<objSchedule> readAllScheduled(){
        /**
         * Name : readAllScheduled
         * Parmas : None.
         * Returns : lstScheduled - a list containing every scheduled class.
         * Purpose :  the purpose of this method is to get every single class we have scheduled and return them as entries
         *            in a list of objSchedule objects.
         * Notes :
         */
        ArrayList<objSchedule> lstScheduled = new ArrayList<objSchedule>();

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
        String strSQL = "SELECT * FROM COURSES_TABLE WHERE COURSE_ID = '" + strCourseID + "';";
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
    public objClassroom getClassroom(String strClassroomName){
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

        return classroom;
    }
    public objProfessor getProfessor(String strProfessorName){
        /**
         * Name : getProfessor
         * Params : strProfessorName - the name of the professor we want data about
         * Returns : professor - The objProfessor we try to model, will return as null if we can not find this professor
         * Purpose : The purpose of this method is to search our database for a professor with this name, and model their
         *           data into an objProfessor, return null if professor can not be found.
         * Notes :
         */
        objProfessor professor = null; //the professor we are trying to model

        return professor;
    }
}
