package dataaccess;

import model.AuthData;
import model.UserData;
import model.GameData;

import java.util.Collection;

public interface DataAccess {
    void clear() throws DataAccessException;
    void createUser(UserData user) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void createAuth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void deleteAuth(String authToken) throws DataAccessException;
    Collection<GameData> listGames() throws DataAccessException;
    int createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
}
