package supermarket;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

class ScreenController
{
    private static void showStage(javafx.event.ActionEvent event, String filename, String sceneName)
    {
        try
        {
            Parent par = FXMLLoader.load(ScreenController.class.getResource(filename));
            Scene scene = new Scene(par, GlobalConstants.SCENE_WIDTH, GlobalConstants.SCENE_HEIGHT);
            Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            appStage.setTitle(sceneName);
            appStage.setScene(scene);
            appStage.setResizable(false);
            appStage.show();
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    static void goToUserHome(javafx.event.ActionEvent event)
    {
        showStage(event, "UserHome.fxml", "Homepage");
    }

    static void goToProducts(javafx.event.ActionEvent event)
    {
        showStage(event, "Product.fxml", "Products");
    }

    static void goToTransactions(javafx.event.ActionEvent event)
    {
        showStage(event, "Transaction.fxml", "Transactions");
    }

    static void logout(javafx.event.ActionEvent event) {
        User.getUserInstance().delete();
        showStage(event, "Login.fxml", "Login");
    }
}
