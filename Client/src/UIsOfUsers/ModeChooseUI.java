package UIsOfUsers;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import client.Client;

public class ModeChooseUI {
    private JButton ModePvP;
    private JButton ModePvE;
    private Client client;

    public ModeChooseUI(Client client){
        this.client = client;
        client.setModeChooseUI(this);

        JFrame frame = new JFrame("Jeu du perdu");
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        ModePvP = new JButton("Mode PvP");
        ModePvP.setBounds(50, 100, 150, 150);
        frame.add(ModePvP);
        ModePvP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //ModePvP start
            }
        });

        ModePvE = new JButton("Mode PvE");
        ModePvE.setBounds(300,100,150,150);
        frame.add(ModePvE);
        ModePvE.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GameUI(client);
            }
        });
        frame.setVisible(true);
    }
}
