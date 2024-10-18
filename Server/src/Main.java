public class Main {
    public static void main(String[] args) {
        // 创建并启动认证服务器线程

            AuthServer authServer = new AuthServer();
            authServer.start();


            GameServer gameServer = new GameServer();
            gameServer.start();


    }
}
