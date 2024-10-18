import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Client {
    private static final  String Server_Address = "127.0.0.1";
    private static final int Server_Port = 12345;
    private ServerConnection serverConnection;
    private UserInputHandler userInputHandler;
    private UI ui;


    public Client(UI ui){
        try{
            Socket socket = new Socket(Server_Address,Server_Port);
            socket.setSoTimeout(3000);
            //System.out.println("SUCCESS CONNECTION "+Server_Address+" : "+Server_Port);
            this.ui=ui;
            ui.appendToOutput("SUCCESS CONNECTION " + Server_Address + " : " + Server_Port);

            serverConnection = new ServerConnection(socket);
            userInputHandler = new UserInputHandler(ui);

        }catch (IOException e){
            ui.appendToOutput("Error connection: " + e.getMessage());
        }
    }

    public void start(){
            while (true){
                boolean endTheGame=false;
                List<String> serverResponses = serverConnection.receiveAllMessages();
                for(String response : serverResponses) {
                    if (response.equals("success")) {
                        ui.appendToOutput("You are successful! Congratulations!");
                        endTheGame=true;
                        break;
                    }else if (response.equals("lose")) {
                        ui.appendToOutput("You lost! Try again if you want.");
                        endTheGame=true;
                        break;
                    }else if (response != null) {
                        ui.appendToOutput("Server: " + response);
                    }
                }
                if (endTheGame) {
                    break;
                }
                    //调用userinputhandler的获取用户输入来获得这个字符
                    String userInput = userInputHandler.getUserInput();
                    //检测用户是否输入exit,如果输入了这个单词传过来，游戏将直接结束
                    if (userInput.equalsIgnoreCase("exit")) {
                        ui.appendToOutput("Exiting game.");
                        break;
                    }
                    //调用serverconnection类的向server发送来发送信息到服务器
                    serverConnection.sendToServer(userInput);

                try {
                    Thread.sleep(50); // 暂停50ms
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

            }
            serverConnection.close();
            userInputHandler.close();

    }

}