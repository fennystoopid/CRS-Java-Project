
package view;

import javax.swing.*;
import java.awt.*;
import model.User;

public class UserEditDialog extends JDialog {
    private JTextField tfUser, tfEmail;
    private JPasswordField pfPass;
    private JComboBox<String> cbRole;
    private boolean ok = false;

    public UserEditDialog(User existing) {
        setModal(true);
        setTitle(existing == null ? "Add User" : "Edit User");
        setSize(350, 350);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel l1 = new JLabel("Username:"); l1.setBounds(10, 10, 100, 25); add(l1);
        tfUser = new JTextField(); tfUser.setBounds(120, 10, 200, 25); add(tfUser);

        JLabel l2 = new JLabel("Password:"); l2.setBounds(10, 45, 100, 25); add(l2);
        pfPass = new JPasswordField(); pfPass.setBounds(120, 45, 200, 25); add(pfPass);

        JLabel l3 = new JLabel("Email:"); l3.setBounds(10, 80, 100, 25); add(l3);
        tfEmail = new JTextField(); tfEmail.setBounds(120, 80, 200, 25); add(tfEmail);

        JLabel l4 = new JLabel("Role:"); l4.setBounds(10, 115, 100, 25); add(l4);
        cbRole = new JComboBox<>(new String[]{"CourseAdministrator", "AcademicOfficer"});
        cbRole.setBounds(120, 115, 200, 25); add(cbRole);

        JButton btnOk = new JButton("OK"); btnOk.setBounds(60, 175, 90, 25); add(btnOk);
        JButton btnCancel = new JButton("Cancel"); btnCancel.setBounds(180, 175, 90, 25); add(btnCancel);

        if (existing != null) {
            tfUser.setText(existing.getUsername()); tfUser.setEnabled(false);
            tfEmail.setText(existing.getEmail());
            cbRole.setSelectedItem(existing.getRole().getRoleName()); // Set the existing role in the dropdown
            
            // Fetch the password from the dataset and display it as **
            pfPass.setText("********");
            pfPass.setEditable(false); // Make the password field non-editable
        }

        btnOk.addActionListener(e -> {
            if (tfUser.getText().trim().isEmpty() || (existing == null && pfPass.getPassword().length == 0)) {
                JOptionPane.showMessageDialog(this, "Username and password (for new users) required."); return;
            }
            ok = true; setVisible(false);
        });

        btnCancel.addActionListener(e -> { ok = false; setVisible(false); });
    }

    public boolean isOk() { return ok; }
    public String getUsername() { return tfUser.getText().trim(); }
    public String getPassword() { return new String(pfPass.getPassword()); }
    public String getEmail() { return tfEmail.getText().trim(); }
    public String getRole() { 
        return cbRole.getSelectedItem().toString(); // Get the selected role from the dropdown
    }
}