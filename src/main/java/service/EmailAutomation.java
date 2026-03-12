package service;

import com.opencsv.exceptions.CsvValidationException;
import model.Student;

import data.CsvReader;
import data.CsvFormat;

import util.EmailTemplates;

import java.util.List;

public class EmailAutomation {

    private EmailNotificationService emailService;

    public EmailAutomation(EmailNotificationService emailService) {
        this.emailService = emailService;
    }

    // Load Student data loader and send a selected email template to all students
    public void sendBulkEmails(String emailType) throws CsvValidationException {
            
        //Set up a new CSVreader object
        CsvReader<Student> reader = new CsvReader<>(CsvFormat.STUDENT);

        List<Student> students = reader.findAll("data/student_information.csv");
        
        System.out.println("Starting Bulk Email Process for: " + emailType);
        
        for (Student s : students) {
            // Skip header
            if (s.getEmail() == null || !s.getEmail().contains("@")) continue;
           
            String fullName = s.getFirstName() + " " + s.getLastName();

            String subject = "";
            String message = "";

            // Email Template for different cases????
            switch (emailType.toLowerCase()) {
                case "account" -> {
                    subject = "Your CRS Account Has Been Created";
                    message = EmailTemplates.accountCreated(fullName);
                }

                case "password" -> {
                    subject = "CRS Password Reset";
                    String tempCode = String.valueOf((int)(Math.random()*900000 + 100000));
                    message = EmailTemplates.passwordReset(fullName, tempCode);
                }

                case "recovery" -> {
                    subject = "Your Course Recovery Plan";
                    String plan = "- Attend remedial class\n- Submit tasks\n- Meet advisor\n";
                    message = EmailTemplates.courseRecoveryPlan(fullName, s.getMajor(), plan);
                }

                case "performance" -> {
                    subject = "Your Performance Report";
                    String report = "Current Status: Active\nYear: " + s.getYear();
                    message = EmailTemplates.performanceReport(fullName, report);
                }

                default -> {
                    System.out.println("Unknown email type: " + emailType);
                    return;
                }
            }

                // Send the Email 
                emailService.sendEmail(s.getEmail(), subject, message);
            }

            System.out.println("All automated emails sent successfully!");

        }

}
