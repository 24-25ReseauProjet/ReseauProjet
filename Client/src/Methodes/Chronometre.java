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
    private boolean isStarted = false;

    public Chronometre(JLabel timerLabel){
        this.timerLabel=timerLabel;
    }

    public void start() {
        if (timer != null) {
            timer.stop();
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
        isStarted=true;
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
        }
        isStoped = true;
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

    public boolean isStoped() {
        return isStoped;
    }

    public boolean isStarted(){
        return isStarted;
    }
}
