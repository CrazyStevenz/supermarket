package supermarket;

import java.sql.*;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.commons.dbutils.DbUtils;

public class LoginController {
    private static String driverClassName = "org.postgresql.Driver";
    private static String url = GlobalConstants.DB_URL;
    private static String dbUsername = GlobalConstants.DB_USERNAME;
    private static String dbPassword = GlobalConstants.DB_PASSWORD;
    private static Connection conn = null;
    private static Statement st = null;
    private static PreparedStatement ps = null;
    private static ResultSet rs = null;

    @FXML
    TextField usernameField;
    @FXML
    PasswordField passwordField;
    @FXML
    Label errorLabel;

    @FXML
    private void login(javafx.event.ActionEvent event) {
        int id = -1, kind = -1;
        String username = null, name = null;

        try {
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);

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
            if (kind != 1 && kind != 2) {
                kind = 1;
            }

            new User(id, username, name, kind);

            ScreenController.goToUserHome(event);
        } else {
            errorLabel.setText("Wrong username or password.");
        }
    }

    @FXML
    private void create() {
        try {
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);
            st = conn.createStatement();

            st.executeUpdate("DROP TABLE IF EXISTS users CASCADE");

            String createTableUsers =
                    "CREATE TABLE users (" +
                        "id serial not null constraint users_pk primary key, " +
                        "username varchar(20), " +
                        "password varchar(40), " +
                        "name varchar(50), " +
                        "kind int" +
                    ")";
            st.executeUpdate(createTableUsers);

            String createTableProducts =
                    "CREATE TABLE products (" +
                        "id serial not null constraint products_pk primary key, " +
                        "name varchar(50) not null, " +
                        "price float(2) not null, " +
                        "stock int" +
                    ");" +
                    "CREATE UNIQUE INDEX products_name_uindex ON products (name)";
            st.executeUpdate(createTableProducts);

            String createSailors =
                "INSERT INTO users (username, password, name, kind) VALUES ('dimitris', '1234', 'Dimitris Antoniou', 1);" +
                "INSERT INTO users (username, password, name, kind) VALUES ('kostas', '1234', 'Kostas Athanasiou', 1);";
            st.executeUpdate(createSailors);

            String createProducts =
                    "INSERT INTO products (name, price, stock) VALUES ('Bananas', 2.8, 1);" +
                    "INSERT INTO products (name, price, stock) VALUES ('Feta Cheese', 8.9, 0);";
            st.executeUpdate(createProducts);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(st);
            DbUtils.closeQuietly(conn);
        }
    }
}
