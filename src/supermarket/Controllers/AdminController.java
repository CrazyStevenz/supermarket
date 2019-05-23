package supermarket.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import org.apache.commons.dbutils.DbUtils;
import supermarket.Models.User;
import java.sql.*;

public class AdminController {
    private static Connection conn = null;
    private static Statement st = null;
    private static PreparedStatement ps = null;
    private static ResultSet rs = null;
    private static User[] users = new User[50];

    @FXML ListView<String> userListView;
    @FXML TextField nameTextField;
    @FXML TextField usernameTextField;
    @FXML TextField kindTextField;
    @FXML Button deleteButton;
    @FXML Button saveButton;
    @FXML Label errorLabel;

    @FXML
    private void loadUsers() {
        errorLabel.setText("");
        nameTextField.setText("");
        usernameTextField.setText("");
        kindTextField.setText("");

        try {
            conn = DatabaseController.getConnection();

            String getUsersQuery =
                    "SELECT id, username, name, kind " +
                    "FROM users " +
                    "ORDER BY id";

            st = conn.createStatement();
            rs = st.executeQuery(getUsersQuery);

            int id, kind, i = 0;
            String username, name;
            while (rs.next()) {
                id = rs.getInt("id");
                username = rs.getString("username");
                name = rs.getString("name");
                kind = rs.getInt("kind");

                users[i] = new User(id, username, name, kind);

                i++;
            }

            ObservableList<String> userItems = FXCollections.observableArrayList();

            for (int j = 0; j < i; j++) {
                userItems.add(users[j].getId() + " - " + users[j].getUsername());
            }

            userListView.setItems(userItems);
            userListView.setPrefWidth(325);
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
    private void newUser() {
        try {
            conn = DatabaseController.getConnection();

            String newUserQuery =
                    "INSERT INTO users (username, password, name, kind) VALUES ('newuser', '1234', 'New User', 1)";

            st = conn.createStatement();
            st.executeUpdate(newUserQuery);

            loadUsers();
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
    private void save() {
        try {
            int index = userListView.getSelectionModel().getSelectedIndex();

            conn = DatabaseController.getConnection();

            String editUserQuery =
                    "UPDATE users " +
                    "SET name = ?, username = ?, kind = ?" +
                    "WHERE id = ?";

            ps = conn.prepareStatement(editUserQuery);
            ps.setString(1, nameTextField.getText());
            ps.setString(2, usernameTextField.getText());
            ps.setInt(3, Integer.parseInt(kindTextField.getText()));
            ps.setInt(4, users[index].getId());
            ps.executeUpdate();

            loadUsers();
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
    private void delete() {
        try {
            int index = userListView.getSelectionModel().getSelectedIndex();

            conn = DatabaseController.getConnection();

            String deleteUserQuery =
                    "DELETE FROM users " +
                    "WHERE id = ?";

            ps = conn.prepareStatement(deleteUserQuery);
            ps.setInt(1, users[index].getId());
            ps.executeUpdate();

            loadUsers();
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
    private void updateDetails() {
        int index = userListView.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            nameTextField.setText(users[index].getName());
            usernameTextField.setText(users[index].getUsername());
            kindTextField.setText(Integer.toString(users[index].getKind()));

            deleteButton.setDisable(false);
            saveButton.setDisable(false);
        } else {
            errorLabel.setText("Refresh the list first");
        }
    }

    @FXML
    private void back(javafx.event.ActionEvent event) {
        ScreenController.goToAdminHome(event);
    }

    @FXML
    private void logout(javafx.event.ActionEvent event) {
        ScreenController.logout(event);
    }
}
