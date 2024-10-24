//package UIsOfUsers;
//
//import client.Client;
//
//import javax.swing.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.HashMap;
//import java.util.Map;
//
//public class PvPChooseRoom {
//    private Client client;
//    private Map<String, JButton> roomButtons; // 存储房间按钮
//
//    public PvPChooseRoom(Client client) {
//        this.client = client;
//
//        // 初始化窗口
//        JFrame frame = new JFrame("Choose a Room");
//        frame.setSize(600, 400);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setLayout(null);
//
//        JLabel titleLabel = new JLabel("Select a Room:");
//        titleLabel.setBounds(50, 20, 200, 30);
//        frame.add(titleLabel);
//
//        // 初始化房间按钮
//        roomButtons = new HashMap<>();
//        for (int i = 1; i <= 6; i++) {
//            String roomName = "Room " + i;
//            JButton roomButton = new JButton(roomName);
//            roomButton.setBounds(50, 50 + (i - 1) * 50, 400, 40);
//            roomButton.setEnabled(true); // 默认设置为可用
//
//            // 按钮点击事件
//            roomButton.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    // 向服务器发送加入房间的请求
//                    client.sendInputToServer("JOIN:" + roomName);
//                    frame.dispose();
//                    // 启动新的游戏界面或者等待其他玩家加入
//                    new PvPGameUI(client);
//                }
//            });
//
//            frame.add(roomButton);
//            roomButtons.put(roomName, roomButton);
//        }
//
//        // 添加一个返回按钮
//        JButton backButton = new JButton("Back to menu");
//        backButton.setBounds(50, 350, 200, 30);
//        frame.add(backButton);
//
//        backButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                frame.dispose();
//                new ModeChooseUI(client);
//            }
//        });
//
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//
//        // 更新房间状态
//        updateRoomStatuses();
//    }
//
//    // 更新房间状态（假设从客户端获取）
//    private void updateRoomStatuses() {
//        // 这里假设从客户端获取每个房间的状态信息
//        Map<String, String> roomStatuses = client.getRoomStatuses();
//
//        for (Map.Entry<String, JButton> entry : roomButtons.entrySet()) {
//            String roomName = entry.getKey();
//            JButton roomButton = entry.getValue();
//            String status = roomStatuses.get(roomName);
//
//            if ("Empty".equals(status)) {
//                roomButton.setText(roomName + " (Empty)");
//                roomButton.setEnabled(true);
//            } else if ("Occupied".equals(status)) {
//                roomButton.setText(roomName + " (Occupied)");
//                roomButton.setEnabled(true);
//            } else if ("Full".equals(status)) {
//                roomButton.setText(roomName + " (Full)");
//                roomButton.setEnabled(false);
//            }
//        }
//    }
//
////    public static void main(String[] args) {
////        Client client = new Client();
////        new PvPChooseRoom(client);
////    }
//}
//
