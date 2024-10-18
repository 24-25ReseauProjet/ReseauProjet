import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentHashMap;

public class AuthServer {
    private static final int PORT = 54321;
    private static final int BUFFER_SIZE = 1024;
    private DatagramSocket serverSocket;
    // 共享的已认证用户表
    private static ConcurrentHashMap<String, Boolean> authenticatedUsers = new ConcurrentHashMap<>();

    public AuthServer() {
        try {
            serverSocket = new DatagramSocket(PORT);
            System.out.println("Auth server is listening on port: " + PORT);
        } catch (IOException e) {
            System.out.println("Error initializing auth server: " + e.getMessage());
        }
    }

    public void start() {
        byte[] receiveBuffer = new byte[BUFFER_SIZE];

        while (true) {
            try {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                serverSocket.receive(receivePacket);
                String clientMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();

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

    private boolean isValidUser(String username, String password) {
        return "user".equals(username) && "password123".equals(password);
    }

    public static boolean isAuthenticated(String username) {
        return authenticatedUsers.getOrDefault(username, false);
    }
}
