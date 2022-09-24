public class objFileData {
    /**
     * File Name : objFileData
     * File Author : Andrew A. Loesel
     * Part of Project : CS 421 Assignment 3 - class scheduler
     * Organization : Saginaw Valley State University
     * Professor : Scott D. James
     * File Purpose : The purpose of this class is to act as a data model for the data that will be presented
     *                in each line of the file that the user presents us.
     */
    private String strCourseName; //the name of the course
    private String strProfessorName; //the name of the professor
    private String strDays; //the days we want to try and schedule the course for
    private String strStartTime; //the start time we want to try to schedule the course at
    private String strEndTime; //the end time we want to try to schedule the couse at

    public objFileData(String strCourseName, String strProfessorName, String strDays, String strStartTime, String strEndTime) {
        /* this is an intellij generated parameterized constructor with all fields accounted for */
        this.strCourseName = strCourseName;
        this.strProfessorName = strProfessorName;
        this.strDays = strDays;
        this.strStartTime = strStartTime;
        this.strEndTime = strEndTime;
    }
    public String toString(){
        /**
         * Name : toString
         * Params : none
         * Returns : A string with all the fields of this object seperated by spaces.
         * Purpose : this is an override of the objects ToString method meant to return meaningful data about this object.
         */
        return this.strCourseName + " " + this.strProfessorName + " " + this.strDays + " " + this.strStartTime +  " " +
                this.strEndTime;
    }
    /* IntelliJ generated getters */

    public String getStrCourseName() {
        return strCourseName;
    }

    public String getStrProfessorName() {
        return strProfessorName;
    }

    public String getStrDays() {
        return strDays;
    }

    public String getStrStartTime() {
        return strStartTime;
    }

    public String getStrEndTime() {
        return strEndTime;
    }
    public void setStrStartTime(String strStartTime){
        this.strStartTime = strStartTime;
    }
    public void setStrEndTime(String strEndTime){
        this.strEndTime = strEndTime;
    }
    public void setStrDays(String strDays){
        this.strDays = strDays;
    }
}
