package supermarket.Controllers;

import java.sql.*; // TODO import each one needed separately
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.apache.commons.dbutils.DbUtils;
import supermarket.Models.Product;
import supermarket.Models.User;

public class ProductController {
    private final static Logger logger = Logger.getLogger(ProductController.class.getName());
    private static FileHandler fh;
    private static Connection conn = null;
    private static Statement st = null;
    private static PreparedStatement ps = null;
    private static ResultSet rs = null;
    private static Product[] products = new Product[50];

    @FXML ListView<String> productListView;
    @FXML TextField nameTextField;
    @FXML TextField priceTextField;
    @FXML TextField stockTextField;
    @FXML Button purchaseButton;
    @FXML Button newProductButton;
    @FXML Button deleteButton;
    @FXML Button saveButton;
    @FXML Label errorLabel;

    @FXML
    private void loadProducts() {
        errorLabel.setText("");
        nameTextField.setText("");
        priceTextField.setText("");
        stockTextField.setText("");

        if (User.getUserInstance().getKind() == 2) {
            nameTextField.setEditable(true);
            priceTextField.setEditable(true);
            stockTextField.setEditable(true);

            newProductButton.setVisible(true);
            deleteButton.setVisible(true);
            saveButton.setVisible(true);
        } else {
            purchaseButton.setVisible(true);
        }

        try {
            conn = DatabaseController.getConnection();

            String getProductsQuery =
                    "SELECT id, name, price, stock " +
                    "FROM products " +
                    "ORDER BY id";

            st = conn.createStatement();
            rs = st.executeQuery(getProductsQuery);

            int id, stock, i = 0;
            float price;
            String name;

            while (rs.next()) {
                id = rs.getInt("id");
                name = rs.getString("name");
                price = rs.getFloat("price");
                stock = rs.getInt("stock");

                if (rs.wasNull()) {
                    products[i] = new Product(id, name, price);
                } else {
                    products[i] = new Product(id, name, price, stock);
                }

                i++;
            }

            ObservableList<String> productItems = FXCollections.observableArrayList();

            for (int j = 0; j < i; j++) {
                productItems.add(products[j].getName() + " - " + products[j].getPrice() + "€ - " + products[j].getStock());
            }

            productListView.setItems(productItems);
            productListView.setPrefWidth(325);
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
    private void updateDetails() {
        int index = productListView.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            nameTextField.setText(products[index].getName());
            priceTextField.setText(Float.toString(products[index].getPrice()));
            stockTextField.setText(Integer.toString(products[index].getStock()));

            deleteButton.setDisable(false);
            saveButton.setDisable(false);
        } else {
            errorLabel.setText("Refresh the list first");
        }
    }

    @FXML
    private void purchase() {
        errorLabel.setText("");
        int index = productListView.getSelectionModel().getSelectedIndex();
        if (index == -1) {
            errorLabel.setText("Select a product first.");
        } else {
            try {
                fh = new FileHandler("..\\supermarket\\logfile.log", true);
                logger.addHandler(fh);
                SimpleFormatter formatter = new SimpleFormatter();
                fh.setFormatter(formatter);

                conn = DatabaseController.getConnection();

                int productStock = products[index].getStock();
                if (productStock < 1) {
                    errorLabel.setText("The product you want to buy has run out.");
                } else {
                    String purchaseQuery =
                            "INSERT INTO transactions (user_id, product_id, amount, purchase_date) " +
                            "VALUES (?, ?, 1, CURRENT_TIMESTAMP(0))";
                    ps = conn.prepareStatement(purchaseQuery);
                    ps.setInt(1, User.getUserInstance().getId());
                    ps.setInt(2, products[index].getId());
                    ps.executeUpdate();

                    String updateProductQuery =
                            "UPDATE products SET stock = ? WHERE id = ?";
                    ps = conn.prepareStatement(updateProductQuery);
                    ps.setInt(1, productStock - 1);
                    ps.setInt(2, products[index].getId());
                    ps.executeUpdate();

                    logger.log(Level.INFO,
                            "User " + User.getUserInstance().getName() +
                            " with id " + User.getUserInstance().getId() +
                            " bought a product");

                    nameTextField.setText("");
                    priceTextField.setText("");
                    stockTextField.setText("");

                    loadProducts();
                }
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
    }

    @FXML
    private void newProduct() {
        try {
            fh = new FileHandler("..\\supermarket\\logfile.log", true);
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            conn = DatabaseController.getConnection();

            String newProductQuery =
                    "INSERT INTO products (name, price, stock) VALUES ('New Product', 0, 0)";

            st = conn.createStatement();
            st.executeUpdate(newProductQuery);

            logger.log(Level.INFO, "A product was added.");

            loadProducts();
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

            int index = productListView.getSelectionModel().getSelectedIndex();

            conn = DatabaseController.getConnection();

            String deleteProductQuery =
                    "DELETE FROM products " +
                    "WHERE id = ?";

            ps = conn.prepareStatement(deleteProductQuery);
            ps.setInt(1, products[index].getId());
            ps.executeUpdate();

            logger.log(Level.INFO, "A product was deleted.");

            loadProducts();
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
            int index = productListView.getSelectionModel().getSelectedIndex();

            conn = DatabaseController.getConnection();

            String editProductQuery =
                    "UPDATE products " +
                    "SET name = ?, price = ?, stock = ?" +
                    "WHERE id = ?";

            ps = conn.prepareStatement(editProductQuery);
            ps.setString(1, nameTextField.getText());
            ps.setFloat(2, Float.parseFloat(priceTextField.getText()));
            ps.setInt(3, Integer.parseInt(stockTextField.getText()));
            ps.setInt(4, products[index].getId());
            ps.executeUpdate();

            logger.log(Level.INFO, "A product was edited.");

            loadProducts();
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
        if (User.getUserInstance().getKind() == 2) {
            ScreenController.goToAdminHome(event);
        } else {
            ScreenController.goToUserHome(event);
        }
    }

    @FXML
    private void logout(javafx.event.ActionEvent event) {
        ScreenController.logout(event);
    }
}
