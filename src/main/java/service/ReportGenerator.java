/* Part of Academic Performance Report, can be used in other programs if usable */
package service;


import model.Student;

// Abstract Class: Hides the implementation details
public abstract class ReportGenerator {
    
    protected Student student;

    public ReportGenerator(Student student) {
        this.student = student;
    }

    public abstract void export(String filePath) throws Exception;
}
