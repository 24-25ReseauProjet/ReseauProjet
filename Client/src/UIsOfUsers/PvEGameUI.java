package UIsOfUsers;

import client.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PvEGameUI {
    private JButton startGameButton;
    private JLabel statusLabel;
    private JLabel remindLabel;
    private JTextArea outputArea;
    private JTextField inputField;
    private JButton back;
    private Client client;
    private String userInput;

    public PvEGameUI(Client client) {
        this.client = client;
        client.setGameUI(this); // 让Client知道这个GameUI

        // 创建游戏窗口
        JFrame frame = new JFrame("Jeu du perdu");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // 添加状态标签
        statusLabel = new JLabel("Game started...");
        statusLabel.setBounds(50, 35, 300, 30);
        frame.add(statusLabel);

        // 添加输出文本区域
        outputArea = new JTextArea();
        outputArea.setBounds(50, 70, 500, 200);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBounds(50, 70, 500, 200);
        frame.add(scrollPane);

        remindLabel = new JLabel("Please enter here and press Enter : ");
        remindLabel.setBounds(50, 270, 300, 30);
        frame.add(remindLabel);

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
                    synchronized (PvEGameUI.this) {
                        userInput = inputField.getText().trim();
                        inputField.setText("");
                        client.sendInputToServer(userInput); // 发送用户输入到服务器
                    }
                }
            }
        });

        // 添加一个按钮用于重新启动游戏或其他功能
        startGameButton = new JButton("Restart Game");
        startGameButton.setBounds(50, 350, 150, 30);
        frame.add(startGameButton);

        startGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText("Game restarted...");
                client.startPvE(); // 重新启动游戏逻辑
            }
        });

        back = new JButton("Back to menu");
        back.setBounds(350,350,150,30);
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
        client.startPvE();
    }

    // 用于从客户端添加消息到输出区域
    public void appendToOutput(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                outputArea.append(message + "\n");
            }
        });
    }

    public static void main(String args[]){
        Client client1 = new Client();
        new PvEGameUI(client1);
    }
}