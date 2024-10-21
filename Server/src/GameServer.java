import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.Arrays;

public class GameServer {
    private static final int PORT = 12345;
    private static final int MAX_CLIENTS = 10;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private static final String[] WORDS = {"apple", "banana", "orange", "grape", "pineapple"};

    public GameServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
            System.out.println("Game server listening on port: " + PORT);
        } catch (IOException e) {
            System.out.println("Error initializing server: " + e.getMessage());
        }
    }

    public void start() {
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                String clientAddress = clientSocket.getInetAddress().toString();
                System.out.println("New client connected: " + clientAddress);

                String username = getUsernameFromClient(clientSocket);

                if (username != null && AuthServer.isAuthenticated(username)) {
                    System.out.println("User " + username + " is authenticated. Starting game.");

                    Game game = new Game(getRandomWord());

                    ClientThread clientThread = new ClientThread(clientSocket, game);
                    threadPool.execute(clientThread);
                } else {
                    System.out.println("Unauthorized access attempt from " + clientAddress);
                    clientSocket.close();
                }

            } catch (IOException e) {
                System.out.println("Error accepting client connection: " + e.getMessage());
            }
        }
    }

    // 从客户端读取用户名
    private String getUsernameFromClient(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // 读取客户端发送的第一条消息，这应该是用户名
            String username = in.readLine();
            return username != null ? username.trim() : null;
        } catch (IOException e) {
            System.out.println("Error reading username from client: " + e.getMessage());
            return null;
        }
    }

    // 从预定义的单词列表中随机选择一个单词
    private String getRandomWord() {
        Random random = new Random();
        return WORDS[random.nextInt(WORDS.length)];
    }


}

