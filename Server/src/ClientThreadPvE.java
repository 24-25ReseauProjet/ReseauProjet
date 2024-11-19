import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThreadPvE extends Thread {

    private Socket clientSocket; // 客户端连接的Socket对象
    private BufferedReader in; // 向客户端读取数据
    private PrintWriter out; // 向客户端发送数据
    private GamePvE gamePvE; // 游戏逻辑对象，管理客户端的游戏状态

    public ClientThreadPvE(Socket clientSocket, GamePvE gamePvE) {
        this.clientSocket = clientSocket;
        this.gamePvE = gamePvE;
    }

    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            out.println("Welcome to the game");

            sendInitialGameState();

            String clientInput;
            while ((clientInput = in.readLine()) != null) {
                String response = gamePvE.processInput(clientInput);
                out.println(response);
                if (gamePvE.isWon()) {
                    break;
                } else if (gamePvE.isLose()) {
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
                cleanupResources();
            } catch (IOException e) {
                System.out.println("Error closing threads: " + e.getMessage());
            }
        }
    }

    private void sendInitialGameState() {
        String initialState = gamePvE.getCurrentState();
        int remainingAttempts = gamePvE.getRemainingAttempts();
        out.println("Game started! Current state: " + initialState + ". Remaining attempts: " + remainingAttempts);
    }

    private void cleanupResources() {
        try {
            if (clientSocket != null) clientSocket.close();
            if (in != null) in.close();
            if (out != null) out.close();
        } catch (IOException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }
}
