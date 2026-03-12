
package util;

import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvWriter {
    
    // Generic method to write ANY list of String arrays to a file
    public static void write(String filePath, List<String[]> data, String[] header) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Write the header first (e.g., "Week", "Task", "Score")
            writer.writeNext(header);
            
            // Write all the data rows
            writer.writeAll(data);
            
            System.out.println("Data successfully saved to: " + filePath);
        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }
    
}
