/* Part of Eligibility Check and Enrolment, can be used in other programs if usable */
package service;

//Use all the model
import com.opencsv.exceptions.CsvValidationException;
import model.*;

//Using CsvReader and CsvFormat function
import data.CsvReader;
import data.CsvFormat;

import java.util.*; 

public class EnrolmentManager {

    private EligibilityChecker checker;
    private ArrayList<Student> students;

    public EnrolmentManager(EligibilityChecker checker) throws CsvValidationException {
        this.checker = checker;
        this.students = (ArrayList<Student>) loadData();
    }
    
    private List<Student> loadData() throws CsvValidationException {
    
        //Load Student
        CsvReader<Student> studReader = new CsvReader<>(CsvFormat.STUDENT);
        List<Student> studentList = studReader.findAll(CsvFormat.STUDENT_FILE);
        
        //Load Courses
        CsvReader<Course> courseReader = new CsvReader<>(CsvFormat.COURSE);
        List<Course> courses = courseReader.findAll(CsvFormat.COURSE_FILE);
        
        // Map CourseID -> CreditHours
        Map<String, Integer> creditMap = new HashMap<>();
        for (Course c : courses) creditMap.put(c.getCourseID(), c.getCredits());
        
        //Load Results
        CsvReader<Results> resReader = new CsvReader<>(CsvFormat.RESULT);
        List<Results> allResults = resReader.findAll(CsvFormat.RESULT_FILE);
        
        //Connect Results to Students (like us goonected fr fr)
        for (Results r : allResults) {
            for (Student s : studentList) {
                if (s.getStudentID().equals(r.getStudentID())) {
                    // Get credits, defaults to 3 if missing
                    int cr = creditMap.getOrDefault(r.getCourseID(), 3);
                    // Add record to student
                    s.addCourseRecord(new CourseRecord(r.getCourseID(), cr, r.getGradePoint()));
                }
            }
        }
        return studentList;
    }
     
    public void addStudent(Student s) {
        students.add(s);
    }
    
    public List<Student> getAllStudents() {
        return students;
    }

    public ArrayList<Student> listIneligibleStudents() {
        ArrayList<Student> list = new ArrayList<>();
        for (Student s : students) {
            if (!checker.isEligible(s)) list.add(s);
        }
        return list;
    }

    public boolean attemptEnrolment(Student s) {
        if (checker.isEligible(s)) {
            s.setEnrolledForNextLevel(true);
            //System.out.println(s.getName() + " has been enrolled successfully.");
            return true;
        } else {
            // Directly print the error message, uh no bro
            //System.err.println("Enrolment Error for " + s.getName() + ": Student does not meet eligibility criteria (CGPA < 2.0 or > 3 Fails).");
            return false;
        }
    }
    
    public List<Student> enrolAllEligible() {
        List<Student> enrolledNow = new ArrayList<>();
        
        for (Student s : students) {
            // If not already enrolled AND eligibility check passes
            if (!s.isEnrolledForNextLevel() && attemptEnrolment(s)) {
                enrolledNow.add(s);
            }
        }
        return enrolledNow; // Return the list so the Panel can use the names
    }
    
}

// This class controls the entire workflow for:
// • Checking eligibility
// • Listing ineligible students
// • Processing enrolment
// It uses try-catch to avoid the program from crashing.
