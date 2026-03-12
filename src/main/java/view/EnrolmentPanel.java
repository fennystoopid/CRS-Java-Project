
package view;

import com.opencsv.exceptions.CsvValidationException;
import model.Student;
import model.User;
import service.EnrolmentManager;
import service.ProgressionEligibilityChecker;
import service.EmailNotificationService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class EnrolmentPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private EnrolmentManager manager;
    private ProgressionEligibilityChecker checker;
    
    private EmailNotificationService emailService;
    private User currentUser;
    private JButton btnEnrol;

    public EnrolmentPanel(User currentUser, EmailNotificationService emailService) throws CsvValidationException {
        this.emailService = emailService;
        this.currentUser = currentUser;
        
        // Init Logic
        checker = new ProgressionEligibilityChecker();
        manager = new EnrolmentManager(checker);
        
        initComponents();
        populateTable();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // Table
        String[] columns = {"ID", "Name", "Program", "CGPA", "Status", "Enrolled?"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Button
        btnEnrol = new JButton("Enrol Eligible Students");
        add(btnEnrol, BorderLayout.SOUTH);
      
        // --- MODIFIED ACTION LISTENER ---
        btnEnrol.addActionListener(e -> {
            // 1. UI BUFFER: Disable button and change text
            btnEnrol.setText("Processing Enrolment & Sending Email...");
            btnEnrol.setEnabled(false);

            // 2. BACKGROUND THREAD
            new Thread(() -> {
                // A. Run the enrolment logic
                List<Student> newlyEnrolled = manager.enrolAllEligible();
                
                // B. Prepare email if students were enrolled
                boolean emailSent = false;
                
                if (!newlyEnrolled.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Total Students Enrolled: ").append(newlyEnrolled.size()).append("\n\n");
                    
                    for (Student s : newlyEnrolled) {
                        sb.append("[").append(s.getStudentID()).append("] ")
                          .append(s.getName())
                          .append("\n");
                    }
                    
                    // Send Email to the CURRENT ADMIN (currentUser)
                    emailSent = emailService.sendEnrolmentEmail(currentUser, sb.toString());
                }

                // C. UPDATE UI (Must be on Swing Thread)
                final boolean finalEmailSent = emailSent; // needed for lambda
                SwingUtilities.invokeLater(() -> {
                    // Refresh Table
                    populateTable();
                    
                    // Reset Button
                    btnEnrol.setText("Enrol Eligible Students");
                    btnEnrol.setEnabled(true);
                    
                    // Show Result Message
                    if (newlyEnrolled.isEmpty()) {
                         JOptionPane.showMessageDialog(this, "No new students were eligible or available for enrolment.");
                    } else {
                        String msg = "Success! " + newlyEnrolled.size() + " students enrolled.";
                        if (finalEmailSent) msg += "\nSummary email sent to " + currentUser.getEmail();
                        else msg += "\n(Note: Email notification failed to send)";
                        
                        JOptionPane.showMessageDialog(this, msg);
                    }
                });
            }).start();
        });
    }
    

    private void populateTable() {
        model.setRowCount(0);
        for (Student s : manager.getAllStudents()) {
            boolean eligible = checker.isEligible(s);
            double cgpa = checker.calculateCGPA(s); // Uses the helper in ProgressionEligibilityChecker
            
            model.addRow(new Object[]{
                s.getStudentID(),
                s.getName(),
                s.getMajor(), // or getProgram()
                String.format("%.2f", cgpa),
                eligible ? "Eligible" : "Not Eligible",
                s.isEnrolledForNextLevel() ? "Yes" : "No"
            });
        }
    }
}