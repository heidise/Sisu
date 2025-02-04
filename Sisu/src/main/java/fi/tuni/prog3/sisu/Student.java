package fi.tuni.prog3.sisu;

/**
 * This class stores student's basic info.
 * @author shuang.fan@tuni.fi
 */
public class Student implements Comparable{
    private String id;
    private String name;
    private String startYear;
    private String endYear;
    private String degree;
    private String major;
    
    /**
     * Constructs a Student object by the given id.
     * @param id The given student number as a String.
     */
    public Student(String id) {
        this.id = id;
        this.name = null;
        this.startYear = null;
        this.endYear = null;
        this.degree = null;
        this.major = null;
    }
    
    /**
     * Constructs a Student object by the student number, name, degree start year,
     * degree target graduation year, degree name and major name.
     * @param id the student number
     * @param name the student full name
     * @param startYear the degree start year
     * @param endYear the degree target graduation year
     * @param degree the degree name
     * @param major the major name
     */
    public Student(String id, String name, String startYear, String endYear,
            String degree, String major) {
        this.id = id;
        this.name = name;
        this.startYear = startYear;
        this.endYear = endYear;
        this.degree = degree;
        this.major = major;
    }
    
    /**
     * Set the student name by the given name.
     * @param name The student's full name.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Set the student's study starting year.
     * @param startYear The starting year.
     */
    public void setStartYear(String startYear) {
        this.startYear = startYear;
    }
    
    /**
     * Set the student's target graduation year.
     * @param endYear The target graduation year.
     */
    public void setEndYear(String endYear) {
        this.endYear = endYear;
    }
    
    /**
     * Set the student's degree.
     * @param degree The degree name.
     */
    public void setDegree(String degree) {
        this.degree = degree;
    }
    
    /**
     * Set the student's major.
     * @param major The major name.
     */
    public void setMajor(String major) {
        this.major = major;
    }
    
    /**
     * Get the student id.
     * @return The student id.
     */
    public String getId() {
        return this.id;
    }
    
    /**
     * Get the student full name.
     * @return The student full name.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Get the student study starting year.
     * @return The student study starting year.
     */
    public String getStartYear() {
        return this.startYear;
    }
    
    /**
     * Get the student study target graduation year.
     * @return The student study target graduation year.
     */
    public String getEndYear() {
        return this.endYear;
    }
    
    /**
     * Get the student degree.
     * @return The student degree.
     */
    public String getDegree() {
        return this.degree;
    }
    
    /**
     * Get the student major.
     * @return the student major name.
     */
    public String getMajor() {
        return this.major;
    }

    /**
     * Compare two student id.
     * @param o The other object, which need to compare.
     * @return 
     */
    @Override
    public int compareTo(Object o) {
        Student other = (Student)(o);
        return this.id.compareTo(other.id);
    }
}