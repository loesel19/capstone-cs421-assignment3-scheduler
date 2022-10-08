public class objReport {
    /**
     * File Name : objProfessor
     * File Author : Andrew A. Loesel
     * Part of Project : CS 421 Assignment 3 - class scheduler
     * Organization : Saginaw Valley State University
     * Professor : Scott D. James
     * File Purpose : This class is going to be an object that models the data of a professor entry in our database.
     */
    private int intTUID;
    private String strCourseName;
    private int intCourseSection;
    private String strProfessorName;
    private String strClassroomName;
    private int intClassroomCapacity;
    private String strDays;
    private String strStartTime;
    private String strEndTime;

    public objReport(int intTUID, String strCourseName, int intCourseSection, String strProfessorName, String strClassroomName,
                     int intClassroomCapacity, String strDays, String strStartTime, String strEndTime) {
        /**
         * @Constructor : the full parameterized constructor
         *                This is an intellij generated constructor
         */
        this.intTUID = intTUID;
        this.strCourseName = strCourseName;
        this.intCourseSection = intCourseSection;
        this.strProfessorName = strProfessorName;
        this.strClassroomName = strClassroomName;
        this.intClassroomCapacity = intClassroomCapacity;
        this.strDays = strDays;
        this.strStartTime = strStartTime;
        this.strEndTime = strEndTime;
    }
}
