package Methodes;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SaveMessages {
    private static final String HISTORY_FILE = "PvEHistory.txt";
    private static final String History_PvP_FILE = "PvPHistory.txt";

    public synchronized void saveMessagePvE(String message){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE, true))) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            writer.write("[" + timestamp + "] " + message);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error while saving PvE messages: " + e.getMessage());
        }
    }

    public synchronized void saveMessagePvP(String message){
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(History_PvP_FILE,true))){
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            writer.write("["+timestamp+"] "+message);
            writer.newLine();
        }catch (IOException e){
            System.err.println("Error while saving PvP messages "+e.getMessage());
        }
    }

}
