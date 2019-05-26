package supermarket.Controllers;

import supermarket.GlobalConstants;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseController {
    static Connection getConnection() {
        String driverClassName = "org.postgresql.Driver";
        Connection conn = null;

        try {
            Class.forName(driverClassName);
            conn = DriverManager.getConnection(GlobalConstants.DB_URL, GlobalConstants.DB_USERNAME, GlobalConstants.DB_PASSWORD);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }
}
