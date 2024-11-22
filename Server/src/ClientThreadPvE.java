import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThreadPvE extends Thread {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private GamePvE gamePvE;

    public ClientThreadPvE(Socket clientSocket, GamePvE gamePvE) {
        this.clientSocket = clientSocket;
        this.gamePvE = gamePvE;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            sendInitialGameState();
            //
            String clientInput;
            while ((clientInput = in.readLine()) != null) {
                String response = gamePvE.processInput(clientInput);
                out.println(response);

                if (gamePvE.isWon() || gamePvE.isLose()) {
                    out.println("CHRONOMETRE:STOP");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error in PvE client communication: " + e.getMessage());
        } finally {
            cleanupResources();
        }
    }

    private void sendInitialGameState() {
        out.println("Game started! Current state: " + gamePvE.getCurrentState() +
                ". Remaining attempts: " + gamePvE.getRemainingAttempts());
        out.println("CHRONOMETRE:START");
    }

    private void cleanupResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
            System.out.println("PvE client resources cleaned up.");
        } catch (IOException e) {
            System.out.println("Error closing PvE resources: " + e.getMessage());
        }
    }
}
