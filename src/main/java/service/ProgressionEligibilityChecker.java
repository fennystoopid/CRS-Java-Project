
package service;

import model.Student;
import model.CourseRecord;

public class ProgressionEligibilityChecker implements EligibilityChecker {

    @Override
    public boolean isEligible(Student s) {
        int failedCount = 0;
        double totalPoints = 0;
        int totalCredits = 0;

        for (CourseRecord c : s.getCourseRecords()) {
            totalPoints += c.getGradePoint() * c.getCreditHours();
            totalCredits += c.getCreditHours();
            if (c.isFailed()) failedCount++;
        }

        // Calculation of CGPA
        double cgpa = (totalCredits > 0) ? totalPoints / totalCredits : 0; 
        
        // Rules: CGPA >= 2.0 AND failedCount <= 3
        return cgpa >= 2.0 && failedCount <= 3;
    }

    public double calculateCGPA(Student s) {
        double totalPoints = 0;
        int totalCredits = 0;
        
        for (model.CourseRecord c : s.getCourseRecords()) {
            totalPoints += c.getGradePoint() * c.getCreditHours();
            totalCredits += c.getCreditHours();
        }
        
        return (totalCredits > 0) ? totalPoints / totalCredits : 0.0;
    }
}

// This class contains the actual rules for checking eligibility.
// The rules given in the assignment:
// 1) Minimum CGPA must be 2.0
// 2) No more than 3 failed courses
