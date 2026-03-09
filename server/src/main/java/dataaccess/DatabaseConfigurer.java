package dataaccess;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseConfigurer {
    public static void configureDataBase() throws DataAccessException {
        DatabaseManager.createDatabase();

        try (Connection conn = DatabaseManager.getConnection()) {
            Statement stmt = conn.createStatement();

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS user (" +
                            "username VARCHAR(255) NOT NULL PRIMARY KEY, " +
                            "password_hash VARCHAR(255) NOT NULL, " +
                            "email VARCHAR(255) NOT NULL"
            );

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS auth(" +
                            "auth_token VARCHAR(255) NOT NULL PRIMARY KEY, " +
                            "username VARCHAR(255) NOT NULL)"
            );

            stmt.executeUpdate(
                    "CREATE TABLE IF NOT EXISTS game(" +
                            "game_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                            "white_username VARCHAR(255), " +
                            "black_username VARCHAR(255), " +
                            "game_name VARCHAR(255) NOT NULL, " +
                            "game_json LONGTEXT NOT NULL"
            );
            stmt.close();
        } catch (SQLException e) {
            throw new DataAccessException("Error configuring database", e);
        }
    }
}
