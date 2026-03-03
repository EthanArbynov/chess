package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import handler.ClearHandler;
import handler.GameHandler;
import handler.UserHandler;
import io.javalin.*;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        DataAccess dao = new MemoryDataAccess();

        ClearService clearService = new ClearService(dao);
        ClearHandler clearHandler = new ClearHandler(clearService);

        UserService userService = new UserService(dao);
        UserHandler userHandler = new UserHandler(userService);

        GameService gameService = new GameService(dao);
        GameHandler gameHandler = new GameHandler(gameService);

        javalin.delete("/db", clearHandler::clear);
        javalin.post("/user", userHandler::register);
        javalin.post("/session", userHandler::login);
        javalin.delete("/session", userHandler:: logout);
        javalin.get("/game", gameHandler::listGames);
        javalin.post("/game", gameHandler::createGame);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
