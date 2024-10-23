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
            while (in.ready()) {// 当有数据时才读取
                String message = in.readLine();
                if (message != null) {
                    messages.add(message);
                }
            }
        } catch (IOException e) {
            System.out.println("Error receive messages from server.");
        }
        return messages;
    }
}
