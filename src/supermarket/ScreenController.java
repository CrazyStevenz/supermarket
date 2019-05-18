package supermarket;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

class ScreenController
{
    private static void showStage(Stage appStage, Scene scene, String sceneName)
    {
        appStage.setTitle(sceneName);
        appStage.setScene(scene);
        appStage.setResizable(false);
        appStage.show();
    }

    static void goToUserHome(javafx.event.ActionEvent event)
    {
        try
        {
            Parent blah = FXMLLoader.load(ScreenController.class.getResource("UserHome.fxml"));
            Scene scene = new Scene(blah, GlobalConstants.SCENE_WIDTH, GlobalConstants.SCENE_HEIGHT);
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
