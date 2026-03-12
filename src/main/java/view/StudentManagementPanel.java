
package view;


import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

import data.StudentRepository;

import model.Student;

public class StudentManagementPanel extends JPanel {
    private StudentRepository repo;
    private JTable table;
    private DefaultTableModel model;

    public StudentManagementPanel(StudentRepository repo) {
        this.repo = repo;
        initUI();
        load();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"StudentID","FirstName","LastName","Major","Year","Email"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void load() {
        model.setRowCount(0);
        List<Student> list = repo.loadStudents();
        for (Student s : list) {
            model.addRow(new Object[]{s.getStudentID(), s.getFirstName(), s.getLastName(), s.getMajor(), s.getYear(), s.getEmail()});
        }
    }
}
