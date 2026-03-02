package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.GameData;
import service.GameService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void listGames(Context ctx) {
        try {
            String token = ctx.header("authorization");

            var games = gameService.listGames(token);

            ArrayList<GameSummary> summaries = new ArrayList<>();
            for (GameData g : games) {
                summaries.add(new GameSummary(g.gameID(), g.whiteUsername(), g.blackUsername(), g.gameName()));
            }

            ListGamesResult result = new ListGamesResult(summaries);

            ctx.status(200);
            ctx.json(result);
        } catch (DataAccessException e) {
            Map<String, String> resp = new HashMap<>();

            if (e.getMessage().equals("unauthorized")) {
                resp.put("message", "Error: unauthorized");
                ctx.status(401);
                ctx.json(resp);
                return;
            }

            resp.put("message", "Error: " + e.getMessage());
            ctx.status(500);
            ctx.json(resp);
        } catch (Exception e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("message", "Error: " + e.getMessage());
            ctx.status(500);
            ctx.json(resp);
        }
    }
}
