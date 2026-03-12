package util;

public class EmailTemplates {

    public static String generateConfirmationEmailBody(String studentName, String confirmationLink) {
        return "Dear " + studentName + ",\n\n"
                + "Your account on the Course Recovery System has been successfully created. "
                + "However, before you can log in, you must confirm your email address.\n\n"
                
                // CRITICAL STEP: Include the link in the message body
                + "Please click the link below to activate your account:\n\n"
                + "Account Activation Link: " + confirmationLink + "\n\n"
                
                + "If you did not sign up for this service, please ignore this email.\n\n"
                + "Regards,\n RIGBY AND LARYYYY"; // Keeping your signature!
    }

    // MODIFICATION END

    // TEMPLATE: ACCOUNT CREATION (The old one is kept, but the new one replaces its function)
    /*
    public static String accountCreated(String studentName) {
        return "Dear " + studentName + ",\n\n"
                + "Your account on the Course Recovery System has been successfully created.\n\n"
                + "You may now log in and manage your academic information womp womp.\n\n"
                + "Regards,\n RIGBY AND LARYYYY";
    }
    */


    // TEMPLATE: PASSWORD RESET
    public static String passwordReset(String studentName, String resetCode) {
        return "Dear " + studentName + ",\n\n"
                + "We received a request to reset your password.\n"
                + "Use the following code to reset it:\n\n"
                + "Reset Code: " + resetCode + "\n\n"
                + "This code will expire in 10 minutes.\n\n"
                + "Regards,\nUser Management System";
    }

    // TEMPLATE: COURSE RECOVERY ACTION PLAN
    public static String courseRecoveryPlan(String studentName, String courseName, String milestones) {
        return "Dear " + studentName + ",\n\n"
                + "You have been placed into Course Recovery for the course: " + courseName + ".\n"
                + "Below is your action plan and upcoming milestones:\n\n"
                + milestones + "\n\n"
                + "Please complete these tasks according to the given deadlines.\n"
                + "All the best.\n\n"
                + "Regards,\nCourse Recovery System";
    }

    // TEMPLATE: ACADEMIC PERFORMANCE REPORT
    public static String performanceReport(String studentName, String reportSummary) {
        return "Dear " + studentName + ",\n\n"
                + "Here is your latest academic performance update:\n\n"
                + reportSummary + "\n\n"
                + "Keep striving for excellence.\n\n"
                + "Regards,\nAcademic Performance Report System";
    }
    
    // TEMPLATE: ELIGIBILITY CHECKER AND ENROLMENT
    public static String EnrolmentStatus(String studentName, String statusSummary) {
        return "Dear " + studentName + ",\n\n"
                + "Here is the lastest student enrolled today:\n\n"
                + statusSummary + "\n\n"
                + "Let's keep up the good work!.\n\n"
                + "Regards,\nEligibility Checker And Enrolment System";
    }

    public static String accountCreated(String fullName) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}