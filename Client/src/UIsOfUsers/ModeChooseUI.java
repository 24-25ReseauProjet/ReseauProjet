package UIsOfUsers;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import client.Client;

public class ModeChooseUI {
    private JButton ModePvP;
    private JButton ModePvE;
    private Client client;
    private JButton back;
    private JLabel LabelOfRemind;

    public ModeChooseUI(Client client){
        this.client = client;
        client.setModeChooseUI(this);

        JFrame frame = new JFrame("Jeu du perdu");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);

        LabelOfRemind = new JLabel("Please select your preferred mode :");
        LabelOfRemind.setBounds(50, 40, 300, 30);
        frame.add(LabelOfRemind);

        ModePvP = new JButton("Mode PvP");
        ModePvP.setBounds(50, 80, 100, 100);
        frame.add(ModePvP);
        ModePvP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //ModePvP start
            }
        });

        ModePvE = new JButton("Mode PvE");
        ModePvE.setBounds(230,80,100,100);
        frame.add(ModePvE);
        ModePvE.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new GameUI(client);
            }
        });

        back = new JButton("Log out");
        back.setBounds(220,220,150,30);
        frame.add(back);

        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                new LoginUI();
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args){
        Client client1=new Client();
        new ModeChooseUI(client1);
    }
}
