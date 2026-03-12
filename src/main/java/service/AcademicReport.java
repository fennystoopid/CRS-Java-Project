/* Part of Academic Performance Report, can be used in other programs if usable */
package service;

//main java input output lib
import model.Results;
import model.Student;
import model.Course;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//OpenCSV lib
import com.opencsv.exceptions.CsvValidationException;

//ITextPDF text lib, import everything cuz uh bs
import com.itextpdf.text.*;

//ITextPDF pdf lib
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;


import data.CsvFormat;
import data.CsvReader;


public class AcademicReport extends ReportGenerator{
    
    // We can make them static final if we want to reuse them everywhere
    private static final CsvReader<Course> courseReader = new CsvReader<>(CsvFormat.COURSE);
    private static final CsvReader<Results> resultReader = new CsvReader<>(CsvFormat.RESULT);
    
    //Use super() to pass student to parent
    public AcademicReport(Student student) {
        super(student); 
    }
    
    //PDF MAKER (no not that other pdf)
    @Override
    public void export(String studID) throws CsvValidationException {
        
        System.out.println("Preparing data for student ID " + studID + " report");
        
        //open document
        Document document = new Document();
        
        //stuff it with information fr fr fr
        try {
            
            //Check for file output, if doesnt exist make one
            File directory = new File("output");
                if (!directory.exists()) {
                directory.mkdirs(); // Create the folder if it's missing
            }

            PdfWriter.getInstance(document, new FileOutputStream("output/AcademicReport_" + studID + ".pdf"));
            document.open();
            
            //font
            Font normalFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            
            // Firstly, the student's details
            document.add(new Paragraph("Student Name: " + student.getFirstName() + " " + student.getLastName()));
            document.add(new Paragraph("Student ID: " + student.getStudentID()));
            document.add(new Paragraph("Program: " + student.getMajor()));
            
            //adding the table
            addTable(document, normalFont);
            
              
        //Error exception
        } catch (DocumentException | IOException e) {
            System.out.println("Failed to generate student report from CSV file to PDF file.");// Added proper error handling
        
        //close document
        } finally {
            document.close();
            System.out.println("AcademicReport_" + studID + ".pdf generated successfully");
        }
    }

   
    private void addSemesterGPA(Document docu, List<Results> semSubjects, Font bsfont) throws DocumentException, CsvValidationException {
        
        double totalPoints = 0.0;
        int totalCredits = 0;
        
        for (Results r : semSubjects) {
            String cid = r.getCourseID();
            Course c = courseReader.findOne(CsvFormat.COURSE_FILE, cid);
            int creditHour = c.getCredits();
            totalPoints += (r.getGradePoint() * creditHour);
            totalCredits += creditHour;
        }

        if (totalCredits > 0) {
            double gpa = totalPoints / totalCredits;
            Paragraph p = new Paragraph(String.format("GPA: %.2f", gpa), bsfont);
            p.setAlignment(Element.ALIGN_CENTER);
            p.setSpacingAfter(10f);
            docu.add(p);
        }
    }
    
