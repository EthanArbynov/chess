package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import jakarta.websocket.Session;
import model.AuthData;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;

import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler {

    private final DataAccess dao;
    private final Gson gson = new Gson();

    // session id -> connection info
    private final Map<String, Connection> connections = new ConcurrentHashMap<>();

    public WebSocketHandler(DataAccess dao) {
        this.dao = dao;
    }

    public void handle(WsConfig ws) {

        ws.onConnect(ctx -> {
            // do nothing yet
        });

        ws.onClose(ctx -> {
            connections.remove(ctx.getSessionId());
        });

        ws.onMessage(ctx -> {
            try {
                UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);

                if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
                    connect(ctx, command);
                } else if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
                    // later
                } else if (command.getCommandType() == UserGameCommand.CommandType.LEAVE) {
                    // later
                } else if (command.getCommandType() == UserGameCommand.CommandType.RESIGN) {
                    // later
                }

            } catch (Exception e) {
                sendError(ctx, "Error: bad command");
            }
        });
    }

    private void connect(WsContext ctx, UserGameCommand command) {
        try {
            AuthData auth = dao.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(ctx, "Error: unauthorized");
                return;
            }

            GameData gameData = dao.getGame(command.getGameID());
            if (gameData == null) {
                sendError(ctx, "Error: bad game");
                return;
            }

            String username = auth.username();

            connections.put(ctx.getSessionId(), new Connection(ctx, command.getGameID(), username));

            ChessGame game = gameData.game();
            LoadGameMessage message = new LoadGameMessage(game);
            ctx.send(gson.toJson(message));

        } catch (DataAccessException e) {
            sendError(ctx, "Error: server problem");
        }
    }

    private void sendError(WsContext ctx, String errorText) {
        try {
            ErrorMessage message = new ErrorMessage(errorText);
            ctx.send(gson.toJson(message));
        } catch (Exception ignored) {
        }
    }

    private static class Connection {
        private final WsContext ctx;
        private final int gameID;
        private final String username;

        public Connection(WsContext ctx, int gameID, String username) {
            this.ctx = ctx;
            this.gameID = gameID;
            this.username = username;
        }
    }
}