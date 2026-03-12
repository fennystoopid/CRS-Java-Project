package data;

import model.Results;
import model.Student;
import model.Course;
import model.RecoveryTask;

public class CsvFormat {
    
    //Constants of uhh file path, hardcoded sadly
    public static final String STUDENT_FILE = "data/student_information.csv";
    public static final String COURSE_FILE = "data/course_assessment_information.csv";
    public static final String RESULT_FILE = "data/results_information.csv";
    public static final String PLAN_FILE = "output/recovery_plan.csv"; 
    
    
    //student_information
    public static final CsvReader.RowMapper<Student> STUDENT = row -> {
        String id = row[0].trim();
        String firstname = row.length > 1 ? row[1].trim() : "";
        String lastname = row.length > 2 ? row[2].trim() : "";
        String program = row.length > 3 ? row[3].trim() : "";
        String year = row.length > 4 ? row[4].trim() : "";
        String email = row.length > 5 ? row[5].trim() : "";
        return new Student(id, firstname, lastname, program, year, email);
    };
    
    //course_assessment_information
    public static final CsvReader.RowMapper<Course> COURSE = row -> {
        return new Course(
            row[0].trim(),                          // ID
            row[1].trim(),                          // Name
            Integer.parseInt(row[2].trim()),        // Credits
            row[3].trim(),                          // Semester
            row[4].trim(),                          // Instructor
            Integer.parseInt(row[5].trim()),        // ExamWeight
            Integer.parseInt(row[6].trim())         // AssignWeight
        );
    };

    //results_information
    public static final CsvReader.RowMapper<Results> RESULT = row -> {
        float gp = 0f;
        try { gp = Float.parseFloat(row[4].trim()); } catch (NumberFormatException e) {} // Safe parse
        
        return new Results(
            row[0].trim(),                      // StudentID
            row[1].trim(),                      // CourseID
            Integer.parseInt(row[2].trim()),    // Semester
            row[3].trim(),                      // Grade
            gp, // GradePoint
            Double.parseDouble(row[5].trim()), // AssignmentScore
            Double.parseDouble(row[6].trim()) // ExamScore
        );
    };
    
    //recovery_plan
    public static final CsvReader.RowMapper<RecoveryTask> RECOVERY = row -> {
        // 1. Create basic task
        RecoveryTask task = new RecoveryTask(row[2].trim(), row[3].trim());
        
        // 2. Hydrate context fields
        task.setStudentId(row[0].trim());
        task.setCourseId(row[1].trim());
        
        // 3. Status fields
        task.setCompleted(Boolean.parseBoolean(row[4].trim()));
        try { 
            task.setScore(Double.parseDouble(row[5].trim())); 
        } catch (Exception e) { 
            task.setScore(-1.0); 
        }
        
        return task;
    };

}

//FORMAT REFERENCE
// For Student 
//nextLine[0] studentID
//nextLine[1] firstName
//nextLine[2] lastName
//nextLine[3] major
//nextLine[4] year
//nextLine[5] email

// For Course
//nextLine[0] CourseID
//nextLine[1] CourseName
//nextLine[2] Credits (integer!)
//nextLine[3] Semester
//nextLine[4] Instructor
//nextLine[5] ExamWeight (integer!)
//nextLine[6] AssignmentWeight (integer!)

// For Results
//nextLine[0] StudentID
//nextLine[1] CourseID
//nextLine[2] Grade
//nextLine[3] GradePoint (float!)