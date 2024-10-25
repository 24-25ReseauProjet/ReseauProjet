package UIsOfUsers;

import client.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PvPGameUI {
    private JFrame frame;
    private JLabel statusLabel;
    private JTextArea outputArea;
    private JTextField inputField;
    private Client client;
    private String userInput;

    public PvPGameUI(Client client) {
        this.client = client;

        // 让客户端知道此UI，用于处理消息显示
        client.setGameUI(this);

        // 创建游戏窗口
        frame = new JFrame("PvP Game - Word Guess");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // 添加状态标签
        statusLabel = new JLabel("Waiting for other player...");
        statusLabel.setBounds(50, 35, 400, 30);
        frame.add(statusLabel);

        // 添加输出文本区域
        outputArea = new JTextArea();
        outputArea.setBounds(50, 70, 500, 200);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBounds(50, 70, 500, 200);
        frame.add(scrollPane);

        JLabel remindLabel = new JLabel("Enter your guess and press Enter:");
        remindLabel.setBounds(50, 270, 300, 30);
        frame.add(remindLabel);

        // 添加输入字段
        inputField = new JTextField();
        inputField.setBounds(50, 300, 400, 30);
        inputField.setEditable(false); // 初始化时设置为不可编辑，等待回合时可编辑
        frame.add(inputField);

        // 输入框监听器，当用户按下回车时
        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    synchronized (PvPGameUI.this) {
                        userInput = inputField.getText().trim();
                        inputField.setText("");
                        client.sendInputToServer(userInput); // 发送用户输入到服务器
                        inputField.setEditable(false); // 发送完毕后暂时不可编辑，等待对手回合
                    }
                }
            }
        });

        JButton back = new JButton("Back to menu");
        back.setBounds(350, 350, 150, 30);
        frame.add(back);

        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new ModeChooseUI(client);
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // 告知客户端启动PvP模式
        client.startPvP();
    }

    // 用于从客户端添加消息到输出区域
    public void appendToOutput(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                outputArea.append(message + "\n");

                // 根据从服务器接收到的消息，更新输入状态
                if (message.contains("Your turn")) {
                    inputField.setEditable(true); // 轮到当前玩家时可输入
                    statusLabel.setText("Your turn! Enter a letter:");
                } else if (message.contains("Waiting for opponent...")) {
                    inputField.setEditable(false); // 对手回合时不可输入
                    statusLabel.setText("Opponent's turn. Please wait...");
                } else if (message.contains("Game over")) {
                    inputField.setEditable(false);
                    statusLabel.setText("Game finished.");
                }
            }
        });
    }
}
