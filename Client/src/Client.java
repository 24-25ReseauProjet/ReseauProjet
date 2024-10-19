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
    private UserInputHandler userInputHandler;
    private String username; // 类成员变量，用于存储用户名
    private UI ui; // 添加 UI 类成员变量

    public Client(UI ui) {
        try {
            this.ui = ui; // 保存传入的 UI 实例
            udpSocket = new DatagramSocket();
            serverAddress = InetAddress.getByName(SERVER_ADDRESS);
            userInputHandler = new UserInputHandler(ui); // 初始化用户输入处理器，传入 UI
        } catch (IOException e) {
            if (ui != null) {
                ui.appendToOutput("Error initializing UDP client: " + e.getMessage());
            }
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
            // 获取用户名
            username = userInputHandler.getUserInput("Enter username (or type 'exit' to quit): ");
            if (username.equalsIgnoreCase("exit")) {
                ui.appendToOutput("Exiting client...");
                return false; // 用户选择退出
            }

            // 获取密码
            String password = userInputHandler.getUserInput("Enter password: ");

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
                ui.appendToOutput("Server response: " + serverResponse);

                if (serverResponse.equalsIgnoreCase("Authentication successful")) {
                    ui.appendToOutput("Authentication successful! Connecting to game server...");
                    return true; // 认证成功
                } else {
                    ui.appendToOutput("Authentication failed. Please try again.");
                }

            } catch (IOException e) {
                ui.appendToOutput("Error communicating with the authentication server: " + e.getMessage());
            }
        }
    }

    private void connectToGameServer() {
        try {
            // 初始化TCP连接到游戏逻辑服务器
            tcpSocket = new Socket(SERVER_ADDRESS, GAME_SERVER_PORT);
            tcpSocket.setSoTimeout(3000);
            ui.appendToOutput("SUCCESS CONNECTION to game server " + SERVER_ADDRESS + " : " + GAME_SERVER_PORT);

            serverConnection = new ServerConnection(tcpSocket); // 使用ServerConnection进行TCP通信

            // 发送用户名作为第一条消息
            serverConnection.sendToServer(username);

        } catch (IOException e) {
            ui.appendToOutput("Error connecting to game server: " + e.getMessage());
        }
    }

    private void startGameCommunication() {
        if (serverConnection != null && userInputHandler != null) {
            while (true) {
                // 接收来自服务器的所有消息
                List<String> serverResponses = serverConnection.receiveAllMessages();

                if (serverResponses.isEmpty()) {
                    ui.appendToOutput("No new messages from the server. Waiting for more data...");
                }

                // 处理所有收到的服务器消息
                for (String serverResponse : serverResponses) {
                    if (serverResponse == null) {
                        ui.appendToOutput("Connection closed by the server.");
                        break;
                    }

                    if (serverResponse.equalsIgnoreCase("success")) {
                        ui.appendToOutput("You have succeeded! Congratulations!");
                        return;
                    } else if (serverResponse.equalsIgnoreCase("lose")) {
                        ui.appendToOutput("You have lost! Try again if you want.");
                        return;
                    } else {
                        ui.appendToOutput("Server: " + serverResponse);
                    }
                }

                // 获取用户输入并发送到服务器
                String userInput = userInputHandler.getUserInput("Please enter a letter:");

                if (userInput.equalsIgnoreCase("exit")) {
                    ui.appendToOutput("Exiting game...");
                    break;
                }

                serverConnection.sendToServer(userInput);

                try {
                    Thread.sleep(50); // 暂停50ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
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

        ui.appendToOutput("Client resources released.");
    }
}
