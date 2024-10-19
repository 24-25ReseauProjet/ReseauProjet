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

    public void BuildANewUi() {
        JFrame frame = new JFrame("The UI Of Game");
        frame.setSize(600, 600); // 增加窗口尺寸
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        client = new Client(this);

        // 添加状态标签
        statusLabel = new JLabel("Welcome to the Game!");
        statusLabel.setBounds(50, 20, 300, 30); // 设置标签的位置和大小
        frame.add(statusLabel);

        // 添加输出文本区域
        outputArea = new JTextArea();
        outputArea.setBounds(50, 70, 500, 200);
        outputArea.setEditable(false); // 设置为不可编辑
        JScrollPane scrollPane = new JScrollPane(outputArea); // 添加滚动条
        scrollPane.setBounds(50, 70, 500, 200); // 确保位置和大小正确
        frame.add(scrollPane);

        // 添加输入字段
        inputField = new JTextField();
        inputField.setBounds(50, 300, 400, 30); // 增加输入框长度
        inputField.setEditable(true); // 确保输入框是可编辑的
        frame.add(inputField);

        // 为输入字段添加键盘监听器，处理回车键
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

        // 添加开始游戏按钮
        startGameBouton = new JButton("Start Game");
        startGameBouton.setBounds(50, 350, 150, 30); // 调整按钮位置和大小
        frame.add(startGameBouton);

        // 为开始游戏按钮添加点击事件
        startGameBouton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startGame();
                inputField.requestFocusInWindow(); // 启动游戏后确保输入框获得焦点
            }
        });

        // 确保所有组件都添加完之后再设置窗口为可见
        frame.setVisible(true);
        inputField.requestFocusInWindow(); // 确保在界面显示后，输入框自动获得焦点
    }

    private void startGame() {
        // 更新状态标签，显示游戏已启动
        statusLabel.setText("Game started...");

        // 在后台线程启动游戏逻辑，避免阻塞UI线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                client = new Client(UI.this); // 传入当前的UI实例
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
