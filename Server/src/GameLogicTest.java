public class GameLogicTest {
    public static void main(String[] args) {
        Game game = new Game("programming");
        System.out.println(game.processInput("p"));
        System.out.println(game.processInput("x"));
        System.out.println(game.processInput("r"));
        System.out.println(game.processInput("g"));
        System.out.println(game.processInput("a"));
        System.out.println(game.processInput("m"));
        System.out.println(game.processInput("i"));
        System.out.println(game.processInput("n"));
        System.out.println(game.processInput("z"));

        // 检查游戏状态111
        if (game.isGameOver()) {
            System.out.println("Game Over!");
        } else {
            System.out.println("Keep guessing!");
        }
    }
}
