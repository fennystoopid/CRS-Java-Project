
package view;

import model.RecoveryTask;
import model.Results;
import model.Course;

import service.RecoveryService;
import service.EmailNotificationService;
import service.UserManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;


public class RecoveryPanel extends JPanel {
    private RecoveryService service;
    
    // UI Components
    private JComboBox<String> cmbCourse;
    private JComboBox<String> cmbStudent;
    private JTextArea txtFailureDetails;
    private JTable taskTable;
    private DefaultTableModel tableModel;
    private JProgressBar progressBar;
    private JLabel lblCourseName;
    
    private JTextField txtWeek, txtTask, txtScore;

    public RecoveryPanel(UserManager userManager, EmailNotificationService emailService) {
        this.service = new RecoveryService(emailService, userManager);
        setLayout(new BorderLayout(10, 10));

        // 1. NORTH: SELECTION 
        JPanel northPanel = new JPanel(new BorderLayout());
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.setBorder(BorderFactory.createTitledBorder("1. Select Failed Student"));
        
        cmbCourse = new JComboBox<>();
        cmbStudent = new JComboBox<>();
        
        lblCourseName = new JLabel("  "); 
        lblCourseName.setForeground(Color.BLUE);
        lblCourseName.setFont(new Font("SansSerif", Font.BOLD, 11));

        JButton btnLoad = new JButton("Load / Start Plan");
        
        // Layout the components
        selectionPanel.add(new JLabel("Course:")); 
        selectionPanel.add(cmbCourse);
        selectionPanel.add(lblCourseName); // Add the name label right after the dropdown
        
        selectionPanel.add(Box.createHorizontalStrut(15)); // Add some space
        selectionPanel.add(new JLabel("Student:")); 
        selectionPanel.add(cmbStudent);
        selectionPanel.add(btnLoad);
        
        txtFailureDetails = new JTextArea(3, 40);
        txtFailureDetails.setEditable(false);
        txtFailureDetails.setBackground(new Color(255, 240, 240)); 
        txtFailureDetails.setBorder(BorderFactory.createTitledBorder("Failure Diagnostics"));

        northPanel.add(selectionPanel, BorderLayout.CENTER);
        northPanel.add(txtFailureDetails, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // 2. CENTER: TABLE 
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("2. Recovery Action Plan"));
        
        String[] columns = {"Week", "Task Description", "Status", "Score"};
        tableModel = new DefaultTableModel(columns, 0);
        taskTable = new JTable(tableModel);
        
        centerPanel.add(new JScrollPane(taskTable), BorderLayout.CENTER);
        
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        centerPanel.add(progressBar, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // 3. SOUTH: CONTROLS
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtWeek = new JTextField(8); txtTask = new JTextField(20); txtScore = new JTextField(5);
        inputPanel.add(new JLabel("Week:")); inputPanel.add(txtWeek);
        inputPanel.add(new JLabel("Task:")); inputPanel.add(txtTask);
        inputPanel.add(new JLabel("Score:")); inputPanel.add(txtScore);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Add Task");
        JButton btnUpdate = new JButton("Update");
        JButton btnDelete = new JButton("Remove");
        JButton btnGrade = new JButton("Mark Done/Undone");
        
        // Reset Plan and Save Plan
        JButton btnClear = new JButton("New/Reset Plan");
        btnClear.setBackground(new Color(255, 200, 200)); // Light Red
        
        JButton btnSave = new JButton("Save Plan (NO AUTOSAVE)");
        btnSave.setBackground(new Color(173, 216, 230)); // Light Blue

        btnPanel.add(btnAdd); btnPanel.add(btnUpdate); btnPanel.add(btnDelete); 
        btnPanel.add(Box.createHorizontalStrut(10)); 
        btnPanel.add(btnGrade);
        btnPanel.add(Box.createHorizontalStrut(20)); // Spacer
        btnPanel.add(btnClear);
        btnPanel.add(btnSave);

        southPanel.add(inputPanel);
        southPanel.add(btnPanel);
        add(southPanel, BorderLayout.SOUTH);

        // EVENTS
        populateCourses();
        cmbCourse.addActionListener(e -> populateFailedStudents());
        btnLoad.addActionListener(e -> loadSelectedStudent());
        
        btnAdd.addActionListener(e -> {
            service.addTask(txtWeek.getText(), txtTask.getText(), getScoreInput());
            refreshTable();
        });
        
        btnUpdate.addActionListener(e -> {
            int row = taskTable.getSelectedRow();
            if (row >= 0) {
                service.updateTask(row, txtWeek.getText(), txtTask.getText(), getScoreInput());
                refreshTable();
            }
        });
        
        btnDelete.addActionListener(e -> {
            int row = taskTable.getSelectedRow();
            if (row >= 0) {
                service.removeTask(row);
                refreshTable();
            }
        });
        
        cmbCourse.addActionListener(e -> {
            populateFailedStudents();
            updateCourseNameLabel();
        });
        
       btnGrade.addActionListener(e -> {
            int row = taskTable.getSelectedRow();
            if (row >= 0) {
                // Get score input using your helper
                double s = getScoreInput(); 
                
                // Call the NEW toggle method
                service.toggleTaskCompletion(row, s);
                
                refreshTable();
            }
        });
        
        // SAVING BUTTON EVENT
        btnSave.addActionListener(e -> {
            // 1. Disable button and update text immediately
            btnSave.setText("Sending Email Update...");
            btnSave.setEnabled(false);

            // 2. Create a background thread for the network/file operation
            new Thread(() -> {
                try {
                    service.saveAllPlans(); // Takes time to send email
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                // 3. Update GUI back on the main thread when done
                SwingUtilities.invokeLater(() -> {
                    btnSave.setText("Save Plan (NO AUTOSAVE)");
                    btnSave.setEnabled(true);
                    JOptionPane.showMessageDialog(this, "Plan Saved & Email Notification to Student Sent!");
                });
            }).start();
        });
        
        // CLEAR ALL PLAN EVENT
        btnClear.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete the existing plan and start a new one?", 
                "Create New Plan", JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                service.clearCurrentPlan();
                refreshTable();
            }
        });
    }

    // HELPERS
    private void populateCourses() {
        cmbCourse.removeAllItems();
        List<String> courses = service.getAllCourses();
        for(String c : courses) cmbCourse.addItem(c);
        if(cmbCourse.getItemCount() > 0) cmbCourse.setSelectedIndex(0);
    }

    private void populateFailedStudents() {
        cmbStudent.removeAllItems();
        String selectedCourse = (String) cmbCourse.getSelectedItem();
        if(selectedCourse == null) return;

        List<Results> failures = service.getFailedStudents();
        List<String> studentsInCourse = failures.stream()
                .filter(r -> r.getCourseID().equalsIgnoreCase(selectedCourse))
                .map(Results::getStudentID)
                .distinct()
                .collect(Collectors.toList());

        if (studentsInCourse.isEmpty()) {
            cmbStudent.addItem("No Failures Found");
            cmbStudent.setEnabled(false);
        } else {
            cmbStudent.setEnabled(true);
            for(String s : studentsInCourse) cmbStudent.addItem(s);
        }
    }

    private void loadSelectedStudent() {
        String sId = (String) cmbStudent.getSelectedItem();
        String cId = (String) cmbCourse.getSelectedItem();
        
        if (sId != null && cId != null && !sId.equals("No Failures Found")) {
            service.setContext(sId, cId);
            
            Results r = service.getCurrentOfficialResult();
            Course c = service.getCourseDetails(cId); // Get Course Details
            
            StringBuilder sb = new StringBuilder();
            sb.append("Current Status: ").append(r.getGrade()).append(" (GP: ").append(r.getGradePoint()).append(")\n");
            
            // Get weights (Default to 100 if course not found to avoid crash)
            double assignWeight = (c != null) ? c.getAssignmentWeight() : 100.0;
            double examWeight = (c != null) ? c.getExamWeight() : 100.0;
            
            // Logic: Fail if score < 50% of weight (standard academic logic)
            if (r.getAssignmentScore() < (assignWeight / 2.0)) {
                sb.append("WARNING: Assignment Failed (")
                  .append(r.getAssignmentScore()).append("/").append((int)assignWeight).append(")\n");
            }
            
            if (r.getExamScore() < (examWeight / 2.0)) {
                sb.append("WARNING: Exam Failed (")
                  .append(r.getExamScore()).append("/").append((int)examWeight).append(")");
            }
            
            txtFailureDetails.setText(sb.toString());
            refreshTable();
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for(RecoveryTask t : service.getCurrentTasks()) {
            String status = t.isCompleted() ? "Completed" : "Pending";
            String score = (t.getScore() >= 0) ? String.valueOf(t.getScore()) : "-";
            tableModel.addRow(new Object[]{t.getStudyWeek(), t.getDescription(), status, score});
        }
        progressBar.setValue(service.calculateProgress());
        txtWeek.setText(""); txtTask.setText(""); txtScore.setText("");
    }
    
    // Helper to safely parse the score field
    private double getScoreInput() {
        try {
            String text = txtScore.getText().trim();
            if (text.isEmpty()) return -1.0; // Default to -1 (no score)
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            return -1.0; // Treat invalid text as no score
        }
    }
    
    // To update Course Text Label 
    private void updateCourseNameLabel() {
        String cId = (String) cmbCourse.getSelectedItem();
        if (cId != null) {
            String name = service.getCourseName(cId);
            lblCourseName.setText(name);
        } else {
            lblCourseName.setText("");
        }
    }
    
}