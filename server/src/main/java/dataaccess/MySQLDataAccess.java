package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import com.mysql.cj.jdbc.PreparedStatementWrapper;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;


public class MySQLDataAccess implements DataAccess {
    private final Gson gson = new Gson();

    @Override
    public void clear() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement deleteAuth = conn.prepareStatement("DELETE FROM auth");
                 PreparedStatement deleteGame = conn.prepareStatement("DELETE FROM game");
                 PreparedStatement deleteUser = conn.prepareStatement("DELETE FROM user")) {
                deleteAuth.executeUpdate();
                deleteGame.executeUpdate();
                deleteUser.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing database", e);
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO user(username, password_hash, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.password(), BCrypt.gensalt());

        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.username());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.email());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("forbidden", e);
        }
    }
}
