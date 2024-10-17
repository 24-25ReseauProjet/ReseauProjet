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
        if(serverConnection!=null&&userInputHandler!=null){
            while (true){
                String serverResponse = serverConnection.receiveFromServer();

                if(serverResponse.equals("success")){
                    System.out.println("Your are successed!Congratuations! ");
                    break;
                }

                if(serverResponse.equals("lose")){
                    System.out.println("Your are lose! Try again if you want.");
                    break;
                }

                if(serverResponse!=null){
                    System.out.println("Server : "+ serverResponse);
                }
                //调用userinputhandler的获取用户输入来获得这个字符
                String userInput = userInputHandler.getUserInput();
                //检测用户是否输入exit,如果输入了这个单词传过来，游戏将直接结束
                if(userInput.equalsIgnoreCase("exit")){
                    System.out.println("exiting game ");
                    break;
                }
                //调用serverconnection类的向server发送来发送信息到服务器
                serverConnection.sendToServer(userInput);


            }
            serverConnection.close();
            userInputHandler.close();
        }
    }

}