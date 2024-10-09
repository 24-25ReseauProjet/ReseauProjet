import java.io.IOException;
import java.util.Random;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.Arrays;

public class GameServer {
    private static final int PORT = 12345;
    private static final int MAX_CLIENTS = 10;
    private ServerSocket serverSocket;
    //private ExecutorService threadPool;

    public GameServer(){
        try{
            serverSocket = new ServerSocket(PORT);
            System.out.println("Game start listening on port : " + PORT);
        }catch (IOException e){
            System.out.println("Error initializing server : " + e.getMessage());
        }
    }

    public void start(){
        while(true) {
            try {
                //accept保持阻塞状态，直到有客户连接
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected : "+ clientSocket.getInetAddress());
                //这里是给用户猜测的单词，后面要改成随机分配的
                Game game = new Game("pineapple");

                ClientThread clientThread = new ClientThread(clientSocket,game);
                clientThread.start();

            }catch (IOException e){
                System.out.println("Error accepting client connection : "+e.getMessage());
            }

        }
    }


}