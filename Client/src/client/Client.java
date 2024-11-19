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
    private Thread messageHandlerThread; // 新增：用于管理消息处理线程

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
            if (pvEGameUI != null) {
                pvEGameUI.appendToOutput("You: " + input);
            }
        }
    }

    public void sendInputToServerPvP(String input) {
        if (serverTCP != null) {
            serverTCP.sendToServer("GAME:" + input);
            if (pvPGameUI != null) {
                pvPGameUI.appendToOutput("You: " + input);
            }
        }
    }

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
        resetConnection();  // 确保释放旧连接
        connectToServer("MODE:" + username + ":PvE");

        // 启动新的消息处理线程
        startMessageHandler(() -> handleServerMessages(pvEGameUI));
    }

    public void startPvP() {
        resetConnection();  // 确保释放旧连接
        connectToServer("MODE:" + username + ":PvP");

        // 启动新的消息处理线程
        startMessageHandler(() -> handleServerMessages(pvPGameUI));
    }

    private void connectToServer(String modeMessage) {
        try {
            Socket tcpSocket = new Socket(SERVER_ADDRESS, GAME_SERVER_PORT);
            serverTCP = new ServerTCP(tcpSocket);
            serverTCP.sendToServer(modeMessage);
        } catch (IOException e) {
            if (pvEGameUI != null) {
                pvEGameUI.appendToOutput("Connection error: " + e.getMessage());
            } else if (pvPGameUI != null) {
                pvPGameUI.appendToOutput("Connection error: " + e.getMessage());
            }
        }
    }

    private void handleServerMessages(Object gameUI) {
        while (!Thread.currentThread().isInterrupted()) {  // 检查线程中断状态
            if (serverTCP == null) break;  // 避免空指针异常
            List<String> serverResponses = serverTCP.receiveMessagesFromServer();
            for (String response : serverResponses) {
                if (gameUI instanceof PvEGameUI) {
                    ((PvEGameUI) gameUI).appendToOutput(response);
                } else if (gameUI instanceof PvPGameUI) {
                    if (response.startsWith("CHAT:")) {
                        ((PvPGameUI) gameUI).appendToChat(response.substring(5));
                    } else {
                        ((PvPGameUI) gameUI).appendToOutput(response);
                    }
                }
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();  // 恢复中断状态
                break;
            }
        }
    }

    // 启动新的消息处理线程
    private void startMessageHandler(Runnable handlerTask) {
        stopMessageHandler();  // 先停止旧的线程
        messageHandlerThread = new Thread(handlerTask);
        messageHandlerThread.start();
    }

    // 停止消息处理线程
    private void stopMessageHandler() {
        if (messageHandlerThread != null && messageHandlerThread.isAlive()) {
            messageHandlerThread.interrupt();  // 请求线程中断
            try {
                messageHandlerThread.join();  // 等待线程终止
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void resetConnection() {
        stopMessageHandler();  // 停止消息处理线程
        if (serverTCP != null) {
            serverTCP.close();
            serverTCP = null;
        }
    }

    public void setGameUI(PvEGameUI pvEGameUI) {
        this.pvEGameUI = pvEGameUI;
    }

    public void setGameUI(PvPGameUI pvPGameUI) {
        this.pvPGameUI = pvPGameUI;
    }

    public void setModeChooseUI(ModeChooseUI modeChooseUI) {
        this.modeChooseUI = modeChooseUI;
    }
}
