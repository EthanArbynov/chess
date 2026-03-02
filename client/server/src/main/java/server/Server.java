package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import handler.ClearHandler;
import handler.UserHandler;
import io.javalin.*;
import service.ClearService;
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

        javalin.delete("/db", clearHandler::clear);
        javalin.post("/user", userHandler::register);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
