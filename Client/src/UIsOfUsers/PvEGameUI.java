package UIsOfUsers;

import client.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PvEGameUI {
    private JFrame frame;
    private JButton startGameButton;
    private JLabel statusLabel;
    private JLabel remindLabel;
    private JTextArea outputArea;
    private JTextField inputField;
    private JButton back;
    private Client client;
    private String userInput;
    private long startTime;
    private Timer timer;
    private JLabel timerLabel;

    public PvEGameUI(Client client) {
        this.client = client;
        client.setGameUI(this);

        // 创建游戏窗口
        frame = new JFrame("PvE Game - Word Guess");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        // 添加状态标签
        statusLabel = new JLabel("Game started...");
        statusLabel.setBounds(50, 35, 300, 30);
        frame.add(statusLabel);

        // 添加计时器标签
        timerLabel = new JLabel("Time: 0 s");
        timerLabel.setBounds(450, 35, 100, 30);
        frame.add(timerLabel);

        // 添加输出文本区域
        outputArea = new JTextArea();
        outputArea.setBounds(50, 70, 500, 200);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBounds(50, 70, 500, 200);
        frame.add(scrollPane);

        // 添加输入提示标签
        remindLabel = new JLabel("Enter your guess and press Enter:");
        remindLabel.setBounds(50, 270, 300, 30);
        frame.add(remindLabel);

        // 添加输入字段
        inputField = new JTextField();
        inputField.setBounds(50, 300, 400, 30);
        inputField.setEditable(true); // PvE模式下，玩家可以立即输入
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

        // 添加重新启动游戏按钮
        startGameButton = new JButton("Restart Game");
        startGameButton.setBounds(50, 350, 150, 30);
        frame.add(startGameButton);

        startGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText("Game restarted...");
                outputArea.setText(""); // 清空输出区域
                startTimer(); // 重新启动计时器
                client.startPvE(); // 重新启动游戏逻辑
            }
        });

        // 添加返回主菜单按钮
        back = new JButton("Back to menu");
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

        // 启动计时器
        startTimer();

        // 开始 PvE 游戏
        client.startPvE();
    }

    // 启动计时器方法
    private void startTimer() {
        if (timer != null) {
            timer.stop(); // 如果计时器已经存在，先停止它
        }

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

                // 根据从服务器接收到的消息，检查游戏是否结束
                if (message.contains("Game over")) {
                    inputField.setEditable(false);
                    statusLabel.setText("Game finished.");

                    // 停止计时器并显示完成时间
                    timer.stop();
                    long totalTime = (System.currentTimeMillis() - startTime) / 1000;
                    outputArea.append("Game completed in " + totalTime + " seconds.\n");
                }
            }
        });
    }

    public static void main(String[] args) {
        Client client1 = new Client();
        new PvEGameUI(client1);
    }
}
