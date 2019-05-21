package supermarket.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.apache.commons.dbutils.DbUtils;
import supermarket.GlobalConstants;
import supermarket.Models.Transaction;
import supermarket.Models.User;

import java.sql.*;

public class TransactionController {
    private static String driverClassName = "org.postgresql.Driver";
    private static String url = GlobalConstants.DB_URL;
    private static String dbUsername = GlobalConstants.DB_USERNAME;
    private static String dbPassword = GlobalConstants.DB_PASSWORD;
    private static Connection conn = null;
    private static Statement st = null;
    private static PreparedStatement ps = null;
    private static ResultSet rs = null;
    private static Transaction[] transactions = new Transaction[50];

    @FXML ListView<String> transactionListView;
    @FXML Label detailsLabel;
    @FXML Label errorLabel;

    @FXML
    private void loadTransactions() {
        errorLabel.setText("");
        try {
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            String getTransactionsQuery =
                    "SELECT * " +
                    "FROM transactions " +
                    "WHERE user_id = " + User.getUserInstance().getId();

            st = conn.createStatement();
            rs = st.executeQuery(getTransactionsQuery);

            int id, user_id, product_id, amount, i = 0;
            String purchase_date;

            while (rs.next()) {
                id = rs.getInt("id");
                user_id = rs.getInt("user_id");
                product_id = rs.getInt("product_id");
                amount = rs.getInt("amount");
                purchase_date = rs.getString("purchase_date");

                if (rs.wasNull()) {
                    transactions[i] = new Transaction(id, user_id, product_id, amount);
                } else {
                    transactions[i] = new Transaction(id, user_id, product_id, amount, purchase_date);
                }

                i++;
            }

            ObservableList<String> transactionItems = FXCollections.observableArrayList();

            for (int j = 0; j < i; j++) {
                String getProductNameQuery =
                        "SELECT name " +
                        "FROM products " +
                        "WHERE id = " + transactions[j].getProduct_id();

                st = conn.createStatement();
                rs = st.executeQuery(getProductNameQuery);

                String productName = "";

                while (rs.next()) {
                    productName = rs.getString("name");
                }

                transactionItems.add(productName + " - " + transactions[j].getAmount());
            }

            transactionListView.setItems(transactionItems);
            transactionListView.setPrefWidth(325);
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
        errorLabel.setText("");
        int index = transactionListView.getSelectionModel().getSelectedIndex();

        try {
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            String getProductNameQuery =
                    "SELECT name " +
                    "FROM products " +
                    "WHERE id = " + transactions[index].getProduct_id();

            st = conn.createStatement();
            rs = st.executeQuery(getProductNameQuery);

            String productName = "";

            while (rs.next()) {
                productName = rs.getString("name");
            }

            detailsLabel.setText(
                    "Product Name: " + productName +
                    "\n\nAmount Bought: " + transactions[index].getAmount() +
                    "\n\nDate: " + transactions[index].getDate()
            );
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
    private void back(javafx.event.ActionEvent event) {
        ScreenController.goToUserHome(event);
    }

    @FXML
    private void logout(javafx.event.ActionEvent event) {
        ScreenController.logout(event);
    }
}