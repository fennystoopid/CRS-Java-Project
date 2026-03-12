/* SHARED , can be used in other programs if usable */
package model;

public class Course {
    private String CourseID;
    private String CourseName;
    private int Credits;
    private String Semester;
    private String Instructor;
    private int ExamWeight;
    private int AssignmentWeight;

    // Constructor
    public Course(String courseID, String coursenaem, int credits, String sem, String instructor, int examweight, int assignweight) {
        this.CourseID = courseID;
        this.CourseName = coursenaem;
        this.Credits = credits;
        this.Semester = sem;
        this.Instructor = instructor;
        this.ExamWeight = examweight;
        this.AssignmentWeight = assignweight;
    }
    
    // Getters
    public String getCourseID() {
        return CourseID;
    }

    public String getCourseName() {
        return CourseName;
    }
    
    public int getCredits() {
        return Credits;
    }
    
    public String getSemester() {
        return Semester;
    }
    
    public String getInstructor() {
        return Instructor;
    }
    
    public int getExamWeight() {
        return ExamWeight;
    }
    
    public int getAssignmentWeight() {
        return AssignmentWeight;
    }
    
    // Setters
    public void setCourseID(String courseID) {
        this.CourseID = courseID;
    }
    
    public void setCourseName(String coursenaem) {
        this.CourseName = coursenaem;
    }
    
    public void setCredits(int credits) {
        this.Credits = credits;
    }
        
    public void setSemester(String sem) {
        this.Semester = sem;
    }
    
    public void setInstructor(String instructor) {
        this.Instructor = instructor;
    }
    
    public void setExamWeight(int examweight) {
        this.ExamWeight = examweight;
    }
    
    public void setAssignmentWeight(int assignweight) {
        this.AssignmentWeight = assignweight;
    }
    
    @Override
    public String toString() {
        return "Course{" +
                "courseID='" + CourseID + '\'' +
                ", courseName='" + CourseName + '\'' +
                ", credits='" + Credits + '\'' +
                ", semester='" + Semester + '\'' +
                ", instructor=" + Instructor +
                ", examWeight='" + ExamWeight + '\'' +
                ", assignmentWeight='" + AssignmentWeight + '\'' +
                '}';
    }
}

