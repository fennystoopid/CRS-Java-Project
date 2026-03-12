package service;

import java.io.*;
import java.util.*;
import model.Role;

import model.User;
import util.DMUtils;
import util.PasswordCodeGenerator; 

public class UserManager {
    private List<User> users = new ArrayList<>();
    private File storageFile;
    private final EmailNotificationService emailService;
    
    // NEW: Store active reset codes temporarily (UserID -> ResetCode)
    private final Map<String, String> passwordResetCodes = new HashMap<>();

    public UserManager(File storageFile, EmailNotificationService emailService) {
        this.storageFile = storageFile;
        this.emailService = emailService; 
        
        if (!storageFile.getParentFile().exists()) storageFile.getParentFile().mkdirs();
        if (!storageFile.exists()) {
            try {
                storageFile.createNewFile();
            } catch (IOException e) { }
        }
        loadFromFile();

        // ensure an admin exists
        if (findByUsername("admin") == null) {
            User admin = new User(DMUtils.generateID("U"), "admin", DMUtils.sha256("admin123"), "admin@example.com", new Role("CourseAdministrator"), true);
            users.add(admin);
            saveToFile();
        }
    }
    

    // CORE USER MANAGEMENT METHODS (Restored and Corrected)

    public synchronized void addUser(User u) {
        u.setActive(true); 

        users.add(u);
        saveToFile();

        // Trigger the email confirmation immediately after saving
        try {
            // Generates the confirmation link (Using UserID as the token)
            String confirmationLink = "http://your-app-url/confirm?id=" + u.getUserID();
            emailService.sendConfirmationEmail(u, confirmationLink);
            System.out.println("Confirmation email sent to: " + u.getEmail());
        } catch (Exception e) {
            System.err.println("Failed to send confirmation email to " + u.getEmail() + ": " + e.getMessage());
        }
    }
    
    public synchronized boolean confirmAccount(String userID) {
        User u = findByID(userID);

        if (u == null) {
            System.err.println("Error: Confirmation failed. User ID not found: " + userID);
            return false;
        }

        if (u.isActive()) {
            System.out.println("User " + u.getUsername() + " is already active.");
            return true; 
        }

        // Activate the user
        activateUser(userID);
        System.out.println("Success: User " + u.getUsername() + " account confirmed and activated.");
        return true;
    }

    public synchronized void activateUser(String userID) {
        for (User u : users) {
            if (u.getUserID().equals(userID)) {
                u.setActive(true);
                saveToFile();
                return;
            }
        }
    }
    
    public synchronized void updateUser(User u) {
        // find by ID
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserID().equals(u.getUserID())) {
                users.set(i, u);
                saveToFile();
                return;
            }
        }
    }

    public synchronized void deactivateUser(String userID) {
        for (User u : users) {
            if (u.getUserID().equals(userID)) {
                u.setActive(false);
                saveToFile();
                return;
            }
        }
    }

    public synchronized User findByUsername(String username) {
        for (User u : users) if (u.getUsername().equals(username)) return u;
        return null;
    }

    /*Changed access modifier to PUBLIC to resolve the 'cannot be accessed' error*/
    public synchronized User findByID(String id) {
        for (User u : users) if (u.getUserID().equals(id)) return u;
        return null;
    }

    public synchronized boolean authenticate(String username, String passwordPlain) {
        User u = findByUsername(username);
        if (u == null) return false;
        // Check if user is active (crucial for confirmation process)
        if (!u.isActive()) return false; 
        String hash = DMUtils.sha256(passwordPlain);
        return u.getPasswordHash().equals(hash);
    }

    public synchronized List<User> listUsers() {
        return new ArrayList<>(users);
    }
    
    // NEW AUTOMATION PART: PASSWORD RECOVERY MANAGEMENT

    public synchronized boolean sendPasswordResetCode(String email) {
        User u = users.stream()
                      .filter(user -> user.getEmail().equalsIgnoreCase(email))
                      .findFirst()
                      .orElse(null);

        if (u == null) {
            System.err.println("Password reset failed: User not found for email " + email);
            return false; 
        }

        String resetCode = PasswordCodeGenerator.generateCode(6); 
        passwordResetCodes.put(u.getUserID(), resetCode);
        
        try {
            emailService.sendPasswordResetEmail(u, resetCode);
            System.out.println("Password reset code sent to: " + u.getEmail());
            return true;
        } catch (Exception e) {
            System.err.println("Failed to send password reset email to " + u.getEmail() + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public synchronized boolean resetPassword(String userID, String code, String newPasswordPlain) {
        String storedCode = passwordResetCodes.get(userID);

        if (storedCode == null || !storedCode.equals(code)) {
            System.err.println("Password reset failed: Invalid or expired code for user ID " + userID);
            return false;
        }

        User u = findByID(userID);
        if (u == null) return false;

        String newPasswordHash = DMUtils.sha256(newPasswordPlain);
        u.setPasswordHash(newPasswordHash);
        updateUser(u);

        passwordResetCodes.remove(userID);
        
        System.out.println("Password successfully reset for user: " + u.getUsername());
        return true;
    }

    // FILE I/O METHODS 

    private void loadFromFile() {
        users.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(storageFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                // CSV: userID,username,passwordHash,email,roleName,active
                String[] parts = line.split(",");
                if (parts.length < 6) continue;
                User u = new User(parts[0], parts[1], parts[2], parts[3], new Role(parts[4]), Boolean.parseBoolean(parts[5]));
                users.add(u);
            }
        } catch (IOException e) { }
    }

    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(storageFile))) {
            for (User u : users) {
                pw.printf("%s,%s,%s,%s,%s,%b\n", u.getUserID(), u.getUsername(), u.getPasswordHash(), u.getEmail(), u.getRole().getRoleName(), u.isActive());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}