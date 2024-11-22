package Methodes;

import UIsOfUsers.PvEGameUI;
import UIsOfUsers.PvPGameUI;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Chronometre {
    private Timer timer;
    private long startTime;
    private long pastedTime;
    private JLabel timerLabel;
    private boolean isStoped=false;

    public Chronometre(JLabel timerLabel){
        this.timerLabel=timerLabel;
    }

    public void start() {
        if (timer != null) {
            timer.stop(); // 如果计时器已经存在，先停止它
        }

        startTime = System.currentTimeMillis();

        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pastedTime = (System.currentTimeMillis() - startTime) / 1000;
                timerLabel.setText("Time: " + pastedTime + " s");
            }
        });
        timer.start();
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
        isStoped = true;
    }

    public boolean isStoped(){
        return isStoped;
    }

    public void reset(){
        stop();
        pastedTime =0;
        timerLabel.setText("Time: 0 s");
    }
    public long getPastedTime() {
        return pastedTime;
    }

    public void setPastedTime(int seconds) {
        this.pastedTime = seconds;
        timerLabel.setText("Time: " + seconds + " s");
    }
}
