import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class UI {
    private JButton startGameBouton;
    private JLabel statusLabel;
    private JTextArea outputArea;
    private Client client;
    private JTextField inputField;
    private String userInput;


    public void BuildANewUi(){
        JFrame frame = new JFrame("The UI Of Game");
        frame.setSize(400,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        frame.setVisible(true);

        client = new Client(this);

        statusLabel = new JLabel("Welcome to the Game!");
        statusLabel.setBounds(50, 50, 300, 30); // 设置标签的位置和大小
        frame.add(statusLabel);

        // 添加输出文本区域
        outputArea = new JTextArea();
        outputArea.setBounds(50, 100, 500, 200);
        outputArea.setEditable(false); // 设置为不可编辑
        JScrollPane scrollPane = new JScrollPane(outputArea); // 添加滚动条
        scrollPane.setBounds(50, 100, 400, 200);
        frame.add(scrollPane);

        inputField = new JTextField();
        inputField.setBounds(50, 350, 30, 30);
        frame.add(inputField);

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    synchronized (UI.this) {
                        userInput = inputField.getText().trim(); // 获取用户输入
                        inputField.setText(""); // 清空输入字段
                        UI.this.notify(); // 通知等待的线程，用户已输入
                    }
                }
            }
        });

        startGameBouton = new JButton("Start Game");
        startGameBouton.setBounds(10, 10, 150, 30); // 设置按钮的位置和大小
        frame.add(startGameBouton);

        startGameBouton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });
    }

    private void startGame() {
        // 更新状态标签，显示游戏已启动
        statusLabel.setText("Game started...");

        // 在后台线程启动游戏逻辑，避免阻塞UI线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.start(); // 调用Client类中的start方法
            }
        }).start();
    }

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
