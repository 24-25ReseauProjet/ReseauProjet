package client;

import Methodes.Authenticator;
import Methodes.Chronometre;
import Methodes.SaveMessages;
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
    private static final int AUTH_SERVER_PORT = 54321;
    private static final int GAME_SERVER_PORT = 12345;
    private ServerUDP serverUDP;
    private Authenticator authenticator;
    private ServerTCP serverTCP;
    private PvEGameUI pvEGameUI;
    private PvPGameUI pvPGameUI;
    private ModeChooseUI modeChooseUI;
    private String username;
    private Thread messageHandlerThread;
    private SaveMessages saveMessages = new SaveMessages();
    private Chronometre chronometre;

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
        resetConnection();
        connectToServer("MODE:" + username + ":PvE");

        startMessageHandler(() -> handleServerMessages(pvEGameUI));
    }

    public void startPvP() {
        resetConnection();
        connectToServer("MODE:" + username + ":PvP");
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
        while (!Thread.currentThread().isInterrupted()) {
            if (serverTCP == null) break;
            List<String> serverResponses = serverTCP.receiveMessagesFromServer();
            for (String response : serverResponses) {
                if (gameUI instanceof PvEGameUI) {
                    if(response.startsWith("CHRONOMETRE:")){
                        handleChronometreMessage(response,gameUI);
                    }else {
                        ((PvEGameUI) gameUI).appendToOutput(response);
                        saveMessages.saveMessagePvE(response);
                    }
                } else if (gameUI instanceof PvPGameUI) {
                    if (response.startsWith("CHAT:")) {
                        ((PvPGameUI) gameUI).appendToChat(response.substring(5));
                    } else if(response.startsWith("CHRONOMETRE:")){
                        handleChronometreMessage(response,gameUI);
                    }else{
                        ((PvPGameUI) gameUI).appendToOutput(response);
                        saveMessages.saveMessagePvP(response);
                    }
                }
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }


    private void startMessageHandler(Runnable handlerTask) {
        stopMessageHandler();
        messageHandlerThread = new Thread(handlerTask);
        messageHandlerThread.start();
    }

    private void stopMessageHandler() {
        if (messageHandlerThread != null && messageHandlerThread.isAlive()) {
            messageHandlerThread.interrupt();
            try {
                messageHandlerThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void handleChronometreMessage(String message,Object gameUI){
        if (!(gameUI instanceof PvEGameUI || gameUI instanceof PvPGameUI)) {
            return;
        }

        Chronometre chronometre = null;

        if (gameUI instanceof PvEGameUI) {
            chronometre = ((PvEGameUI) gameUI).getChronometre();
        } else if (gameUI instanceof PvPGameUI) {
            chronometre = ((PvPGameUI) gameUI).getChronometre();
        }

        if (chronometre == null) {
            return;
        }

        if (message.equals("CHRONOMETRE:START")) {
            chronometre.start();
        } else if (message.equals("CHRONOMETRE:STOP")) {
            chronometre.stop();
        }
    }

    public void resetConnection() {
        stopMessageHandler();
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
