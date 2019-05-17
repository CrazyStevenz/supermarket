package supermarket;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.apache.commons.dbutils.DbUtils;

import java.sql.*;

public class LoginController
{
    private static String driverClassName = "org.postgresql.Driver";
    private static String url = GlobalConstants.DB_URL;
    private static String dbUsername = GlobalConstants.DB_USERNAME;
    private static String dbPassword = GlobalConstants.DB_PASSWORD;
    private static Connection conn = null;
    private static Statement st = null;
    private static PreparedStatement ps = null;
    private static ResultSet rs = null;

    @FXML TextField username;
    @FXML PasswordField password;
    @FXML Button login;
    @FXML Label errorLabel;

    @FXML
    private void login(javafx.event.ActionEvent event)
    {
        try
        {
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);

            String un = username.getText();
            String pw = password.getText();

            String validateUser =
                "SELECT name, kind " +
                "FROM users " +
                "WHERE username = ? AND password = ?" +
                "LIMIT 1";

            ps = conn.prepareStatement(validateUser);
            ps.setString(1, un);
            ps.setString(2, pw);
            rs = ps.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                String kind = rs.getString("kind");

                if (name == null) errorLabel.setText(name + kind);
            }

            ScreenController.goToUserHome(event);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        finally
        {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(ps);
            DbUtils.closeQuietly(conn);
        }
    }

    @FXML
    private void create()
    {
        try
        {
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(url, dbUsername, dbPassword);
            st = conn.createStatement();

            st.executeUpdate("DROP TABLE IF EXISTS users CASCADE");

            String createTableUsers =
                "CREATE TABLE users(" +
                    "id serial not null constraint users_pk primary key, " +
                    "username varchar(20), " +
                    "password varchar(40), " +
                    "name varchar(50), " +
                    "kind int" +
                ")";
            st.executeUpdate(createTableUsers);

            String createSailors =
                "INSERT INTO users (username, password, name, kind) VALUES ('dimitris', '1234', 'Dimitris Antoniou', 1);" +
                "INSERT INTO users (username, password, name, kind) VALUES ('kostas', '1234', 'Kostas Athanasiou', 1);";
            st.executeUpdate(createSailors);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        finally
        {
            DbUtils.closeQuietly(rs);
            DbUtils.closeQuietly(st);
            DbUtils.closeQuietly(conn);
        }
    }
}
