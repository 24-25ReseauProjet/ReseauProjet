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
    private static final int BUFFER_SIZE = 1024;
    private DatagramSocket udpSocket;
    private InetAddress serverAddress;
    private Socket tcpSocket;
    private ServerConnection serverConnection;
    private GameUI gameUI;
    private String username; // 添加 username 成员变量

    public Client() {
        try {
            udpSocket = new DatagramSocket();
            serverAddress = InetAddress.getByName(SERVER_ADDRESS);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean authenticateWithCredentials(String username, String password) {
        this.username = username; // 保存用户名
        String authMessage = "AUTH:" + username + ":" + password;

        try {
            byte[] sendBuffer = authMessage.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, AUTH_SERVER_PORT);
            udpSocket.send(sendPacket);

            byte[] receiveBuffer = new byte[BUFFER_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            udpSocket.receive(receivePacket);

            String serverResponse = new String(receivePacket.getData(), 0, receivePacket.getLength());
            if (serverResponse.equalsIgnoreCase("Authentication successful")) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void setGameUI(GameUI gameUI) {
        this.gameUI = gameUI;
    }

    public void sendInputToServer(String input) {
        // 发送用户输入到服务器
        if (serverConnection != null) {
            serverConnection.sendToServer(input);
            gameUI.appendToOutput("You: " + input);
        }
    }


    public void start() {
        try {
            // 连接到游戏服务器
            tcpSocket = new Socket(SERVER_ADDRESS, GAME_SERVER_PORT);
            tcpSocket.setSoTimeout(3000);
            gameUI.appendToOutput("Connected to game server: " + SERVER_ADDRESS + ":" + GAME_SERVER_PORT);

            serverConnection = new ServerConnection(tcpSocket); // 初始化服务器连接

            // 发送用户名作为第一条消息
            serverConnection.sendToServer(username); // 使用成员变量 username

            // 发送一条指令给服务器，请求游戏启动
            serverConnection.sendToServer("START_GAME");

            // 启动一个线程来监听来自服务器的消息
            new Thread(() -> {
                while (true) {
                    List<String> serverResponses = serverConnection.receiveAllMessages();
                    for (String response : serverResponses) {
                        gameUI.appendToOutput("Server: " + response);
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