import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerUDP {
    private static final int BUFFER_SIZE = 1024;
    private static final int UDP_SERVER_PORT = 54321;
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;

    public ServerUDP(String serverAdress, int serverPort) throws IOException {
        this.socket=new DatagramSocket();
        this.serverAddress = InetAddress.getByName(serverAdress);
        this.serverPort=serverPort;
    }

    public void sendMessage(String message) throws IOException{
        byte[] sendBuffer = message.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer,sendBuffer.length,serverAddress,serverPort);
        socket.send(sendPacket);
    }

    public String receiveMessage() throws IOException{
        byte[] receiveBuffer = new byte[BUFFER_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);
        return new String(receivePacket.getData(), 0, receivePacket.getLength());
    }
}

