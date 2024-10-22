import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainScreenUI {
    private JFrame frame;

    public MainScreenUI() {

        frame = new JFrame("Main Screen");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        //欢迎标题
        JLabel userLabel = new JLabel("Welcome to game");
        userLabel.setBounds(144, 50, 200, 30);
        frame.add(userLabel);

        // Register 按钮
        JButton registerButton = new JButton("Register");
        registerButton.setBounds(100, 100, 200, 30);
        frame.add(registerButton);

      //Login 按钮
        JButton loginButton = new JButton("Login");
        loginButton.setBounds(100, 150, 200, 30);
        frame.add(loginButton);


        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterUI(); // 打开注册界面
                frame.dispose(); // 关闭主界面
            }
        });


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new LoginUI(); // 打开登录界面
                frame.dispose(); // 关闭主界面
            }
        });

        frame.setVisible(true);
    }


}
