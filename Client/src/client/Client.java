package client;

import Methodes.Authenticator;
import Servers.ServerTCP;
import Servers.ServerUDP;
import UIsOfUsers.PvEGameUI;
import UIsOfUsers.PvPGameUI;
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
    private PvPGameUI pvPGameUI;
    private ModeChooseUI modeChooseUI;
    private String username;

    public Client() {
        try {
            serverUDP = new ServerUDP(SERVER_ADDRESS, AUTH_SERVER_PORT);
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

    public void sendInputToServerPvP(String input) {
        if (serverTCP != null) {
            serverTCP.sendToServer("GAME:" + input);//GAME是用来让信息进入游戏逻辑而不是聊天逻辑的
            // 判断当前是否在 PvE 或 PvP 模式，并相应地更新 UI
            if (pvEGameUI != null) {
                pvEGameUI.appendToOutput("You: " + input);
            } else if (pvPGameUI != null) {
                pvPGameUI.appendToOutput("You: " + input);
            }
        }
    }

    // 发送指定信号（CHAT)到服务器
    public void sendChatMessageToServer(String message) {
        if (serverTCP != null) {
            serverTCP.sendToServer("CHAT:" + message);
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
            pvPGameUI.appendToOutput("Connected to game server: " + SERVER_ADDRESS + ": " + GAME_SERVER_PORT);
            serverTCP = new ServerTCP(tcpSocket);
            serverTCP.sendToServer("MODE:" + username + ":PvP");

            new Thread(() -> {
                while (true) {
                    List<String> serverResponses = serverTCP.receiveMessagesFromServer();
                    for (String response : serverResponses) {
                        if (response.startsWith("CHAT:")) {
                            String chatMessage = response.substring(5);
                            pvPGameUI.appendToChat(chatMessage);
                        } else if (response.startsWith("GAME:")) {
                            String gameMessage = response.substring(5);
                            pvPGameUI.appendToOutput(gameMessage);
                        } else {
                            pvPGameUI.appendToOutput(response);
                        }
                    }
                    try {
                        Thread.sleep(50); // 暂停 50ms
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();
        } catch (IOException e) {
            pvPGameUI.appendToOutput("Error in startPvP: " + e.getMessage());
        }
    }

    // 设置 PvE 游戏的 UI
    public void setGameUI(PvEGameUI pvEGameUI) {
        this.pvEGameUI = pvEGameUI;
    }

    // 设置 PvP 游戏的 UI
    public void setGameUI(PvPGameUI pvPGameUI) {
        this.pvPGameUI = pvPGameUI;
    }

    public void setModeChooseUI(ModeChooseUI modeChooseUI) {
        this.modeChooseUI = modeChooseUI;
    }
}
