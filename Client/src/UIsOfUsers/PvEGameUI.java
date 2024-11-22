package UIsOfUsers;

import Methodes.Chronometre;
import client.Client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class PvEGameUI {
    private JFrame frame;
    private JButton startGameButton;
    private JLabel statusLabel;
    private JLabel remindLabel;
    private JTextArea outputArea;
    private JTextField inputField;
    private JButton back;
    private Client client;
    private String userInput;
    private JLabel timerLabel;
    private Chronometre chronometre;

    public PvEGameUI(Client client) {
        this.client = client;
        client.setGameUI(this);

        frame = new JFrame("PvE Game - Word Guess");
        frame.setSize(600, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        statusLabel = new JLabel("Game started...");
        statusLabel.setBounds(50, 35, 300, 30);
        frame.add(statusLabel);

        timerLabel = new JLabel("Time: 0 s");
        timerLabel.setBounds(450, 35, 100, 30);
        frame.add(timerLabel);

        chronometre = new Chronometre(timerLabel);

        outputArea = new JTextArea();
        outputArea.setBounds(50, 70, 500, 200);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        scrollPane.setBounds(50, 70, 500, 200);
        frame.add(scrollPane);

        remindLabel = new JLabel("Enter your guess and press Enter:");
        remindLabel.setBounds(50, 270, 300, 30);
        frame.add(remindLabel);

        inputField = new JTextField();
        inputField.setBounds(50, 300, 400, 30);
        inputField.setEditable(true);
        frame.add(inputField);

        inputField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    synchronized (PvEGameUI.this) {
                        userInput = inputField.getText().trim();
                        inputField.setText("");
                        client.sendInputToServer(userInput);
                    }
                }
            }
        });

        startGameButton = new JButton("Restart Game");
        startGameButton.setBounds(50, 350, 150, 30);
        frame.add(startGameButton);
        startGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusLabel.setText("Game restarted...");
                outputArea.setText("");
                chronometre.reset();
                chronometre.start();
                client.startPvE();
            }
        });

        back = new JButton("Back to menu");
        back.setBounds(350, 350, 150, 30);
        frame.add(back);
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new ModeChooseUI(client);
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        client.startPvE();
    }

    public void appendToOutput(String message) {
            SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                outputArea.append(message + "\n");

                if (chronometre.isStoped()) {
                    inputField.setEditable(false);
                    statusLabel.setText("Game finished.");
                    long totalTime = chronometre.getPastedTime();
                    outputArea.append("And game completed in " + totalTime + " seconds.\n");
                }
            }
        });
    }

    public Chronometre getChronometre() {
        return chronometre;
    }
}
