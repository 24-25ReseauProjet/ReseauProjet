import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

// 该类用于管理与服务器的连接，提供发送和接收消息的功能
public class ServerConnection {
    private Socket socket; // 与服务器通信的Socket对象
    private PrintWriter out; // 用于向服务器发送消息的输出流
    private BufferedReader in; // 用于从服务器接收消息的输入流

    // 构造方法，初始化ServerConnection对象，设置输入输出流
    public ServerConnection(Socket socket) {
        this.socket = socket;
        try {
            // 初始化输出流，设置自动刷新
            out = new PrintWriter(socket.getOutputStream(), true);
            // 初始化输入流
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            // 处理异常，输出错误信息
            System.out.println("Error setting up connection: " + e.getMessage());
        }
    }

    // 发送消息到服务器
    public void sendToServer(String message) {
        if (out != null) {
            // 如果输出流可用，发送消息
            out.println(message);
        } else {
            // 如果输出流为空，打印错误信息
            System.out.println("Error: Output stream is null.");
        }
    }

    // 接收从服务器发送的所有消息
    public List<String> receiveAllMessages() {
        List<String> messages = new ArrayList<>();
        try {
            // 检查输入流是否有可读的数据
            while (in.ready()) {
                // 读取一行消息
                String message = in.readLine();
                if (message != null) {
                    // 将消息添加到列表中
                    messages.add(message);
                }
            }
        } catch (IOException e) {
            // 处理读取过程中可能出现的异常
            System.out.println("Error receiving messages from server.");
        }
        // 返回从服务器接收到的所有消息
        return messages;
    }

    // 关闭连接，释放资源
    public void close() {
        try {
            // 关闭输入流
            if (in != null) in.close();
            // 关闭输出流
            if (out != null) out.close();
            // 关闭Socket连接
            if ((socket != null) && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            // 处理关闭过程中可能出现的异常
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}
