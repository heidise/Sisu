package fi.tuni.prog3.sisu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class is used to define the students' basic info. 
 * Read from and write the updated info to students.txt.
 * @author shuang.fan@tuni.fi
 */
public class StudentsData {
    private static final String FILENAME = "src/main/students.txt";
    
    /**
    * Check the students.txt exists or not.
    * @return True, if the file exists.
    */
    public static Boolean fileExists() {
        var file = new File(FILENAME);
        return file.exists();
    }
    
    /**
     * Read the students from file.
     * @return The list of students.
     * @throws IOException 
     */
    public static ArrayList<Student> readFromFile() throws IOException {
        try (var file = new BufferedReader(new FileReader(FILENAME))) {
            var students = new ArrayList<Student>();
            var lines = file.lines().toArray();
            for (var l : lines) {
                var line = (String)l;
                var splits = line.split(";");
                if (splits.length == 6) {
                    var id = splits[0];
                    var name = splits[1];
                    var startYear = splits[2];
                    var endYear = splits[3];
                    var degree = splits[4];
                    var major = splits[5];
                    var student = new Student(id, name, startYear, endYear, degree, major);
                    students.add(student);
                }
                else {
                    var id = line;
                    var student = new Student(id);
                    students.add(student);
                }                
            }
            file.close();
            return students;
        }
        catch (IOException e) {
            return null;
        }
    }
    
    /**
     * Write the current students info to students.txt.
     * <p>Format: {id};{name};{start year};{end year};{degree};{major}</p>
     * @param list The students list.
     * @throws IOException if there is any IO error
     */
    public static void writeToFile(ArrayList<Student> list) throws IOException {
        try (var file = new BufferedWriter(new FileWriter(FILENAME))){
            Boolean isFirst = true;
            for (var element : list) {
                var id = element.getId();
                var name = element.getName();
                var startYear = element.getStartYear();
                var endYear = element.getEndYear();
                var degree = element.getDegree();
                var major = element.getMajor();
                if (isFirst) {
                    file.write(String.format("%s;%s;%s;%s;%s;%s", 
                    id, name, startYear, endYear, degree, major));
                    isFirst = false;
                }
                else {
                    file.write(String.format("\n%s;%s;%s;%s;%s;%s", 
                    id, name, startYear, endYear, degree, major));                        
                }
            }
            file.close();
        }
    }
}
