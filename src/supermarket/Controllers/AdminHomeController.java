package supermarket.Controllers;

import javafx.fxml.FXML;

public class AdminHomeController {
    @FXML
    private void back(javafx.event.ActionEvent event) {
        ScreenController.goToAdminHome(event);
    }

    @FXML
    private void logout(javafx.event.ActionEvent event) {
        ScreenController.logout(event);
    }

    @FXML
    private void goToProducts(javafx.event.ActionEvent event) {
        ScreenController.goToProducts(event);
    }

    @FXML
    private void goToTransactions(javafx.event.ActionEvent event) {
        ScreenController.goToTransactions(event);
    }

    @FXML
    private void goToStores(javafx.event.ActionEvent event) {
        ScreenController.goToStores(event);
    }

    @FXML
    private void goToAdminPanel(javafx.event.ActionEvent event) {
        ScreenController.goToAdminPanel(event);
    }

    @FXML
    private void goToLog(javafx.event.ActionEvent event) {
        ScreenController.goToLog(event);
    }
}
