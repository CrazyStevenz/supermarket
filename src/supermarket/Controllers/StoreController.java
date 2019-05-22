package supermarket.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.apache.commons.dbutils.DbUtils;
import supermarket.GlobalConstants;
import supermarket.Models.Store;
import supermarket.Models.User;

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
    @FXML TextField addressTextField;
    @FXML TextField phoneTextField;
    @FXML TextField hoursTextField;
    @FXML Button newStoreButton;
    @FXML Button deleteButton;
    @FXML Button saveButton;
    @FXML Label detailsLabel;
    @FXML Label errorLabel;

    @FXML
    private void loadTransactions() {
        errorLabel.setText("");

        if (User.getUserInstance().getKind() == 2) {
            addressTextField.setEditable(true);
            phoneTextField.setEditable(true);
            hoursTextField.setEditable(true);

            newStoreButton.setVisible(true);
            deleteButton.setVisible(true);
            saveButton.setVisible(true);
        }

        try {
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            String getStoresQuery =
                    "SELECT * " +
                    "FROM stores " +
                    "ORDER BY id";

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

        addressTextField.setText(stores[index].getAddress());
        phoneTextField.setText(stores[index].getPhone());
        hoursTextField.setText(stores[index].getWorkHours());

        deleteButton.setDisable(false);
        saveButton.setDisable(false);
    }

    @FXML
    private void newStore() {
        try {
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            String newStoreQuery =
                    "INSERT INTO stores (address, phone, work_hours) " +
                    "VALUES ('New Store Address', '0000000000', '00:00-00:00')";

            st = conn.createStatement();
            st.executeUpdate(newStoreQuery);

            loadTransactions();
        } catch (SQLException e) {
            errorLabel.setText("Database failure.");
        } catch (Exception e) {
            errorLabel.setText("Something went wrong.");
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(conn);
        }
    }

    @FXML
    private void delete() {
        try {
            int index = storeListView.getSelectionModel().getSelectedIndex();

            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            String deleteStoreQuery =
                    "DELETE FROM stores " +
                    "WHERE id = ?";

            ps = conn.prepareStatement(deleteStoreQuery);
            ps.setInt(1, stores[index].getId());
            ps.executeUpdate();

            loadTransactions();
        } catch (SQLException e) {
            errorLabel.setText("Database failure.");
        } catch (Exception e) {
            errorLabel.setText("Something went wrong.");
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(conn);
        }

        deleteButton.setDisable(true);
        saveButton.setDisable(true);
    }

    @FXML
    private void save() {
        try {
            int index = storeListView.getSelectionModel().getSelectedIndex();

            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            String editStoreQuery =
                    "UPDATE stores " +
                    "SET address = ?, phone = ?, work_hours = ?" +
                    "WHERE id = ?";

            ps = conn.prepareStatement(editStoreQuery);
            ps.setString(1, addressTextField.getText());
            ps.setString(2, phoneTextField.getText());
            ps.setString(3, hoursTextField.getText());
            ps.setInt(4, stores[index].getId());
            ps.executeUpdate();

            loadTransactions();
        } catch (SQLException e) {
            errorLabel.setText("Database failure.");
        } catch (Exception e) {
            errorLabel.setText("Something went wrong.");
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(conn);
        }

        deleteButton.setDisable(true);
        saveButton.setDisable(true);
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
