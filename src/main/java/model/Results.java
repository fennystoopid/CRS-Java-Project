package model;

public class Results {
    private String StudentID;
    private String CourseID;
    private int Semester;
    private String Grade;
    private float GradePoint;
    private double assignmentScore;
    private double examScore;

    // Constructor
    public Results(String studentID, String courseID, int semesterint, String grade, float gradePoint, double assign, double exam) {
        this.StudentID = studentID;
        this.CourseID = courseID;
        this.Semester = semesterint;
        this.Grade = grade;
        this.GradePoint = gradePoint;
        this.assignmentScore = assign;
        this.examScore = exam;
    }
    
    // Getters
    public String getStudentID() {
        return StudentID;
    }
    
    public String getCourseID() {
        return CourseID;
    }
    
    public int getSemester() {
        return Semester;
    }
    
    public String getGrade() {
        return Grade;
    }
    
    public float getGradePoint() {
        return GradePoint;
    }
    
    public double getAssignmentScore() {
        return assignmentScore;
    }
    
    public double getExamScore() {
        return examScore;
    }
    
    // Setters
    public void setStudentID(String studentID) {
        this.StudentID = studentID;
    }
    
    public void setCourseID(String courseID) {
        this.CourseID = courseID;
    }
    
    public void setSemester(int semesterint) {
        this.Semester = semesterint;
    }
    
    public void setGrade(String grade) {
        this.Grade = grade;
    }
    
    public void setGradePoint(float gradePoint) {
        this.GradePoint = gradePoint;
    }
    
    public void setAssignmentScore(double assign) {
        this.assignmentScore = assign;
    }
    
    public void setExamScore(double exam) {
        this.examScore = exam;
    }
    
    @Override
    public String toString() {
        return "Results{" +
                "studentID='" + StudentID + '\'' +
                ", courseID='" + CourseID + '\'' +
                ", semester='" + Semester + '\'' +
                ", grade='" + Grade + '\'' +
                ", gradepoint='" + GradePoint + '\'' +
                '}';
    }
    
}
