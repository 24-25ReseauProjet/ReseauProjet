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
    private static final int PORT = 54321;
    private static final int BUFFER_SIZE = 1024;
    private DatagramSocket serverSocket;
    private static ConcurrentHashMap<String, Boolean> authenticatedUsers = new ConcurrentHashMap<>();
    private Map<String, String> validUsers = new HashMap<>();
    private static final String USER_DATA_FILE = "user_data.txt";

    public AuthServer() {
        try {
            serverSocket = new DatagramSocket(PORT);
            System.out.println("Auth server is listening on port: " + PORT);
            loadUserData();

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

                if ("RELOAD_USER_DATA".equals(clientMessage)) {
                    loadUserData();
                    System.out.println("Reloaded user data upon request.");
                    continue;
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
        return password.equals(validUsers.get(username));
    }

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
