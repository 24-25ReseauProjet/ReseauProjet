import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameUI {
    private JButton startGameButton;
    private JLabel statusLabel;
    private JTextArea outputArea;
    private Client client;
    private JTextField inputField;
    private String userInput;

    public GameUI(Client client) {
        this.client = client;
        client.setGameUI(this); // 让Client知道这个GameUI

        // 创建游戏窗口
        JFrame frame = new JFrame("The UI Of Game");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // 添加状态标签
        statusLabel = new JLabel("Game started...");
        statusLabel.setBounds(50, 20, 300, 30);
        frame.add(statusLabel);

        // 添加输出文本区域
        outputArea = new JTextArea();
        outputArea.setBounds(50, 70, 500, 200);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBounds(50, 70, 500, 200);
        frame.add(scrollPane);

        // 添加输入字段
        inputField = new JTextField();
        inputField.setBounds(50, 300, 400, 30);
        inputField.setEditable(true);
        frame.add(inputField);

        // 输入框监听器，当用户按下回车时
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    synchronized (GameUI.this) {
                        userInput = inputField.getText().trim();
                        inputField.setText("");
                        client.sendInputToServer(userInput); // 发送用户输入到服务器
                    }
                }
            }
        });

        // 添加一个按钮用于重新启动游戏或其他功能（可选）
        startGameButton = new JButton("Restart Game");
        startGameButton.setBounds(50, 350, 150, 30);
        frame.add(startGameButton);

        startGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // 这里可以加入重置游戏或者其他操作
                statusLabel.setText("Game restarted...");
                client.start(); // 重新启动游戏逻辑
            }
        });

        frame.setVisible(true);

        // 启动游戏逻辑
        client.start();
    }


    // 用于从客户端添加消息到输出区域
    public void appendToOutput(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                outputArea.append(message + "\n");
            }
        });
    }

    public synchronized String waitForUserInput() {
        try {
            userInput = null;
            while (userInput == null) {
                wait(); // 等待用户输入
            }
            return userInput;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
