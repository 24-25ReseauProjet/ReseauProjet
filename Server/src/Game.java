public class Game {
    private String wordToGuess;
    private StringBuilder currentGuess; // StringBuilder是Java里面需要频繁修改字符的时候使用的
    private int remainingGuesses;
    private boolean gameWon;

    // 构造函数，初始化游戏状态
    public Game(String wordToGuess) {
        this.wordToGuess = wordToGuess.toLowerCase();
        this.currentGuess = new StringBuilder("_".repeat(wordToGuess.length()));
        this.remainingGuesses = 6; // 默认允许玩家猜6次
        this.gameWon = false;
    }

    public String processInput(String input) {
        // 如果用户输入的不是一个单字母，则输出下列文字
        if (input == null || input.length() != 1 || !Character.isLetter(input.charAt(0))) {
            return "Enter a valid single letter, please.";
        }

        char inputChar = Character.toLowerCase(input.charAt(0)); // 统一转换为小写处理
        boolean foundMatch = false; // 在下面这个for里面没有找到用户输入的这个字母

        // 检查用户是否已经猜过这个字母
        if (currentGuess.indexOf(String.valueOf(inputChar)) != -1) {
            return "You have already guessed the letter '" + inputChar + "'. Try another one.";
        }

        // 遍历单词，更新当前猜测状态
        for (int i = 0; i < wordToGuess.length(); i++) {
            if (wordToGuess.charAt(i) == inputChar) {
                currentGuess.setCharAt(i, inputChar);
                foundMatch = true;
            }
        }

        // 更新游戏状态
        if (foundMatch) {
            if (isWon()) { // 检查是否已经赢得游戏
                gameWon = true;
                return "Congratulations! You've guessed the word: " + wordToGuess;
            } else {
                return "Good guess! Current state: " + currentGuess.toString() + ". Remaining attempts: " + remainingGuesses;
            }
        } else {
            remainingGuesses -= 1; // 减少剩余尝试次数
            return "Sorry, that letter isn't in the word. Current state: " + currentGuess.toString() + ". Remaining attempts: " + remainingGuesses;
        }
    }

    // 获取游戏状态
    public boolean isGameOverWin() {
        return gameWon;
    }

    public boolean isGameLose() {
        return this.remainingGuesses == 0;
    }

    // 判断是否胜利
    public boolean isWon() {
        if (currentGuess.indexOf("_") == -1) {
            gameWon = true;
        }
        return gameWon;
    }

    // 获取当前游戏的状态
    public String getCurrentState() {
        return currentGuess.toString();
    }

    // 获取剩余的尝试次数
    public int getRemainingAttempts() {
        return remainingGuesses;
    }
}
