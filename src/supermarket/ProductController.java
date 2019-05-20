package supermarket;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.apache.commons.dbutils.DbUtils;
import java.sql.*;

public class ProductController {
    private static String driverClassName = "org.postgresql.Driver";
    private static String url = GlobalConstants.DB_URL;
    private static String dbUsername = GlobalConstants.DB_USERNAME;
    private static String dbPassword = GlobalConstants.DB_PASSWORD;
    private static Connection conn = null;
    private static Statement st = null;
    private static PreparedStatement ps = null;
    private static ResultSet rs = null;
    private static Product[] products = new Product[50];

    @FXML ListView<String> productListView;
    @FXML Label detailsLabel;

    @FXML
    private void loadProducts() {
        try {
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            String getProductsQuery =
                    "SELECT id, name, price, stock " +
                    "FROM products";

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
        int index = productListView.getSelectionModel().getSelectedIndex();

        detailsLabel.setText(
                "Product Name: " + products[index].getName() +
                 "\n\nPrice: " + products[index].getPrice() +
                 "\n\nStock: " + products[index].getStock()
        );
    }
}
