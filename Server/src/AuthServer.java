import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

public class AuthServer {
    // 定义服务器监听的端口号
    private static final int PORT = 54321;
    // 定义接收缓冲区的大小
    private static final int BUFFER_SIZE = 1024;
    private DatagramSocket serverSocket;
    // 共享的已认证用户表，用于保存已认证成功的用户信息
    private static ConcurrentHashMap<String, Boolean> authenticatedUsers = new ConcurrentHashMap<>();

    // 构造函数，初始化服务器套接字
    public AuthServer() {
        try {
            serverSocket = new DatagramSocket(PORT); // 创建DatagramSocket对象，绑定指定端口
            System.out.println("Auth server is listening on port: " + PORT);
        } catch (IOException e) {
            System.out.println("Error initializing auth server: " + e.getMessage());
        }
    }

    // 启动认证服务器的方法
    public void start() {
        // 用于接收客户端数据包的缓冲区
        byte[] receiveBuffer = new byte[BUFFER_SIZE];

        while (true) { // 持续监听客户端请求
            try {
                // 创建用于接收数据的DatagramPacket对象
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                // 接收来自客户端的数据包
                serverSocket.receive(receivePacket);
                // 将接收到的数据转换为字符串
                String clientMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                // 获取客户端的IP地址
                InetAddress clientAddress = receivePacket.getAddress();
                // 获取客户端的端口号
                int clientPort = receivePacket.getPort();

                System.out.println("Received authentication request from " + clientAddress + ":" + clientPort);

                // 调用认证方法处理客户端的请求
                String responseMessage = authenticate(clientMessage);
                // 将响应消息转换为字节数组
                byte[] responseBuffer = responseMessage.getBytes();
                // 创建用于发送响应的DatagramPacket对象
                DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, clientAddress, clientPort);
                // 发送响应消息到客户端
                serverSocket.send(responsePacket);

                System.out.println("Sent authentication response: " + responseMessage);

            } catch (IOException e) {
                System.out.println("Error handling client authentication request: " + e.getMessage());
            }
        }
    }

    // 认证逻辑，根据接收到的消息进行用户身份验证
    private String authenticate(String message) {
        // 检查消息是否以 "AUTH:" 开头，这表示是一个认证请求
        if (message.startsWith("AUTH:")) {
            // 以冒号分隔消息内容
            String[] parts = message.split(":");
            // 确保消息格式正确（应该包含3部分内容）
            if (parts.length == 3) {
                String username = parts[1]; // 从消息中获取用户名
                String password = parts[2]; // 从消息中获取密码
                // 验证用户名和密码是否正确
                if (isValidUser(username, password)) {
                    // 如果认证成功，将用户名加入已认证用户表
                    authenticatedUsers.put(username, true);
                    return "Authentication successful"; // 返回认证成功的响应
                } else {
                    return "Authentication failed"; // 返回认证失败的响应
                }
            }
        }
        // 如果消息格式不正确，返回格式错误提示
        return "Invalid authentication request format";
    }

    // 验证用户信息的方法（此处为简单的硬编码验证）
    private boolean isValidUser(String username, String password) {
        // 检查用户名和密码是否匹配硬编码的正确值
        return "user".equals(username) && "password123".equals(password);
    }

    // 静态方法，检查用户是否已经认证过
    public static boolean isAuthenticated(String username) {
        // 从已认证用户表中查找用户的认证状态
        return authenticatedUsers.getOrDefault(username, false);
    }
}
