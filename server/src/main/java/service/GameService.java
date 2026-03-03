package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;

import chess.ChessGame;

public class GameService {
    private final DataAccess dao;

    public GameService(DataAccess dao) {
        this.dao = dao;
    }

    public Collection<GameData> listGames(String authToken) throws DataAccessException {
    if (authToken == null || authToken.isEmpty()) {
        throw new DataAccessException("unauthorized");
    }

    AuthData auth = dao.getAuth(authToken);
    if (auth == null) {
        throw new DataAccessException("unauthorized");
    }

    return dao.listGames();
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        if (authToken == null || authToken.isEmpty()) {
            throw new DataAccessException("unauthorized");
        }

        AuthData auth = dao.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("unauthorized");
        }

        if (gameName == null || gameName.isEmpty()) {
            throw new DataAccessException("bad request");
        }

        int id = dao.createGame(gameName);
        return id;
    }

    public void joinGame(String authToken, Integer gameID, String playerColor) throws DataAccessException {
        if (authToken == null || authToken.isEmpty()) {
            throw new DataAccessException("unauthorized");
        }

        AuthData auth = dao.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("unauthorized");
        }

        if (gameID == null) {
            throw new DataAccessException("bad request");
        }

        if (playerColor == null) {
            throw new DataAccessException("bad request");
        }

        if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
            throw new DataAccessException("bad request");
        }

        GameData game = dao.getGame(gameID);
        if (game == null) {
            throw new DataAccessException("bad request");
        }

        String username = auth.username();
        if (playerColor.equals("WHITE")) {
            if (game.whiteUsername() != null && !game.whiteUsername().equals(username)) {
                throw new DataAccessException("forbidden");
            }

            GameData updated = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
            dao.updateGame(updated);
            return;
        }

        if (game.blackUsername() != null && !game.blackUsername().equals(username)) {
            throw new DataAccessException("forbidden");
        }

        GameData updated = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
        dao.updateGame(updated);
    }
}
