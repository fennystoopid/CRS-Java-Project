/* Part of Academic Performance Report, can be used in other programs if usable */
package data;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvReader<Type> {

    //Row Mapper, basically function to define the rows/format of the csv file
    public interface RowMapper<Type> {
        Type map(String[] row);
    }

    private final RowMapper<Type> mapper;

    //Constructor
    public CsvReader(RowMapper<Type> mapper) {
        this.mapper = mapper;
    }
    
    //For single, specific data in the csv file
    public Type findOne(String filePath, String searchId) throws CsvValidationException {
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] row;
            while ((row = reader.readNext()) != null) {
                // Assuming column 0 is always the ID we search for
                if (row.length > 0 && row[0].trim().equals(searchId)) {
                    return mapper.map(row); // Use the strategy
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }
        return null;
    }

    //Taking all data in the csv file
    public List<Type> findAll(String filePath) throws CsvValidationException {
        List<Type> list = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            reader.readNext(); // skip header
            String[] row;
            while ((row = reader.readNext()) != null) {
                // Enhanced check to skip empty lines or "ghost" rows at the end of files
                // Checks if row exists AND has content (not just a single empty string)
                if (row.length > 0 && !(row.length == 1 && row[0].trim().isEmpty())) {
                    list.add(mapper.map(row)); // Use the strategy
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }
        return list;
    }
    
    //Taking one Listed data in the csv file
    public List<Type> findOneList(String filePath, String searchId) throws CsvValidationException {
        List<Type> list = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            reader.readNext(); // skip header
            String[] row;
            while ((row = reader.readNext()) != null) {
                if (row.length > 0 && row[0].trim().equals(searchId)) {
                    list.add(mapper.map(row)); // Use the strategy
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV: " + e.getMessage());
        }
        return list;
    }
}
