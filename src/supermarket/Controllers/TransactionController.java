package supermarket.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.apache.commons.dbutils.DbUtils;
import supermarket.Models.Transaction;
import supermarket.Models.User;
import java.sql.*;

public class TransactionController {
    private static Connection conn = null;
    private static Statement st = null;
    private static PreparedStatement ps = null;
    private static ResultSet rs = null;
    private static Transaction[] transactions = new Transaction[50];

    @FXML ListView<String> transactionListView;
    @FXML TextField nameTextField;
    @FXML TextField amountTextField;
    @FXML TextField dateTextField;
    @FXML Button saveButton;
    @FXML Button deleteButton;
    @FXML Label detailsLabel;
    @FXML Label errorLabel;

    @FXML
    private void loadTransactions() {
        errorLabel.setText("");
        nameTextField.setText("");
        amountTextField.setText("");
        dateTextField.setText("");

        if (User.getUserInstance().getKind() == 2) {
            amountTextField.setEditable(true);
            dateTextField.setEditable(true);

            saveButton.setVisible(true);
            deleteButton.setVisible(true);
        }

        try {
            conn = DatabaseController.getConnection();

            String getTransactionsQuery =
                    "SELECT * " +
                    "FROM transactions " +
                    "WHERE user_id = ? " +
                    "ORDER BY id";

            ps = conn.prepareStatement(getTransactionsQuery);
            ps.setInt(1, User.getUserInstance().getId());
            rs = ps.executeQuery();

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
        if (index != -1) {
            try {
                conn = DatabaseController.getConnection();

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

                nameTextField.setText(productName);
                amountTextField.setText(Integer.toString(transactions[index].getAmount()));
                dateTextField.setText(transactions[index].getDate());
            } catch (SQLException e) {
                errorLabel.setText("Database failure.");
            } catch (Exception e) {
                errorLabel.setText("Something went wrong.");
            } finally {
                DbUtils.closeQuietly(rs);
                DbUtils.closeQuietly(ps);
                DbUtils.closeQuietly(conn);
            }

            deleteButton.setDisable(false);
            saveButton.setDisable(false);
        } else {
            errorLabel.setText("Refresh the list first");
        }
    }

    @FXML
    private void delete() {
        try {
            int index = transactionListView.getSelectionModel().getSelectedIndex();

            conn = DatabaseController.getConnection();

            String deleteProductQuery =
                    "DELETE FROM transactions " +
                    "WHERE id = ?";

            ps = conn.prepareStatement(deleteProductQuery);
            ps.setInt(1, transactions[index].getId());
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
            int index = transactionListView.getSelectionModel().getSelectedIndex();

            conn = DatabaseController.getConnection();

            String editProductQuery =
                    "UPDATE transactions " +
                    "SET amount = ?, purchase_date = ? " +
                    "WHERE id = ?";

            ps = conn.prepareStatement(editProductQuery);
            ps.setInt(1, Integer.parseInt(amountTextField.getText()));
            ps.setString(2, dateTextField.getText());
            ps.setInt(3, transactions[index].getId());
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
