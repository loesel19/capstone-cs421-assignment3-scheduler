import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class main {
    /**
     * File Name :  main
     * File Author : Andrew A. Loesel
     * Part of Project : CS 421 Assignment 3 - class scheduler
     * Organization : Saginaw Valley State University
     * Professor : Scott D. James
     * File Purpose : This is the object that will first run when our program is launched. This will be the
     *                highest object in the stack for the program during execution. Since we approached this
     *                program with a high amount of OOP this file is just a shell used to launch user interactions.
     */
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
