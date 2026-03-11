package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLDataAccessTest {
    private MySQLDataAccess dao;

    @BeforeAll
    static void initializeDatabase() throws DataAccessException {
        DatabaseConfigurer.configureDataBase();
    }

    @BeforeEach
    void setUp() throws DataAccessException {
        dao = new MySQLDataAccess();
        dao.clear();
    }

    @Test
    void clearPositive() throws DataAccessException {
        dao.createUser(new UserData("user1", "pass1", "u1@gmail.com"));
        dao.createAuth(new AuthData("token1", "user1"));
        dao.createGame("game1");

        dao.clear();

        assertNull(dao.getUser("user1"));
        assertNull(dao.getAuth("token1"));
        assertTrue(dao.listGames().isEmpty());
    }

    @Test
    void createUserPositive() throws DataAccessException {
        UserData user = new UserData("alice", "password123", "alice@gmail.com");

        dao.createUser(user);
        UserData stored = dao.getUser("alice");

        assertNotNull(stored);
        assertEquals("alice", stored.username());
        assertEquals("alice@gmail.com", stored.email());
        assertNotEquals("password123", stored.password());
    }

    @Test
    void createUserNegativeDuplicateUsername() throws DataAccessException {
        UserData user = new UserData("alice", "password123", "alice@gmail.com");
        dao.createUser(user);

        assertThrows(DataAccessException.class, () -> dao.createUser(user));
    }

    @Test
    void getUserPositive() throws DataAccessException {
        dao.createUser(new UserData("bob", "secret", "bob@email.com"));

        UserData user = dao.getUser("bob");

        assertNotNull(user);
        assertEquals("bob", user.username());
        assertEquals("bob@email.com", user.email());
    }

    @Test
    void getUserNegativeMissingUser() throws DataAccessException {
        UserData user = dao.getUser("missing");

        assertNull(user);
    }

    @Test
    void createAuthPositive() throws DataAccessException {
        dao.createUser(new UserData("charlie", "pw", "charlie@email.com"));
        AuthData auth = new AuthData("token123", "charlie");

        dao.createAuth(auth);
        AuthData stored = dao.getAuth("token123");

        assertNotNull(stored);
        assertEquals("token123", stored.authToken());
        assertEquals("charlie", stored.username());
    }

    @Test
    void createAuthNegativeDuplicateToken() throws DataAccessException {
        dao.createUser(new UserData("charlie", "pw", "charlie@email.com"));
        AuthData auth = new AuthData("token123", "charlie");
        dao.createAuth(auth);

        assertThrows(DataAccessException.class, () -> dao.createAuth(auth));
    }

    @Test
    void getAuthPositive() throws DataAccessException {
        dao.createUser(new UserData("david", "pw", "david@email.com"));
        dao.createAuth(new AuthData("tokenABC", "david"));

        AuthData auth = dao.getAuth("tokenABC");

        assertNotNull(auth);
        assertEquals("tokenABC", auth.authToken());
        assertEquals("david", auth.username());
    }

    @Test
    void getAuthNegativeMissingToken() throws DataAccessException {
        AuthData auth = dao.getAuth("missing");

        assertNull(auth);
    }

    @Test
    void deleteAuthPositive() throws DataAccessException {
        dao.createUser(new UserData("ellen", "pw", "ellen@gmail.com"));
        dao.createAuth(new AuthData("tokenDelete", "ellen"));

        dao.deleteAuth("tokenDelete");

        assertNull(dao.getAuth("tokenDelete"));
    }

    @Test
    void deleteAuthNegativeMissingTokenDoesNothing() {
        assertDoesNotThrow(() -> dao.deleteAuth("missing"));
    }

    @Test
    void listGamesPositive() throws DataAccessException {
        dao.createGame("game one");
        dao.createGame("game two");

        Collection<GameData> games = dao.listGames();

        assertEquals(2, games.size());
    }

    @Test
    void listGamesNegativeEmptyDatabase() throws DataAccessException {
        Collection<GameData> games = dao.listGames();

        assertNotNull(games);
        assertTrue(games.isEmpty());
    }
    @Test
    void createGamePositive() throws DataAccessException {
        int gameID = dao.createGame("fresh game");

        GameData game = dao.getGame(gameID);

        assertTrue(gameID > 0);
        assertNotNull(game);
        assertEquals("fresh game", game.gameName());
        assertNull(game.whiteUsername());
        assertNull(game.blackUsername());
        assertNotNull(game.game());
    }

    @Test
    void createGameNegativeNullName() {
        assertThrows(DataAccessException.class, () -> dao.createGame(null));
    }

    @Test
    void getGamePositive() throws DataAccessException {
        int gameID = dao.createGame("stored game");

        GameData game = dao.getGame(gameID);

        assertNotNull(game);
        assertEquals(gameID, game.gameID());
        assertEquals("stored game", game.gameName());

        ChessPiece piece = game.game().getBoard().getPiece(new ChessPosition(2, 1));
        assertNotNull(piece);
    }

    @Test
    void getGameNegativeMissingGame() throws DataAccessException {
        GameData game = dao.getGame(999999);

        assertNull(game);
    }

    @Test
    void updateGamePositivePersistsPlayersAndBoard() throws Exception {
        int gameID = dao.createGame("update test");
        GameData original = dao.getGame(gameID);

        ChessGame game = original.game();
        ChessMove move = new ChessMove(
                new ChessPosition(2, 1),
                new ChessPosition(3, 1),
                null
        );
        game.makeMove(move);

        GameData updated = new GameData(gameID, "whitePlayer", "blackPlayer", "update test", game);

        dao.updateGame(updated);
        GameData reloaded = dao.getGame(gameID);

        assertNotNull(reloaded);
        assertEquals("whitePlayer", reloaded.whiteUsername());
        assertEquals("blackPlayer", reloaded.blackUsername());
        assertEquals("update test", reloaded.gameName());

        ChessPiece oldSquare = reloaded.game().getBoard().getPiece(new ChessPosition(2, 1));
        ChessPiece newSquare = reloaded.game().getBoard().getPiece(new ChessPosition(3, 1));

        assertNull(oldSquare);
        assertNotNull(newSquare);
    }

    @Test
    void updateGameNegativeMissingGame() {
        ChessGame game = new ChessGame();
        GameData missing = new GameData(999999, "w", "b", "missing", game);

        assertThrows(DataAccessException.class, () -> dao.updateGame(missing));
    }
}
