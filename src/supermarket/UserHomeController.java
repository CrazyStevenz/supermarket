package supermarket;

import javafx.fxml.FXML;

public class UserHomeController
{
    @FXML
    private void goToProducts(javafx.event.ActionEvent event)
    {
        ScreenController.goToProducts(event);
    }
}
