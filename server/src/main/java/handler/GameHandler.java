package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.GameData;
import service.GameService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class GameHandler {
    private final GameService gameService;
    private final Gson gson = new Gson();

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

    public void createGame(Context ctx) {
        try {
            String token = ctx.header("authorization");

            CreateGameRequest req = gson.fromJson(ctx.body(), CreateGameRequest.class);
            if (req == null || req.gameName == null || req.gameName.isEmpty()) {
                Map<String, String> resp = new HashMap<>();
                resp.put("message", "Error: bad request");
                ctx.status(400);
                ctx.json(resp);
                return;
            }
            int id = gameService.createGame(token, req.gameName);

            ctx.status(200);
            ctx.json(new CreateGameResult(id));
        } catch (DataAccessException e) {
            Map<String, String> resp = new HashMap<>();
            String msg = e.getMessage();

            if (msg.equals("unauthorized")) {
                resp.put("message", "Error: unauthorized");
                ctx.status(401);
                ctx.json(resp);
                return;
            }

            if (msg.equals("bad request")) {
                resp.put("message", "Error: bad request");
                ctx.status(400);
                ctx.json(resp);
                return;
            }
            resp.put("message", "Error: " + msg);
            ctx.status(500);
            ctx.json(resp);
        } catch (Exception e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("message", "Error: " + e.getMessage());
            ctx.status(500);
            ctx.json(resp);
        }
    }

    public void joinGame(Context ctx) {
        try {
            String token = ctx.header("authorization");
            JoinGameRequest req = gson.fromJson(ctx.body(), JoinGameRequest.class);

            if (req == null || req.gameID == null || req.playerColor == null) {
                Map<String, String> resp = new HashMap<>();
                resp.put("message", "Error: bad request");
                ctx.status(400);
                ctx.json(resp);
                return;
            }
            gameService.joinGame(token, req.gameID, req.playerColor);
            ctx.status(200);
            ctx.result("{}");
        } catch (DataAccessException e) {
            String msg = e.getMessage();
            Map<String, String> resp = new HashMap<>();

            if (msg.equals("unauthorized")) {
                resp.put("message", "Error: unauthorized");
                ctx.status(401);
                ctx.json(resp);
                return;
            }

            if (msg.equals("bad request")) {
                resp.put("message", "Error: bad request");
                ctx.status(400);
                ctx.json(resp);
                return;
            }

            if (msg.equals("forbidden")) {
                resp.put("message", "Error: forbidden");
                ctx.status(403);
                ctx.json(resp);
                return;
            }

            resp.put("message", "Error: " + msg);
            ctx.status(500);
            ctx.json(resp);
        }
    }
}
