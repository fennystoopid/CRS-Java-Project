
package view;

import com.opencsv.exceptions.CsvValidationException;
import data.StudentRepository;
import data.TimestampLogger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import model.User;
import service.UserManager;
import service.EmailNotificationService;

public class DashboardFrame extends JFrame {
    private UserManager userManager;
    private TimestampLogger logger;
    private User currentUser;

    EmailNotificationService emailService = new EmailNotificationService("raifizudin@gmail.com", "ilyv knja yrlk ofjd");
    
    public DashboardFrame(UserManager userManager, TimestampLogger logger, User currentUser) throws CsvValidationException {
        this.userManager = userManager;
        this.logger = logger;
        this.currentUser = currentUser;
        initUI();
    }

    private void initUI() throws CsvValidationException {
        setTitle("Dashboard - " + currentUser.getUsername() + " (" + currentUser.getRole().getRoleName() + ")");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());

        // Top: welcome + logout
        JPanel top = new JPanel(new BorderLayout());
        JLabel welcome = new JLabel("Welcome, " + currentUser.getUsername() + " - Role: " + currentUser.getRole().getRoleName());
        top.add(welcome, BorderLayout.WEST);
        JButton btnLogout = new JButton("Logout");
        top.add(btnLogout, BorderLayout.EAST);
        root.add(top, BorderLayout.NORTH);

        btnLogout.addActionListener(e -> {
            logger.logLogout(currentUser.getUsername());
            SwingUtilities.invokeLater(() -> {
                LoginFrame lf = new LoginFrame(userManager, logger);
                lf.setVisible(true);
            });
            this.dispose();
        });

        // Center: tabs depending on role
        JTabbedPane tabs = new JTabbedPane();

        // Student management (both roles can view)
        File studentCsv = new File("data/student_information.csv");
        StudentRepository repo = new StudentRepository(studentCsv);
        StudentManagementPanel studentPanel = new StudentManagementPanel(repo);
        tabs.addTab("Students", studentPanel);

        // User management - only for CourseAdministrator
        if (currentUser.getRole().getRoleName().equals("CourseAdministrator")) {
            UserManagementPanel ump = new UserManagementPanel(userManager);
            tabs.addTab("User Management", ump);
        }
        
        // AcademicReport
        ReportPanel reportPanel = new ReportPanel(emailService);
        tabs.addTab("Academic Reports", reportPanel);
        
        // Eligibility Check and Enrolment
        tabs.addTab("Enrolment", new EnrolmentPanel(currentUser, emailService));
        
        // Course Recovery Plan
        RecoveryPanel recoveryPanel = new RecoveryPanel(userManager, emailService);
        tabs.addTab("Course Recovery Plan", recoveryPanel);
        
        root.add(tabs, BorderLayout.CENTER);

        add(root);
    }
}
