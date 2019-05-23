package supermarket.Controllers;

import java.sql.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.commons.dbutils.DbUtils;
import supermarket.Models.User;

public class LoginController {
    private static Connection conn = null;
    private static Statement st = null;
    private static PreparedStatement ps = null;
    private static ResultSet rs = null;

    @FXML TextField usernameField;
    @FXML PasswordField passwordField;
    @FXML Label errorLabel;

    @FXML
    private void login(javafx.event.ActionEvent event) {
        int id = -1, kind = -1;
        String username = null, name = null;

        try {
            conn = DatabaseController.getConnection();

            username = usernameField.getText();
            String password = passwordField.getText();

            String validateUser =
                    "SELECT id, name, kind " +
                    "FROM users " +
                    "WHERE username = ? AND password = ?" +
                    "LIMIT 1";

            ps = conn.prepareStatement(validateUser);
            ps.setString(1, username);
            ps.setString(2, password);
            rs = ps.executeQuery();

            while (rs.next()) {
                id = rs.getInt("id");
                name = rs.getString("name");
                kind = rs.getInt("kind");
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("08004")) {
                errorLabel.setText(e.getMessage());
            } else {
                errorLabel.setText("Something went wrong, please try again.");
            }
        } catch (Exception e) {
            errorLabel.setText("Something went horribly wrong.");
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(conn);
        }

        if (username != null && name != null && !name.equals("") && id != -1) { // TODO Check if null returns ""
            new User(id, username, name, kind).login();

            if (kind != 2) {
                ScreenController.goToUserHome(event);
            } else {
                ScreenController.goToAdminHome(event);
            }
        } else {
            errorLabel.setText("Wrong username or password.");
        }
    }

    @FXML
    private void create() {
        try {
            conn = DatabaseController.getConnection();
            st = conn.createStatement();

            st.executeUpdate("DROP TABLE IF EXISTS users CASCADE");
            st.executeUpdate("DROP TABLE IF EXISTS products CASCADE");
            st.executeUpdate("DROP TABLE IF EXISTS transactions CASCADE");
            st.executeUpdate("DROP TABLE IF EXISTS stores CASCADE");

            String createTableUsers =
                    "CREATE TABLE users (" +
                        "id SERIAL NOT NULL CONSTRAINT users_pk PRIMARY KEY, " +
                        "username VARCHAR(20), " +
                        "password VARCHAR(40), " +
                        "name VARCHAR(50), " +
                        "kind INT" +
                    ")";
            st.executeUpdate(createTableUsers);

            String createTableProducts =
                    "CREATE TABLE products (" +
                        "id SERIAL NOT NULL constraint products_pk PRIMARY KEY, " +
                        "name VARCHAR(50) NOT NULL, " +
                        "price FLOAT(2) NOT NULL, " +
                        "stock INT" +
                    ");" +
                    "CREATE UNIQUE INDEX products_name_uindex ON products (name)";
            st.executeUpdate(createTableProducts);

            String createTableTransactions =
                    "CREATE TABLE transactions (" +
                        "id SERIAL NOT NULL CONSTRAINT transactions_pk PRIMARY KEY, " +
                        "user_id INT NOT NULL, " +
                        "product_id INT NOT NULL, " +
                        "amount INT NOT NULL, " +
                        "purchase_date TIMESTAMP" +
                    ");" +
                    "ALTER TABLE transactions ADD CONSTRAINT transactions_users_id_fk " +
                        "FOREIGN KEY (user_id) REFERENCES users ON UPDATE CASCADE ON DELETE CASCADE;";
            st.executeUpdate(createTableTransactions);

            String createTableStores =
                    "CREATE TABLE stores (" +
                            "id SERIAL NOT NULL CONSTRAINT stores_pk PRIMARY KEY, " +
                            "address VARCHAR(50), " +
                            "phone VARCHAR(20), " +
                            "work_hours VARCHAR(20) " +
                        ");" +
                    "CREATE UNIQUE INDEX stores_phone_uindex ON stores (phone)";
            st.executeUpdate(createTableStores);

            String createSailors =
                    "INSERT INTO users (username, password, name, kind) VALUES ('d', '1234', 'Dimitris Antoniou', 1);" +
                    "INSERT INTO users (username, password, name, kind) VALUES ('kostas', '1234', 'Kostas Athanasiou', 1);" +
                    "INSERT INTO users (username, password, name, kind) VALUES ('admin', '1234', 'Admin', 2);";
            st.executeUpdate(createSailors);

            String createProducts =
                    "INSERT INTO products (name, price, stock) VALUES ('Bananas', 2.8, 1);" +
                    "INSERT INTO products (name, price, stock) VALUES ('Feta Cheese', 8.9, 0);" +
                    "INSERT INTO products (name, price, stock) VALUES ('Milk', 0.99, 6);";
            st.executeUpdate(createProducts);

            String createStores =
                    "INSERT INTO stores (address, phone, work_hours) VALUES ('Aristotelous 42', '2310000000', '09:00-21:00');" +
                    "INSERT INTO stores (address, phone, work_hours) VALUES ('Agias Sofias 128', '2310123456', '07:00-20:00');";
            st.executeUpdate(createStores);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(st);
            DbUtils.closeQuietly(conn);
        }
    }
}
