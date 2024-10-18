import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

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

    public List<String> receiveAllMessages(){
        List<String> messages = new ArrayList<>();
        try {
            while (in.ready()) {  // 当有数据时才读取
                String message = in.readLine();
                if (message != null) {
                    messages.add(message);
                }
            }
        } catch (IOException e) {
            System.out.println("Error in receive all the messages from server. ");
        }
        return messages;
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
