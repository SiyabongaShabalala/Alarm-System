import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;

public class Client_Home extends Application {
    private int step = 0;
    private boolean armed = false;
    private String houseAddress = "123 Security Lane";

    @Override
    public void start(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10); grid.setVgap(10);

        TextField addrField = new TextField(houseAddress);
        Label statusLabel = new Label("Status: Disarmed");

        Button btn1 = new Button("1");
        Button btn2 = new Button("2");
        Button btn3 = new Button("3");
        Button btnMove = new Button("Move");

        // Keypad Logic for code 132
        btn1.setOnAction(e -> handleKeypad(1, statusLabel));
        btn3.setOnAction(e -> handleKeypad(3, statusLabel));
        btn2.setOnAction(e -> handleKeypad(2, statusLabel));

        btnMove.setOnAction(e -> {
            if (armed) {
                houseAddress = addrField.getText();
                sendAlarm("Home", houseAddress);
                statusLabel.setText("ALARM TRIGGERED!");
            }
        });

        grid.add(new Label("Address:"), 0, 0); grid.add(addrField, 1, 0);
        grid.add(btn1, 0, 1); grid.add(btn2, 1, 1);
        grid.add(btn3, 0, 2); grid.add(btnMove, 1, 2);
        grid.add(statusLabel, 0, 3, 2, 1);

        Scene scene = new Scene(grid, 300, 250);
        primaryStage.setTitle("Home");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleKeypad(int num, Label label) {
        int[] code = {1, 3, 2};
        if (num == code[step]) {
            step++;
            if (step == 3) {
                armed = !armed;
                label.setText("Status: " + (armed ? "Armed" : "Disarmed"));
                step = 0;
            }
        } else {
            step = 0; // Reset on wrong button
        }
    }

    private void sendAlarm(String type, String addr) {
        try (Socket s = new Socket("localhost", 4321);
             DataOutputStream out = new DataOutputStream(s.getOutputStream())) {
            out.writeUTF(type);
            out.writeUTF(addr);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) { launch(args); }
}