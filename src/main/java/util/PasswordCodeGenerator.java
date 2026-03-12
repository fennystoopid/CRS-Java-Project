package util;

import java.security.SecureRandom;

public class PasswordCodeGenerator {

    // Define the character set for the code (e.g., only digits for a simple code)
    private static final String CHARACTERS = "0123456789";
    private static SecureRandom random = new SecureRandom();

    public static String generateCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Code length must be positive.");
        }
        
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            // Get a random index from the defined character set
            int randomIndex = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomIndex));
        }
        return code.toString();
    }
    
    // Example main method for testing (can be removed later)
    public static void main(String[] args) {
        System.out.println("Generated 6-digit code: " + generateCode(6));
    }
}