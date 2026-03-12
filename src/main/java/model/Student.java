/* SHARED , can be used in other programs if usable */
package model;

import java.util.ArrayList;
import java.util.List;
import util.DMUtils;

public class Student extends User {
    private String firstName;
    private String lastName;
    private String major;
    private String year;
    private String email;
    
    // Eligibility Fields
    private List<CourseRecord> courseRecords = new ArrayList<>();
    private boolean enrolledForNextLevel = false;
    
    // Constructor
    public Student(String studentID, String firstName, String lastName, String major, String year, String email, String passwordHash) {
        super(studentID, studentID, passwordHash, email, new Role("Student"), true);
        
        this.firstName = firstName;
        this.lastName = lastName;
        this.major = major;
        this.year = year;
        this.email = email;
    }
    
    // Constructor For CSV
    public Student(String studentID, String firstName, String lastName, String program, String year, String email) {
        // We call the main constructor above, passing a default password hash
        this(studentID, firstName, lastName, program, year, email, DMUtils.sha256("123")); 
    }

    // Getters
    public String getStudentID() {
        return super.getUserID();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMajor() {
        return major;
    }

    public String getYear() {
        return year;
    }

    public String getEmail() {
        return email;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    //For eligibility to work (WAAAAAAA)
    public List<CourseRecord> getCourseRecords() {
        return courseRecords;
    }
    
    public boolean isEnrolledForNextLevel() {
        return enrolledForNextLevel;
    }
    
        public void setEnrolledForNextLevel(boolean status) {
        this.enrolledForNextLevel = status;
    }
    
    // Add methods to access them
    public void addCourseRecord(CourseRecord c) {
        courseRecords.add(c);
    }
    
    // Helper to get Full Name
    public String getName() {
        return getFirstName() + " " + getLastName();
    }
    
    @Override
    public String toString() {
        return "Student{" +
                "studentID='" + super.getUserID() + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", program='" + major + '\'' +
                ", year=" + year +
                ", email='" + email + '\'' +
                '}';
    }
}