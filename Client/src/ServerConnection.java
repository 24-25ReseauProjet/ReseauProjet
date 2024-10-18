import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnection {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ServerConnection(Socket socket){
        this.socket=socket;
        try{
            out = new PrintWriter(socket.getOutputStream(),true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch(IOException e){
            System.out.println("ERROR SET UP CONNECTION " + e.getMessage());
        }
    }

    public void sendToServer(String message){
        if(out!=null){
            out.println(message);
        }else{
            System.out.println("ERROR OUTPUT ");
        }
    }
    //目前为止还是仅仅接收server的一行消息
    public String receiveFromServer(){
        try{
            if(in!=null){
                return in.readLine();
            }
        }catch (IOException e){
            System.out.println("ERROR RECEIVE DATA ");
        }
        return null;
    }

    public void close(){
        try{
            if(in!=null) in.close();
            if(out!=null) out.close();
            if((socket!=null)&&!socket.isClosed()) socket.close();
        }catch (IOException e){
            System.out.println("ERROR CLOSE SERVER "+ e.getMessage());
        }
    }


}
