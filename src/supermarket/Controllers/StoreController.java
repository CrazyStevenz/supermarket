package supermarket.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.apache.commons.dbutils.DbUtils;
import supermarket.Models.Store;
import supermarket.Models.User;
import java.sql.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class StoreController {
    private final static Logger logger = Logger.getLogger(ProductController.class.getName());
    private static FileHandler fh;
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
            conn = DatabaseController.getConnection();

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
        if (index != -1) {
            addressTextField.setText(stores[index].getAddress());
            phoneTextField.setText(stores[index].getPhone());
            hoursTextField.setText(stores[index].getWorkHours());

            deleteButton.setDisable(false);
            saveButton.setDisable(false);
        } else {
            errorLabel.setText("Refresh the list first");
        }
    }

    @FXML
    private void newStore() {
        try {
            fh = new FileHandler("..\\supermarket\\logfile.log", true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            conn = DatabaseController.getConnection();

            String newStoreQuery =
                    "INSERT INTO stores (address, phone, work_hours) " +
                    "VALUES ('New Store Address', '0000000000', '00:00-00:00')";

            st = conn.createStatement();
            st.executeUpdate(newStoreQuery);

            logger.log(Level.INFO, "A store was added.");

            loadTransactions();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            errorLabel.setText("Database failure.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            errorLabel.setText("Something went wrong.");
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(conn);
            fh.close();
        }
    }

    @FXML
    private void delete() {
        try {
            fh = new FileHandler("..\\supermarket\\logfile.log", true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            int index = storeListView.getSelectionModel().getSelectedIndex();

            conn = DatabaseController.getConnection();

            String deleteStoreQuery =
                    "DELETE FROM stores " +
                    "WHERE id = ?";

            ps = conn.prepareStatement(deleteStoreQuery);
            ps.setInt(1, stores[index].getId());
            ps.executeUpdate();

            logger.log(Level.INFO, "A store was deleted.");

            loadTransactions();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            errorLabel.setText("Database failure.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            errorLabel.setText("Something went wrong.");
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(conn);
            fh.close();
        }

        deleteButton.setDisable(true);
        saveButton.setDisable(true);
    }

    @FXML
    private void save() {
        try {
            fh = new FileHandler("..\\supermarket\\logfile.log", true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            int index = storeListView.getSelectionModel().getSelectedIndex();

            conn = DatabaseController.getConnection();

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

            logger.log(Level.INFO, "A store was edited.");

            loadTransactions();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage());
            errorLabel.setText("Database failure.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage());
            errorLabel.setText("Something went wrong.");
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(conn);
            fh.close();
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
