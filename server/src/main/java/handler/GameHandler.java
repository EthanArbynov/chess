package handler;

import dataaccess.DataAccessException;
import io.javalin.http.Context;
import model.GameData;
import service.GameService;

import java.util.ArrayList;

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
            handleListGamesError(ctx, e);
        } catch (Exception e) {
            HandlerUtil.sendError(ctx, 500, e.getMessage());
        }
    }

    public void createGame(Context ctx) {
        try {
            String token = ctx.header("authorization");
            CreateGameRequest req = gson.fromJson(ctx.body(), CreateGameRequest.class);
            if (req == null || req.gameName == null || req.gameName.isEmpty()) {
                HandlerUtil.sendError(ctx, 400, "bad request");
                return;
            }
            int id = gameService.createGame(token, req.gameName);
            ctx.status(200);
            ctx.json(new CreateGameResult(id));
        } catch (DataAccessException e) {
            handleCreateGameError(ctx, e);
        } catch (Exception e) {
            HandlerUtil.sendError(ctx, 500, e.getMessage());
        }
    }

    public void joinGame(Context ctx) {
        try {
            String token = ctx.header("authorization");
            JoinGameRequest req = gson.fromJson(ctx.body(), JoinGameRequest.class);

            if (req == null || req.gameID == null || req.playerColor == null) {
                HandlerUtil.sendError(ctx, 400, "bad request");
                return;
            }
            gameService.joinGame(token, req.gameID, req.playerColor);
            ctx.status(200);
            ctx.result("{}");
        } catch (DataAccessException e) {
            handleJoinGameError(ctx, e);
        } catch (Exception e) {
            HandlerUtil.sendError(ctx, 500, "bad request");
        }
    }

    private void handleListGamesError(Context ctx, DataAccessException e) {
        String msg = e.getMessage();

        if ("unauthorized".equals(msg)) {
            HandlerUtil.sendError(ctx, 401, "unauthorized");
            return;
        }

        HandlerUtil.sendError(ctx, 500, msg);
    }

    private void handleCreateGameError(Context ctx, DataAccessException e) {
        String msg = e.getMessage();

        if ("unauthorized".equals(msg)) {
            HandlerUtil.sendError(ctx, 401, "unauthorized");
            return;
        }

        if ("bad request".equals(msg)) {
            HandlerUtil.sendError(ctx, 400, "bad request");
            return;
        }

        HandlerUtil.sendError(ctx, 500, msg);
    }

    private void handleJoinGameError(Context ctx, DataAccessException e) {
        String msg = e.getMessage();

        if ("unauthorized".equals(msg)) {
            HandlerUtil.sendError(ctx, 401, "unauthorized");
            return;
        }

        if ("bad request".equals(msg)) {
            HandlerUtil.sendError(ctx, 400, "bad request");
            return;
        }

        if ("forbidden".equals(msg)) {
            HandlerUtil.sendError(ctx, 403, "forbidden");
            return;
        }

        HandlerUtil.sendError(ctx, 500, msg);
    }
}
