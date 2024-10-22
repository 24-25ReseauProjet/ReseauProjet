import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

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
        // 返回按钮
        JButton returnButton = new JButton("Return Main");
        returnButton.setBounds(210, 200, 120, 30); // 调整按钮位置
        frame.add(returnButton);

        // 点击登录按钮
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());

                // 在尝试登录之前发送 reload 命令
                sendReloadCommandToAuthServer();

                // 然后进行用户认证
                authenticateUser(username, password);
            }
        });

        // 点击返回按钮
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // 关闭当前登录界面
                new MainScreenUI(); // 打开主界面
            }
        });

        frame.setVisible(true);
    }

    private void sendReloadCommandToAuthServer() {
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName("127.0.0.1");
            int serverPort = 54321; // 认证服务器的端口
            String message = "RELOAD_USER_DATA";
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
            socket.send(packet);
            socket.close();
            System.out.println("Sent reload command to auth server.");
        } catch (IOException e) {
            System.out.println("Failed to send reload command to auth server: " + e.getMessage());
        }
    }

    private void authenticateUser(String username, String password) {
        // 建立一个Client实例，套用用户信息进行认证
        this.client = new Client();
        boolean authenticated = client.authenticate(username, password);

        if (authenticated) {
            JOptionPane.showMessageDialog(frame, "Authentication Successful!");
            frame.dispose();
            new GameUI(client);
        } else {
            JOptionPane.showMessageDialog(frame, "Authentication Failed. Please try again.");
        }
    }
}
