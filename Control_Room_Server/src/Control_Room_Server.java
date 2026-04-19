import javax.swing.*;
import java.io.*;
import java.net.*;

public class Control_Room_Server {
    private static String currentAddress = "No alarms";
    private static boolean dispatch = false;

    public static void main(String[] args) {
        System.out.println("Control Room Server started on port 4321...");
        try (ServerSocket serverSocket = new ServerSocket(4321)) {
            while (true) {
                try (Socket socket = serverSocket.accept();
                     DataInputStream in = new DataInputStream(socket.getInputStream());
                     DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

                    String clientType = in.readUTF();

                    if (clientType.equals("Home")) {
                        String address = in.readUTF();
                        if (!address.equals("No alarms")) {
                            currentAddress = address;
                            System.out.println("INTRUDER ALERT: " + currentAddress);
                            
                            // Pop up window with 3 buttons
                            int userIn = JOptionPane.showConfirmDialog(null, 
                                "Must a guard be dispatched to the client at " + address + "?", 
                                "Alarm Triggered", JOptionPane.YES_NO_CANCEL_OPTION);
                            
                            if (userIn == JOptionPane.YES_OPTION) {
                                dispatch = true;
                            }
                        }
                    } else if (clientType.equals("Guard")) {
                        // Send address if dispatch is true, else "No alarms"
                        if (dispatch) {
                            out.writeUTF(currentAddress);
                        } else {
                            out.writeUTF("No alarms");
                        }

                        String guardAction = in.readUTF();
                        if (!guardAction.equals("No action")) {
                            System.out.println("Guard Status: " + guardAction);
                            // If guard finishes, reset system
                            if (guardAction.equals("House is save")) {
                                currentAddress = "No alarms";
                                dispatch = false;
                            }
                        }
                    }
                } catch (Exception e) { System.out.println("Connection error: " + e.getMessage()); }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}