public class objSchedule{
    /**
     * File Name : objSchedule
     * File Author : Andrew A. Loesel
     * Part of Project : CS 421 Assignment 3 - class scheduler
     * Organization : Saginaw Valley State University
     * Professor : Scott D. James
     * File Purpose : The purpose of this file is to act as a data model object for schedule entries. This object
     *                will come in handy when trying to read entries from our schedule table.
     */

    //GLOBAL VARIABLE GLOBAL VARIABLES GLOBAL VARIABLES
    int intTUID; //The TUID for this object in the schedule table
    int intCourseTUID; // the TUID for this objects course in the course table
    int intCourseSection; //the course section
    int intClassroomTUID; //the TUID for this objects classroom in the classroom table
    int intProfessorTUID; //the TUID for this oubjects professor in the professor table
    String strStartTime; //the start time of this course
    String strEndTime; //the end time of this course
    String strDays; //the day(s) this course will take place on.

    public objSchedule(int intTUID, int intCourseTUID, int intCourseSection, int intClassroomTUID, int intProfessorTUID, String strStartTime, String strEndTime, String strDays) {
        /**
         * Name : objSchedule
         * Params : intTUID - The TUID for this object in the schedule table
         *          intCourseTUID - the TUID for this objects course in the course table
         *          strCourseSection - the course section
         *          intClassroomTUID - the TUID for this objects classroom in the classroom table
         *          intProfessorTUID - the TUID for this oubjects professor in the professor table
         *          strStartTime - the start time of this course
         *          strEndTime - the end time of this course
         *          strDays - the day(s) this course will take place on.
         * Purpose : this is the intellij generated paramterized constructor for objSchedule.
         */
        this.intTUID = intTUID;
        this.intCourseTUID = intCourseTUID;
        this.intCourseSection = intCourseSection;
        this.intClassroomTUID = intClassroomTUID;
        this.intProfessorTUID = intProfessorTUID;
        this.strStartTime = strStartTime;
        this.strEndTime = strEndTime;
        this.strDays = strDays;
    }
    public String toString(){
        return " TUID : " + this.intTUID + ", Course_TUID : " + this.intCourseTUID + ", Course_Section : " + this.intCourseSection
                + ", Classroom_TUID : " + this.intClassroomTUID + ", Professor_TUID : " + this.intProfessorTUID + ", Start_Time : "
                + this.strStartTime + ", End_Time : " + this.strEndTime + ", Days : " + this.strDays;
    }

/* Intellij generated getters */

    public int getIntTUID() {
        return intTUID;
    }

    public int getIntCourseTUID() {
        return intCourseTUID;
    }

    public int getStrCourseSection() {
        return intCourseSection;
    }

    public int getIntClassroomTUID() {
        return intClassroomTUID;
    }

    public int getIntProfessorTUID() {
        return intProfessorTUID;
    }

    public String getStrStartTime() {
        return strStartTime;
    }

    public String getStrEndTime() {
        return strEndTime;
    }

    public String getStrDays() {
        return strDays;
    }
}