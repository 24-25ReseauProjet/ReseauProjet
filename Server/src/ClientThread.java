import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread {

    private Socket clientSocket; // 客户端连接的Socket对象
    private BufferedReader in; // 向客户端读取数据
    private PrintWriter out; // 向客户端发送数据
    private Game game; // 游戏逻辑对象，管理客户端的游戏状态

    public ClientThread(Socket clientSocket, Game game) {
        this.clientSocket = clientSocket;
        this.game = game;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            out.println("Welcome to the game");

            sendInitialGameState();

            String clientInput;
            while ((clientInput = in.readLine()) != null) {
                String response = game.processInput(clientInput);
                out.println(response);
                if (game.isWon()) {
                    break;
                } else if (game.isLose()) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error in client communication: " + e.getMessage());
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing threads: " + e.getMessage());
            }
        }
    }

    private void sendInitialGameState() {
        String initialState = game.getCurrentState();
        int remainingAttempts = game.getRemainingAttempts();
        out.println("Game started! Current state: " + initialState + ". Remaining attempts: " + remainingAttempts);
    }
}
