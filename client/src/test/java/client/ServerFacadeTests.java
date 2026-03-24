package client;

import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    public void clearDatabase() throws Exception {
        facade.clear();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerPositive() throws Exception {
        String authToken = facade.register("player1", "password", "p1@email.com");
        assertNotNull(authToken);
        assertFalse(authToken.isBlank());
    }

    @Test
    public void registerNegative() throws Exception {
        facade.register("player1", "password", "p1@email.com");

        assertThrows(ClientException.class, () ->
                facade.register("player1", "password", "p1@email.com"));
    }

    @Test
    public void loginPositive() throws Exception {
        facade.register("player1", "password", "p1@email.com");

        String authToken = facade.login("player1", "password");
        assertNotNull(authToken);
        assertFalse(authToken.isBlank());
    }

    @Test
    public void loginNegative() {
        assertThrows(ClientException.class, () ->
                facade.login("badUser", "badPassword"));
    }

    @Test
    public void logoutPositive() throws Exception {
        String authToken = facade.register("player1", "password", "p1@email.com");

        assertDoesNotThrow(() -> facade.logout(authToken));
    }

    @Test
    public void logoutNegative() {
        assertThrows(ClientException.class, () ->
                facade.logout("badAuthToken"));
    }

    @Test
    public void createGamePositive() throws Exception {
        String authToken = facade.register("player1", "password", "p1@email.com");

        int gameID = facade.createGame(authToken, "testGame");
        assertTrue(gameID > 0);
    }

    @Test
    public void createGameNegative() {
        assertThrows(ClientException.class, () ->
                facade.createGame("badAuthToken", "testGame"));
    }

    @Test
    public void listGamesPositive() throws Exception {
        String authToken = facade.register("player1", "password", "p1@email.com");
        facade.createGame(authToken, "testGame");

        ListGamesResponse response = facade.listGames(authToken);
        assertNotNull(response);
        assertNotNull(response.games());
        assertEquals(1, response.games().size());
    }

    @Test
    public void listGamesNegative() {
        assertThrows(ClientException.class, () ->
                facade.listGames("badAuthToken"));
    }

    @Test
    public void joinGamePositive() throws Exception {
        String authToken = facade.register("player1", "password", "p1@email.com");
        int gameID = facade.createGame(authToken, "testGame");

        assertDoesNotThrow(() -> facade.joinGame(authToken, "WHITE", gameID));
    }

    @Test
    public void joinGameNegative() throws Exception {
        String authToken = facade.register("player1", "password", "p1@email.com");
        int gameID = facade.createGame(authToken, "testGame");

        assertThrows(ClientException.class, () ->
                facade.joinGame("badAuthToken", "WHITE", gameID));
    }
}