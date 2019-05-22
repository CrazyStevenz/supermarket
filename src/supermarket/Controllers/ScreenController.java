package supermarket.Controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import supermarket.GlobalConstants;
import supermarket.Models.User;

public class ScreenController {
    private static void showStage(ActionEvent event, String filename, String sceneName) {
        try {
            Parent par = FXMLLoader.load(ScreenController.class.getResource(filename));
            Scene scene = new Scene(par, GlobalConstants.SCENE_WIDTH, GlobalConstants.SCENE_HEIGHT);
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setTitle(sceneName);
            appStage.setScene(scene);
            appStage.setResizable(false);
            appStage.show();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    static void goToUserHome(ActionEvent event) {
        showStage(event, "../../resources/fxml/UserHome.fxml", "Homepage");
    }

    static void goToAdminHome(ActionEvent event) {
        showStage(event, "../../resources/fxml/AdminHome.fxml", "Homepage");
    }

    static void goToProducts(ActionEvent event) {
        showStage(event, "../../resources/fxml/Product.fxml", "Products");
    }

    static void goToTransactions(ActionEvent event) {
        showStage(event, "../../resources/fxml/Transaction.fxml", "Transactions");
    }

    static void goToStores(ActionEvent event) {
        showStage(event, "../../resources/fxml/Store.fxml", "Stores");
    }

    static void goToAbout(ActionEvent event) {
        showStage(event, "../../resources/fxml/About.fxml", "About");
    }

    public static void logout(javafx.event.ActionEvent event) {
        User.getUserInstance().delete();
        showStage(event, "../../resources/fxml/Login.fxml", "Login");
    }
}
