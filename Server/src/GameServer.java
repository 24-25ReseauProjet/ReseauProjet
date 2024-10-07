import java.util.Random;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.Arrays;

public class GameServer {
    private static final int PORT = 12345;
    private static final int MAX_CLIENTS = 10;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private String secretWord;
    private char[] currentGuess;
    private static final List<String> WORD_LIST = Arrays.asList("APPLE", "BANANA", "CHERRY", "ORANGE", "GRAPE");

    public GameServer() {
        try {
            serverSocket = new ServerSocket(PORT);
            threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
            generateSecretWord();
            System.out.println("Game Server started. Waiting for clients...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateSecretWord() {
        Random random = new Random();
        secretWord = WORD_LIST.get(random.nextInt(WORD_LIST.size()));
        currentGuess = new char[secretWord.length()];
        Arrays.fill(currentGuess, 'X');
    }

    public void start() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                threadPool.execute(new ClientThread(clientSocket, secretWord, currentGuess));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}