import java.sql.SQLException;

public class TestClass {
    private static DatabaseAccessObject DAO = new DatabaseAccessObject();
    public static void main(String args[]) throws SQLException, ClassNotFoundException {
        System.out.println("Test1 : " + Test1());
        System.out.println("Test2 : " + Test2());
        System.out.println("Test3 : " + Test3());
    }
    public static boolean Test1() throws SQLException, ClassNotFoundException {
        /**
         * Name : Test1
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : Here we check to see if our database startup will create a new database if none exists
         *
         */
        //this test is to see if our DatabaseAccessObject.checkDBExists returns false when there is no database
        System.out.println("Running Test1 ...");
        try{
            DAO.startUp();
        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;

    }
    public static boolean Test2() throws SQLException, ClassNotFoundException {
        /**
         * Name : Test2
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : The purpose of this method is to test if we can create an entity in each of our database tables
         *           when the create method is given valid data.
         */
        System.out.println("Running Test2 ...");
        if(!DAO.addCourse("CSC 105", "PROGRAMMING", 4)){
            return false;
        }
        if(!DAO.addClassroom("A", 30)){
            return false;
        }
        if(!DAO.addProfessor("DWAYNE JOHNSTON")){
            return false;
        }
        if(!DAO.addSchedule(1, "01", 1, 1, "08:30", "10:30", "MW")){
            return false;
        }
        return true;
    }
    public static boolean Test3() throws SQLException, ClassNotFoundException {
        /**
         * Name : Test3
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : the purpose of this method is to see if we can search for objects in our database given good input
         * Notes :
         */
        System.out.println("Running Test3 ...");
        objCourse course = DAO.getCourse("CSC 105");
        if(course == null){
            return false;
        }
        System.out.println(course.getIntCourseTUID() + " : " + course.getStrCourseID() + " : " +
                course.getStrCourseTitle() + " : " + course.getIntCreditHours());

        return true;
    }
}
