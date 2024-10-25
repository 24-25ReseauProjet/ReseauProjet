package client;

import Methodes.Authenticator;
import Servers.ServerTCP;
import Servers.ServerUDP;
import UIsOfUsers.PvEGameUI;
import UIsOfUsers.ModeChooseUI;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class Client {
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int AUTH_SERVER_PORT = 54321; // UDP认证服务器端口
    private static final int GAME_SERVER_PORT = 12345; // 游戏逻辑服务器TCP端口
    private ServerUDP serverUDP;
    private Authenticator authenticator;
    private ServerTCP serverTCP;
    private PvEGameUI pvEGameUI;
    private ModeChooseUI modeChooseUI;
    private String username; // 添加 username 成员变量

    public Client() {
        try {
            serverUDP = new ServerUDP(SERVER_ADDRESS,AUTH_SERVER_PORT);
            this.authenticator = new Authenticator(serverUDP);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendInputToServer(String input) {
        if (serverTCP != null) {
            serverTCP.sendToServer(input);
            pvEGameUI.appendToOutput("You: " + input);
        }
    }

    public boolean authenticate(String username, String password) {
        this.username = username;
        return authenticator.authenticateWithCredentials(username, password);
    }

    public void startPvE() {
        try {
            Socket tcpSocket = new Socket(SERVER_ADDRESS, GAME_SERVER_PORT);
            tcpSocket.setSoTimeout(3000);
            pvEGameUI.appendToOutput("Connected to game server: " + SERVER_ADDRESS + ": " + GAME_SERVER_PORT);
            serverTCP = new ServerTCP(tcpSocket);
            serverTCP.sendToServer("MODE:" + username + ":PvE");

            new Thread(() -> {
                while (true) {
                    List<String> serverResponses = serverTCP.receiveMessagesFromServer();
                    for (String response : serverResponses) {
                        pvEGameUI.appendToOutput(response);
                    }
                    try {
                        Thread.sleep(50); // 暂停 50ms
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();

        } catch (IOException e) {
            pvEGameUI.appendToOutput("Error connecting to game server: " + e.getMessage());
        }
    }

    public void startPvP() {
        try {
            Socket tcpSocket = new Socket(SERVER_ADDRESS, GAME_SERVER_PORT);
            tcpSocket.setSoTimeout(3000);
            pvEGameUI.appendToOutput("Connected to game server: " + SERVER_ADDRESS + ": " + GAME_SERVER_PORT);
            serverTCP = new ServerTCP(tcpSocket);

            serverTCP.sendToServer("MODE:" + username + ":PvP");

            new Thread(() -> {
                while (true) {
                    List<String> serverResponses = serverTCP.receiveMessagesFromServer();
                    for (String response : serverResponses) {
                        pvEGameUI.appendToOutput(response);
                    }
                    try {
                        Thread.sleep(50); // 暂停 50ms
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();


        }catch (IOException e){
            System.out.println("Error in startPvP "+ e.getMessage());
        }
    }

    public void setGameUI(PvEGameUI pvEGameUI) {
        this.pvEGameUI = pvEGameUI;
    }

    public void setModeChooseUI(ModeChooseUI modeChooseUI){
        this.modeChooseUI = modeChooseUI;
    }

}