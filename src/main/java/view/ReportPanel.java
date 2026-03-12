/* Part of Academic Performance Report, can be used in other programs if usable */
package view;

import model.Student;
import data.CsvFormat;
import data.CsvReader;
import service.AcademicReport;
import service.EmailNotificationService;
import util.EmailTemplates;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class ReportPanel extends javax.swing.JPanel {
    
    // Logger setup
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ReportPanel.class.getName());

    // UI Components
    private javax.swing.JButton EmailButton;
    private javax.swing.JButton GenerateButton;
    private javax.swing.JLabel StudentIDText;
    private javax.swing.JTextField StudentIDTextField;
    
    //Email Service
    private EmailNotificationService emailService;

    // Constructor
    public ReportPanel(EmailNotificationService emailService) {
        this.emailService = emailService;
        initComponents();
    }

    // InitComponents
    private void initComponents() {

        StudentIDText = new javax.swing.JLabel();
        StudentIDTextField = new javax.swing.JTextField();
        GenerateButton = new javax.swing.JButton();
        EmailButton = new javax.swing.JButton();

        // Label setup
        StudentIDText.setText("Student ID:");

        // Button setup
        GenerateButton.setText("Generate Report (PDF)");
        GenerateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                GenerateButtonActionPerformed(evt);
            }
        });

        EmailButton.setText("Send Report via Email");
        EmailButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                EmailButtonActionPerformed(evt);
            }
        });

        // CHANGE 3: Layout Manager (GroupLayout is fine, but applying to 'this')
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(GenerateButton)
                        .addGap(18, 18, 18)
                        .addComponent(EmailButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(StudentIDText)
                        .addGap(18, 18, 18)
                        .addComponent(StudentIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(100, Short.MAX_VALUE))
        );
        
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(StudentIDText)
                    .addComponent(StudentIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(GenerateButton)
                    .addComponent(EmailButton))
                .addContainerGap(165, Short.MAX_VALUE))
        );
    }

    //Generate Report
    private void GenerateButtonActionPerformed(java.awt.event.ActionEvent evt) {                                             
        String studentId = StudentIDTextField.getText().trim();
        
        if(studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Student ID.");
            return;
        }

        try {
            CsvReader<Student> studentReader = new CsvReader<>(CsvFormat.STUDENT);
            Student student = studentReader.findOne(CsvFormat.STUDENT_FILE, studentId);
            
            if (student != null) {
                AcademicReport report = new AcademicReport(student);
                report.export(student.getStudentID());
                
                try {
                    File pdfFile = new File("output/AcademicReport_" + student.getStudentID() + ".pdf");
                    if (pdfFile.exists() && java.awt.Desktop.isDesktopSupported()) {
                        java.awt.Desktop.getDesktop().open(pdfFile); // Opens the PDF immediately
                    }
                } catch (Exception ex) {
                    // If it fails to open (no PDF viewer), just ignore it silently
                    System.err.println("Could not auto-open PDF: " + ex.getMessage());
                }
                
                JOptionPane.showMessageDialog(this, "Academic Report PDF Generated for " + student.getFirstName() + " " + student.getLastName() + " ");
            } else {
                JOptionPane.showMessageDialog(this, "Student ID not found in database.");
            }
            
        } catch (Exception ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }                                            

    //Send Report Via Email
    private void EmailButtonActionPerformed(java.awt.event.ActionEvent evt) {                                          
        String studentId = StudentIDTextField.getText().trim();

        if(studentId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Student ID.");
            return;
        }

        try {
            
            CsvReader<Student> studentReader = new CsvReader<>(CsvFormat.STUDENT);
            Student student = studentReader.findOne(CsvFormat.STUDENT_FILE, studentId);
            
            if (student != null) {
                // Generate the PDF first to ensure it exists
                AcademicReport report = new AcademicReport(student);
                report.export(student.getStudentID());
                
                String pdfFileName = "output/AcademicReport_" + student.getStudentID() + ".pdf";
                
                File pdfFile = new File(pdfFileName);
                if (!pdfFile.exists()) {
                    JOptionPane.showMessageDialog(this, "Error: PDF was not generated.");
                    return;
                }
                
                String to = student.getEmail(); // Assuming sending to the student's email
                //String to = "afigaming05@gmail.com"; //hardcoded for testing
                
                String subject = "Academic Performance Report - " + student.getFirstName();
                String body = EmailTemplates.performanceReport(
                    student.getFirstName(),
                    "Please find your official academic results attached to this email."
                );
                
                // Change button text to indicate loading...
                EmailButton.setText("Sending...");
                EmailButton.setEnabled(false);
                
                // Run in background thread to avoid freezing the GUI
                new Thread(() -> {
                    boolean success = emailService.sendEmailWithAttachment(to, subject, body, pdfFileName);
                    
                    // Update GUI back on the main thread
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        EmailButton.setText("Send Report via Email");
                        EmailButton.setEnabled(true);
                        
                        if (success) {
                            JOptionPane.showMessageDialog(this, "Email sent successfully to " + to);
                        } else {
                            JOptionPane.showMessageDialog(this, "Failed to send email. Check internet or password.");
                        }
                    });
                }).start();

            } else {
                JOptionPane.showMessageDialog(this, "Student ID not found.");
            } 
        } catch (Exception ex) {
            ex.printStackTrace(); // Good for debugging
            JOptionPane.showMessageDialog(this, "Error sending email: " + ex.getMessage());
            EmailButton.setText("Send Report via Email");
            EmailButton.setEnabled(true);
        }
                
    }
}