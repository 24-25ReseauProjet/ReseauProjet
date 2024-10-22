import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterUI {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private UserDataManager userDataManager; // 用于管理用户数据

    public RegisterUI() {
        userDataManager = new UserDataManager();

        // 创建注册窗口
        frame = new JFrame("User Registration");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // 用户名标签和输入框
        JLabel userLabel = new JLabel("Username:");
        userLabel.setBounds(50, 50, 100, 30);
        frame.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 50, 200, 30);
        frame.add(usernameField);

        // 密码标签和输入框
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(50, 100, 100, 30);
        frame.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 100, 200, 30);
        frame.add(passwordField);

        // 确认密码标签和输入框
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setBounds(50, 150, 120, 30);
        frame.add(confirmPasswordLabel);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(150, 150, 200, 30);
        frame.add(confirmPasswordField);



        // 注册按钮
        JButton registerButton = new JButton("Register");
        registerButton.setBounds(80, 200, 120, 30); // 调整按钮位置
        frame.add(registerButton);

        // 返回按钮
        JButton returnButton = new JButton("Return Main");
        returnButton.setBounds(210, 200, 120, 30); // 调整按钮位置
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

        // 返回按钮点击事件
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // 关闭当前注册界面
                new MainScreenUI(); // 打开主界面
            }
        });

        frame.setVisible(true);
    }
    private void registerUser(String username, String password) {
        boolean registered = userDataManager.register(username, password);

        if (registered) {
            JOptionPane.showMessageDialog(frame, "Registration Successful!");
            frame.dispose();
            new LoginUI();
        } else {
            // 用户名已存在
            JOptionPane.showMessageDialog(frame, "Username already exists. Please choose a different username.");
        }
    }

}
