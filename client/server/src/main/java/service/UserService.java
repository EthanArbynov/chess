package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final DataAccess dao;

    public UserService(DataAccess dao) {
        this.dao = dao;
    }

    public static class RegisterResult {
        public String username;
        public String authToken;

        public RegisterResult(String username, String authToken) {
            this.username = username;
            this.authToken = authToken;
        }
    }

    public RegisterResult register(UserData user) throws DataAccessException {
        if (user == null || user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("bad request");
        }
        dao.createUser(user);
        String token = UUID.randomUUID().toString();
        dao.createAuth(new AuthData(token, user.username()));
        return new RegisterResult(user.username(), token);
    }
}
