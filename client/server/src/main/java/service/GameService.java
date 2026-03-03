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

    public Collection<GameData> listGames(string authToken) throws DataAccessException {
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

}
