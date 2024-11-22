import java.util.HashSet;
import java.util.Set;

public class GamePvE {
    private String wordToGuess;
    private StringBuilder currentGuess;
    private int remainingGuesses;
    private boolean gameWon;
    private Set<Character> gussedLetters;

    public GamePvE(String wordToGuess) {
        this.wordToGuess = wordToGuess.toLowerCase();
        this.currentGuess = new StringBuilder("_".repeat(wordToGuess.length()));
        this.remainingGuesses = 6;
        this.gameWon = false;
        this.gussedLetters = new HashSet<>();
    }

    public String processInput(String input) {
        if (input == null || input.length() != 1 || !Character.isLetter(input.charAt(0))) {
            return "Enter a valid single letter, please.";
        }

        char inputChar = Character.toLowerCase(input.charAt(0));
        if (gussedLetters.contains(inputChar)) {
            return "You have already guessed the letter '" + inputChar + "'. Try another one.";
        }

        gussedLetters.add(inputChar);
        boolean foundMatch = false;

        for (int i = 0; i < wordToGuess.length(); i++) {
            if (wordToGuess.charAt(i) == inputChar) {
                currentGuess.setCharAt(i, inputChar);
                foundMatch = true;
            }
        }

        if(isWon()){
            return "Congratulations! You've guessed the word: " + wordToGuess+" and - Game over!.";
        }

        if (foundMatch) {
            return "Good guess! Current state: " + currentGuess.toString() + ". Remaining attempts: " + remainingGuesses;
        } else {
            remainingGuesses -= 1;
            if(isLose()){
                return "Sorry, you are failed, try again if you want. Final state : "+currentGuess.toString();
            }
            return "Sorry, that letter isn't in the word. Current state: " + currentGuess.toString() + ". Remaining attempts: " + remainingGuesses;
        }
    }

    public boolean isWon() {
        if (currentGuess.indexOf("_") == -1) {
            gameWon = true;
        }
        return gameWon;
    }

    public boolean isLose(){
        return this.remainingGuesses==0;
    }

    public String getCurrentState() {
        return currentGuess.toString();
    }

    public int getRemainingAttempts() {
        return remainingGuesses;
    }
}
