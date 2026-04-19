import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class Guard extends JFrame {
    private JLabel label1 = new JLabel("No alarms");
    private JButton btn1 = new JButton("On my way");
    private JButton btn2 = new JButton("Send backup");
    private char phase = '0';
    private String currentAction = "No action";

    public Guard() {
        setTitle("Guard");
        setLayout(new FlowLayout());
        setSize(300, 200);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(label1);
        add(btn1); add(btn2);
        btn1.setVisible(false); btn2.setVisible(false);

        btn1.addActionListener(e -> {
            if (phase == '0') {
                currentAction = "Guard on his way";
                btn1.setText("Arrived");
                phase = '1';
            } else if (phase == '1') {
                currentAction = "Guard arrived";
                btn1.setText("Location save");
                btn2.setVisible(true);
                phase = '2';
            } else if (phase == '2') {
                currentAction = "House is save";
                resetGuard();
            }
        });

        btn2.addActionListener(e -> currentAction = "Send backup");

        setVisible(true);
        startPolling();
    }

    private void resetGuard() {
        phase = '0';
        btn1.setText("On my way");
        btn1.setVisible(false);
        btn2.setVisible(false);
        label1.setText("No alarms");
    }

    private void startPolling() {
        Timer timer = new Timer(3000, e -> {
            try (Socket s = new Socket("localhost", 4321);
                 DataOutputStream out = new DataOutputStream(s.getOutputStream());
                 DataInputStream in = new DataInputStream(s.getInputStream())) {

                out.writeUTF("Guard");
                String address = in.readUTF();
                label1.setText(address);

                if (!address.equals("No alarms")) {
                    btn1.setVisible(true);
                }

                out.writeUTF(currentAction);
                // Reset action after sending unless in a sequence
                if (currentAction.equals("House is save")) currentAction = "No action";

            } catch (IOException ex) { label1.setText("Server Offline"); }
        });
        timer.start();
    }

    public static void main(String[] args) { new Guard(); }
}