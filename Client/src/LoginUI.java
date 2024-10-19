import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginUI {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Client client;

    public LoginUI() {
        // 创建认证窗口
        frame = new JFrame("User Authentication");
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

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(150, 150, 100, 30);
        frame.add(loginButton);

        // 登录按钮点击事件
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                authenticateUser(username, password);
            }
        });

        frame.setVisible(true);
    }

    private void authenticateUser(String username, String password) {
        // 创建 Client 对象进行认证
        client = new Client();
        boolean authenticated = client.authenticateWithCredentials(username, password);

        if (authenticated) {
            JOptionPane.showMessageDialog(frame, "Authentication Successful!");
            frame.dispose(); // 关闭认证窗口
            new GameUI(client); // 打开游戏窗口
        } else {
            JOptionPane.showMessageDialog(frame, "Authentication Failed. Please try again.");
        }
    }
}
