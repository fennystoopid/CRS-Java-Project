package service;

import data.CsvFormat;
import data.CsvReader;
import util.CsvWriter;
import model.RecoveryTask;
import model.Results;
import model.Student;
import model.Course;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RecoveryService {
    private String currentStudentId;
    private String currentCourseId;
    private final List<RecoveryTask> currentTasks;
    
    private final EmailNotificationService emailService;
    private final UserManager userManager;
    
    //Update Constructor to accept dependencies
    public RecoveryService(EmailNotificationService emailService, UserManager userManager) {
        this.currentTasks = Collections.synchronizedList(new ArrayList<>());
        this.emailService = emailService;
        this.userManager = userManager;
    }

    public Course getCourseDetails(String courseId) {
        if (courseId == null) return null;
        CsvReader<Course> reader = new CsvReader<>(CsvFormat.COURSE);
        try {
            return reader.findOne(CsvFormat.COURSE_FILE, courseId);
        } catch (Exception e) {
            System.err.println("Error finding course details: " + e.getMessage());
            return null;
        }
    }
    
    public synchronized void saveAllPlans() {
        if (currentStudentId == null) return;
        
        List<RecoveryTask> allTasks = new ArrayList<>();
        File f = new File(CsvFormat.PLAN_FILE);
        // (file loading and merging logic remains the same) 
        if (f.exists()) {
            CsvReader<RecoveryTask> reader = new CsvReader<>(CsvFormat.RECOVERY);
            try { allTasks = reader.findAll(CsvFormat.PLAN_FILE); } catch (Exception e) {}
        }

        allTasks.removeIf(t -> t.getStudentId().equalsIgnoreCase(currentStudentId) && 
                               t.getCourseId().equalsIgnoreCase(currentCourseId));
        
        allTasks.addAll(currentTasks);

        List<String[]> rows = new ArrayList<>();
        for (RecoveryTask t : allTasks) {
            rows.add(new String[]{
                t.getStudentId(), t.getCourseId(), t.getStudyWeek(),
                t.getDescription(), String.valueOf(t.isCompleted()), String.valueOf(t.getScore())
            });
        }

        String[] header = {"StudentID", "CourseID", "Week", "Description", "Completed", "Score"};
        CsvWriter.write(CsvFormat.PLAN_FILE, rows, header);
        
        notifyStudentOfPlan();
    }
    
    private void notifyStudentOfPlan() {
        if (currentStudentId == null || currentTasks.isEmpty()) return;
        
        Student student = null;
        try {
            CsvReader<Student> reader = new CsvReader<>(CsvFormat.STUDENT);
            student = reader.findOne(CsvFormat.STUDENT_FILE, currentStudentId);
        } catch (Exception e) {
            System.err.println("Error looking up student details: " + e.getMessage());
        }
        
        if (student != null) {
            System.out.println("Found student: " + student.getName() + " | Email: " + student.getEmail());
            
            StringBuilder milestones = new StringBuilder();
            for (RecoveryTask task : currentTasks) {
                milestones.append(" • ").append(task.getStudyWeek())
                          .append(": ").append(task.getDescription())
                          .append("\n");
            }
            
            boolean success = emailService.sendCourseRecoveryPlanEmail(
                student, 
                currentCourseId, 
                milestones.toString()
            );
            
            if (success) {
                System.out.println("Course Recovery Plan email sent to: " + student.getEmail());
            } else {
                System.err.println("Failed to send Course Recovery Plan email to: " + student.getEmail());
            }
        } else {
            System.err.println("Cannot find student with ID: " + currentStudentId + " in " + CsvFormat.STUDENT_FILE);
        }
    }


    // --- Context & Setup ---
    public void setContext(String studentId, String courseId) {
        this.currentStudentId = studentId;
        this.currentCourseId = courseId;
        loadPlanForCurrentContext();
    }
    
    // ... [Keep getFailedStudents and getAllCourses same as before] ...
    public List<Results> getFailedStudents() {
        CsvReader<Results> reader = new CsvReader<>(CsvFormat.RESULT);
        try {
            return reader.findAll(CsvFormat.RESULT_FILE).stream()
                    .filter(r -> r.getGradePoint() < 2.0)
                    .collect(Collectors.toList());
        } catch (Exception e) { return new ArrayList<>(); }
    }

    public List<String> getAllCourses() {
        CsvReader<Results> reader = new CsvReader<>(CsvFormat.RESULT);
        try {
             return reader.findAll(CsvFormat.RESULT_FILE).stream()
                     .map(Results::getCourseID)
                     .distinct()
                     .collect(Collectors.toList());
        } catch (Exception e) { return new ArrayList<>(); }
    }

    public String getCourseName(String courseId) {
        if (courseId == null) return "";
        
        CsvReader<Course> reader = new CsvReader<>(CsvFormat.COURSE);
        try {
            // Find the course in the CSV
            Course c = reader.findOne(CsvFormat.COURSE_FILE, courseId);
            if (c != null) {
                return c.getCourseName(); 
            }
        } catch (Exception e) {
            System.err.println("Error finding course name: " + e.getMessage());
        }
        return "Unknown Course";
    }
    
    // --- Task Operations ---
    
    // NEW: Clear tasks to start a fresh plan
    public synchronized void clearCurrentPlan() {
        if (currentStudentId == null) return;
        currentTasks.clear();
    }

    public synchronized void addTask(String week, String desc, double score) {
        if (currentStudentId == null) return;
        RecoveryTask t = new RecoveryTask(week, desc);
        t.setStudentId(currentStudentId);
        t.setCourseId(currentCourseId);
        
        // If the user typed a valid score (>= 0), save it
        if (score >= 0) {
            t.setScore(score);
        }
        
        currentTasks.add(t);
    }

    public synchronized void updateTask(int index, String week, String desc, double score) {
        if (isValidIndex(index)) {
            RecoveryTask t = currentTasks.get(index);
            
            //Only update if the user actually typed something
            if (week != null && !week.trim().isEmpty()) {
                t.setStudyWeek(week);
            }

            //Only update if not empty
            if (desc != null && !desc.trim().isEmpty()) {
                t.setDescription(desc);
            }
            
            //Only update if a valid score (>= 0) was provided.
            //If the text box was empty, your panel sends -1.0, 
            //so this check fails and the OLD score is preserved.
            if (score >= 0) {
                t.setScore(score);
            }
        }
    }

    public synchronized void removeTask(int index) {
        if (isValidIndex(index)) {
            currentTasks.remove(index);
        }
    }

    public synchronized void toggleTaskCompletion(int index, double score) {
        if (isValidIndex(index)) {
            RecoveryTask t = currentTasks.get(index);
            
            // Toggle the boolean
            boolean newState = !t.isCompleted();
            t.setCompleted(newState);
            
            // Only update the score if we are marking it as "Done" and a valid score was typed.
            // If we are marking "Undone", we usually leave the old score or ignore it.
            if (newState && score >= 0) {
                t.setScore(score);
            }

        }
    }
    
    public List<RecoveryTask> getCurrentTasks() {
        return new ArrayList<>(currentTasks);
    }

    public int calculateProgress() {
        if (currentTasks.isEmpty()) return 0;
        long count = currentTasks.stream().filter(RecoveryTask::isCompleted).count();
        return (int) ((count * 100.0) / currentTasks.size());
    }

    // File I/O

    private synchronized void loadPlanForCurrentContext() {
        currentTasks.clear();
        File f = new File(CsvFormat.PLAN_FILE);
        if (!f.exists()) return;

        CsvReader<RecoveryTask> reader = new CsvReader<>(CsvFormat.RECOVERY);
        try {
            List<RecoveryTask> studentTasks = reader.findOneList(CsvFormat.PLAN_FILE, currentStudentId);
            for (RecoveryTask t : studentTasks) {
                if (t.getCourseId().equalsIgnoreCase(currentCourseId)) {
                    currentTasks.add(t);
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading plan: " + e.getMessage());
        }
    }


    public Results getCurrentOfficialResult() {
        CsvReader<Results> reader = new CsvReader<>(CsvFormat.RESULT);
        try {
            List<Results> all = reader.findAll(CsvFormat.RESULT_FILE);
            for(Results r : all) {
                if(r.getStudentID().equalsIgnoreCase(currentStudentId) && 
                   r.getCourseID().equalsIgnoreCase(currentCourseId)) return r;
            }
        } catch (Exception e) {}
        return null;
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < currentTasks.size();
    }
}