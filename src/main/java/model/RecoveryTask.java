/* Part of Course Recovery Plan, can be used in other programs if usable */
package model;

public class RecoveryTask {
    
    //Context Field for CSV mapping
    private String studentId;
    private String courseId;
    
    private String studyWeek;
    private String description;
    private boolean completed;
    private double score; // -1.0 if not graded

    public RecoveryTask(String studyWeek, String description) {
        this.studyWeek = studyWeek;
        this.description = description;
        this.completed = false;
        this.score = -1.0;
    }
    
    //Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    
    public String getStudyWeek() { return studyWeek; }
    public void setStudyWeek(String studyWeek) { this.studyWeek = studyWeek; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    
    public double getScore() { return score; }
    public void setScore(double score) {this.score = score;}
    
    @Override
    public String toString() {
        String status = completed ? "[X]" : "[ ]";
        String gradeInfo = (score >= 0) ? " | Score: " + score : "";
        return status + " [" + studyWeek + "] " + description + gradeInfo;
    }
    
}

