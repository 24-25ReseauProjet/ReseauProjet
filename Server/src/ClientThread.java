import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.net.Socket;


class ClientThread extends Thread {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private Compt compt;
    private GameServer server;

    public ClientThread(String name, Socket clientSocket, Compt compt, GameServer server) {
        super(name);
        this.clientSocket = clientSocket;
        this.compt = compt;
        this.server = server;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            out.println("Welcome to the Hangman game! Try to guess the 4-digit number.");
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                if (clientMessage.equalsIgnoreCase("EXIT")) {
                    out.println("Goodbye!");
                    break;
                }

                if (clientMessage.matches("\\d{4}")) {
                    String result = server.evaluateGuess(clientMessage);
                    out.println("Result: " + result);
                    if (result.equals("4A0B")) {
                        out.println("Congratulations! You've guessed the correct number!");
                        break;
                    }
                } else {
                    out.println("Invalid input. Please enter a 4-digit number.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
