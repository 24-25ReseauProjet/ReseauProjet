package Servers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerTCP {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public ServerTCP(Socket socket) {
        this.socket = socket;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.out.println("Error setting up connection: " + e.getMessage());
            cleanupResources();
        }
    }

    public void sendToServer(String message) {
        if (out != null) {
            out.println(message);
        } else {
            System.out.println("Error: Output stream null.");
        }
    }

    public List<String> receiveMessagesFromServer() {
        List<String> messages = new ArrayList<>();
        try {
            while (in.ready()) {
                String message = in.readLine();
                if (message != null) {
                    messages.add(message);
                }
            }
        } catch (IOException e) {
            System.out.println("Error receiving messages from server: " + e.getMessage());
            cleanupResources();
        }
        return messages;
    }

    public void close() {
        cleanupResources();
    }

    private void cleanupResources() {
        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("ServerTCP resources cleaned up.");
        } catch (IOException e) {
            System.out.println("Error closing resources: " + e.getMessage());
        }
    }
}
