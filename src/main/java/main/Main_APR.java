package main;

import data.TimestampLogger;
import javax.swing.SwingUtilities;
import java.io.File;
import service.UserManager;
import service.EmailNotificationService;
import service.RecoveryService; 
import view.LoginFrame;

public class Main_APR {
    public static void main(String[] args) {
        
        final String SENDER_EMAIL = "raifizudin@gmail.com"; 
        final String SENDER_PASSWORD = "lybd bcqx rgym onui"; 

        File userFile = new File("data/users.csv");
        File logFile = new File("data/activity_log.txt");

        // 1. Instantiate the Email Service
        EmailNotificationService emailService = new EmailNotificationService(SENDER_EMAIL, SENDER_PASSWORD);
        
        // 2. Instantiate the UserManager, passing the Email Service
        UserManager userManager = new UserManager(userFile, emailService);

        // Instantiate RecoveryService and inject dependencies
        RecoveryService recoveryService = new RecoveryService(emailService, userManager); 

        TimestampLogger logger = new TimestampLogger(logFile);

        SwingUtilities.invokeLater(() -> {
            // Note: If LoginFrame needs RecoveryService, you must update its constructor
            LoginFrame lf = new LoginFrame(userManager, logger);
            lf.setVisible(true);
            
            // If the RecoveryService is used elsewhere, you'll need to pass it there
        });
    }
}