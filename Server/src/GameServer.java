import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {
    private static final int PORT = 12345;
    private static final int MAX_CLIENTS = 10;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private static final String[] WORDS = {"apple", "banana", "orange", "grape", "pineapple"};
    private Queue<Socket> pvpQueue = new LinkedList<>(); // PvP匹配队列

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

                String[] userInfo = parseClientMessage(clientSocket);
                if (userInfo != null) {
                    String username = userInfo[0];
                    String modeChoose = userInfo[1];

                    if (AuthServer.isAuthenticated(username)) {
                        System.out.println("User " + username + " is authenticated. Starting game.");

                        switch (modeChoose) {
                            case "PvP":
                                System.out.println("PvP mode selected for user: " + username);
                                addPlayerToPvPQueue(clientSocket); // 添加玩家到PvP队列
                                break;
                            case "PvE":
                                System.out.println("PvE mode selected for user: " + username);
                                GamePvE gamePvE = new GamePvE(getRandomWord());
                                ClientThreadPvE clientThreadPvE = new ClientThreadPvE(clientSocket, gamePvE);
                                threadPool.execute(clientThreadPvE);
                                break;
                            default:
                                System.out.println("Invalid mode selected: " + modeChoose);
                                clientSocket.close();
                                break;
                        }
                    } else {
                        System.out.println("Unauthorized access attempt from " + clientAddress);
                        clientSocket.close();
                    }
                } else {
                    System.out.println("Failed to parse client message or invalid message format.");
                    clientSocket.close();
                }

            } catch (IOException e) {
                System.out.println("Error accepting client connection: " + e.getMessage());
            }
        }
    }

    // 添加玩家到PvP队列并尝试匹配
    private void addPlayerToPvPQueue(Socket clientSocket) {
        pvpQueue.add(clientSocket);
        if (pvpQueue.size() >= 2) {
            Socket player1 = pvpQueue.poll();
            Socket player2 = pvpQueue.poll();

            // 启动PvP游戏实例
            GamePvP gamePvP = new GamePvP(getRandomWord());
            ClientThreadPvP clientThreadPvP = new ClientThreadPvP(player1, player2, gamePvP);
            threadPool.execute(clientThreadPvP);

            System.out.println("Starting PvP game between two players...");
        } else {
            // 如果只有一个玩家，发送等待信息
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("Waiting for other player...");
            } catch (IOException e) {
                System.out.println("Error sending waiting message to client: " + e.getMessage());
            }
        }
    }

    private String[] parseClientMessage(Socket socket) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message = in.readLine();
            if (message != null && message.startsWith("MODE:")) {
                String[] parts = message.split(":");
                if (parts.length == 3) {
                    String username = parts[1].trim();
                    String mode = parts[2].trim();
                    return new String[]{username, mode};
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading message from client: " + e.getMessage());
        }
        return null;
    }

    // 从预定义的单词列表中随机选择一个单词
    private String getRandomWord() {
        Random random = new Random();
        return WORDS[random.nextInt(WORDS.length)];
    }
}
