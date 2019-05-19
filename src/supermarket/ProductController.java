package supermarket;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.Button;
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

    private static int[] id = new int[10];
    private static String[] name = new String[10];
    private static float[] price = new float[10];
    private static int[] stock = new int[10];

    @FXML ScrollPane productScrollPane;

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

            VBox testVBox = new VBox();

            for (int j = 0; j < i; j++)
            {
                testVBox.getChildren().add(new Button(id[j] + " - " + name[j] + " - " + price[j] + " - " + stock[j]));
            }

            productScrollPane.setContent(testVBox);
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
}
