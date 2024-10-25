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

    @Override
    public void run() {
        // 发送“游戏开始”消息给两位玩家
        out1.println("Game started! You are Player 1.");
        out2.println("Game started! You are Player 2.");

        try {
            boolean gameOn = true;
            while (gameOn) {
                out1.println("Your turn!"); // 提示玩家1输入
                String guess1 = in1.readLine();
                String result1 = game.processInput(guess1, 1);
                out1.println(result1);
                out2.println("Player 1 guessed: " + guess1);

                if (game.isWon()) {
                    out1.println("Game over! You win!");
                    out2.println("Game over! Player 1 wins!");
                    break;
                }

                // 轮到玩家2
                out2.println("Your turn!"); // 提示玩家2输入
                String guess2 = in2.readLine();
                String result2 = game.processInput(guess2, 2);
                out2.println(result2);
                out1.println("Player 2 guessed: " + guess2);

                if (game.isWon()) {
                    out2.println("Game over! You win!");
                    out1.println("Game over! Player 2 wins!");
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
