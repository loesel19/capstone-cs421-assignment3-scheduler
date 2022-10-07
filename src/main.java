import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class main {
    public static void main(String args[]) throws SQLException, IOException, ClassNotFoundException {
        /**
         * Name : main
         * Params : args[] - a string array containing arguments for program execution
         * Returns : none
         * Purpose : the purpose of this method is to run our program. Since we have created objects to handle
         *           the logic and actions of this program we will simply instantiate those objects and call
         *           the appropriate functions in the appropriate order.
         */
        DatabaseAccessObject databaseAccessObject = new DatabaseAccessObject();
        FileInteractionObject fileInteractionObject = new FileInteractionObject();
        databaseAccessObject.startUp();
        fileInteractionObject.instanciateBufferedReader(getFilePath());
        SchedulerObject schedulerObject = new SchedulerObject(databaseAccessObject, fileInteractionObject);
        schedulerObject.scheduleAll(fileInteractionObject.readAllFileLine());
        databaseAccessObject.endSession();
    }
    private static String getFilePath(){
        /**
         * Name : getFilePath
         * Params : none
         * Returns : a string containing the path to the file we want to read in.
         * Purpose : the purpose of this method is to get the file path containing the courses we want to schedule.
         */
        Scanner scanner = new Scanner(System.in);
        System.out.println("Input the name of the file you would like to schedule");
        return scanner.next();
    }
}
