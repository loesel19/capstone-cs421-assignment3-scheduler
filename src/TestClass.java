import java.sql.SQLException;
import java.util.ArrayList;

public class TestClass {
    private static DatabaseAccessObject DAO = new DatabaseAccessObject();
    public static void main(String args[]) throws SQLException, ClassNotFoundException {
        System.out.println("BEGIN DATABASE TESTS .....");
        System.out.println("Test1 : " + Test1());
        System.out.println("Test2 : " + Test2());
        System.out.println("Test3 : " + Test3());
        System.out.println("Test4 : " + Test4());
        System.out.println("END DATABASE TESTS .....");
    }
    public static boolean Test1() throws SQLException, ClassNotFoundException {
        /**
         * Name : Test1
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : Here we check to see if our database startup will create a new database if none exists
         * Notes :
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
         * Notes :
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
         * Notes : This test does not include anything from the schedule table
         */
        System.out.println("Running Test3 ...");
        objCourse course = DAO.getCourse("CSC 105");
        if(course == null){
            System.out.println("Failed to get Course ");
            return false;
        }
        System.out.println(course.getIntCourseTUID() + " : " + course.getStrCourseID() + " : " +
                course.getStrCourseTitle() + " : " + course.getIntCreditHours());
        //now we do professor
        objProfessor professor = DAO.getProfessor("DWAYNE JOHNSTON");
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
    public static boolean Test4() throws SQLException, ClassNotFoundException {
        /**
         * Name : Test4
         * Returns : boolean - true -> test passed, false -> test failed
         * Purpose : the purpose of this method is to test that our method for getting a list with all scheduled courses
         *           works properly.
         */
        System.out.println("Running Test4 ...");
        //first schedule a few more courses
        DAO.addSchedule(1, "02", 1, 1, "10:30", "12:30", "MW");
        DAO.addSchedule(1, "03", 1, 1, "12:30", "02:30", "MW");
        DAO.addSchedule(1, "04", 1, 1, "02:30", "04:30", "MW");

        ArrayList<objSchedule> schedule = DAO.readAllScheduled();
        //check that the object is not null and contains 4 objSchedule
        if(schedule == null || schedule.size() != 4){

            return false;
        }
        for(objSchedule s : schedule){
            System.out.println(s.intTUID + " : " + s.getIntCourseTUID() + " : " + s.getStrCourseSection() + " : " + s.getIntClassroomTUID() + " : " +
                    s.getIntProfessorTUID() + " : " + s.getStrStartTime() + " : " + s.getStrEndTime() + " : " + s.getStrDays());
        }

        return true;
    }
}
