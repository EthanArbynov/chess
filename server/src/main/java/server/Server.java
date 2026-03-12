package server;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.DatabaseConfigurer;
import dataaccess.MySQLDataAccess;
import handler.ClearHandler;
import handler.GameHandler;
import handler.UserHandler;
import io.javalin.*;
import io.javalin.http.staticfiles.Location;
import io.javalin.json.JavalinGson;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        try{
            DatabaseConfigurer.configureDataBase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        javalin = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/web";
                staticFiles.location = Location.CLASSPATH;
            });
            config.jsonMapper(new JavalinGson());
        });

        DataAccess dao = new MySQLDataAccess();

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
        javalin.put("/game", gameHandler::joinGame);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
