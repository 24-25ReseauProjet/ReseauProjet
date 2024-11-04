import java.util.HashSet;
import java.util.Set;

public class GamePvE {
    private String wordToGuess;
    private StringBuilder currentGuess; // StringBuilder是Java里面需要频繁修改字符的时候使用的
    private int remainingGuesses;
    private boolean gameWon;
    private Set<Character> gussedLetters;

    // 构造函数，初始化游戏状态
    public GamePvE(String wordToGuess) {
        this.wordToGuess = wordToGuess.toLowerCase();
        this.currentGuess = new StringBuilder("_".repeat(wordToGuess.length()));
        this.remainingGuesses = 6; // 默认允许玩家猜6次
        this.gameWon = false;
        this.gussedLetters = new HashSet<>();
    }

    public String processInput(String input) {
         //如果用户输入的不是一个单字母，则输出下列文字
        if (input == null || input.length() != 1 || !Character.isLetter(input.charAt(0))) {
            return "Enter a valid single letter, please.";
        }

        char inputChar = Character.toLowerCase(input.charAt(0)); // 统一转换为小写处理
        // 检查用户是否已经猜过这个字母
        if (gussedLetters.contains(inputChar)) {
            return "You have already guessed the letter '" + inputChar + "'. Try another one.";
        }

        gussedLetters.add(inputChar);
        boolean foundMatch = false; // 在下面这个for里面没有找到用户输入的这个字母

        for (int i = 0; i < wordToGuess.length(); i++) {
            if (wordToGuess.charAt(i) == inputChar) {
                currentGuess.setCharAt(i, inputChar);
                foundMatch = true;
            }
        }

        if(isWon()){
            return "Congratulations! You've guessed the word: " + wordToGuess;
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
