package supermarket.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.apache.commons.dbutils.DbUtils;
import supermarket.GlobalConstants;
import supermarket.Models.Store;
import supermarket.Models.Transaction;

import java.sql.*;

public class StoreController {
    private static String driverClassName = "org.postgresql.Driver";
    private static String url = GlobalConstants.DB_URL;
    private static String dbUsername = GlobalConstants.DB_USERNAME;
    private static String dbPassword = GlobalConstants.DB_PASSWORD;
    private static Connection conn = null;
    private static Statement st = null;
    private static PreparedStatement ps = null;
    private static ResultSet rs = null;
    private static Store[] stores = new Store[50];

    @FXML ListView<String> storeListView;
    @FXML Label detailsLabel;
    @FXML Label errorLabel;

    @FXML
    private void loadTransactions() {
        errorLabel.setText("");
        try {
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            String getStoresQuery =
                    "SELECT * " +
                            "FROM stores";

            st = conn.createStatement();
            rs = st.executeQuery(getStoresQuery);

            int id, i = 0;
            String address, phone, work_hours;

            while (rs.next()) {
                id = rs.getInt("id");
                address = rs.getString("address");
                phone = rs.getString("phone");
                work_hours = rs.getString("work_hours");

                stores[i] = new Store(id, address, phone, work_hours);

                i++;
            }

            ObservableList<String> productItems = FXCollections.observableArrayList();

            for (int j = 0; j < i; j++) {
                productItems.add(stores[j].getAddress());
            }

            storeListView.setItems(productItems);
            storeListView.setPrefWidth(325);
        } catch (SQLException e) {
            errorLabel.setText("Database failure.");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(conn);
        }
    }

    @FXML
    private void updateDetails() {
        int index = storeListView.getSelectionModel().getSelectedIndex();

        detailsLabel.setText(
                "Address: " + stores[index].getAddress() +
                "\n\nPhone: " + stores[index].getPhone() +
                "\n\nWork Hours: " + stores[index].getWorkHours()
        );
    }

    @FXML
    private void back(javafx.event.ActionEvent event) {
        ScreenController.goToUserHome(event);
    }

    @FXML
    private void logout(javafx.event.ActionEvent event) {
        ScreenController.logout(event);
    }
}
