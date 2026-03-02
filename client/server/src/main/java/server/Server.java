package server;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import handler.ClearHandler;
import io.javalin.*;
import service.ClearService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        DataAccess dao = new MemoryDataAccess();

        ClearService clearService = new ClearService(dao);
        ClearHandler clearHandler = new ClearHandler(clearService);

        javalin.delete("/db", clearHandler::clear);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
