import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int AUTH_SERVER_PORT = 54321; // UDP认证服务器端口
    private static final int GAME_SERVER_PORT = 12345; // 游戏逻辑服务器TCP端口
    private static final int BUFFER_SIZE = 1024;
    private DatagramSocket udpSocket;
    private InetAddress serverAddress;
    private Socket tcpSocket;
    private ServerConnection serverConnection;
    private UserInputHandler userInputHandler;
    private String username; // 类成员变量，用于存储用户名

    public Client() {
        try {
            // 初始化UDP套接字用于认证
            udpSocket = new DatagramSocket();
            serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            userInputHandler = new UserInputHandler(); // 初始化用户输入处理器
        } catch (IOException e) {
            System.out.println("Error initializing UDP client: " + e.getMessage());
        }
    }

    public void start() {
        // 1. 使用UDP进行认证
        boolean authenticated = authenticateWithUDP();

        // 2. 如果认证成功，使用TCP连接到游戏逻辑服务器
        if (authenticated) {
            connectToGameServer();
            startGameCommunication(); // 开始游戏通信
        }

        close();
    }

    private boolean authenticateWithUDP() {
        while (true) {
            System.out.print("Enter username (or type 'exit' to quit): ");
            username = userInputHandler.getUserInput(); // 获取用户名并存储到类的成员变量

            if (username.equalsIgnoreCase("exit")) {
                System.out.println("Exiting client...");
                return false; // 用户选择退出
            }

            System.out.print("Enter password: ");
            String password = userInputHandler.getUserInput(); // 获取密码

            // 构建认证请求消息
            String authMessage = "AUTH:" + username + ":" + password;

            try {
                // 发送认证请求到服务器
                byte[] sendBuffer = authMessage.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, serverAddress, AUTH_SERVER_PORT);
                udpSocket.send(sendPacket);

                // 接收服务器的响应
                byte[] receiveBuffer = new byte[BUFFER_SIZE];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                udpSocket.receive(receivePacket);

                // 解析服务器响应
                String serverResponse = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Server response: " + serverResponse);

                if (serverResponse.equalsIgnoreCase("Authentication successful")) {
                    System.out.println("Authentication successful! Connecting to game server...");
                    return true; // 认证成功
                } else {
                    System.out.println("Authentication failed. Please try again.");
                }

            } catch (IOException e) {
                System.out.println("Error communicating with the authentication server: " + e.getMessage());
            }
        }
    }

    private void connectToGameServer() {
        try {
            // 初始化TCP连接到游戏逻辑服务器
            tcpSocket = new Socket(SERVER_ADDRESS, GAME_SERVER_PORT);
            tcpSocket.setSoTimeout(3000);
            System.out.println("SUCCESS CONNECTION to game server " + SERVER_ADDRESS + " : " + GAME_SERVER_PORT);

            serverConnection = new ServerConnection(tcpSocket); // 使用ServerConnection进行TCP通信

            // 发送用户名作为第一条消息
            serverConnection.sendToServer(username);

        } catch (IOException e) {
            System.out.println("Error connecting to game server: " + e.getMessage());
        }
    }

    private void startGameCommunication() {
        if (serverConnection != null && userInputHandler != null) {
            while (true) {
                String serverResponse = serverConnection.receiveFromServer(); // 从游戏服务器接收消息

                if (serverResponse == null) {
                    System.out.println("Connection closed by the server.");
                    break;
                }

                if (serverResponse.equalsIgnoreCase("success")) {
                    System.out.println("You have succeeded! Congratulations!");
                    break;
                } else if (serverResponse.equalsIgnoreCase("lose")) {
                    System.out.println("You have lost! Try again if you want.");
                    break;
                } else {
                    System.out.println("Server: " + serverResponse);
                }

                // 获取用户输入并发送到服务器
                String userInput = userInputHandler.getUserInput();

                if (userInput.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting game...");
                    break;
                }

                serverConnection.sendToServer(userInput);
            }
            try {
                Thread.sleep(50); // 暂停50ms
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            // 关闭游戏连接
            serverConnection.close();
        }
    }

    private void close() {
        if (udpSocket != null && !udpSocket.isClosed()) {
            udpSocket.close();
        }
        if (userInputHandler != null) {
            userInputHandler.close();
        }

        System.out.println("Client resources released.");
    }

}
