import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FileInteractionObject {
    /**
     * File Name : FileInteractionObject
     * File Author : Andrew A. Loesel
     * Part of Project : CS 421 Assignment 3 - class scheduler
     * Organization : Saginaw Valley State University
     * Professor : Scott D. James
     * File Purpose : The purpose of this file is to handle our file interactions in the application. We will mostly need
     *                to read in data from a file in a specified format. We will use an object to get this data.
     */


    BufferedReader bufferedReader;
    private static final String SECTION_FILE_STRING = "Section_File.txt";
    private static final String COURSE_CATALOG_PATH_STRING = "Course_Catalog.txt";//the path of the course catalog we want to use

    public boolean instanciateBufferedReader(String strFilePath) throws FileNotFoundException {
        /**
         * Name : instanciateBufferedReader
         * Params : none.
         * Returns : boolean - true -> successfully instanced bufferedReader, false -> failure
         */

        try {

            bufferedReader = new BufferedReader(new FileReader(strFilePath));
            return true;
        } catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
    public void closeBufferedReader() throws IOException {
        /**
         * @Name : closeBufferedReader
         * @Params : none
         * @Returns : none
         * @Purpose : The purpose of this method is to close the buffered reader object of this object
         */
        if(!(bufferedReader == null))
            this.bufferedReader.close();
    }
    public objFileData readFileLine() throws IOException {
        /**
         * Name : readFileLine
         * Params : none.
         * Returns : fileData - an object containing the data items that we will try to extract from this line of the file.
         * Purpose : The purpose of this function is to try reading 1 line of the file into an objFileData object which models
         *           the data from that line, and returning that object.
         * Notes :
         */
        objFileData fileData = null;

        String line = bufferedReader.readLine();
        String[] strValues = line.split("\t");
        try{
            fileData = new objFileData(strValues[0], strValues[1], strValues[2], strValues[3], strValues[4]);
        } catch (Exception ex){
            return null;
        }

        return fileData;
    }
    public void writeOutSectionFile(HashMap<String, Integer> mapSections) throws IOException {
        File file = new File(SECTION_FILE_STRING);
        if(file.exists()){
           file.delete();
        }
        file.createNewFile();
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        boolean blnFirst = true;
        for(Map.Entry<String, Integer> e : mapSections.entrySet()){
            if(blnFirst){
                blnFirst = false;
                bufferedWriter.write(e.getKey() + "\t" + e.getValue());
                continue;
            }
            bufferedWriter.write("\n" + e.getKey() + "\t" + e.getValue());
        }
        bufferedWriter.flush();
        bufferedWriter.close();
        file = null;
    }
    private HashMap<String, Integer> readSectionsFromFile() throws IOException {
        HashMap<String, Integer> mapSections = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(SECTION_FILE_STRING));
        String line;
        while((line = br.readLine()) != null){
            mapSections.put(line.split("\t")[0], Integer.parseInt(line.split("\t")[1]));
        }
        return mapSections;
    }
    public HashMap<String, Integer> getSectionMap() throws IOException {
        File file = new File(SECTION_FILE_STRING);
        HashMap<String, Integer> mapSection = new HashMap<>();
        if(!file.exists()){
            //add all of the courses with section 0 to the map
            instanciateBufferedReader(COURSE_CATALOG_PATH_STRING);
            ArrayList<objCourse> lstCourses = readAllCatalogedCourses();
            for (objCourse c : lstCourses){
                mapSection.put(c.getStrCourseID(), 0);
            }
            //now make buffered reader null
            bufferedReader = null;
            return mapSection;
        }
        //the file already existed, so we need to read in all the lines of that file, load our hashmap and return it. Create a method to do it
        return readSectionsFromFile();
    }
    public ArrayList<objFileData> readAllFileLine() throws IOException {
        /**
         * Name : readAllFileLine
         * Params : none.
         * Returns : lstAllFileLine - an arraylist containing objFileData object data models of each line of code.
         * Purpose : The purpose of this method is to return the data objects models of each line of the file in a single
         *           arrayList.
         * Notes :
         */
        ArrayList<objFileData> lstFileData = new ArrayList<>();
        String line; //a line of data from our file
        //we want to read a line and check that it is not null. if line is null we are at the end of the file and will move on.
        while((line = bufferedReader.readLine()) != null){
            String[] strValues = line.split("\t");
            try{
                //add a new objFileData to the list with this data.
                lstFileData.add(new objFileData(strValues[0], strValues[1], strValues[2], strValues[3], strValues[4]));
            } catch (Exception ex){
                //if a non null line fails to read return null
                return null;
            }
        }

        return lstFileData;
    }
    public ArrayList<objCourse> readAllCatalogedCourses() throws IOException {
        /**
         * Name : readAllCatalogedCourses
         * Params : None
         * Returns : lstCourses - an arrayList of objCourse objects that contains data models for all cataloged courses.
         * Purpose : the purpose of this method is to read the course catalog file and return an arraylist of all the
         *           courses as data models.
         */
        ArrayList<objCourse> lstCourses = new ArrayList<objCourse>();
        String line; //a line of data from the file
        //now read until we reach the end of file
        while((line = bufferedReader.readLine()) != null){
            String[] strValues = line.split("\t");
            //now try to add a new objCourse to the list with params course_name, course_title, credits
            try {
                lstCourses.add(new objCourse(strValues[0], strValues[1], Integer.parseInt(strValues[2])));
            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }

        }
        return lstCourses;
    }
    public ArrayList<objClassroom> readAllCatalogedClassrooms() throws IOException {
        /**
         * Name : readAllCatalogedClassrooms
         * Params : none
         * Returns : lstClassrooms - an arraylist of objClassroom objects that contain data models for all cataloged classrooms.
         * Purpose : The purpose of this method is to read all classrooms from the catalog file and return an arraylist
         *           of all the classrooms as data models.
         */
        ArrayList<objClassroom> lstClassrooms = new ArrayList<objClassroom>();
        String line; //a line of data from the file
        //now we want to read until we reach end of file
        while((line = bufferedReader.readLine()) != null){
            String[] strValues = line.split("\t");
            try{
                lstClassrooms.add(new objClassroom(strValues[0], Integer.parseInt(strValues[1])));
            }catch (Exception ex){
                ex.printStackTrace();
                return null;
            }
        }
        return lstClassrooms;
    }
    public ArrayList<objProfessor> readAllCatalogedProfessors() throws IOException {
        /**
         * Name : readAllCatalogedProfessors
         * Params : none
         * Returns : lstProfessors - an arraylist of professor data model objects with all cataloged professors.
         * Purpose : the purpose of this method is to read all of the professors from our professor catalog file, and
         *           return an arraylist of all the professors as data models
         */
        ArrayList<objProfessor> lstProfessors = new ArrayList<objProfessor>();
        String line; //a line of data from the file
        //now read each line of the file until we reach end of file
        while((line = bufferedReader.readLine()) != null){
            try{
                lstProfessors.add(new objProfessor(line));
            }catch(Exception ex){
                ex.printStackTrace();
                return null;
            }
        }
        return lstProfessors;
    }
    public HashMap<String, ArrayList<String>> getTimes() throws IOException {
        /**
         * Name : getTimes
         * Params : none
         * Returns : mapTimes - a Hashmap where keys are single weekdays and values are an arraylist of possible time a class can ocurr on that day
         * Purpose : the purpose of this method is to read in a file containing each possible start and end time on a given day and
         *           then return a hashmap with the days as keys and all times as a value.
         */
        HashMap<String, ArrayList<String>> mapTimes = new HashMap<>();
        String line;
        String key = "";
        ArrayList<String> lstTemp = new ArrayList<>();
        if((line = bufferedReader.readLine()) != null) {
            key = line;
        }
        while((line = bufferedReader.readLine()) != null){
            if(line.split(":").length != 2){
                //now we are on to a new day/hours section
                //add old one to hashmap
                mapTimes.put(key, lstTemp);
                lstTemp = new ArrayList<>();
                key = line;
                continue;
            }
            lstTemp.add(line);
        }
        mapTimes.put(key, lstTemp);


        return mapTimes;
    }
}
