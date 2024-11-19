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
                System.out.println("New client connected: " + clientSocket.getInetAddress());

                String[] userInfo = parseClientMessage(clientSocket);
                if (userInfo != null) {
                    String username = userInfo[0];
                    String modeChoose = userInfo[1];

                    handleClientMode(clientSocket, username, modeChoose);
                } else {
                    System.out.println("Failed to parse client message or invalid message format.");
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("Error accepting client connection: " + e.getMessage());
            }
        }
    }

    private void handleClientMode(Socket clientSocket, String username, String mode) {
        PrintWriter out = null;
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Error setting up client output: " + e.getMessage());
        }

        if (AuthServer.isAuthenticated(username)) {
            switch (mode) {
                case "PvP":
                    out.println("MODE_CONFIRMED:PvP");
                    addPlayerToPvPQueue(clientSocket);
                    break;
                case "PvE":
                    out.println("MODE_CONFIRMED:PvE");
                    startPvEGame(clientSocket);
                    break;
                default:
                    out.println("ERROR: Invalid mode.");
                    break;
            }
        } else {
            out.println("ERROR: Authentication failed.");
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing unauthenticated socket: " + e.getMessage());
            }
        }
    }

    private void startPvEGame(Socket clientSocket) {
        GamePvE gamePvE = new GamePvE(getRandomWord());
        ClientThreadPvE clientThreadPvE = new ClientThreadPvE(clientSocket, gamePvE);
        threadPool.execute(clientThreadPvE);
    }

    private void addPlayerToPvPQueue(Socket clientSocket) {
        if (validateSocket(clientSocket)) {
            pvpQueue.add(clientSocket);

            if (pvpQueue.size() >= 2) {
                Socket player1 = pvpQueue.poll();
                Socket player2 = pvpQueue.poll();

                if (validateSocket(player1) && validateSocket(player2)) {
                    GamePvP gamePvP = new GamePvP(getRandomWord());
                    ClientThreadPvP clientThreadPvP = new ClientThreadPvP(player1, player2, gamePvP);
                    threadPool.execute(clientThreadPvP);
                    System.out.println("PvP game started between two players.");
                } else {
                    if (validateSocket(player1)) pvpQueue.add(player1);
                    if (validateSocket(player2)) pvpQueue.add(player2);
                }
            } else {
                notifyPlayerWait(clientSocket);
            }
        } else {
            System.out.println("Invalid socket. Skipping client.");
        }
    }


    private boolean validateSocket(Socket socket) {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }

    private void notifyPlayerWait(Socket clientSocket) {
        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println("Waiting for another player...");
        } catch (IOException e) {
            System.out.println("Error sending wait message: " + e.getMessage());
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

    private String getRandomWord() {
        Random random = new Random();
        return WORDS[random.nextInt(WORDS.length)];
    }
}
