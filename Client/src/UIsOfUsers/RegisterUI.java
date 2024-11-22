package UIsOfUsers;

import Methodes.UserDataManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterUI {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private UserDataManager userDataManager;

    public RegisterUI() {
        userDataManager = new UserDataManager();

        frame = new JFrame("User Registration");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 50, 100, 30);
        frame.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 50, 200, 30);
        frame.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 100, 100, 30);
        frame.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 100, 200, 30);
        frame.add(passwordField);

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setBounds(50, 150, 120, 30);
        frame.add(confirmPasswordLabel);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(150, 150, 200, 30);
        frame.add(confirmPasswordField);



        JButton registerButton = new JButton("Register");
        registerButton.setBounds(80, 200, 120, 30);
        frame.add(registerButton);

        // 返回按钮
        JButton returnButton = new JButton("Return Main");
        returnButton.setBounds(210, 200, 120, 30);
        frame.add(returnButton);


        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (password.equals(confirmPassword)) {
                    registerUser(username, password);
                } else {
                    JOptionPane.showMessageDialog(frame, "Passwords do not match. Please try again.");
                }
            }
        });

        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new MainScreenUI();
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    private void registerUser(String username, String password) {
        boolean registered = userDataManager.register(username, password);

        if (registered) {
            JOptionPane.showMessageDialog(frame, "Registration Successful!");
            frame.dispose();
            new LoginUI();
        } else {
            JOptionPane.showMessageDialog(frame, "Username already exists. Please choose a different username.");
        }
    }

}
