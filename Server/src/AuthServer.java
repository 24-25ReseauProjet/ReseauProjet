import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.Map;

public class AuthServer {
    // 定义服务器监听的端口号
    private static final int PORT = 54321;
    // 定义接收缓冲区的大小
    private static final int BUFFER_SIZE = 1024;
    private DatagramSocket serverSocket;
    // 共享的已认证用户表，用于保存已认证成功的用户信息
    private static ConcurrentHashMap<String, Boolean> authenticatedUsers = new ConcurrentHashMap<>();
    // 存储有效用户信息的 HashMap
    private Map<String, String> validUsers = new HashMap<>();
    // 用户数据文件
    private static final String USER_DATA_FILE = "user_data.txt";

    // 构造函数，初始化服务器套接字
    public AuthServer() {
        try {
            serverSocket = new DatagramSocket(PORT); // 创建DatagramSocket对象，绑定指定端口
            System.out.println("Auth server is listening on port: " + PORT);
            loadUserData(); // 加载用户数据

        } catch (IOException e) {
            System.out.println("Error initializing auth server: " + e.getMessage());
        }
    }

    // 启动认证服务器的方法
    public void start() {
        byte[] receiveBuffer = new byte[BUFFER_SIZE];

        while (true) {
            try {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                serverSocket.receive(receivePacket);
                String clientMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

                if ("RELOAD_USER_DATA".equals(clientMessage)) {
                    loadUserData(); // 重新加载用户数据
                    System.out.println("Reloaded user data upon request.");
                    continue; // 不处理其他逻辑，直接继续监听新的请求
                }
                System.out.println("Received authentication request from " + clientAddress + ":" + clientPort);

                String responseMessage = authenticate(clientMessage);
                byte[] responseBuffer = responseMessage.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, clientAddress, clientPort);
                serverSocket.send(responsePacket);

                System.out.println("Sent authentication response: " + responseMessage);

            } catch (IOException e) {
                System.out.println("Error handling client authentication request: " + e.getMessage());
            }
        }
    }

    // 认证逻辑，根据接收到的消息进行用户身份验证
    private String authenticate(String message) {
        if (message.startsWith("AUTH:")) {
            String[] parts = message.split(":");
            if (parts.length == 3) {
                String username = parts[1];
                String password = parts[2];
                if (isValidUser(username, password)) {
                    authenticatedUsers.put(username, true);
                    return "Authentication successful";
                } else {
                    return "Authentication failed";
                }
            }
        }
        return "Invalid authentication request format";
    }

    // 修改的验证用户信息的方法，基于从文件加载的数据
    private boolean isValidUser(String username, String password) {
        return password.equals(validUsers.get(username));
    }

    // 从文件加载用户数据的方法
    private void loadUserData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String username = parts[0];
                    String password = parts[1];
                    validUsers.put(username, password);
                }
            }
            System.out.println("Loaded user data from file.");
        } catch (IOException e) {
            System.out.println("Error loading user data: " + e.getMessage());
        }
    }

    public static boolean isAuthenticated(String username) {
        return authenticatedUsers.getOrDefault(username, false);
    }
}
