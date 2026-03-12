
package view;


import com.opencsv.exceptions.CsvValidationException;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import data.TimestampLogger;

import model.User;
import service.UserManager;

public class LoginFrame extends JFrame {
    private UserManager userManager;
    private TimestampLogger logger;

    public LoginFrame(UserManager userManager, TimestampLogger logger) {
        this.userManager = userManager;
        this.logger = logger;
        initUI();
    }

    private void initUI() {
        setTitle("User Management - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(380, 200);
        setLocationRelativeTo(null);

        JPanel p = new JPanel();
        p.setLayout(null);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setBounds(20, 20, 80, 25);
        p.add(lblUser);

        JTextField tfUser = new JTextField();
        tfUser.setBounds(110, 20, 220, 25);
        p.add(tfUser);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setBounds(20, 60, 80, 25);
        p.add(lblPass);

        JPasswordField pf = new JPasswordField();
        pf.setBounds(110, 60, 220, 25);
        p.add(pf);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(110, 100, 100, 30);
        p.add(btnLogin);

        JButton btnQuit = new JButton("Quit");
        btnQuit.setBounds(230, 100, 100, 30);
        p.add(btnQuit);

        JLabel info = new JLabel("Default admin: admin / admin123");
        info.setBounds(20, 140, 300, 25);
//        p.add(info);

        btnLogin.addActionListener(e -> {
            String username = tfUser.getText().trim();
            String password = new String(pf.getPassword());
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter username & password.");
                return;
            }
            boolean ok = userManager.authenticate(username, password);
            if (ok) {
                User u = (User) userManager.findByUsername(username);
                logger.logLogin(username);
                SwingUtilities.invokeLater(() -> {
                    try {
                        DashboardFrame db = new DashboardFrame(userManager, logger, u);
                        db.setVisible(true);
                    } catch (CsvValidationException ex) {
                        System.getLogger(LoginFrame.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                    }
                });
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials or account inactive.");
            }
        });

        btnQuit.addActionListener(e -> System.exit(0));

        add(p);
    }
}