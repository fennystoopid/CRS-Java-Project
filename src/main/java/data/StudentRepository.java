package data;

import java.io.*;
import java.util.*;

import model.Student;

public class StudentRepository {
    private final File csvFile;

    public StudentRepository(File csvFile) {
        this.csvFile = csvFile;
    }

    public List<Student> loadStudents() {
        List<Student> list = new ArrayList<>();
        if (!csvFile.exists()) return list;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            // header
            br.readLine();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] d = line.split(",");
                if (d.length < 6) continue;
                Student s = new Student(d[0], d[1], d[2], d[3], d[4], d[5]);
                list.add(s);
            }
        } catch (IOException e) {}
        return list;
    }
}
