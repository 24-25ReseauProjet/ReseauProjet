import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

public class Client {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int AUTH_SERVER_PORT = 54321; // UDP认证服务器端口
    private static final int GAME_SERVER_PORT = 12345; // 游戏逻辑服务器TCP端口
    private ServerUDP serverUDP;
    private Autenticator autenticator;
    private ServerTCP serverTCP;
    private GameUI gameUI;
    private String username; // 添加 username 成员变量
    private UserDataManager userDataManager; // 用于用户数据管理的类

    public Client() {
        try {
            serverUDP = new ServerUDP(SERVER_ADDRESS,AUTH_SERVER_PORT);
            this.autenticator = new Autenticator(serverUDP);
            this.userDataManager = new UserDataManager(); // 初始化 UserDataManager 对象
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setGameUI(GameUI gameUI) {
        this.gameUI = gameUI;
    }

    public void sendInputToServer(String input) {
        if (serverTCP != null) {
            serverTCP.sendToServer(input);
            gameUI.appendToOutput("You: " + input);
        }
    }

    // 使用文件进行本地验证登录
    public boolean authenticate(String username, String password) {
        this.username = username;
        return userDataManager.authenticate(username, password);
    }


    public boolean register(String username, String password) {
        return userDataManager.register(username, password);
    }


    public void start() {
        try {
            Socket tcpSocket = new Socket(SERVER_ADDRESS, GAME_SERVER_PORT);
            tcpSocket.setSoTimeout(3000);
            gameUI.appendToOutput("Connected to game server: " + SERVER_ADDRESS + ": " + GAME_SERVER_PORT);
            serverTCP = new ServerTCP(tcpSocket);
            serverTCP.sendToServer(username);
            serverTCP.sendToServer("START_GAME");

            new Thread(() -> {
                while (true) {
                    List<String> serverResponses = serverTCP.receiveAllMessages();
                    for (String response : serverResponses) {
                        gameUI.appendToOutput(response);
                    }

                    try {
                        Thread.sleep(50); // 暂停 50ms
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();

        } catch (IOException e) {
            gameUI.appendToOutput("Error connecting to game server: " + e.getMessage());
        }
    }
}