public class objCourse{
    /**
     * File Name : objClass
     * File Author : Andrew A. Loesel
     * Part of Project : CS 421 Assignment 3 - class scheduler
     * Organization : Saginaw Valley State University
     * Professor : Scott D. James
     * File Purpose : This object will act as a data model for classes. This will be useful when we want to read data
     *                on a class from our database.
     */

    //GLOBAL VARIABLES GLOBAL VARIABLES GLOBAL VARIABLES GLOBAL VARIABLES
    private int intCourseTUID; //The TUID corresponding to this course in the courses table, ex. 0
    private String strCourseID; //The course ID for this course, ex. CSC 105
    private String strCourseTitle; //The title for this course, ex. Computers and Programming.
    private int intCreditHours; //The credit hours that this course is worth, ex. 4

    public objCourse(int intCourseTUID, String strCourseID, String strCourseTitle, int intCreditHours) {
        /**
         * Name : objCourse
         * Params : intCourseTUID - The TUID corresponding to this course in the courses table, ex. 0
         *          strCourseID - The course ID for this course, ex. CSC 105
         *          strCourseTitle - The title for this course, ex. Computers and Programming.
         *          intCreditHours - The credit hours that this course is worth, ex. 4
         * Purpose : This is the paramaterized constructor for objCourse, it will be used to have a data model that
         *           represents a course entry in the courses table.
         */
        this.intCourseTUID = intCourseTUID;
        this.strCourseID = strCourseID;
        this.strCourseTitle = strCourseTitle;
        this.intCreditHours = intCreditHours;
    }

    /* IntelliJ generated getters */
    public int getIntCourseTUID() {
        return intCourseTUID;
    }

    public String getStrCourseID() {
        return strCourseID;
    }

    public String getStrCourseTitle() {
        return strCourseTitle;
    }

    public int getIntCreditHours() {
        return intCreditHours;
    }
}