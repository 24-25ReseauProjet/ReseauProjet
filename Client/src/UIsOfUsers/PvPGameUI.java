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
    private long startTime;
    private Timer timer;
    private JLabel timerLabel;
    private boolean isTimerStarted = false; // 标记计时器是否已启动

    public PvPGameUI(Client client) {
        this.client = client;
        client.setGameUI(this);

        frame = new JFrame("PvP Game - Word Guess");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        statusLabel = new JLabel("Waiting for other player...");
        statusLabel.setBounds(50, 35, 400, 30);
        frame.add(statusLabel);

        timerLabel = new JLabel("Time: 0 s");
        timerLabel.setBounds(450, 35, 100, 30);
        frame.add(timerLabel);

        outputArea = new JTextArea();
        outputArea.setBounds(50, 70, 500, 200);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBounds(50, 70, 500, 200);
        frame.add(scrollPane);

        JLabel remindLabel = new JLabel("Enter your guess and press Enter:");
        remindLabel.setBounds(50, 270, 300, 30);
        frame.add(remindLabel);

        inputField = new JTextField();
        inputField.setBounds(50, 300, 400, 30);
        inputField.setEditable(false);
        frame.add(inputField);

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    synchronized (PvPGameUI.this) {
                        userInput = inputField.getText().trim();
                        inputField.setText("");
                        client.sendInputToServer(userInput);
                        inputField.setEditable(false);
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

        client.startPvP();
    }

    // 启动计时器方法
    private void startTimer() {
        startTime = System.currentTimeMillis();

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                timerLabel.setText("Time: " + elapsedTime + " s");
            }
        });
        timer.start();
    }

    // 用于从客户端添加消息到输出区域
    public void appendToOutput(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                outputArea.append(message + "\n");

                // 检查是否接收到“游戏开始”信号
                if (message.contains("Game start") && !isTimerStarted) {
                    startTimer(); // 启动计时器
                    isTimerStarted = true; // 确保计时器只启动一次
                }

                if (message.contains("Your turn")) {
                    inputField.setEditable(true);
                    statusLabel.setText("Your turn! Enter a letter:");
                } else if (message.contains("Waiting for opponent...")) {
                    inputField.setEditable(false);
                    statusLabel.setText("Opponent's turn. Please wait...");
                } else if (message.contains("Game over")) {
                    inputField.setEditable(false);
                    statusLabel.setText("Game finished.");
                    if (isTimerStarted) {
                        timer.stop(); // 停止计时器
                        long totalTime = (System.currentTimeMillis() - startTime) / 1000;
                        outputArea.append("Game completed in " + totalTime + " seconds.\n");
                    }
                }
            }
        });
    }
}
