package supermarket;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import org.apache.commons.dbutils.DbUtils;
import java.sql.*;

public class ProductController
{
    private static String driverClassName = "org.postgresql.Driver";
    private static String url = GlobalConstants.DB_URL;
    private static String dbUsername = GlobalConstants.DB_USERNAME;
    private static String dbPassword = GlobalConstants.DB_PASSWORD;
    private static Connection conn = null;
    private static Statement st = null;
    private static PreparedStatement ps = null;
    private static ResultSet rs = null;

    private static int[] id = new int[50];
    private static String[] name = new String[50];
    private static float[] price = new float[50];
    private static int[] stock = new int[50];

    ListView<String> productList = new ListView<>();
    ListView<String> cartList = new ListView<>();
    ObservableList<String> productItems = FXCollections.observableArrayList();
    ObservableList<String> cartItems = FXCollections.observableArrayList();

    @FXML ScrollPane productScrollPane;
    @FXML ScrollPane cartScrollPane;

    @FXML
    private void loadProducts()
    {
        try
        {
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            String getProductsQuery =
                "SELECT *" +
                "FROM products";

            st = conn.createStatement();
            rs = st.executeQuery(getProductsQuery);

            int i = 0;
            while (rs.next())
            {
                id[i] = rs.getInt("id");
                name[i] = rs.getString("name");
                price[i] = rs.getFloat("price");
                stock[i] = rs.getInt("stock");

                i++;
            }

            for (int j = 0; j < i; j++)
            {
                productItems.add(name[j] + " - " + price[j] + "€ - " + stock[j] + " left");
            }

            productList.setItems(productItems);
            productList.setPrefWidth(325);
            productScrollPane.setContent(productList);
        }
        catch (Exception e)
        {
            // TODO
        }
        finally
        {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(conn);
        }
    }

    @FXML
    private void moveToCart()
    {
        cartItems.add(productList.getSelectionModel().getSelectedItems().toString());
        cartList.setItems(cartItems);
        cartList.setPrefWidth(325);
        cartScrollPane.setContent(cartList);
    }
}
