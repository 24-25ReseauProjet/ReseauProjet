package Methodes;

import Servers.ServerUDP;

import java.io.IOException;

public class Authenticator {
    private ServerUDP serverUDP;

    public Authenticator(ServerUDP serverUDP){
        this.serverUDP = serverUDP;
    }

    public boolean authenticateWithCredentials(String username, String password) {
            String authMessage = "AUTH:" + username + ":" + password;

        try {
            serverUDP.sendMessage(authMessage);
            String serverResponse = serverUDP.receiveMessage();
            return serverResponse.equalsIgnoreCase("Authentication successful");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
