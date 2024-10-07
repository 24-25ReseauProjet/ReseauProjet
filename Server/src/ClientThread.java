// ClientThread.java
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread implements Runnable {
    private Socket socket;
    private String secretWord;
    private char[] currentGuess;

    public ClientThread(Socket socket, String secretWord, char[] currentGuess) {
        this.socket = socket;
        this.secretWord = secretWord;
        this.currentGuess = currentGuess;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            out.println("Welcome to the word guessing game!");
            out.println("The word to guess: " + new String(currentGuess));
            String input;
            while ((input = in.readLine()) != null) {
                if (input.length() == 2 && input.charAt(0) >= '0' && input.charAt(0) <= '9') {
                    int index = Character.getNumericValue(input.charAt(0));
                    char guessedChar = input.charAt(1);
                    if (index >= 0 && index < secretWord.length() && secretWord.charAt(index) == guessedChar) {
                        currentGuess[index] = guessedChar;
                        out.println("Correct guess! Current word state: " + new String(currentGuess));
                    } else {
                        out.println("Incorrect guess or invalid index. Try again.");
                    }
                } else {
                    out.println("Invalid input format. Please enter in the format: <index><character> (e.g., 2A)");
                }
                if (isGameOver()) {
                    out.println("Congratulations! You guessed the word: " + secretWord);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isGameOver() {
        for (char c : currentGuess) {
            if (c == 'X') {
                return false;
            }
        }
        return true;
    }
}