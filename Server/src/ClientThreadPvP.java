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

    // 处理消息的方法
    private void processMessage(String message, int player) {
        //玩家叫啥都暂且先叫玩家1玩家2
        PrintWriter outSelf = (player == 0) ? out1 : out2;
        PrintWriter outOther = (player == 0) ? out2 : out1;
        String playerName = (player == 0) ? "Player 1" : "Player 2";

        //消息按不同种类进行区分逻辑处理
        if (message.startsWith("CHAT:")) {
            String chatMessage = message.substring(5);
            outOther.println("CHAT:" + playerName + ": " + chatMessage);
            outSelf.println("CHAT:" + playerName + ": " + chatMessage);
        } else if (message.startsWith("GAME:")) {
            String gameInput = message.substring(5);
            if (game.getPlayerTurn() != player) {
                outSelf.println("GAME:Not your turn!");
                return;
            }
            String result = game.processInput(gameInput, player);
            outSelf.println("GAME:" + result);
            outOther.println("GAME:" + playerName + " guessed: " + gameInput + " - " + game.getCurrentState());

            if (game.isWon()) {
                outSelf.println("GAME:Game over! You win!");
                outOther.println("GAME:Game over! " + playerName + " wins!");
            } else {
                // 通知下一个玩家他们的回合
                int nextPlayer = game.getPlayerTurn();
                PrintWriter outNext = (nextPlayer == 0) ? out1 : out2;
                PrintWriter outWait = (nextPlayer == 0) ? out2 : out1;

                outNext.println("GAME:Your turn! Enter a letter:");
                outWait.println("GAME:Waiting for opponent...");
            }
        } else {
            outSelf.println("GAME:Unknown message type.");
        }
    }

    @Override
    public void run() {
        out1.println("GAME:Game started! You are Player 1.");
        out2.println("GAME:Game started! You are Player 2.");

        // 游戏开始时，通知玩家 1 他们的回合
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

                // 这里需要暂停一下，避免无限循环占用CPU
                try {
                    Thread.sleep(50); // 暂停50ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // 检查游戏是否结束
                if (game.isWon()) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error in PvP game: " + e.getMessage());
        } finally {
            try {
                player1Socket.close();
                player2Socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
