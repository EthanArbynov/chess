package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryDataAccess implements DataAccess {
    public Map<String, UserData> users = new HashMap<>();
    public Map<String, AuthData> authTokens = new HashMap<>();
    public Map<Integer, GameData> games = new HashMap<>();
    public int nextGameId = 1;

    @Override
    public void clear() {
        users.clear();
        authTokens.clear();
        games.clear();
        nextGameId = 1;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (users.containsKey(user.username())) {
            throw new DataAccessException("forbidden");
        }

        String hashed = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        users.put(user.username(), new UserData(user.username(), hashed, user.email()));
    }

    @Override
    public UserData getUser(String username) {
        return users.get(username);
    }

    @Override
    public void createAuth(AuthData auth) {
        authTokens.put(auth.authToken(), auth);
    }

    @Override
    public AuthData getAuth(String authToken) {
        return authTokens.get(authToken);
    }

    @Override
    public void deleteAuth(String authToken) {
        authTokens.remove(authToken);
    }

    @Override
    public Collection<GameData> listGames() {
        return games.values();
    }

    @Override
    public int createGame(String gameName) {
        int id = nextGameId;
        nextGameId++;

        chess.ChessGame game = new chess.ChessGame();

        GameData gameData = new GameData(id, null, null, gameName, game);
        games.put(id, gameData);
        return id;
    }

    @Override
    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        if (!games.containsKey(game.gameID())) {
            throw new DataAccessException("game not found");
        }
        games.put(game.gameID(), game);
    }
}
