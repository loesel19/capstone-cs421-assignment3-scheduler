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

        UserInteractionObject userInteractionObject = new UserInteractionObject();
        userInteractionObject.startUp();
    }

}
