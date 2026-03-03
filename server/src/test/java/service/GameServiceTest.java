package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private DataAccess makeDao() {
        return new dataaccess.MemoryDataAccess();
    }

    private String registerAndGetToken(UserService userService, String username) throws Exception {
        return userService.register(new UserData(username, "pw", username + "@x.com")).authToken;
    }

    @Test
    void listGamesPositiveReturnsCollection() throws Exception {
        var dao = makeDao();
        var userService = new UserService(dao);
        var gameService = new GameService(dao);

        String token = registerAndGetToken(userService, "alice");
        gameService.createGame(token, "game1");

        var games = gameService.listGames(token);

        assertNotNull(games);
        assertTrue(games.size() >= 1);
    }

    @Test
    void listGamesNegativeNullTokenThrowsUnauthorized() throws Exception {
        var dao = makeDao();
        var gameService = new GameService(dao);

        var ex = assertThrows(DataAccessException.class, () -> gameService.listGames(null));
        assertEquals("unauthorized", ex.getMessage());
    }

    @Test
    void createGamePositiveReturnsGameId() throws Exception {
        var dao = makeDao();
        var userService = new UserService(dao);
        var gameService = new GameService(dao);

        String token = registerAndGetToken(userService, "alice");

        int gameId = gameService.createGame(token, "My Game");

        assertTrue(gameId > 0);
        assertNotNull(dao.getGame(gameId));
        assertEquals("My Game", dao.getGame(gameId).gameName());
    }

    @Test
    void createGameNegativeBlankNameThrowsBadRequest() throws Exception {
        var dao = makeDao();
        var userService = new UserService(dao);
        var gameService = new GameService(dao);

        String token = registerAndGetToken(userService, "alice");

        var ex = assertThrows(DataAccessException.class, () -> gameService.createGame(token, ""));
        assertEquals("bad request", ex.getMessage());
    }

    @Test
    void joinGamePositiveSetsWhitePlayer() throws Exception {
        var dao = makeDao();
        var userService = new UserService(dao);
        var gameService = new GameService(dao);

        String token = registerAndGetToken(userService, "alice");
        int gameId = gameService.createGame(token, "G");

        gameService.joinGame(token, gameId, "WHITE");

        assertEquals("alice", dao.getGame(gameId).whiteUsername());
    }

    @Test
    void joinGameNegativeBadColorThrowsBadRequest() throws Exception {
        var dao = makeDao();
        var userService = new UserService(dao);
        var gameService = new GameService(dao);

        String token = registerAndGetToken(userService, "alice");
        int gameId = gameService.createGame(token, "G");

        var ex = assertThrows(DataAccessException.class,
                () -> gameService.joinGame(token, gameId, "GREEN"));

        assertEquals("bad request", ex.getMessage());
    }

    @Test
    void joinGameNegativeStealColorThrowsForbidden() throws Exception {
        var dao = makeDao();
        var userService = new UserService(dao);
        var gameService = new GameService(dao);

        String tokenA = registerAndGetToken(userService, "alice");
        String tokenB = registerAndGetToken(userService, "bob");

        int gameId = gameService.createGame(tokenA, "G");

        gameService.joinGame(tokenA, gameId, "WHITE");

        var ex = assertThrows(DataAccessException.class,
                () -> gameService.joinGame(tokenB, gameId, "WHITE"));

        assertEquals("forbidden", ex.getMessage());
    }
}
