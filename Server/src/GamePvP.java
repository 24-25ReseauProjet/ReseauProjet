import java.util.HashSet;
import java.util.Set;

public class GamePvP {
    private String wordToGuess;
    private StringBuilder currentGuess;
    private Set<Character> guessedLetters;
    private boolean gameWon;
    private int playerTurn; // 用于控制玩家轮流猜词 (0: Player 1, 1: Player 2)

    // 构造函数，初始化游戏状态
    public GamePvP(String wordToGuess) {
        this.wordToGuess = wordToGuess.toLowerCase();
        this.currentGuess = new StringBuilder("_".repeat(wordToGuess.length()));
        this.guessedLetters = new HashSet<>();
        this.gameWon = false;
        this.playerTurn = 0; // 从Player 1 开始
    }

    // 处理玩家输入
    public synchronized String processInput(String input, int player) {
        if (player != playerTurn) {
            return "Not your turn!";
        }

        // 输入验证
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
            return "Congratulations! The word was: " + wordToGuess;
        }

        // 切换到下一个玩家
        if (foundMatch) {
            return "Good guess! Current state: " + currentGuess.toString();
        } else {
            playerTurn = (playerTurn + 1) % 2; // 切换回合
            return "Wrong guess! Current state: " + currentGuess.toString();
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
