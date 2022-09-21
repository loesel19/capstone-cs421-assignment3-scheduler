public class objClassroom{
    /**
     * File Name : objClassroom
     * File Author : Andrew A. Loesel
     * Part of Project : CS 421 Assignment 3 - class scheduler
     * Organization : Saginaw Valley State University
     * Professor : Scott D. James
     * File Purpose : This object will act as a data model for a classroom entry in the classroom table.
     */

    //GLOBAL VARIABLES GLOBAL VARIABLES GLOBAL VARIABLES GLOBAL VARIABLES
    int intClassroomTUID; //The TUID coressponding to this classroom's entry in the classroom table, ex. 1
    String strClassroomName; //The name of this classroom, ex. A
    int intCapacity; //the capacity of this classroom, ex. 30

    public objClassroom(int intClassroomTUID, String strClassroomName, int intCapacity) {
        /**
         * Name : objClassroom
         * Params : intClassroomTUID - The TUID coressponding to this classroom's entry in the classroom table, ex. 1
         *          strClassroomName - The name of this classroom, ex. A
         *          intCapacity - the capacity of this classroom, ex. 30
         * Purpose : This is the paramaterized constructor for objClassroom. This constructor will be used to create
         *           an instance of objClassroom which models data of an entry in our classroom table.
         */
        this.intClassroomTUID = intClassroomTUID;
        this.strClassroomName = strClassroomName;
        this.intCapacity = intCapacity;
    }
}