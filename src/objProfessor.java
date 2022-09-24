public class objProfessor{
    /**
     * File Name : objProfessor
     * File Author : Andrew A. Loesel
     * Part of Project : CS 421 Assignment 3 - class scheduler
     * Organization : Saginaw Valley State University
     * Professor : Scott D. James
     * File Purpose : This class is going to be an object that models the data of a professor entry in our database.
     */

    private int intProfessorTUID; //The TUID of this professor in the professor table, ex. 0
    private String strProfessorName; //The name of this professor, ex. JOHN ARCHIBALD

    public objProfessor(int intProfessorTUID, String strProfessorName) {
        /**
         * Name : objProfessor
         * Params : intProfessorTUID - The TUID of this professor in the professor table, ex. 0
         *          strProfessorName - The name of this professor, ex. JOHN ARCHIBALD
         * Purpose : the purpose of this constructor is to instanciate an instance of objProfessor and attribute it
         *           with data corresponding to this entry in the professors table.
         */
        this.intProfessorTUID = intProfessorTUID;
        this.strProfessorName = strProfessorName;
    }
    public objProfessor(String strProfessorName){
        /**
         * Name : objProfessor
         * Params : strProfessorName - The name of this professor, ex. JOHN ARCHIBALD
         * Purpose : this is a parameterized constructor for objProfessor which will be used when we read in professors
         *           from a file.
         */
        this.strProfessorName = strProfessorName;
    }

    /* IntelliJ generated Getters */
    public int getIntProfessorTUID() {
        return intProfessorTUID;
    }

    public String getStrProfessorName() {
        return strProfessorName;
    }
}