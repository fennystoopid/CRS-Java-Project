
package view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import model.Role;
import model.User;
import service.UserManager;
import util.DMUtils;

public class UserManagementPanel extends JPanel {
    private UserManager userManager;
    private JTable table;
    private DefaultTableModel model;

    public UserManagementPanel(UserManager userManager) {
        this.userManager = userManager;
        initUI();
        load();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        model = new DefaultTableModel(new String[]{"ID","Username","Email","Role","Active"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel controls = new JPanel();
        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDeactivate = new JButton("Deactivate");
        JButton btnResetPass = new JButton("Reset Password");
        controls.add(btnAdd); controls.add(btnEdit); controls.add(btnDeactivate); controls.add(btnResetPass);
        add(controls, BorderLayout.SOUTH);

        btnAdd.addActionListener(e -> onAdd());
        btnEdit.addActionListener(e -> onEdit());
        btnDeactivate.addActionListener(e -> onDeactivate());
        btnResetPass.addActionListener(e -> onReset());
    }

    private void load() {
        model.setRowCount(0);
        List<User> users = userManager.listUsers();
        for (User u : users) {
            model.addRow(new Object[]{u.getUserID(), u.getUsername(), u.getEmail(), u.getRole().getRoleName(), u.isActive()});
        }
    }

    private void onAdd() {
        UserEditDialog dlg = new UserEditDialog(null);
        dlg.setVisible(true);
        if (dlg.isOk()) {
            String id = DMUtils.generateID("U");
            String uname = dlg.getUsername();
            String pass = dlg.getPassword();
            String email = dlg.getEmail();
            String role = dlg.getRole();
            User u = new User(id, uname, DMUtils.sha256(pass), email, new Role(role), true);
            userManager.addUser(u);
            load();
        }
    }

    private void onEdit() {
        int r = table.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
        String id = (String) model.getValueAt(r, 0);
        User u = userManager.findByID(id);
        if (u == null) return;
        UserEditDialog dlg = new UserEditDialog(u);
        dlg.setVisible(true);
        if (dlg.isOk()) {
            u.setEmail(dlg.getEmail());
            u.setRole(new Role(dlg.getRole()));
            userManager.updateUser(u);
            load();
        }
    }

    private void onDeactivate() {
        int r = table.getSelectedRow();
        if (r == -1) { JOptionPane.showMessageDialog(this, "Select a user first."); return; }
        String id = (String) model.getValueAt(r, 0);
        userManager.deactivateUser(id);
        load();
    }

    private void onReset() {
        int r = table.getSelectedRow();
        if (r == -1) { 
            JOptionPane.showMessageDialog(this, "Select a user first."); 
            return; 
        }
        String id = (String) model.getValueAt(r, 0);
        User u = userManager.findByID(id);
        if (u == null) return;

        // Open a dialog to input the new password
        String newPassword = JOptionPane.showInputDialog(this, "Enter new password for user: " + u.getUsername(), "Reset Password", JOptionPane.PLAIN_MESSAGE);
        if (newPassword != null && !newPassword.trim().isEmpty()) {
            u.setPasswordHash(DMUtils.sha256(newPassword));
            userManager.updateUser(u);
            JOptionPane.showMessageDialog(this, "Password successfully reset.");
        } else {
            JOptionPane.showMessageDialog(this, "Password reset canceled or invalid input.");
        }
    }
}
