import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread{

    private Socket clientSocket;//客户端连接的Socket对象
    private BufferedReader in;//向客户端读取数据
    private PrintWriter out;//向客户端发送数据
    private Game game;//游戏逻辑对象，管理客户端的游戏状态

    public ClientThread(Socket clientSocket,Game game){
        this.clientSocket = clientSocket;
        this.game = game;
    }

    //线程的主逻辑部分，线程启动时会自动执行run中的代码
    public void run(){
        try{
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(),true);

            out.println("Welcome to the game");

            String clientInput;
            //一直监听用户的消息，直到游戏结束
            while((clientInput = in.readLine())!=null) {
                String response = game.processInput(clientInput);
                if (game.isGameOverWin()) {
                    //同样的问题需要调试，就是get line一次只能返回一行内容，也就是说一次通信只能通信一行，需要修改
                    out.println("success");
                    break;
                }

                if(game.isGameLose()){
                    out.println("lose");
                }
                out.println(response);
            }
        }catch (IOException e){
            System.out.println("Error in client communication : "+e.getMessage());
        }finally {
            //像大二system一样，如果进程没有被关掉就关掉他们
            try {
                if(in!=null) in.close();
                if(out!=null) out.close();
                if(clientSocket!=null&&!clientSocket.isClosed()) clientSocket.close();
            }catch (IOException e){
                System.out.println("Error closing threads : " + e.getMessage());
            }
        }
    }

}