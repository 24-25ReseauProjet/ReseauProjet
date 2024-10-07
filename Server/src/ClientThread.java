// ClientThread.java
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread implements Runnable {
    private Socket socket;
    private Compt compt;

    public ClientThread(Socket socket, Compt compt) {
        this.socket = socket;
        this.compt = compt;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            out.println("Welcome to Jeu de perdu!");
            out.println("Current game state: " + compt);
            String input;
            while ((input = in.readLine()) != null) {
                // Game logic for removing elements and losing condition
                int index = Integer.parseInt(input);
                if (index >= 0 && index < compt.t.length && compt.t[index] != -1) {
                    compt.t[index] = -1; // Mark the element as removed
                    out.println("Element removed. Current game state: " + compt);
                } else {
                    out.println("Invalid move. Try again.");
                }
                if (isGameOver()) {
                    out.println("Game over! You lost.");
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isGameOver() {
        int count = 0;
        for (int value : compt.t) {
            if (value != -1) {
                count++;
            }
        }
        return count <= 1; // Game is over when only one element is left
    }
}
