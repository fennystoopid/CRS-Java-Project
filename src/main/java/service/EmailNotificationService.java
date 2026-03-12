package service;
        
import java.io.*;
import java.util.*;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

//Import from the project :3
import model.Student; 
import model.User; 
import util.EmailTemplates;

public class EmailNotificationService {

    private String senderEmail;
    private String senderPassword;

    // Constructor
    public EmailNotificationService(String senderEmail, String senderPassword) {
        this.senderEmail = senderEmail;
        this.senderPassword = senderPassword;
    }

    // MODIFICATION START: New method for sending the confirmation email

    public boolean sendConfirmationEmail(User user, String confirmationLink) {
        // 1. Get the subject and body from the EmailTemplates utility
        String subject = "Account Confirmation Required - Welcome to ASSIGNMENT";
        
        String messageBody = EmailTemplates.generateConfirmationEmailBody(
            user.getUsername(),
            confirmationLink
        );

        // 2. Use the existing robust sendEmail method
        return sendEmail(user.getEmail(), subject, messageBody);
    }
    // MODIFICATION END
    
    // NEW MODIFICATION START: Method for sending the password reset email

    public boolean sendPasswordResetEmail(User user, String resetCode) {
        // 1. Define the subject
        String subject = "Password Reset Request for Your ASSIGNMENT Account";
        
        // 2. Get the templated body
        String messageBody = EmailTemplates.passwordReset(
            user.getUsername(),
            resetCode
        );

        // 3. Use the existing robust sendEmail method (no attachment needed)
        return sendEmail(user.getEmail(), subject, messageBody);
    }
    
    //Method for sending the Course Recovery Plan email

    public boolean sendCourseRecoveryPlanEmail(User user, String courseName, String milestones) {
        // 1. Define the subject
        String subject = "Action Required: Your Course Recovery Plan for " + courseName;
        
        // 2. Get the templated body (uses the courseRecoveryPlan template)
        String messageBody = EmailTemplates.courseRecoveryPlan(
            user.getUsername(),
            courseName,
            milestones
        );

        // 3. Use the existing robust sendEmail method (no attachment needed)
        System.out.println("Attempting to send recovery plan for " + courseName + " to " + user.getEmail());
        return sendEmail(user.getEmail(), subject, messageBody);
    }
    
    public boolean sendEnrolmentEmail(User user, String enrolledStud) {
        // 1. Define the subject
        String subject = "New Update: The new enrolled students ";
        
        // 2. Get the templated body (uses the courseRecoveryPlan template)
        String messageBody = EmailTemplates.EnrolmentStatus(
            user.getUsername(),
            enrolledStud
        );

        // 3. Use the existing robust sendEmail method (no attachment needed)
        System.out.println("Attempting to send enrolled students names to " + user.getEmail());
        return sendEmail(user.getEmail(), subject, messageBody);
    }
    // NEW MODIFICATION END

    //updated SIMPLE EMAIL (Text Only)
    public boolean sendEmail(String recipientEmail, String subject, String messageBody) {
        // We assume the confirmation email doesn't need an attachment, so we call the attachment method with null
        return sendEmailWithAttachment(recipientEmail, subject, messageBody, null);
    }

    public boolean sendEmailWithAttachment(String recipientEmail, String subject, String messageBody, String filePath) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);

            // Body Part
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setText(messageBody);

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(mimeBodyPart);

            // Attachment Part (If file path is provided)
            if (filePath != null) {
                File f = new File(filePath);
                if (f.exists()) {
                    MimeBodyPart attachmentBodyPart = new MimeBodyPart();
                    attachmentBodyPart.attachFile(f);
                    multipart.addBodyPart(attachmentBodyPart);
                } else {
                    System.err.println("Attachment file not found: " + filePath);
                }
            }

            message.setContent(multipart);
            Transport.send(message);
            System.out.println("Email sent successfully to: " + recipientEmail);
            return true;

        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}