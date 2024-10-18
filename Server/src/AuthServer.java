import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class AuthServer {
    private static final int AUTH_PORT = 12346;

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket(AUTH_PORT)) {
            System.out.println("Authentication server is listening on port " + AUTH_PORT);

            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received authentication request: " + received);

                // Example simple validation (username: user1, password: password1)
                String responseMessage;
                if ("user1:password1".equals(received)) {
                    responseMessage = "Authentication successful";
                } else {
                    responseMessage = "Authentication failed";
                }

                byte[] responseBuffer = responseMessage.getBytes();
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();

                DatagramPacket responsePacket = new DatagramPacket(responseBuffer, responseBuffer.length, clientAddress, clientPort);
                socket.send(responsePacket);

                System.out.println("Sent authentication response: " + responseMessage);
            }
        } catch (Exception e) {
            System.err.println("Error in authentication server: " + e.getMessage());
        }
    }
}
