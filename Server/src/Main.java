public class Main {
    public static void main(String[] args) {
        Thread authServerThread = new Thread(() -> {
            AuthServer authServer = new AuthServer();
            authServer.start();
        });

        Thread gameServerThread = new Thread(() -> {
            GameServer gameServer = new GameServer();
            gameServer.start();
        });

        authServerThread.start();
        gameServerThread.start();

        System.out.println("Both AuthServer and GameServer have been started.");
    }
}
