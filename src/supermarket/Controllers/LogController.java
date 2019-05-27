package supermarket.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class LogController {
    @FXML TextArea logTextArea;
    @FXML Label errorLabel;

    @FXML
    private void read() {
        try {
            logTextArea.setText("");

            FileInputStream fstream = new FileInputStream("..\\supermarket\\logfile.log");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;

            while ((strLine = br.readLine()) != null)   {
                if (logTextArea.getText().equals("")) {
                    logTextArea.setText(strLine);
                } else {
                    logTextArea.setText(logTextArea.getText() + "\n" + strLine);
                }
            }

            fstream.close();
        } catch (Exception e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void back(javafx.event.ActionEvent event) {
        ScreenController.goToAdminHome(event);
    }

    @FXML
    private void logout(javafx.event.ActionEvent event) {
        ScreenController.logout(event);
    }
}
