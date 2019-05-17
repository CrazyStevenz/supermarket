package supermarket;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

class ScreenController
{
    static void goToUserHome(javafx.event.ActionEvent event) {
        try {
            Parent blah = FXMLLoader.load(ScreenController.class.getResource("UserHome.fxml"));
            Scene scene = new Scene(blah, 1024, 576); // TODO Global constant
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setTitle("Homepage");
            appStage.setScene(scene);
            appStage.setResizable(false);
            appStage.show();
        }
        catch (Exception e) {
            // TODO
        }
    }
}
