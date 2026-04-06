package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.commands.MakeMoveCommand;
import websocket.messages.NotificationMessage;

import io.javalin.websocket.WsConfig;
import io.javalin.websocket.WsContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketHandler {

    private final DataAccess dao;
    private final Gson gson = new Gson();

    private final Map<String, Connection> connections = new ConcurrentHashMap<>();

    public WebSocketHandler(DataAccess dao) {
        this.dao = dao;
    }

    public void handle(WsConfig ws) {

        ws.onConnect(ctx -> {
            // do nothing yet
        });

        ws.onClose(ctx -> {
            connections.remove(ctx.sessionId());
        });

        ws.onMessage(ctx -> {
            try {
                UserGameCommand command = gson.fromJson(ctx.message(), UserGameCommand.class);

                if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
                    connect(ctx, command);
                } else if (command.getCommandType() == UserGameCommand.CommandType.MAKE_MOVE) {
                    MakeMoveCommand moveCommand = gson.fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(ctx, moveCommand);
                } else if (command.getCommandType() == UserGameCommand.CommandType.LEAVE) {
                    leave(ctx, command);
                } else if (command.getCommandType() == UserGameCommand.CommandType.RESIGN) {
                    resign(ctx, command);
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

            connections.put(ctx.sessionId(), new Connection(ctx, command.getGameID(), username));

            ChessGame game = gameData.game();
            LoadGameMessage message = new LoadGameMessage(game);
            ctx.send(gson.toJson(message));

            String messageText;

            if (username.equals(gameData.whiteUsername())) {
                messageText = username + " connected as WHITE";
            } else if (username.equals(gameData.blackUsername())) {
                messageText = username + " connected as BLACK";
            } else {
                messageText = username + " connected as an observer";
            }

            NotificationMessage note = new NotificationMessage(messageText);
            broadcastExcept(command.getGameID(), ctx.sessionId(), gson.toJson(note));

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

    private void makeMove(WsContext ctx, MakeMoveCommand command) {
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

            ChessGame game = gameData.game();
            if (game.isGameOver()) {
                sendError(ctx, "Error: game is over");
                return;
            }
            game.makeMove(command.getMove());

            GameData updatedGame = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    game
            );

            dao.updateGame(updatedGame);

            LoadGameMessage loadMessage = new LoadGameMessage(game);
            broadcast(command.getGameID(), gson.toJson(loadMessage));

            NotificationMessage moveNote =
                    new NotificationMessage(auth.username() + " made a move");

            broadcastExcept(command.getGameID(), ctx.sessionId(), gson.toJson(moveNote));

            if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                NotificationMessage checkmateNote =
                        new NotificationMessage("WHITE is in checkmate");
                broadcast(command.getGameID(), gson.toJson(checkmateNote));

            } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                NotificationMessage checkmateNote =
                        new NotificationMessage("BLACK is in checkmate");
                broadcast(command.getGameID(), gson.toJson(checkmateNote));

            } else if (game.isInStalemate(ChessGame.TeamColor.WHITE) ||
                    game.isInStalemate(ChessGame.TeamColor.BLACK)) {
                NotificationMessage stalemateNote =
                        new NotificationMessage("The game is in stalemate");
                broadcast(command.getGameID(), gson.toJson(stalemateNote));

            } else if (game.isInCheck(ChessGame.TeamColor.WHITE)) {
                NotificationMessage checkNote =
                        new NotificationMessage("WHITE is in check");
                broadcast(command.getGameID(), gson.toJson(checkNote));

            } else if (game.isInCheck(ChessGame.TeamColor.BLACK)) {
                NotificationMessage checkNote =
                        new NotificationMessage("BLACK is in check");
                broadcast(command.getGameID(), gson.toJson(checkNote));
            }

        } catch (Exception e) {
            sendError(ctx, "Error: invalid move");
        }
    }

    private void broadcast(int gameID, String message) {
        for (Connection connection : connections.values()) {
            if (connection.gameID == gameID) {
                connection.ctx.send(message);
            }
        }
    }

    private void broadcastExcept(int gameID, String excludedSessionID, String message) {
        for (Map.Entry<String, Connection> entry : connections.entrySet()) {
            if (entry.getValue().gameID == gameID && !entry.getKey().equals(excludedSessionID)) {
                entry.getValue().ctx.send(message);
            }
        }
    }

    private void leave(WsContext ctx, UserGameCommand command) {
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

            String white = gameData.whiteUsername();
            String black = gameData.blackUsername();

            if (username.equals(white)) {
                white = null;
            }
            if (username.equals(black)) {
                black = null;
            }

            GameData updatedGame = new GameData(
                    gameData.gameID(),
                    white,
                    black,
                    gameData.gameName(),
                    gameData.game()
            );

            dao.updateGame(updatedGame);
            connections.remove(ctx.sessionId());

            NotificationMessage note = new NotificationMessage(username + " left the game");
            broadcastExcept(command.getGameID(), ctx.sessionId(), gson.toJson(note));

        } catch (Exception e) {
            sendError(ctx, "Error: could not leave game");
        }
    }

    private void resign(WsContext ctx, UserGameCommand command) {
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

            boolean isPlayer = username.equals(gameData.whiteUsername()) || username.equals(gameData.blackUsername());
            if (!isPlayer) {
                sendError(ctx, "Error: observers cannot resign");
                return;
            }

            ChessGame game = gameData.game();
            game.setGameOver(true);

            GameData updatedGame = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    game
            );

            dao.updateGame(updatedGame);

            NotificationMessage note = new NotificationMessage(username + " resigned the game");
            broadcast(command.getGameID(), gson.toJson(note));

        } catch (Exception e) {
            sendError(ctx, "Error: could not resign");
        }
    }
}