public class Game {
    private String wordToGuess;
    private StringBuilder currentGuess;//StringBuilder是Java里面需要频繁修改字符的时候使用的
    private int remainingGuesses;
    private boolean gameWon;

    //构造函数，初始化游戏状态
    public Game(String wordToGuess){
        this.wordToGuess = wordToGuess.toLowerCase();
        this.currentGuess = new StringBuilder("_".repeat(wordToGuess.length()));
        this.remainingGuesses = 6;//默认允许玩家猜6次
        this.gameWon = false;
    }

    public String processInput(String input){
        if(input==null||input.length()!=1){
            return "Enter one letter please : ";
        }

        char inputChar = input.charAt(0);//将输入的String形式改成Char形式方便使用charAt

        boolean foundMatch = false;//在下面这个for里面没有找到用户输入的这个字母

        for(int i = 0;i<wordToGuess.length();i++){
            if(wordToGuess.charAt(i) == inputChar){
                currentGuess.setCharAt(i,inputChar);
                foundMatch =true;
            }
        }

        if(foundMatch){
            //为了在猜到了之后不再返回一遍enter one letter
            if(isWon()){
                return "BIEN JOUE VOUS AVEZ TROUVE LA REPONSE : "+ currentGuess.toString();
            }
            return "Good guess! Current state : "+ currentGuess.toString()+". Remaining times : "+ remainingGuesses;
        }else{
            remainingGuesses-=1;
            return "Sorry, that letter isn't in the word. Current state : "+currentGuess.toString()+". Remaining times : "+ remainingGuesses;
        }
    }

    //获取游戏状态
    public boolean isGameOver(){
        return remainingGuesses==0||gameWon;
    }

    //判断是否胜利
    public boolean isWon(){
        if(currentGuess.indexOf("_")==-1){
            gameWon=true;
            return true;
        }
        return gameWon;
    }

}
