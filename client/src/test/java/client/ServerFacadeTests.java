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
}