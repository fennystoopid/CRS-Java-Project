
package main;

import java.io.*;
import java.util.*;

public class DataSeeder {

    public static void main(String[] args) {
        Locale.setDefault(Locale.US); // Force dot decimal separator

        String courseFile = "data/course_assessment_information.csv";
        String studentFile = "data/student_information.csv";
        String resultsFile = "data/results_information.csv";
        
        System.out.println("Generating schedule-aware results...");
        
        try {
            List<CourseInfo> allCourses = loadCourses(courseFile);
            Map<String, Integer> studentMaxSemesters = loadStudentMaxSemesters(studentFile);
            
            if(allCourses.isEmpty() || studentMaxSemesters.isEmpty()) {
                System.err.println("Files missing. Check data/ folder.");
                return;
            }
            
            BufferedWriter writer = new BufferedWriter(new FileWriter(resultsFile));
            writer.write("StudentID,CourseID,Semester,Grade,GradePoint,AssignmentScore,ExamScore");
            writer.newLine();

            Random rand = new Random();

            for (int i = 1; i <= 100; i++) {
                String studentID = String.format("S%03d", i);
                if (studentID.equals("S003")) continue; // Skip S003

                int maxSem = studentMaxSemesters.getOrDefault(studentID, 2);
                boolean hasTakenExam = (i <= 51); // S001-S051 take exams

                // Iterate through each semester the student has attended
                for (int sem = 1; sem <= maxSem; sem++) {
                    
                    // 1. Determine Season of this Semester
                    // Odd = Spring, Even = Fall
                    String currentSeason = (sem % 2 != 0) ? "Spring" : "Fall";
                    
                    // 2. Filter valid courses for this season
                    List<CourseInfo> availableCourses = new ArrayList<>();
                    for (CourseInfo c : allCourses) {
                        // Allow matching season OR Summer courses (available year-round)
                        if (c.season.equalsIgnoreCase(currentSeason) || c.season.equalsIgnoreCase("Summer")) {
                            availableCourses.add(c);
                        }
                    }
                    
                    // 3. Select realistic workload (4 to 6 courses)
                    Collections.shuffle(availableCourses);
                    int coursesToTake = 4 + rand.nextInt(3); // 4, 5, or 6
                    int actualCount = Math.min(coursesToTake, availableCourses.size());
                    
                    List<CourseInfo> selectedCourses = availableCourses.subList(0, actualCount);

                    // 4. Generate Results
                    for (CourseInfo c : selectedCourses) {
                        double assignScore, examScore;

                        if (hasTakenExam) {
                            double ability = rand.nextDouble(); 
                            if (ability > 0.2) { // 80% pass rate
                                assignScore = (c.assignWeight * 0.55) + (rand.nextDouble() * (c.assignWeight * 0.45));
                                examScore = (c.examWeight * 0.55) + (rand.nextDouble() * (c.examWeight * 0.45));
                            } else { // Fail
                                assignScore = rand.nextDouble() * (c.assignWeight * 0.45);
                                examScore = rand.nextDouble() * (c.examWeight * 0.45);
                            }
                        } else { // Absent
                            assignScore = rand.nextDouble() * 10;
                            examScore = 0.0;
                        }

                        GradeResult gr = calculateGrade(assignScore + examScore);
                        
                        String line = String.format(Locale.US, "%s,%s,%d,%s,%.1f,%.1f,%.1f",
                                studentID, c.id, sem, gr.grade, gr.point, assignScore, examScore);
                        
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }

            writer.close();
            System.out.println("Success! Generated results with correct Semester/Course matching.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- Helpers ---

    private static Map<String, Integer> loadStudentMaxSemesters(String path) throws IOException {
        Map<String, Integer> map = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = br.readLine(); 
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 5) {
                String id = parts[0].trim();
                String year = parts[4].trim();
                int maxSem;
                switch (year.toLowerCase()) {
                    case "freshman": maxSem = 2; break; 
                    case "sophomore": maxSem = 4; break; 
                    case "junior": maxSem = 6; break; 
                    case "senior": maxSem = 8; break; 
                    default: maxSem = 2; 
                }
                map.put(id, maxSem);
            }
        }
        br.close();
        return map;
    }

    private static List<CourseInfo> loadCourses(String path) throws IOException {
        List<CourseInfo> list = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = br.readLine(); 
        
        while ((line = br.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length >= 7) {
                String id = parts[0].trim();
                if(id.equalsIgnoreCase("CourseID")) continue;
                try {
                    // Index 3 is Semester (Summer/Spring/Fall)
                    String sem = parts[3].trim(); 
                    int ew = Integer.parseInt(parts[5].trim());
                    int aw = Integer.parseInt(parts[6].trim());
                    list.add(new CourseInfo(id, sem, ew, aw));
                } catch (Exception e) { }
            }
        }
        br.close();
        return list;
    }

    private static GradeResult calculateGrade(double score) {
        if (score >= 80) return new GradeResult("A", 4.0);
        if (score >= 75) return new GradeResult("A-", 3.7);
        if (score >= 70) return new GradeResult("B+", 3.3);
        if (score >= 65) return new GradeResult("B", 3.0);
        if (score >= 60) return new GradeResult("B-", 2.7);
        if (score >= 55) return new GradeResult("C+", 2.3);
        if (score >= 50) return new GradeResult("C", 2.0);
        if (score >= 45) return new GradeResult("C-", 1.7);
        if (score >= 40) return new GradeResult("D", 1.0);
        return new GradeResult("F", 0.0);
    }

    static class CourseInfo {
        String id; String season; int examWeight; int assignWeight;
        public CourseInfo(String id, String s, int e, int a) { 
            this.id = id; this.season = s; this.examWeight = e; this.assignWeight = a; 
        }
    }
    
    static class GradeResult {
        String grade; double point;
        public GradeResult(String g, double p) { this.grade = g; this.point = p; }
    }
}