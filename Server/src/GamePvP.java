import java.util.HashSet;
import java.util.Set;

public class GamePvP {
    private String wordToGuess;
    private StringBuilder currentGuess;
    private Set<Character> guessedLetters;
    private boolean gameWon;
    private int playerTurn;

    public GamePvP(String wordToGuess) {
        this.wordToGuess = wordToGuess.toLowerCase();
        this.currentGuess = new StringBuilder("_".repeat(wordToGuess.length()));
        this.guessedLetters = new HashSet<>();
        this.gameWon = false;
        this.playerTurn = 0;
    }

    public synchronized String processInput(String input, int player) {
        if (player != playerTurn) {
            return "Not your turn!";
        }

        if (input == null || input.length() != 1 || !Character.isLetter(input.charAt(0))) {
            return "Enter a valid single letter, please.";
        }

        char inputChar = Character.toLowerCase(input.charAt(0));
        if (guessedLetters.contains(inputChar)) {
            return "You have already guessed the letter '" + inputChar + "'. Try another one.";
        }

        guessedLetters.add(inputChar);
        boolean foundMatch = false;

        for (int i = 0; i < wordToGuess.length(); i++) {
            if (wordToGuess.charAt(i) == inputChar) {
                currentGuess.setCharAt(i, inputChar);
                foundMatch = true;
            }
        }

        if (isWon()) {
            return "Congratulations! The word was: " + wordToGuess + ". Player " + (player + 1) + " wins!";
        }

        if (foundMatch) {
            return "Good guess! Current state: " + currentGuess.toString();
        } else {
            playerTurn = (playerTurn + 1) % 2;
            return "Wrong guess! Current state: " + currentGuess.toString() + ". Next player's turn.";
        }
    }

    public boolean isWon() {
        if (currentGuess.indexOf("_") == -1) {
            gameWon = true;
        }
        return gameWon;
    }

    public String getCurrentState() {
        return currentGuess.toString();
    }

    public int getPlayerTurn() {
        return playerTurn;
    }
}
