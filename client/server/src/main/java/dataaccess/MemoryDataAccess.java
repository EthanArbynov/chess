package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

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
}
