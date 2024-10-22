package Methodes;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserDataManager {
    private static final String USER_DATA_FILE = "user_data.txt"; // 用户数据文件


    // 注册用户并保存到文件
    public boolean register(String username, String password) {
        Map<String, String> users = loadUserData();

        if (users.containsKey(username)) {

            return false;
        }

        // 将新用户信息保存到文件
        users.put(username, password);
        saveUserData(users);
        return true;
    }

    // 加载用户数据
    private Map<String, String> loadUserData() {
        Map<String, String> users = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(USER_DATA_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user data: " + e.getMessage());
        }

        return users;
    }

    // 保存用户数据
    private void saveUserData(Map<String, String> users) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_DATA_FILE))) {
            for (Map.Entry<String, String> entry : users.entrySet()) {
                writer.write(entry.getKey() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving user data: " + e.getMessage());
        }
    }
}
