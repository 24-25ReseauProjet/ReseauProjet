import java.util.Random;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {
    private static final int PORT = 12345;
    private static final int MAX_CLIENTS = 10;
    private ServerSocket serverSocket;
    private ExecutorService threadPool;
    private Compt compt;
    private String secretNumber;

    public GameServer(int size) {
        try {
            serverSocket = new ServerSocket(PORT);
            threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
            compt = new Compt(size);
            secretNumber = generateSecretNumber();
            System.out.println("Server started on port 12345" + PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to generate a random 4-digit secret number
    private String generateSecretNumber() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.length() < 4) {
            int digit = random.nextInt(10);
            if (sb.indexOf(String.valueOf(digit)) == -1) {
                sb.append(digit);
            }
        }
        return sb.toString();
    }

    // Method to evaluate the guess
    public String evaluateGuess(String guess) {
        int A = 0; // correct number and position
        int B = 0; // correct number but wrong position
        for (int i = 0; i < 4; i++) {
            if (guess.charAt(i) == secretNumber.charAt(i)) {
                A++;
            } else if (secretNumber.contains(Character.toString(guess.charAt(i)))) {
                B++;
            }
        }
        return A + "A" + B + "B";
    }

    public void start() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected:: " + clientSocket.getInetAddress());
                ClientThread clientThread = new ClientThread("ClientThread", clientSocket, compt, this);
                threadPool.execute(clientThread);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
