import java.io.IOException;
import java.net.Socket;

public class Client {
    private static final  String Server_Address = "127.0.0.1";
    private static final int Server_Port = 12345;
    private ServerConnection serverConnection;
    private UserInputHandler userInputHandler;

    public Client(){
        try{
            Socket socket = new Socket(Server_Address,Server_Port);
            System.out.println("SUCCESS CONNECTION "+Server_Address+" : "+Server_Port);

            serverConnection = new ServerConnection(socket);
            userInputHandler = new UserInputHandler();

        }catch (IOException e){
            System.out.println("error connection "+e.getMessage());
        }
    }

    public void start(){
        /*if(serverConnection!=null&&userInputHandler!=null){
            userInputHandler.getUserInput();
        }else{
            System.out.println("CANNOT ETABLIR THE CONNECTION");
        }*/
        if(serverConnection!=null&&userInputHandler!=null){
            while (true){
                String userInput = userInputHandler.getUserInput();
                if(userInput.equalsIgnoreCase("exit")){
                    System.out.println("exiting game ");
                    break;
                }

                serverConnection.sendToServer(userInput);//用户输入发送到服务器
                String serverResponse = serverConnection.receiveFromServer();
                if(serverResponse!=null){
                System.out.println("Server : "+ serverResponse);
                if(serverResponse.equals("GAME_OVER")){
                    System.out.println("Game ended. Exiting now. ");
                    break;
                }
                } else{
                    System.out.println("Cannot establish the connection. ");
                }
            }
            serverConnection.close();
            userInputHandler.close();
        }
    }

}