    private void addFinalCGPA(Document docu, List<Results> allResults, Font bsfont) throws DocumentException, CsvValidationException {
        double grandTotalPoints = 0.0;
        int grandTotalCredits = 0;

        for (Results r : allResults) {
            String cid = r.getCourseID();
            Course c = courseReader.findOne(CsvFormat.COURSE_FILE, cid);
            int creditHour = c.getCredits();
            grandTotalPoints += (r.getGradePoint() * creditHour);
            grandTotalCredits += creditHour;
        }

        docu.add(new Paragraph("\n")); // Space before the final box

        if (grandTotalCredits > 0) {
            double cgpa = grandTotalPoints / grandTotalCredits;
        
            Paragraph p = new Paragraph("Final CGPA: " + String.format("%.2f", cgpa), bsfont);
            p.setAlignment(Element.ALIGN_CENTER);

            // Make it look like a distinct box
            PdfPTable totalTable = new PdfPTable(1);
            totalTable.setWidthPercentage(50);
        
            PdfPCell cell = new PdfPCell(p);
            cell.setBorderWidth(2f);
            cell.setPadding(10f);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER); // Center content
        
            totalTable.addCell(cell);
            docu.add(totalTable);
        } else {
            docu.add(new Paragraph("CGPA: 0.00", bsfont));
        }
    }
     
    public void addTable(Document docu, Font bsfont) throws CsvValidationException, DocumentException{
        
        //All results extracted from results_info 
        List<Results> resultsList = resultReader.findOneList(CsvFormat.RESULT_FILE, student.getStudentID());
        
        // If no results, notify in the report using empth table cell
        if (resultsList.isEmpty()) {
            
            //new table for sayin yo table empty bro
            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            
            PdfPCell empty = new PdfPCell(new Phrase("No results found for this student.", bsfont));
            empty.setBackgroundColor(BaseColor.PINK);
            empty.setColspan(5);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(empty);
            
            docu.add(table);
            return;
        }
        
        //GROUP the results by Semester (Key = Sem Number, Value = List of Subjects)
        Map<Integer, List<Results>> resultsBySem = resultsList.stream().collect(Collectors.groupingBy(Results::getSemester));
        
        //Sort the semesters after that shit so sem 1 on top and bla bla
        Map<Integer, List<Results>> sortedResults = new TreeMap<>(resultsBySem);
        
        for (Map.Entry<Integer, List<Results>> entry : sortedResults.entrySet()) { 
            
            //Get the basics aka constants
            int semesterint = entry.getKey();
            List<Results> currentSemData = entry.getValue();
          
            //YearsOfStudy (sounds like GearsOfWar lmaoo)
            int yearOfStudy = ((semesterint - 1) / 2) + 1;
            String YearnSemText = "--- Year " + yearOfStudy + " ---" + "\n Semester " + semesterint; 
            String SemOnlyText = "Semester " + semesterint;
            
            //The TitleText changes based on the semester num being odd well obviously shows the year lmao fuck this shit
            Paragraph TitleText;
            
            if (semesterint % 2 == 1) {
                TitleText = new Paragraph(YearnSemText, bsfont);
            } else {
                TitleText = new Paragraph(SemOnlyText, bsfont);
            }
            //Setting up the TitleText and add it into document
            TitleText.setAlignment(Element.ALIGN_CENTER);
            TitleText.setSpacingAfter(10f);
            docu.add(TitleText);
            
            // Then, Build a table: CourseID | CourseName | Credits | Grade | GradePoint
            PdfPTable table = new PdfPTable(new float[]{2f, 5f, 1.5f, 1.5f, 2f});
            //width of the table, 100f is the entire pdf file width
            table.setWidthPercentage(90f);
            table.setSpacingAfter(10f);
        
            
            Stream.of("Course ID", "Course Name", "Credits", "Grade", "Grade Point")
                    .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle, bsfont));
                    table.addCell(header);
            });
            
            // --- INNER LOOP: Runs for every SUBJECT in this semester ---
            for (Results r : currentSemData) {
                //Get the courseName and Credits from course csv
                String cid = r.getCourseID(); //CourseID
                Course c = courseReader.findOne(CsvFormat.COURSE_FILE, cid);
                String courseName = c != null ? c.getCourseName() : ""; //CourseName
                String credits = c != null ? String.valueOf(c.getCredits()) : ""; //Credits
                
                Stream.of(cid, courseName, credits, r.getGrade(), String.valueOf(r.getGradePoint()))
                .forEach(columnTitle -> {
                    PdfPCell row = new PdfPCell();
                    row.setBackgroundColor(BaseColor.WHITE);
                    row.setBorderWidth(1);
                    row.setPhrase(new Phrase(columnTitle, bsfont));
                    table.addCell(row);
                });
                
            }
            
            docu.add(table);     
            // We pass just this semester's subjects to calculate the GPA for this table
            addSemesterGPA(docu, currentSemData, bsfont);
        }
        
        // We pass ALL results to calculate the final CGPA
        addFinalCGPA(docu, resultsList, bsfont);
        
    }

}


//What the libraries i actually use in this forsaken code
//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.Phrase;
//import com.itextpdf.text.Font;
//import com.itextpdf.text.FontFactory;
//import com.itextpdf.text.Element;
