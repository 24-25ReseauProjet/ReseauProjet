public class Main {
    public static void main(String[] args) {
        // 创建并启动认证服务器线程
        Thread authServerThread = new Thread(() -> {
            AuthServer authServer = new AuthServer();
            authServer.start();
        });

        // 创建并启动游戏逻辑服务器线程
        Thread gameServerThread = new Thread(() -> {
            GameServer gameServer = new GameServer();
            gameServer.start();
        });

        // 启动两个线程
        authServerThread.start();
        gameServerThread.start();

        System.out.println("Both AuthServer and GameServer have been started.");
    }
}
