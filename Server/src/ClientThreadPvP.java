import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThreadPvP extends Thread {
    private Socket player1Socket;
    private Socket player2Socket;
    private PrintWriter out1;
    private PrintWriter out2;
    private BufferedReader in1;
    private BufferedReader in2;
    private GamePvP game;

    public ClientThreadPvP(Socket player1, Socket player2, GamePvP game) {
        this.player1Socket = player1;
        this.player2Socket = player2;
        this.game = game;

        try {
            out1 = new PrintWriter(player1Socket.getOutputStream(), true);
            out2 = new PrintWriter(player2Socket.getOutputStream(), true);
            in1 = new BufferedReader(new InputStreamReader(player1Socket.getInputStream()));
            in2 = new BufferedReader(new InputStreamReader(player2Socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processMessage(String message, int player) {
        PrintWriter outSelf = (player == 0) ? out1 : out2;
        PrintWriter outOther = (player == 0) ? out2 : out1;
        String playerName = (player == 0) ? "Player 1" : "Player 2";

        String[] parts = message.split(":", 2);
        String messageType = parts[0]; // 消息类型（例如 "CHAT" 或 "GAME"）
        String payload = parts.length > 1 ? parts[1] : ""; // 消息内容

        switch (messageType) {
            case "CHAT": {
                String chatMessage = payload;
                outOther.println("CHAT:" + playerName + ": " + chatMessage);
                outSelf.println("CHAT:" + playerName + ": " + chatMessage);
                break;
            }

            case "GAME": {
                String gameInput = payload;

                if (game.getPlayerTurn() != player) {
                    outSelf.println("GAME:Not your turn!");
                    return;
                }

                String result = game.processInput(gameInput, player);
                outSelf.println("GAME:" + result);
                outOther.println("GAME:" + playerName + " guessed: " + gameInput + " - " + game.getCurrentState());

                if (game.isWon()) {
                    //outSelf.println("GAME:Game over! You win!");
                    outOther.println("GAME:Game over! " + playerName + " wins!");
                } else {
                    //Next player
                    int nextPlayer = game.getPlayerTurn();
                    PrintWriter outNext = (nextPlayer == 0) ? out1 : out2;
                    PrintWriter outWait = (nextPlayer == 0) ? out2 : out1;

                    outNext.println("GAME:Your turn! Enter a letter:");
                    outWait.println("GAME:Waiting for opponent...");
                }
                break;
            }

            default: {
                outSelf.println("GAME:Unknown message type.");
                break;
            }
        }
    }


    @Override
    public void run() {
        out1.println("GAME:Game started! You are Player 1.");
        out1.println("CHRONOMETRE:START");
        out2.println("GAME:Game started! You are Player 2.");
        out2.println("CHRONOMETRE:START");

        out1.println("GAME:Your turn! Enter a letter:");
        out2.println("GAME:Waiting for opponent...");

        try {
            while (true) {
                if (in1.ready()) {
                    String message1 = in1.readLine();
                    processMessage(message1, 0);
                }
                if (in2.ready()) {
                    String message2 = in2.readLine();
                    processMessage(message2, 1);
                }

                if (game.isWon()) {
                    out1.println("CHRONOMETRE:STOP");
                    out2.println("CHRONOMETRE:STOP");
                    break;
                }

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } catch (IOException e) {
            System.out.println("Error in PvP game: " + e.getMessage());
        } finally {
            try {
                player1Socket.close();
                player2Socket.close();
                cleanupResources();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void cleanupResources() {
        try {
            if (player1Socket != null) player1Socket.close();
            if (player2Socket != null) player2Socket.close();
            if (in1 != null) in1.close();
            if (in2 != null) in2.close();
            if (out1 != null) out1.close();
            if (out2 != null) out2.close();
        } catch (IOException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }
}
