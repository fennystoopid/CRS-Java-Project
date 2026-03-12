/* Part of Eligibility Check and Enrolment, can be used in other programs if usable */
package model;

public class CourseRecord {
    private String courseCode;
    private int creditHours;
    private double gradePoint;
    
    // Constant for minimum passing grade point (eliminating magic number)
    private static final double MIN_PASS_GRADE_POINT = 2.0; 

    public CourseRecord(String courseCode, int creditHours, double gradePoint) {
        this.courseCode = courseCode;
        this.creditHours = creditHours;
        this.gradePoint = gradePoint;
    }

    public int getCreditHours() { return creditHours; }
    public double getGradePoint() { return gradePoint; }
    
    // Uses the constant for clear failure logic
    public boolean isFailed() { return gradePoint < MIN_PASS_GRADE_POINT; } 
}

